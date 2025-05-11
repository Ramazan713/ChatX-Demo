import prisma from "../../prisma"
import { UpdateRoomInput } from "../types/schemas";
import { mapUserRoomToDto } from "../utils/mapper";
import { ChatRoomDto } from "../types/types";

class RoomService {

    async getUserRoom(userId: string, roomId: string): Promise<ChatRoomDto|null>{
        const userRoom = await prisma.userRoom.findUnique({
            where: { userId_roomId: {userId, roomId} },
            include: { room: true }
        })
        if(!userRoom) return null
        return mapUserRoomToDto(userRoom)
    }

    async existsUserRoom(userId: string, roomName: string): Promise<boolean>{
        return (await prisma.userRoom.findFirst({
            where: {room: {name: roomName}, userId}
        })) != null
    }

    async listUserRooms(userId: string): Promise<ChatRoomDto[]>{
        const userRooms = await prisma.userRoom.findMany({
            where: {
                userId: userId
            },
            include: {
                room: true
            },
        })
        return userRooms.map(r => mapUserRoomToDto(r))
    }

    async updateRoom(userId: string, roomId: string, data: UpdateRoomInput): Promise<ChatRoomDto>{
        const userRoom = await prisma.userRoom.update({
            where: {userId_roomId: {roomId, userId}},
            data: {
                muted: data.muted ?? undefined,
                leftAt: data.left ? new Date() : undefined
            },
            include: {room: true}
        })
        return mapUserRoomToDto(userRoom)
    }

    async deleteRoom(userId: string, roomId: string): Promise<void>{
        await prisma.userRoom.delete({
            where: {userId_roomId: {roomId, userId}},
        })
    }

    async joinRoom(userId: string, roomName: string, isPublic: boolean = true): Promise<ChatRoomDto> {
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
            return mapUserRoomToDto(userRoom)
        })
    }
}

const roomService = new RoomService()

export {
    roomService
}