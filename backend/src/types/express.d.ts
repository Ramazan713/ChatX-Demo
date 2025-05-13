
declare namespace Express {
    export interface Request {
      user?: {
        id: string
        username: string
      },
      validated?: {
        body?:  any;
        params?: any;
        query?: any;
      };
    }
}
