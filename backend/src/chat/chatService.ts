import prisma from "../prisma";
import { mapMessageToDto } from "./mapper";
import Message from "./models/message";
import { ChatMessageDto, FetchMessageOptions } from "./types";


class ChatService {

    async existsUserRoom(userId: string, roomName: string): Promise<boolean>{
        return (await prisma.userRoom.findFirst({
            where: {room: {name: roomName}, userId}
        })) != null
    }

    async listUserRooms(userId: string){
        const userRooms = await prisma.userRoom.findMany({
            where: {
                userId: userId
            },
            include: {
                room: true
            },
        })
        return userRooms
    }

    async joinRoom(userId: string, roomName: string, isPublic: boolean = true) {
        return await prisma.$transaction(async(txn) => {
            let room = await txn.room.findUnique({
                where: { name: roomName }
            })
    
            if(room == null){
                room = await txn.room.create({
                    data: {
                        name: roomName,
                        isPublic: isPublic,
                    }
                })
            }
    
            let userRoom = await txn.userRoom.findFirst({
                where: { userId: userId, roomId: room.id },
                include: { room: true }
            })
            if(!userRoom){
                userRoom = await txn.userRoom.create({
                    data: {
                        roomId: room.id,
                        userId
                    },
                    include: { room: true }
                })
                await txn.room.update({
                    where: {id: room.id},
                    data: {
                        participants: {
                            connect: {userId_roomId: {userId, roomId: room.id}}
                        }
                    }
                })
            }
            return userRoom
        })
    }

    async leftRoom(userId: string, roomId: string){
        return await prisma.$transaction(async(txn) => {
            const userRoom = await txn.userRoom.update({
                where: {userId_roomId: {userId, roomId}},
                data: {
                    leftAt: new Date()
                },
                include: { room: true }
            })
            return userRoom
        })
    }


    async getMessages({
        userId, roomId, limit = 20, afterId, since
    }: FetchMessageOptions): Promise<ChatMessageDto[]>{
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


    async sendMessage(userId: string, roomId: string, message: Message): Promise<ChatMessageDto | null>{
        const userRoom = await prisma.userRoom.findUnique({
            where: {userId_roomId: { userId, roomId }, OR: [{leftAt: {isSet: false}}, {leftAt: null}]}
        })
        if(!userRoom) return null
        const createdMessage = await prisma.message.create({
            data: {
                text: message.message,
                roomId: roomId,
                authorId: userId,
            },
            include: {
                author: true,
            }
        })
        return mapMessageToDto(createdMessage)
    }

    async markAsReads(messageIds: string[], username: string, userId: string): Promise<ChatMessageDto[]>{
        const messages = await prisma.message.findMany({
            where: {id: {in: messageIds}},
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

    async markAsRead(messageId: string, username: string, userId: string): Promise<ChatMessageDto>{
        const message = await prisma.message.findUnique({
            where: {id: messageId},
            include: {author: true}
        })
        if(message?.authorId == userId) return mapMessageToDto(message)
        if(message?.readBy.includes(username)) return mapMessageToDto(message)
        const updatedMessage = await prisma.message.update({
            where: {id: messageId},
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

const chatService = new ChatService()
export {
    chatService
}