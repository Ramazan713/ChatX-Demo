import prisma from "../prisma";
import EncryptUtil from "../utils/encryptUtil";


class UserService {
    async userExists(username: string): Promise<boolean>{
        return (await prisma.user.findUnique({
            where: {username: username}
        })) != null
    }


    async createUser(username: string, plainPassword: string){
        const hashedPassword = await EncryptUtil.encrpt(plainPassword)
        return prisma.user.create({
            data: {
                username,
                password: hashedPassword
            }
        })
    }

    async getUser(username: string){
        return prisma.user.findUnique({
            where: {username: username}
        })
    }

    

}


const userService = new UserService()

export {
    userService
}