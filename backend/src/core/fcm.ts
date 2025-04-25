import * as admin from "firebase-admin";
import serviceAccount from "../../secrets/chatx-adminsdk.json";

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount as any),
});

export const fcm = admin.messaging();
