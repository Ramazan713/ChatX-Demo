import { Request, Response } from 'express';
import { messageService } from '../services/messageService';
import { RoomMesageInput } from '../types/schemas';


export default class MessagesController {

    async markAsRead(req: Request, res: Response){
        const user = req.user!!
        const messageId = req.params.messageId;
        const roomId = req.params.roomId

        const messageDto = await messageService.markAsRead(messageId, user.username, user.id, roomId);
        res.status(200).send(messageDto);
    }

    async getMessages(req: Request, res: Response): Promise<any>{
        const userId = req.user!!.id
        const limit  = Number(req.query.limit) || 20;
        const roomId = req.params.roomId;
        console.log(roomId)
        const since = req.query.since ? new Date(req.query.since as string) : undefined;
        const afterId = req.query.afterId as string | undefined;

        const messages = await messageService.getMessages({userId, roomId, limit, since, afterId})
        res.json(messages)
    }

    async sendMessage(req: Request, res: Response): Promise<any>{
        const { message }: RoomMesageInput = req.body
        const userId = req.user!!.id
        const roomId = req.params.roomId
    
        const createdMessage = await messageService.sendMessage(userId, roomId, message)
        if(!createdMessage) return res.status(404).send({"error": "not found"})
        return res.send(createdMessage)
    }

}