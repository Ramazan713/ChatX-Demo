import { NextFunction, Request, Response } from "express";
import JWTUtil from "../utils/jwtUtils";
import prisma from "../prisma";
import { Socket } from "socket.io";


export default async function authRequired(req: Request, res: Response, next: NextFunction): Promise<any>{
    try{
        const authorization = req.headers["authorization"]
        if(!authorization) throw Error()
        const token = authorization.split(" ")[1]
        const payload = JWTUtil.verify(token) as {id: string}        
        const id = payload.id

        const user = await prisma.user.findUnique({
            where: { id }
        })
        if(!user) throw Error()
        
        req.user = user
        next()
    }catch(e){
        return res.status(401).send("Unauthorized")
    }
}


export async function authSocketRequired(socket: Socket, next: any){
    try{
        let token = socket.handshake.auth.token as string | undefined

        if(!token){
            token = socket.handshake.headers.authorization
            if(token && token.startsWith("Bearer ")){
                token = token.substring(7)
            }
        }

        if(!token) throw Error("empty token")
        
        const payload = JWTUtil.verify(token) as {id: string}        
        const id = payload.id

        const user = await prisma.user.findUnique({
            where: { id }
        })
        if(!user) throw Error()

        socket.data.user = user
        next()
    }catch(e){
        console.log("UnAuthrized")
        next(new Error("Unathrozied"))
    }
}