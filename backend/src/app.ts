import express from "express";
import chatRouter from "./chat/router";
import authRouter from "./auth/router";

const app = express();

app.use(express.json())

app.use("/api/chat", chatRouter)
app.use("/api/auth", authRouter)

app.get("/",(req, res) => {
    res.send("Hello World")
})

export default app