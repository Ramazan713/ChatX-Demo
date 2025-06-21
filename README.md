# ChatX

ChatX is a full-stack real-time chat application featuring a TypeScript (Node.js, Express, Prisma, MongoDB) backend and a modern Android client built with Kotlin and Jetpack Compose.

> **Note:** This is a demo application intended for learning and experimentation purposes.

## Project Structure

```
backend/   # Node.js REST API & Socket.io server
mobile/    # Android client (Kotlin, Jetpack Compose)
```

## Backend

- **Tech Stack:** Node.js, Express, Prisma, MongoDB, TypeScript, Socket.io, Firebase FCM
- **Getting Started:**
  1. `cd backend`
  2. Install dependencies:  
     ```sh
     npm install
     ```
  3. Set up your `.env` file (see `prisma/schema.prisma` for DB structure)
  4. Generate Prisma client and push schema:  
     ```sh
     npx prisma generate
     npx prisma db push
     ```
  5. Start the server:  
     ```sh
     npm run dev
     ```

## Mobile

- **Tech Stack:** Kotlin, Jetpack Compose, Koin, Room, Ktor
- **Getting Started:**
  1. Open `mobile` in Android Studio.
  2. Configure `keystore.properties` and `local.properties` as needed.
  3. Build and run on an emulator or device.

## Features

- Real-time room-based messaging
- Read receipts
- Push notifications (Firebase FCM)
- User and device management
- Room creation, update, and deletion
- Modern Android UI

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.