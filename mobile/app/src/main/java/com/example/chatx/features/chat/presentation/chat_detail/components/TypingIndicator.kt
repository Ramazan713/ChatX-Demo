package com.example.chatx.features.chat.presentation.chat_detail.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TypingIndicator(
    typingUsers: List<String>,
    modifier: Modifier = Modifier
) {
    val targetAlpha = if (typingUsers.isEmpty()) 0f else 1f

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 300)
    )

    val text = when (typingUsers.size) {
        0 -> ""
        1 -> "${typingUsers[0]} yazıyor..."
        2 -> "${typingUsers[0]} ve ${typingUsers[1]} yazıyor..."
        else -> {
            val firstTwo = typingUsers.take(2)
            "${firstTwo[0]}, ${firstTwo[1]} ve diğerleri yazıyor..."
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TypingIndicatorPreview() {
    TypingIndicator(
        typingUsers = listOf("Ahmet")
    )
}