import { Room, UserRoom, Message } from "../../../generated/prisma";
import { ChatMessageDto, ChatRoomDto } from "./../types/types";

export function mapUserRoomToDto(ur: UserRoom & { room: Room }): ChatRoomDto {
    const updatedAt = ur.leftAt ?? ur.room.updatedAt;
    return {
      id:        ur.room.id,
      name:      ur.room.name,
      isPublic:  ur.room.isPublic,
      joinedAt:  ur.joinedAt,
      leftAt:    ur.leftAt,
      updatedAt,
      muted: ur.muted
    };
}

export function mapUserRoomsToDto(userRooms: ({room: Room} & UserRoom)[]): ChatRoomDto[] {
    return userRooms.map((userRoom) => {
        return mapUserRoomToDto(userRoom)
    }).sort((a,b)=> a.updatedAt < b.updatedAt ? 1 : -1)
}


export function mapMessageToDto(msg: Message & { author: { username: string } }): ChatMessageDto {
    return {
      id:       msg.id,
      roomId:   msg.roomId,
      username: msg.author.username,
      text:     msg.text,
      createdAt: msg.createdAt,
      readBy: msg.readBy
    };
}