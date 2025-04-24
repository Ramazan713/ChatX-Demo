import { createSocketValidator } from "../middleware/validate";
import { chatService } from "./chatService";
import { mapMessageToDto } from "./mapper";
import { chatSchemas } from "./schemas";
import { ChatSocket } from "./types";


const typingUsersByRoom = new Map<string, Set<string>>()
const withValidation = createSocketValidator<typeof chatSchemas, ChatSocket>();

export const chatHandlers = {
    joinRoom: withValidation("join room", chatSchemas["join room"], async (socket, { roomId }) => {
        console.log("join room: ", roomId)
        socket.join(roomId);
        socket.data.joinedRoom = roomId;
      }
    ),
    roomMessage: withValidation("room message", chatSchemas["room message"], async (socket, data) => {
        const { roomId, message, tempId } = data;
        const userId = socket.data.user.id;
        
        const createdMessage = await chatService.sendMessage(userId, roomId, { message });
        if (!createdMessage) return;
        
        const mappedMessage = mapMessageToDto(createdMessage);
        console.log("message: ", roomId, mappedMessage);
        
        emitTypingState(socket, roomId, false);
        
        socket.nsp.to(roomId).emit("room message", { tempId, ...mappedMessage });
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
        const updatedMessages = await chatService.markAsReads(messageIds, user.username, user.id);
        const mappedMessages = updatedMessages.map(m => mapMessageToDto(m))
        socket.nsp.to(roomId).emit("read messages", mappedMessages)
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