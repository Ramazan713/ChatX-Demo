import { z } from "zod";


export const createDeviceSchema = z.object({
    token: z.string().max(1024),
    platform: z.enum(["android", "ios"])
})

export type CreateDeviceRequest = z.infer<typeof createDeviceSchema>

