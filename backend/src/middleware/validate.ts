import { NextFunction, Request, Response } from "express";
import { Socket } from "socket.io";
import { z, ZodError } from "zod";


export default function validateBody<T>(schema: z.ZodType<T>): any{
    return (req: Request, res: Response, next: NextFunction) => {
        try{
            req.body = schema.parse(req.body)
            next()
        }catch(err){
            if(err instanceof ZodError){
                return res.status(400).send({
                    error: "Validation failed",
                    payload: req.body,
                    details: err.formErrors
                })
            }
            next(err)
        }
    }
}


export function createSocketValidator<
  EventMap extends Record<string, z.ZodType>,
  SocketType extends Socket
>() {
  // Event şeması ve socket tipi için validation oluşturucu
  return function withValidation<EventName extends keyof EventMap>(
    eventName: EventName,
    schema: EventMap[EventName],
    handler: (socket: SocketType, data: z.infer<EventMap[EventName]>) => void | Promise<void>
  ) {
    return (socket: SocketType) => {
      socket.on(eventName as string, async (payload) => {
        try {
          // Şema ile doğrulama
          const validationResult = schema.safeParse(payload);
          
          if (!validationResult.success) {
            const errorData = { 
              event: String(eventName),
              message: "Invalid data format", 
              payload,
              errors: validationResult.error.errors 
            }
            socket.emit("validation error", errorData);
            console.error(`Validation error in ${String(eventName)}:`, errorData);
            return;
          }
          const result = handler(socket, validationResult.data);
          
          if (result instanceof Promise) {
            await result;
          }
        } catch (error) {
          console.error(`Error in ${String(eventName)} handler:`, error);
          socket.emit("error", { 
            event: String(eventName),
            message: `Failed to process ${String(eventName)}` 
          });
        }
      });
    };
  };
}