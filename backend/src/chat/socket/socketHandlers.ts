import { createSocketValidator } from "../../middleware/validateSocket";
import prisma from "../../prisma";
import { messageService } from "../services/messageService";
import { chatSchemas } from "../types/schemas";
import { ChatSocket } from "../types/types";
import { notifyRoom } from "../utils/notify";


const typingUsersByRoom = new Map<string, Set<string>>()
const withValidation = createSocketValidator<typeof chatSchemas, ChatSocket>();

export const chatHandlers = {
    joinRoom: withValidation("join room", chatSchemas["join room"], async (socket, { roomId }) => {
        const userId = socket.data.user.id;
        const userRoom = await prisma.userRoom.findFirst({
          where: {roomId, userId, OR: [{leftAt: {isSet: false}}, {leftAt: null}]}
        })

        if(!userRoom){
          console.log("join room failed: ", roomId)
          return
        }
        console.log("join room: ", roomId)
        socket.join(roomId);
        socket.data.joinedRoom = roomId;
      }
    ),
    roomMessage: withValidation("room message", chatSchemas["room message"], async (socket, data) => {
        const { roomId, message, tempId } = data;
        const userId = socket.data.user.id;
        
        const createdMessage = await messageService.sendMessage(userId, roomId, message);
        if (!createdMessage) return;
        
        console.log("message: ", roomId, createdMessage);
        
        emitTypingState(socket, roomId, false);
        
        socket.nsp.to(roomId).emit("room message", { tempId, ...createdMessage });

        await notifyRoom(userId, createdMessage);
      }
    ),
    
    typing: withValidation("typing", chatSchemas["typing"], (socket, { roomId }) => {
        emitTypingState(socket, roomId, true);
      }
    ),  
    
    stopTyping: withValidation("stop typing", chatSchemas["stop typing"], (socket, { roomId }) => {
        emitTypingState(socket, roomId, false);
      }
    ),  
    
    readMessages: withValidation("read messages", chatSchemas["read messages"], async (socket, { messageIds, roomId }) => {
        const user = socket.data.user
        const updatedMessages = await messageService.markAsReads(messageIds, user.username, user.id, roomId);
        socket.nsp.to(roomId).emit("read messages", updatedMessages)
    }) ,

    disconnect: (socket: ChatSocket) => {
      socket.on("disconnect",() => {
        console.log("user disconnect: ", socket.data.user.username)
        const joinedRoom = socket.data.joinedRoom;
        if(joinedRoom != null){
            socket.leave(joinedRoom)
            socket.data.joinedRoom = undefined
        }
      })
    }

    
  };
  
  function emitTypingState(socket: ChatSocket, roomId: string, add: boolean){
    const username = socket.data.user.username

    if(!typingUsersByRoom.has(roomId)){
        typingUsersByRoom.set(roomId, new Set())
    }
    const typingUsers = typingUsersByRoom.get(roomId)!!
    if(add){
        typingUsers.add(username)
    }
    else{
        typingUsers.delete(username)
    }
    // console.log("typing:", add, ": ", typingUsers, "::", username)
    socket.nsp.to(roomId).emit("typing",{ usernames: [...typingUsers] })
}