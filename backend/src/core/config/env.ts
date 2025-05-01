
export const ENV = {
    ACCESS_TOKEN_EXPIRES: Number.parseInt(process.env.ACCESS_TOKEN_EXPIRES as string),
    REFRESH_TOKEN_EXPIRES: Number.parseInt(process.env.REFRESH_TOKEN_EXPIRES as string),
    JWT_SECRET: process.env.JWT_SECRET as string
}