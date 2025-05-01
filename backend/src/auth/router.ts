import { Router } from "express";
import AuthController from "./authController";

const router = Router()
const authController = new AuthController()


router.post("/login", authController.login)

router.post("/signUp", authController.signUp)

router.post("/refresh", authController.refresh)

export default router