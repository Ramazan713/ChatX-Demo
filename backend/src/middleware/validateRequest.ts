import { NextFunction, Request, Response } from "express";
import z, { ZodTypeAny, ZodType, AnyZodObject, ZodError } from "zod";

interface SchemaMap {
  body?: ZodTypeAny;
  params?: ZodTypeAny;
  query?: ZodTypeAny;
}

export function validateBody(body: ZodTypeAny): any {
  return validateRequest({ body })
}

export function validateParams(params: ZodTypeAny): any {
  return validateRequest({ params })
}

export function validateQuery(query: ZodTypeAny): any {
  return validateRequest({ query })
}

export function validateRequest<S extends SchemaMap>(schemas: S): any {
  return (req: Request, res: Response, next: NextFunction) => {
    try {
        
      const validated: SchemaMap = {};

      if (schemas.params) {
        const p = schemas.params.safeParse(req.params);
        if (!p.success) throw p.error;
        validated.params = p.data;
      }

      if (schemas.query) {
        const q = schemas.query.safeParse(req.query);
        if (!q.success) throw q.error;
        validated.query = q.data;
      }

      if (schemas.body) {
        const b = schemas.body.safeParse(req.body);
        if (!b.success) throw b.error;
        validated.body = b.data;
        req.body = b.data
      }

      req.validated = validated;
      next();
    } catch (err: any) {
        if(err instanceof ZodError){
            return res.status(400).send({
                error: "Validation failed",
                payload: req.params,
                details: err.formErrors
            })
        }
        next(err)
    }
  };
}