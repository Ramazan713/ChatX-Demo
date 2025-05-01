import { PrismaClient } from "../generated/prisma";
import { createUserAuthExtensions } from "./auth/prisma-extensions/user-auth-extensions";


const prisma = new PrismaClient({
    omit: {
        user: {
            password: true
        }
    }
}).$extends(createUserAuthExtensions)

export default prisma