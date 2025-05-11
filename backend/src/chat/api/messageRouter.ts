
import { Router } from "express";
import authRequired from "../../middleware/auth";
import validateBody from "../../middleware/validate";
import { roomMessageSchema } from "../types/schemas";
import MessagesController from "./messageController";

const router = Router({mergeParams: true})
const messageController = new MessagesController()

router.use(authRequired)

router.get("/", messageController.getMessages)
router.post("/", validateBody(roomMessageSchema), messageController.sendMessage)
router.post("/:messageId/read", messageController.markAsRead)


export default router