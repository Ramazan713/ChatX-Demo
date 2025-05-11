// src/chat/types.ts

import { Namespace, Socket } from "socket.io";
import { AuthUser } from "../../models/user";
import { ChatClientEvents, ChatServerEvents } from "./schemas";

export interface ChatMessagesWithRoomDto {
    messages: ChatMessageDto[]
    room: ChatRoomDto
}


export interface ChatMessageDto {
    id: string;
    roomId: string;
    username: string;
    text: string;
    createdAt: Date;
    readBy: string[];
}

export interface ChatRoomDto {
    id: string;
    name: string;
    isPublic: boolean;
    joinedAt: Date;
    updatedAt: Date;
    leftAt: Date | null;
    muted: boolean;
}

export interface FetchMessageOptions {
  userId:    string;
  roomId:    string;
  limit?:    number;
  since?:    Date;    // en son gelen createdAt
  afterId?:  string;  // aynı timestamp'te son id
}

export interface ChatSocketData {
    user: AuthUser;
    joinedRoom?: string;
}

// Inter-server events
export interface ChatInterServerEvents {}

// Socket ve namespace tipleri
export type ChatSocket = Socket<
  ChatClientEvents,
  ChatServerEvents,
  ChatInterServerEvents,
  ChatSocketData
>;

export type ChatNamespace = Namespace<
  ChatClientEvents,
  ChatServerEvents,
  ChatInterServerEvents,
  ChatSocketData
>;
  