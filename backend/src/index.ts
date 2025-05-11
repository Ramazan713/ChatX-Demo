import "dotenv/config";
import httpServer from "./server";

const PORT = 5000;
httpServer.listen(PORT, "0.0.0.0", () => {
    console.log(`listening on http://localhost:${PORT}`)
})