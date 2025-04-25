import express from "express";
import chatRouter from "./chat/router";
import authRouter from "./auth/router";
import deviceRouter from "./devices/router";

const app = express();

app.use(express.json())

app.use("/api/chat", chatRouter)
app.use("/api/auth", authRouter)
app.use("/api/devices", deviceRouter)


app.get("/",(req, res) => {
    res.send("Hello World")
})

export default app