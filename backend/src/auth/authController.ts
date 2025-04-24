import { Request, Response } from "express";
import { userService } from "./userService";


export default class AuthController {

    async login(req: Request, res: Response): Promise<any>{
        const { username, password }: { username: string, password: string } = req.body
        if(!username || !password) return res.status(400).send("password and username required")

        const user = await userService.getUser(username)
        if(!user) return res.status(400).send("password or username wrong")
        
        const passwordMatch = await user.comparePassword(password)
        if(!passwordMatch) return res.status(400).send("password or username wrong")

        res.send({
            user: user,
            token: user.generateToken()
        })
    }

    async signUp(req: Request, res: Response): Promise<any>{
        const { username, password }: { username: string, password: string } = req.body
        if(!username || !password) return res.status(400).send("password and username required")

        let user = await userService.getUser(username)
        if(user) return res.status(400).send("username already exists")
        
        user = await userService.createUser(username, password)
        res.send({
            user: user,
            token: user.generateToken()
        })
    }
}