import { PrismaClient } from "../generated/prisma";
import EncrptUtil from "./utils/encryptUtil";
import JWTUtil from "./utils/jwtUtils";


const prisma = new PrismaClient({
    omit: {
        user: {
            password: true
        }
    }
}).$extends({
    result: {
        user: {
            generateToken: {
                needs: {
                    id: true,
                    username: true
                },
                compute(user){
                    return ()=>{
                        return JWTUtil.sign({
                            id: user.id,
                            username: user.username
                        })
                    }
                }
            },
            comparePassword: {
                needs: {
                    password: true,
                },
                compute(user) {
                    return (password: string): Promise<boolean> => {
                        return EncrptUtil.compare(password, user.password)
                    }
                },
            }
        }
    }
})

export default prisma