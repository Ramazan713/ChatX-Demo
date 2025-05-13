import prisma from "../../prisma";
import { MessageQueryInput } from "../types/schemas";
import { ChatMessageDto, ChatMessagesPageDto } from "../types/types";
import { mapMessageToDto } from "../utils/mapper";

class MessageService {

    async getMessages({
        userId, roomId, afterId, beforeId, limit = 20,
    }: MessageQueryInput & { userId: string, roomId: string }): Promise<ChatMessagesPageDto> {
        const userRoom = await prisma.userRoom.findUnique({
            where: {userId_roomId: { userId, roomId }}
        })
        if(!userRoom) return { messages: [], pageInfo: { hasNextPage: false, nextItemId: null, hasPreviousPage: false, previousItemId: null } };

        const whereCommon: any = {
            roomId,
            AND: [
                { createdAt: { gte: userRoom.joinedAt } },
                ...(userRoom.leftAt ? [{ createdAt: { lte: userRoom.leftAt } }] : []),
            ],
        }

        let orderDirection: 'asc' | 'desc' = 'desc'
        if (afterId)  orderDirection = 'asc'

        const whereCursor = {
            ...(afterId  ? { id: { gt: afterId  } } : {}),
            ...(beforeId ? { id: { lt: beforeId } } : {}),
        }

        let items = await prisma.message.findMany({
            where: { ...whereCommon, ...whereCursor },
            include: { author: true },
            orderBy: [
                { createdAt: orderDirection },
                { id:        orderDirection },
            ],
            take: limit + 1,
        })

        const hasExtra = items.length > limit
        if (hasExtra) items = items.slice(0, limit)
        if (orderDirection === 'desc') items = items.reverse();


        const first  = items[0]
        const last   = items[items.length - 1]
        const nextItemId     = last  ? last.id  : null
        const previousItemId = first ? first.id : null

        const hasNextPage = !!last
            && await prisma.message.count({
                where: {
                    roomId,
                    ...whereCommon,
                    createdAt: { gt:  last.createdAt }
                },
                take: 1
            }) > 0

        const hasPreviousPage = !!first
            && await prisma.message.count({
                where: {
                    roomId,
                    ...whereCommon,
                    createdAt: { lt:  first.createdAt }  
                },
                take: 1
            }) > 0
       
        
        
        return {
            messages: items.map(m => mapMessageToDto(m)),
            pageInfo: {
                hasNextPage,
                nextItemId,
                hasPreviousPage,
                previousItemId
            }
        }
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
};
