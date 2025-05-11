import express from "express";
import roomRouter from "./chat/api/roomRouter";
import messageRouter from "./chat/api/messageRouter";
import authRouter from "./auth/router";
import deviceRouter from "./devices/router";
import cookieParser from "cookie-parser";

const app = express();

app.use(express.json())
app.use(cookieParser());

app.use("/api/rooms", roomRouter)
app.use("/api/rooms/:roomId/messages", messageRouter)
app.use("/api/auth", authRouter)
app.use("/api/devices", deviceRouter)


app.get("/",(req, res) => {
    res.send("Hello World")
})

export default app