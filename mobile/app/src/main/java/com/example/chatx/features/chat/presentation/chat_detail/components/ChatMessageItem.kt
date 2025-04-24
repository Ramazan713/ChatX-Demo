package com.example.chatx.features.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatx.core.extentions.formatReadableForChat
import com.example.chatx.features.chat.domain.models.ChatMessage
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Composable
fun ChatMessageItem(
    msg: ChatMessage,
    currentUsername: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMine = msg.getIsMine(currentUsername)
    Row(
        modifier = modifier,
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {

        Column(
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(isMine && msg.failed){
                    IconButton(
                        onClick = onRetry,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = Color.Red
                        )
                    }
                }
                Text(msg.text)
            }

            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = msg.createdAt.formatReadableForChat(),
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.width(4.dp))
                if(isMine && !msg.failed){
                    when {
                        msg.pending -> Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Pending",
                            modifier = Modifier.size(12.dp),
                            tint = Color.Gray
                        )
                        msg.readBy.isNotEmpty() -> Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "read",
                            modifier = Modifier.size(12.dp),
                            tint = Color.Blue
                        )
                        else -> Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Sent",
                            modifier = Modifier.size(12.dp),
                            tint = Color.Blue
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ChatMessageItemPreview() {
    ChatMessageItem(
        msg = ChatMessage(
            id = "1",
            username = "namex",
            text = "Hello",
            roomId = "1",
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            readBy = listOf("namey", "namea"),
            pending = false,
            failed = true
        ),
        currentUsername = "namex",
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        onRetry = {}
    )
}