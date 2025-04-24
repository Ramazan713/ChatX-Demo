package com.example.chatx.features.chat.presentation.chat_list.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatx.features.chat.domain.models.ChatRoom
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun RoomRow(
    room: ChatRoom,
    onShowClick: () -> Unit,
    onLeaveClick: () -> Unit,
    onJoinClick: () -> Unit,
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
        Text(
            text = room.name,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
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
                TextButton(
                    onClick = onLeaveClick,
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Leave")
                }

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
        onLeaveClick = {},
        onJoinClick = {},
        onShowClick = {},
        room = ChatRoom(
            id = "1",
            name = "room1",
            isPublic = true,
            joinedAt = date,
            updatedAt = date,
            leftAt = null
        )
    )
}