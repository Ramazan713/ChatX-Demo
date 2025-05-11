import prisma from "../../prisma";
import { mapMessageToDto } from "../utils/mapper";
import { ChatMessageDto, FetchMessageOptions } from "../types/types";

class MessageService {

    async getMessages({
        userId, roomId, limit = 20, afterId, since
    }: FetchMessageOptions): Promise<ChatMessageDto[]>
    {
        const userRoom = await prisma.userRoom.findUnique({
            where: {userId_roomId: { userId, roomId }}
        })
        if(!userRoom) return []

        const cursors: any[] = [];
        if (since && afterId) {
            cursors.push(
                { createdAt: { gt: since } },
                {
                    AND: [
                        { createdAt: { equals: since } },
                        { id:        { gt: afterId } }
                    ]
                }
            );
        } else if (since) {
            cursors.push({ createdAt: { gt: since } });
        }

        const where: any = {
            roomId,
            AND: [
            // joinedAt <= createdAt
            { createdAt: { gte: userRoom.joinedAt } },
            // createdAt <= leftAt  (eğer leftAt yoksa tümü)
            ...(userRoom.leftAt ? [{ createdAt: { lte: userRoom.leftAt } }] : []),
            // cursor filter’ı
            ...(cursors.length ? [{ OR: cursors }] : [])
            ]
        };

         const rows = await prisma.message.findMany({
            where,
            include: { author: true },
            orderBy: [
                { createdAt: 'desc' },
                { id:        'desc' }
            ],
            take: limit
        });
        return rows.reverse().map(r => mapMessageToDto(r))
    }


    async sendMessage(userId: string, roomId: string, message: string): Promise<ChatMessageDto | null>{
        const userRoom = await prisma.userRoom.findUnique({
            where: {userId_roomId: { userId, roomId }, OR: [{leftAt: {isSet: false}}, {leftAt: null}]}
        })
        if(!userRoom) return null
        const createdMessage = await prisma.message.create({
            data: {
                text: message,
                roomId: roomId,
                authorId: userId,
            },
            include: {
                author: true,
            }
        })
        return mapMessageToDto(createdMessage)
    }

    async markAsReads(messageIds: string[], username: string, userId: string, roomId: string): Promise<ChatMessageDto[]>{
        const messages = await prisma.message.findMany({
            where: {id: {in: messageIds}, roomId},
        })

        const updetableMessages = messages.filter((m) => !(m.authorId == userId || m.readBy.includes(username)))
        
        await prisma.message.updateMany({
            where: {id: {in: updetableMessages.map(m=>m.id)}},
            data: {
                readBy: {
                    push: username
                }
            }
        })
        const updatedMessages = await prisma.message.findMany({
            where: {id: {in: messageIds}},
            include: {author: true}
        })
        return updatedMessages.map(m => mapMessageToDto(m))
    }

    async markAsRead(messageId: string, username: string, userId: string, roomId: string): Promise<ChatMessageDto>{
        const message = await prisma.message.findUnique({
            where: {id: messageId, roomId},
            include: {author: true}
        })
        if(message?.authorId == userId) return mapMessageToDto(message)
        if(message?.readBy.includes(username)) return mapMessageToDto(message)
        const updatedMessage = await prisma.message.update({
            where: {id: messageId, roomId},
            data: {
                readBy: {
                    push: username
                }
            },
            include: {
                author: true,
            }
        })
        return mapMessageToDto(updatedMessage)
    }
}


const messageService = new MessageService()
export {
    messageService
}