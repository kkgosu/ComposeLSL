package com.example.composelsl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composelsl.R
import com.example.composelsl.ui.theme.ReceivedMessageColor

/**
 * @author Konstantin Koval on 15.04.2023
 */
var recipientOriginalName = "Declan"

@Composable
fun ReceivedMessage(
    text: String,
    modifier: Modifier = Modifier,
    quotedMessage: String? = null,
    quotedImage: Int? = null,
    messageTime: String,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        horizontalAlignment = alignment,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 8.dp, end = 60.dp, top = 2.dp, bottom = 2.dp)
    ) {
        SubcomposeColumn(
            modifier = Modifier
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(ReceivedMessageColor),
            content = {
                RecipientName(
                    name = recipientOriginalName,
                )
                if (quotedMessage != null || quotedImage != null) {
                    QuotedMessage(
                        quotedMessage = quotedMessage,
                        quotedImage = quotedImage,
                        sent = false,
                    )
                }
                ChatFlexBoxLayout(
                    modifier = Modifier,
                    text = text,
                    messageTime = messageTime,
                    messageStatus = null
                )
            }
        )
    }
}

@Composable
private fun RecipientName(
    name: String,
    modifier: Modifier = Modifier,
    onClick: ((String) -> Unit)? = null
) {
    Text(
        modifier = modifier
            .clickable {
                onClick?.invoke(name)
            }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        text = name,
        color = Color.White,
        fontSize = 15.sp,
        maxLines = 1,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Bold,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview
@Composable
fun ReceivedMessagePreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ReceivedMessage(
            text = "Heisenberg",
            quotedImage = null,
            quotedMessage = null,
            messageTime = "13:37"
        )
        ReceivedMessage(
            text = "Heisenberg",
            quotedImage = null,
            quotedMessage = "Say my name \uD83D\uDE0E",
            messageTime = "13:37"
        )
        ReceivedMessage(
            text = "Heisenberg",
            quotedImage = R.drawable.heisenberg,
            quotedMessage = "Say my name \uD83D\uDE0E",
            messageTime = "13:37"
        )
    }
}