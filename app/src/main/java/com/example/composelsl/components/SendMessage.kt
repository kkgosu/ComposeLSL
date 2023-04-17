package com.example.composelsl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composelsl.R
import com.example.composelsl.ui.theme.SentMessageColor

/**
 * @author Konstantin Koval on 14.04.2023
 */
@Composable
fun SentMessage(
    text: String,
    modifier: Modifier = Modifier,
    quotedMessage: String? = null,
    quotedImage: Int? = null,
    messageTime: String,
    messageStatus: MessageStatus,
    alignment: Alignment.Horizontal = Alignment.End
) {
    Column(
        horizontalAlignment = alignment,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 60.dp, end = 8.dp, top = 2.dp, bottom = 2.dp)
    ) {
        SubcomposeColumn(
            modifier = Modifier
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(SentMessageColor)
                .clickable { },
            content = {
                if (quotedMessage != null || quotedImage != null) {
                    QuotedMessage(
                        quotedMessage = quotedMessage,
                        quotedImage = quotedImage,
                        sent = true
                    )
                }
                ChatFlexBoxLayout(
                    modifier = Modifier.padding(top = if (quotedMessage == null && quotedImage == null) 2.dp else 0.dp),
                    text = text,
                    messageStat = {
                        MessageTimeText(
                            modifier = Modifier.wrapContentSize(),
                            messageTime = messageTime,
                            messageStatus = messageStatus
                        )
                    }
                )
            }
        )
    }
}

@Preview
@Composable
fun SentMessagePreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SentMessage(
            text = "Heisenberg",
            quotedMessage = null,
            messageTime = "13:37",
            messageStatus = MessageStatus.READ,
            quotedImage = null
        )
        SentMessage(
            text = "Heisenberg",
            quotedMessage = null,
            messageTime = "13:37",
            messageStatus = MessageStatus.READ,
            quotedImage = R.drawable.heisenberg
        )
        SentMessage(
            text = "Heisenberg",
            quotedMessage = "Say my name \uD83D\uDE0E",
            messageTime = "13:37",
            messageStatus = MessageStatus.READ
        )
        SentMessage(
            text = "Heisenberg",
            quotedMessage = "Say my name \uD83D\uDE0E",
            messageTime = "13:37",
            messageStatus = MessageStatus.READ,
            quotedImage = R.drawable.heisenberg
        )
    }
}
