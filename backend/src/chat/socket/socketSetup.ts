// src/features/chat/setup.ts
import { Server } from "socket.io";
import { ChatNamespace } from "../types/types";
import { authSocketRequired } from "../../middleware/auth";
import { chatHandlers } from "./socketHandlers";

export function setupChatNamespace(io: Server) {
  const chatNs: ChatNamespace = io.of("/chat");
  
  // Auth middleware
  chatNs.use(authSocketRequired);
  
  chatNs.on("connection", (socket) => {
    console.log(`User connected to chat: ${socket.data.user.username}`);
    
    // Tüm event handler'ları kaydet
    Object.values(chatHandlers).forEach(handler => handler(socket));
  });
  
  return chatNs;
}