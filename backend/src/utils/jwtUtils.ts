import jwt from "jsonwebtoken"


export default class JWTUtil{

    static sign(data: any, options?: jwt.SignOptions){
        const token = jwt.sign(data, process.env.JWT_SECRET as any, options)
        return token
    }

    static verify(token: string, options?: jwt.VerifyOptions){
        return jwt.verify(token, process.env.JWT_SECRET as any, options)
    }
}