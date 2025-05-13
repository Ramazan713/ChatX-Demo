import { Request, Response } from 'express';
import { messageService } from '../services/messageService';
import { RoomMesageInput } from '../types/schemas';


export default class MessagesController {

    async markAsRead(req: Request, res: Response){
        const user = req.user!!
        const messageId = req.params.messageId;
        const roomId = req.validated?.params?.roomId;

        const messageDto = await messageService.markAsRead(messageId, user.username, user.id, roomId);
        res.status(200).send(messageDto);
    }

    async getMessages(req: Request, res: Response): Promise<any>{
        const userId = req.user!!.id
        const roomId = req.params.roomId;

        const messages = await messageService.getMessages({userId, roomId, ...req.validated?.query})
        res.json(messages)
    }

    async sendMessage(req: Request, res: Response): Promise<any>{
        const { message }: RoomMesageInput = req.validated?.body
        const userId = req.user!!.id
        const roomId = req.validated?.params?.roomId;
    
        const createdMessage = await messageService.sendMessage(userId, roomId, message)
        if(!createdMessage) return res.status(404).send({"error": "not found"})
        return res.send(createdMessage)
    }

}