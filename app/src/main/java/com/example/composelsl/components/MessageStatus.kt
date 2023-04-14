package com.example.composelsl.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @author Konstantin Koval on 14.04.2023
 */
@Composable
fun MessageTimeText(
    modifier: Modifier = Modifier,
    messageTime: String,
    messageStatus: MessageStatus
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(top = 1.dp, bottom = 1.dp),
            text = messageTime,
            fontSize = 12.sp,
            color = Color.White
        )
        Icon(
            modifier = Modifier
                .size(22.dp)
                .padding(start = 4.dp),
            imageVector = when (messageStatus) {
                MessageStatus.PENDING -> Icons.Default.AccessTime
                MessageStatus.RECEIVED -> Icons.Default.Done
                else -> Icons.Default.DoneAll
            },
            tint = if (messageStatus == MessageStatus.READ) Color.White else Color(0xff424242),
            contentDescription = "messageStatus"
        )
    }
}

enum class MessageStatus {
    PENDING, RECEIVED, READ
}

data class ChatMessage(
    val id: Long,
    var message: String,
    var date: Long
)