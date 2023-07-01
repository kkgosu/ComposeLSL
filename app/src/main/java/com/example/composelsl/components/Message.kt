package com.example.composelsl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composelsl.ui.theme.SentMessageColor

/**
 * Лейаут, содержащий смс и статус (время + прочитанность). [messageStat] располагается в зависимости от
 * кол-во строк смс.
 *
 * @author Konstantin Koval on 14.04.2023
 */
@Composable
fun ChatFlexBoxLayout(
    text: String,
    messageTime: String,
    messageStatus: MessageStatus?,
    modifier: Modifier = Modifier,
) {
    val chatRowData = remember { ChatRowData(text) }
    val content = @Composable {
        Message(
            text = text,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                // maxWidth текста = maxWidth - horizontal padding
                chatRowData.lineCount = textLayoutResult.lineCount
                chatRowData.lastLineWidth = textLayoutResult.getLineRight(chatRowData.lineCount - 1)
                chatRowData.textWidth = textLayoutResult.size.width
            }
        )
        MessageTimeText(messageTime = messageTime, messageStatus = messageStatus)
    }

    Layout(
        modifier = modifier.padding(start = 2.dp, end = 6.dp, bottom = 2.dp),
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->
        if (measurables.size != 2) throw IllegalArgumentException("There should be 2 components for this layout")
        val placeables: List<Placeable> = measurables.map { measurable ->
            // измеряем каждого ребенка с максимальными констреинтами по ширине,
            // тк смс может занимать всё свободное пространство родителя
            measurable.measure(Constraints(0, constraints.maxWidth))
        }
        val message = placeables.first()
        val status = placeables.last()
        // считаем параметры строки
        calculateMessageParams(chatRowData, constraints, message, status)
        layout(width = chatRowData.parentWidth, height = chatRowData.rowHeight) {
            message.placeRelative(0, 0)
            status.placeRelative(
                chatRowData.parentWidth - status.width,
                chatRowData.rowHeight - status.height
            )
        }
    }
}

private fun calculateMessageParams(
    chatRowData: ChatRowData,
    constraints: Constraints,
    message: Placeable,
    status: Placeable
) {
    if ((chatRowData.rowWidth == 0 || chatRowData.rowHeight == 0)) {
        chatRowData.parentWidth = constraints.maxWidth
        calculateChatWidthAndHeight(chatRowData, message, status)
        // Parent width of this chat row is either result of width calculation
        // or quote or other sibling width if they are longer than calculated width.
        // minWidth of Constraint equals (text width + horizontal padding)
        chatRowData.parentWidth =
            chatRowData.rowWidth.coerceAtLeast(minimumValue = constraints.minWidth)
    }
}

data class ChatRowData(
    var text: String,
    // Width of the text without padding
    var textWidth: Int = 0,
    var lastLineWidth: Float = 0f,
    var lineCount: Int = 0,
    var rowWidth: Int = 0,
    var rowHeight: Int = 0,
    var parentWidth: Int = 0,
    var measuredType: Int = 0,
)


private fun calculateChatWidthAndHeight(
    chatRowData: ChatRowData,
    message: Placeable,
    status: Placeable?,
) {
    if (status != null) {
        val lineCount = chatRowData.lineCount
        val lastLineWidth = chatRowData.lastLineWidth
        val parentWidth = chatRowData.parentWidth
        val padding = (message.measuredWidth - chatRowData.textWidth) / 2
        // Multiple lines and last line and status is longer than text size and right padding
        if (lineCount > 1 && lastLineWidth + status.measuredWidth >= chatRowData.textWidth + padding) {
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = message.measuredHeight + status.measuredHeight
            chatRowData.measuredType = 0
        } else if (lineCount > 1 && lastLineWidth + status.measuredWidth < chatRowData.textWidth + padding) {
            // Multiple lines and last line and status is shorter than text size and right padding
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = message.measuredHeight
            chatRowData.measuredType = 1
        } else if (lineCount == 1 && message.width + status.measuredWidth >= parentWidth) {
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = message.measuredHeight + status.measuredHeight
            chatRowData.measuredType = 2
        } else {
            chatRowData.rowWidth = message.measuredWidth + status.measuredWidth
            chatRowData.rowHeight = message.measuredHeight
            chatRowData.measuredType = 3
        }
    } else {
        chatRowData.rowWidth = message.width
        chatRowData.rowHeight = message.height
    }
}

@Composable
private fun Message(
    text: String,
    modifier: Modifier = Modifier,
    onTextLayout: (TextLayoutResult) -> Unit,
) {
    Text(
        modifier = modifier
            .padding(horizontal = 6.dp)
            .padding(bottom = 4.dp),
        text = text.trim(),
        onTextLayout = onTextLayout,
        color = Color.White,
        fontSize = 16.sp,
    )
}

@Preview
@Composable
fun MessagePreview() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp)
            .shadow(1.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(SentMessageColor)
    ) {
        ChatFlexBoxLayout(
            modifier = Modifier.padding(
                start = 2.dp,
                top = 2.dp,
                end = 4.dp,
                bottom = 2.dp
            ),
            text = "Say my name \uD83D\uDE0E\nSay my name \uD83D\uDE0E",
            messageTime = "13:37",
            messageStatus = MessageStatus.READ,
        )
    }
}

@Preview
@Composable
fun ChatFlexPreview() {
    SubcomposeColumn(
        modifier = Modifier
            .shadow(1.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(SentMessageColor)
            .clickable { },
        content = {
            ChatFlexBoxLayout(
                modifier = Modifier.padding(
                    start = 2.dp,
                    top = 2.dp,
                    end = 4.dp,
                    bottom = 2.dp
                ),
                text = "Say my name \uD83D\uDE0E ",
                messageTime = "13:37",
                messageStatus = MessageStatus.READ
            )
        }
    )
}