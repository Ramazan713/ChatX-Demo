import jwt from "jsonwebtoken"
import { ENV } from "../core/config/env"


export default class JWTUtil{

    static sign(data: any, options?: jwt.SignOptions){
        const token = jwt.sign(data, ENV.JWT_SECRET, options)
        return token
    }

    static verify(token: string, options?: jwt.VerifyOptions){
        return jwt.verify(token, ENV.JWT_SECRET, options)
    }
}