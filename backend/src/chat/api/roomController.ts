import { Request, Response } from 'express';
import { messageService } from '../services/messageService';
import { roomService } from '../services/roomService';
import { CreateRoomInput, MessageQueryInput } from '../types/schemas';


export default class RoomController {

    async listRooms(req: Request, res: Response){
        const userId = req.user!!.id
        const userRooms = await roomService.listUserRooms(userId)
        res.json(userRooms)
    }

    async updateRoom(req: Request, res: Response){
        const userId = req.user!!.id
        const roomId = req.validated?.params?.roomId;
        const userRoom = await roomService.updateRoom(userId, roomId, req.validated?.body)
        res.json(userRoom)
    }

    async deleteRoom(req: Request, res: Response){
        const userId = req.user!!.id
        const roomId = req.validated?.params?.roomId;
        await roomService.deleteRoom(userId, roomId)
        res.status(204).send()
    }

    async join(req: Request, res: Response): Promise<any>{
        const userId = req.user!!.id
        const { name, isPublic }: CreateRoomInput = req.validated?.body

        try {
            const exists = await roomService.existsUserRoom(userId, name);
            const userRoom = await roomService.joinRoom(userId, name, isPublic ?? true);
            return res.status(exists ? 200 : 201).json(userRoom);
          } catch (err: any) {
            console.error(err);
            return res.status(500).json({ error: 'Katılırken hata oluştu.' });
        }
    }

    async getRoomsWithDetail(req: Request, res: Response){
        const userId = req.user!!.id
        const roomId = req.validated?.params?.roomId as string;
        const { include }: MessageQueryInput = req.validated?.query

        const userRoom = await roomService.getUserRoom(userId, roomId)
        
        let response: any

        if(include == "messages"){
            const messagesResponse = await messageService.getMessages({userId, roomId, ...req.validated?.query, include})
            response = {
                "room": userRoom,
                "messages": messagesResponse.messages,
                "pageInfo": messagesResponse.pageInfo
            }
        }else{
            response = {
                "room": userRoom,
            }
        }
        res.json(response)
    }
}