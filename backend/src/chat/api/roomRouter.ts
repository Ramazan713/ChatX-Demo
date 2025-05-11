
import { Router } from "express";
import authRequired from "../../middleware/auth";
import validateBody from "../../middleware/validate";
import { createRoomSchema, updateRoomSchema } from "../types/schemas";
import RoomController from "./roomController";

const router = Router()
const roomController = new RoomController()

router.use(authRequired)

router.get("/", roomController.listRooms)
router.post("/", validateBody(createRoomSchema), roomController.join)

router.get("/:roomId", roomController.getRoomsWithDetail)
router.patch("/:roomId", validateBody(updateRoomSchema), roomController.updateRoom)
router.delete("/:roomId", roomController.deleteRoom)

export default router