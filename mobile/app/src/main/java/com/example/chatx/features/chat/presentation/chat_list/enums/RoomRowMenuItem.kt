package com.example.chatx.features.chat.presentation.chat_list.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.MaterialTheme
import com.example.chatx.core.domain.models.IMenuItemEnum
import com.example.chatx.core.domain.models.IconInfo
import com.example.chatx.core.domain.utils.UiColor
import com.example.chatx.core.domain.utils.UiText

enum class RoomRowMenuItem: IMenuItemEnum {
    Mute {
        override val title: UiText
            get() = UiText.Text("Mute")
        override val iconInfo: IconInfo
            get() = IconInfo(
                imageVector = Icons.Default.NotificationsOff
            )
    },
    UnMute {
        override val title: UiText
            get() = UiText.Text("UnMute")
        override val iconInfo: IconInfo
            get() = IconInfo(
                imageVector = Icons.Default.Notifications
            )
    },
    Leave {
        override val title: UiText
            get() = UiText.Text("Leave")
        override val iconInfo: IconInfo
            get() = IconInfo(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp
            )
    },
    Delete {
        override val title: UiText
            get() = UiText.Text("Delete")
        override val iconInfo: IconInfo
            get() = IconInfo(
                imageVector = Icons.Default.DeleteForever,
                tintColor = UiColor.ComposeColor {
                    MaterialTheme.colorScheme.error
                }
            )
    };


    companion object {
        fun getItems(isMute: Boolean, isLeft: Boolean): List<RoomRowMenuItem> {
            val items = entries.toMutableList()
            if(isMute){
                items.remove(Mute)
            }else{
                items.remove(UnMute)
            }
            if(isLeft){
                items.remove(Leave)
                items.remove(Mute)
                items.remove(UnMute)
            }else{
                items.remove(Delete)
            }
            return items
        }
    }
}