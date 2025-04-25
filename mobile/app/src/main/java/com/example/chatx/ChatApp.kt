package com.example.chatx

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.chatx.core.di.coreModule
import com.example.chatx.core.features.devices.di.devicesModule
import com.example.chatx.features.app.di.appModule
import com.example.chatx.features.auth.di.authModule
import com.example.chatx.features.chat.di.chatModule
import com.example.chatx.features.home.di.homeModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ChatApp: Application(), LifecycleObserver {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ChatApp)
            modules(
                homeModule, chatModule, coreModule, authModule,
                devicesModule, appModule
            )
        }

        createChannels()

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())

    }

    private fun createChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.chat_channel_name)
            val id = getString(R.string.chat_channel_id)
            val descriptionText = getString(R.string.chat_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = descriptionText

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private class AppLifecycleObserver: DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            isInForeground = true
        }
        override fun onStop(owner: LifecycleOwner) {
            isInForeground = false
        }
    }

    companion object {
        var isInForeground = false
            private set
    }
}