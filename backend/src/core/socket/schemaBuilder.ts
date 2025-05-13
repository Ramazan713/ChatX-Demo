import { ObjectId } from "mongodb";
import { z } from "zod";



export function createSchemaBuilder() {
  const commonFields = {
    objectId: ({ message }: {message ?: string} = {}) => z.string().refine(val => ObjectId.isValid(val),{message}),
    roomId: () => z.string().refine(val => ObjectId.isValid(val))
  };

  return {
    ...commonFields,
    
    build: {
      pagination: () => z.object({
        page: z.number().int().positive().default(1),
        limit: z.number().int().positive().max(100).default(20)
      }),
    }
  };
}

export const schema = createSchemaBuilder();