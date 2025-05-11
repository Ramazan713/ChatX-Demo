import http from "http"
import app from "./app";
import { Server } from "socket.io";
import { setupChatNamespace } from "./chat/socket/socketSetup";


const httpServer = http.createServer(app)

const io = new Server(httpServer)

setupChatNamespace(io)

export default httpServer


