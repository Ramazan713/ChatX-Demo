import { Request, Response } from "express";
import { userService } from "./userService";
import JWTUtil from "../utils/jwtUtils";
import prisma from "../prisma";
import { ENV } from "../core/config/env";


const setRefreshTokenCookie = (res: Response, user: any, path: string): void => {
    res.cookie("refreshToken", user.generateRefreshToken(), {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        path: path,
        maxAge: ENV.REFRESH_TOKEN_EXPIRES,
        sameSite: "strict"
    });
}

export default class AuthController {
    async login(req: Request, res: Response): Promise<any>{
        const { username, password }: { username: string, password: string } = req.body
        if(!username || !password) return res.status(400).send("password and username required")

        const user = await userService.getUser(username)
        if(!user) return res.status(400).send("password or username wrong")
        
        const passwordMatch = await user.comparePassword(password)
        if(!passwordMatch) return res.status(400).send("password or username wrong")

        setRefreshTokenCookie(res, user, "/api/auth/login")

        res.send({
            user: user,
            token: user.generateAccessToken()
        })
    }

    async signUp(req: Request, res: Response): Promise<any>{
        const { username, password }: { username: string, password: string } = req.body
        if(!username || !password) return res.status(400).send("password and username required")

        let user = await userService.getUser(username)
        if(user) return res.status(400).send("username already exists")
        
        user = await userService.createUser(username, password)
        setRefreshTokenCookie(res, user, "/api/auth/signUp")

        res.send({
            user: user,
            token: user.generateAccessToken()
        })
    }


    async refresh(req: Request, res: Response): Promise<any>{
        const token = req.cookies["refreshToken"]
        if(!token) return res.status(401).json({ message: "Refresh token missing" })

        
        try {
            const payload = JWTUtil.verify(token) as {sub: string}
            const user = await prisma.user.findUnique({
                where: { id: payload.sub }
            })
            if(!user) return res.status(401).json({ message: "user not found" })

            setRefreshTokenCookie(res, user, "/api/auth/refresh")
    
            res.send({
                user: user,
                token: user.generateAccessToken()
            })
        } catch {
            return res.status(403).json({ message: 'Invalid refresh token' });
        }
    }


    
}