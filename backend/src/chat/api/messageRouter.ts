
import { Router } from "express";
import authRequired from "../../middleware/auth";
import { messageIdSchema, messageQuerySchema as messageQuerySchema, roomIdSchema, roomMessageSchema } from "../types/schemas";
import MessagesController from "./messageController";
import { validateBody, validateParams, validateQuery } from "../../middleware/validateRequest";

const router = Router({mergeParams: true})
const messageController = new MessagesController()

router.use(authRequired)
router.use(validateParams(roomIdSchema))

router.get("/", validateQuery(messageQuerySchema),messageController.getMessages)
router.post("/", validateBody(roomMessageSchema), messageController.sendMessage)
router.post("/:messageId/read", validateParams(messageIdSchema), messageController.markAsRead)


export default router