//src/chat/schemas.ts

import { z } from "zod";
import { schema } from "../../core/socket/schemaBuilder";

export const roomMessageSchema = z.object({
    message: z.string().min(1).max(2000),
    tempId: z.string().nullable().default(null)
})

export type RoomMesageInput = z.infer<typeof roomMessageSchema>


export const createRoomSchema = z.object({
    name: z.string().min(1).max(50),
    isPublic: z.boolean().nullable().default(null)
})
export type CreateRoomInput = z.infer<typeof createRoomSchema>


export const updateRoomSchema = z.object({
    muted: z.boolean().optional().nullable(),
    left: z.boolean().optional().nullable(),
})
export type UpdateRoomInput = z.infer<typeof updateRoomSchema>


export const roomIdSchema = z.object({
  roomId: schema.objectId()
})

export const messageIdSchema = z.object({
  messageId: schema.objectId()
})


export const messageQuerySchema = z.object({
  afterId: schema.objectId().optional().nullable().default(null),
  beforeId: schema.objectId().optional().nullable().default(null),
  limit: z.number({coerce: true}).min(1).max(100).default(20),
  include: z.string().min(1).max(32).optional().nullable().default(null)
})
export type MessageQueryInput = z.infer<typeof messageQuerySchema>



export const chatSchemas = {
  "join room": z.object({
    roomId: schema.roomId()
  }),
  
  "typing": z.object({
    roomId: schema.roomId()
  }),
  
  "stop typing": z.object({
    roomId: schema.roomId()
  }),
  
  "room message": z.object({
    roomId: schema.roomId(),
    message: z.string().min(1).max(2000),
    tempId: z.string().nullable().default(null)
  }),
  
  "read messages": z.object({
    messageIds: z.array(z.string()),
    roomId: schema.roomId()
  })
};

// Tip çıkarımları
export type ChatEventSchemas = typeof chatSchemas;
export type ChatEventName = keyof ChatEventSchemas;
export type ChatEventPayload<T extends ChatEventName> = z.infer<ChatEventSchemas[T]>;

// Socket.io tip entegrasyonu için
export type ChatClientEvents = {
  [K in ChatEventName]: (payload: ChatEventPayload<K>) => void;
};

// Socket.io server-to-client tipleri
export interface ChatServerEvents {
  "error": (data: { event?: string, message: string, errors?: any[] }) => void;
  "joined room": (data: { roomId: string; message: string }) => void;
  "user online": (data: { roomId: string; username: string }) => void;
  "user offline": (data: { roomId: string; username: string }) => void;
  "typing": (data: { usernames: string[] }) => void;
  "stop typing": (data: { username: string }) => void;
  "room message": (msg: any) => void;
  "read messages": (messages: any[]) => void;
}
