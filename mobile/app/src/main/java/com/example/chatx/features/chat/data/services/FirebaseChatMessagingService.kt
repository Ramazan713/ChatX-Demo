package com.example.chatx.features.chat.data.services

import android.Manifest
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chatx.ChatApp
import com.example.chatx.MainActivity
import com.example.chatx.R
import com.example.chatx.core.features.devices.domain.manager.DeviceTokenManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FirebaseChatMessagingService: FirebaseMessagingService() {

    private val deviceTokenManager by inject<DeviceTokenManager>()
    private val applicationScope by inject<CoroutineScope>()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (ChatApp.isInForeground) return

        showNotification(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        applicationScope.launch {
            deviceTokenManager.registerDevice(token)
        }
    }

    private fun showNotification(msg: RemoteMessage) {
        val msgData = msg.data
        val roomId = msgData["roomId"] ?: return
        val title  = msgData["title"] ?: "Yeni mesaj"
        val body   = msgData["body"]  ?: ""

        val intent = Intent(this, MainActivity::class.java).apply {
            data = Uri.parse("chatx://chat/$roomId")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(this, getString(R.string.chat_channel_id))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this).notify(roomId.hashCode(), notification)
    }

}