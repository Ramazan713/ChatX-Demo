import bcrypt from "bcrypt";

export default class EncrptUtil {

    static async encrpt(data: string, saltRounds = 10): Promise<string>{
        return bcrypt.hashSync(data, saltRounds);
    }

    static async compare(plainData: string, hashedData: string): Promise<boolean>{
        return bcrypt.compare(plainData, hashedData)
    }

}