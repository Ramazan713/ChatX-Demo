
import { Router } from "express";
import authRequired from "../../middleware/auth";
import { createRoomSchema, messageQuerySchema, roomIdSchema, updateRoomSchema } from "../types/schemas";
import RoomController from "./roomController";
import { validateBody, validateParams, validateRequest } from "../../middleware/validateRequest";

const router = Router()
const roomController = new RoomController()

router.use(authRequired)

router.get("/", roomController.listRooms)
router.post("/", validateBody(createRoomSchema), roomController.join)

router.get("/:roomId", validateRequest({query: messageQuerySchema, params: roomIdSchema}), roomController.getRoomsWithDetail)

router.patch("/:roomId", validateRequest({params: roomIdSchema, body: updateRoomSchema}), roomController.updateRoom)
router.delete("/:roomId", validateParams(roomIdSchema), roomController.deleteRoom)

export default router