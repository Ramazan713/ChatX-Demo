import { Router } from "express";
import ChatController from "./controller";
import authRequired from "../middleware/auth";
import validateBody from "../middleware/validate";
import { createRoomSchema, roomMessageSchema } from "./schemas";

const router = Router()
const chatController = new ChatController()

router.use(authRequired)

router.get("/rooms", chatController.listRooms)
router.post("/join", validateBody(createRoomSchema), chatController.join)
router.post("/leave/:roomId", chatController.left)
router.get("/messages/:roomId", chatController.getMessages)
router.post("/message/read/:messageId", chatController.markAsRead)

router.post("/messages", validateBody(roomMessageSchema),chatController.sendMessage)

export default router