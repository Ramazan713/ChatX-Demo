import { Request, Response } from 'express';
import { chatService } from './chatService';
import { mapMessageToDto, mapUserRoomsToDto, mapUserRoomToDto } from './mapper';
import { CreateRoomInput, RoomMesageInput } from './schemas';


export default class ChatController {

    async listRooms(req: Request, res: Response){
        const userId = req.user!!.id
        const userRooms = await chatService.listUserRooms(userId)
        const mappedData = mapUserRoomsToDto(userRooms)
        res.json(mappedData)
    }

    async join(req: Request, res: Response): Promise<any>{
        const userId = req.user!!.id
        const { name, isPublic }: CreateRoomInput = req.body

        try {
            const exists = await chatService.existsUserRoom(userId, name);
            const userRoom = await chatService.joinRoom(userId, name, isPublic ?? true);
            const mappedData = mapUserRoomToDto(userRoom)
            return res.status(exists ? 200 : 201).json(mappedData);
          } catch (err: any) {
            console.error(err);
            return res.status(500).json({ error: 'Katılırken hata oluştu.' });
        }
    }

    async left(req: Request, res: Response){
        const userId = req.user!!.id
        const roomId = req.params.roomId;

        const userRoom = await chatService.leftRoom(userId, roomId);
        const mappedData = mapUserRoomToDto(userRoom)
        res.status(200).send(mappedData);
    }

    async markAsRead(req: Request, res: Response){
        const user = req.user!!
        const messageId = req.params.messageId;

        const messageDto = await chatService.markAsRead(messageId, user.username, user.id);
        res.status(200).send(messageDto);
    }

    async getMessages(req: Request, res: Response): Promise<any>{
        const userId = req.user!!.id
        const limit  = Number(req.query.limit) || 20;
        const roomId = req.params.roomId;
        const since = req.query.since ? new Date(req.query.since as string) : undefined;
        const afterId = req.query.afterId as string | undefined;

        const messages = await chatService.getMessages({userId, roomId, limit, since, afterId})
        res.json(messages)
    }

    async sendMessage(req: Request, res: Response): Promise<any>{
        const { roomId, message }: RoomMesageInput = req.body
        const userId = req.user!!.id
    
        const createdMessage = await chatService.sendMessage(userId, roomId, {message})
        if(!createdMessage) return res.status(404).send({"error": "not found"})
        return res.send(createdMessage)
    }

}