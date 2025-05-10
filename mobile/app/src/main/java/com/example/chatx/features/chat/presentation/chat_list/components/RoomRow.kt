package com.example.chatx.features.chat.presentation.chat_list.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatx.core.presentation.selections.PersistentIconMenu
import com.example.chatx.features.chat.domain.models.ChatRoom
import com.example.chatx.features.chat.presentation.chat_list.enums.RoomRowMenuItem
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun RoomRow(
    room: ChatRoom,
    onShowClick: () -> Unit,
    onJoinClick: () -> Unit,
    onMenuItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = MaterialTheme.shapes.medium
    Column(
        modifier = modifier
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape = shape)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = room.name,
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if(room.muted){
                    Icon(Icons.Default.NotificationsOff, contentDescription = null)
                }
                IconButton(
                    onClick = onMenuItemClick
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                }
            }
        }
        Text(room.updatedAt.toString())

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End)
        ) {
            if(room.leftAt != null){
                ElevatedButton(
                    onClick = onShowClick
                ) {
                    Text("Show")
                }
            }else{
                ElevatedButton(
                    onClick = onJoinClick
                ) {
                    Text("Join")
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomRowPreview() {
    val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    RoomRow(
        modifier = Modifier.padding(4.dp),
        onMenuItemClick = {},
        onJoinClick = {},
        onShowClick = {},
        room = ChatRoom(
            id = "1",
            name = "room1",
            isPublic = true,
            joinedAt = date,
            updatedAt = date,
            leftAt = null,
            muted = true
        )
    )
}