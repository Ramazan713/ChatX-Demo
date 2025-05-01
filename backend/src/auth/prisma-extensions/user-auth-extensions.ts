import { Prisma } from "../../../generated/prisma"
import { ENV } from "../../core/config/env"
import EncrptUtil from "../../utils/encryptUtil"
import JWTUtil from "../../utils/jwtUtils"


export const createUserAuthExtensions = Prisma.defineExtension({
    result: {
        user: {
            generateAccessToken: {
              needs: { id: true, username: true },
              compute(user) {
                return () => {
                  return JWTUtil.sign(
                    { id: user.id, username: user.username },
                    { expiresIn: ENV.ACCESS_TOKEN_EXPIRES },
                  )
                }
              }
            },
            generateRefreshToken: {
              needs: { id: true, username: true },
              compute(user) {
                return () => {
                  return JWTUtil.sign(
                    { id: user.id, username: user.username },
                    { expiresIn: ENV.REFRESH_TOKEN_EXPIRES },
                  )
                }
              }
            },
            comparePassword: {
              needs: { password: true },
              compute(user) {
                return (password: string): Promise<boolean> => {
                  return EncrptUtil.compare(password, user.password)
                }
              },
            }
          }
        } 
})