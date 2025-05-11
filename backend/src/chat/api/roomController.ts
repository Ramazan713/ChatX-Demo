import { Request, Response } from 'express';
import { messageService } from '../services/messageService';
import { roomService } from '../services/roomService';
import { CreateRoomInput } from '../types/schemas';


export default class RoomController {

    async listRooms(req: Request, res: Response){
        const userId = req.user!!.id
        const userRooms = await roomService.listUserRooms(userId)
        res.json(userRooms)
    }

    async updateRoom(req: Request, res: Response){
        const userId = req.user!!.id
        const roomId = req.params.roomId;
        const userRoom = await roomService.updateRoom(userId, roomId, req.body)
        res.json(userRoom)
    }

    async deleteRoom(req: Request, res: Response){
        const userId = req.user!!.id
        const roomId = req.params.roomId;
        await roomService.deleteRoom(userId, roomId)
        res.status(204).send()
    }

    async join(req: Request, res: Response): Promise<any>{
        const userId = req.user!!.id
        const { name, isPublic }: CreateRoomInput = req.body

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
        const limit  = Number(req.query.limit) || 20;
        const roomId = req.params.roomId;
        const since = req.query.since ? new Date(req.query.since as string) : undefined;
        const afterId = req.query.afterId as string | undefined;
        const include = req.query.include

        const userRoom = await roomService.getUserRoom(userId, roomId)
        
        let response: any

        if(include == "messages"){
            const messages = await messageService.getMessages({userId, roomId, limit, afterId, since})
            response = {
                "room": userRoom,
                "messages": messages
            }
        }else{
            response = {
                "room": userRoom,
            }
        }
        res.json(response)
    }
}