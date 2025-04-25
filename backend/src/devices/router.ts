import { Request, Response, Router } from "express";
import authRequired from "../middleware/auth";
import prisma from "../prisma";
import validateBody from "../middleware/validate";
import { createDeviceSchema, CreateDeviceRequest } from "./schemas";


const router = Router()
router.use(authRequired)

router.get("/", async(req: Request, res: Response) => {
    const userId = req.user!!.id
    const devices = await prisma.userDevice.findMany({
        where: {userId: userId},
        select: {
            token: true, platform: true, createdAt: true
        }
    })
    res.send(devices)
})

router.post("/", validateBody(createDeviceSchema), async(req: Request, res: Response) => {
    const { token, platform }: CreateDeviceRequest = req.body
    const userId = req.user!!.id

    const device = await prisma.userDevice.upsert({
        where: { token, userId },
        create: {
            token, platform, userId
        },
        update: {
            token, platform
        },
        select: {
            token: true, platform: true, createdAt: true
        }
    })
    res.status(201).send(device)
})

router.delete("/:token", async(req: Request, res: Response): Promise<any> => {
    const userId = req.user!!.id
    const token = req.params.token

    const exists = await prisma.userDevice.findFirst({
        where: { userId, token }
    })
    if(!exists) return res.sendStatus(404)

    await prisma.userDevice.delete({where: { userId, token }})
    res.sendStatus(204)
})

export default router