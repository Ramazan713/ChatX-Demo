import http from "http"
import app from "./app";
import { Server } from "socket.io";
import { setupChatNamespace } from "./chat/socketSetup";


const httpServer = http.createServer(app)

const io = new Server(httpServer)

setupChatNamespace(io)

export default httpServer


