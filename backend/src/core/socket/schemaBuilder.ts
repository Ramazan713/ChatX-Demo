import { z } from "zod";


export function createSchemaBuilder() {
  const commonFields = {
    roomId: () => z.string().length(24),
    userId: () => z.string().length(24),
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