import { MulticastMessage } from "firebase-admin/lib/messaging/messaging-api";
import { fcm } from "../../core/fcm";
import prisma from "../../prisma";
import type { ChatMessageDto } from "./../types/types";



export async function notifyRoom(
    userId: string,
    dto: ChatMessageDto
) {
  
  const roomId = dto.roomId
  const roomName = (await prisma.room.findUnique({
    where: {id: roomId},
  }))?.name
  if(!roomName) return
  

  const userRooms = await prisma.userRoom.findMany({
    where: { roomId, muted: false, userId: {notIn: [userId]}, OR: [{leftAt: {isSet: false}}, {leftAt: null}] },
    include: { user: { include: { devices: true } } }
  });

  const tokens = userRooms
    .flatMap(ur => ur.user.devices.map(d => d.token))
    .filter(t => !!t);

  console.log("notify tokens size: ", tokens.length)

  if (tokens.length === 0) return;

  const message: MulticastMessage = {
    tokens,
    android: {
      collapseKey: roomId,
      priority: "high",
    },
    apns: {
      headers: {
        "apns-priority": "10"
      }
    },
    data: {
        title: `#${roomName}`,
      body: `${dto.username}: ${dto.text}`,
      roomId:    dto.roomId,
      messageId: dto.id,
      timestamp: dto.createdAt.toISOString(),
    }
  };


  const resp = await fcm.sendEachForMulticast(message);
  const badTokens = resp.responses
    .map((r, i) => r.success ? null : tokens[i])
    .filter((t): t is string => !!t);
  if (badTokens.length > 0) {
    await prisma.userDevice.deleteMany({
      where: { token: { in: badTokens } }
    });
  }
}
