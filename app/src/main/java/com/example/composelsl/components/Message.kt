package com.example.composelsl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
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
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.White,
    fontSize: TextUnit = 16.sp,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    messageStat: @Composable () -> Unit,
    onMeasure: ((ChatRowData) -> Unit)? = null
) {
    val chatRowData = remember { ChatRowData() }
    val content = @Composable {
        Message(
            modifier = modifier
                .padding(
                    start = 2.dp,
                    end = 6.dp,
                )
                .wrapContentSize(),
            text = text,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                // maxWidth текста = maxWidth - horizontal padding
                chatRowData.lineCount = textLayoutResult.lineCount
                chatRowData.lastLineWidth = textLayoutResult.getLineRight(chatRowData.lineCount - 1)
                chatRowData.textWidth = textLayoutResult.size.width
            }
        )
        messageStat()
    }

    Layout(
        modifier = modifier.padding(
            start = 2.dp,
            end = 6.dp,
        ),
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
        if ((chatRowData.rowWidth == 0 || chatRowData.rowHeight == 0) || chatRowData.text != text) {
            chatRowData.parentWidth = constraints.maxWidth
            calculateChatWidthAndHeight(chatRowData, message, status)
            // Parent width of this chat row is either result of width calculation
            // or quote or other sibling width if they are longer than calculated width.
            // minWidth of Constraint equals (text width + horizontal padding)
            chatRowData.parentWidth = chatRowData.rowWidth.coerceAtLeast(minimumValue = constraints.minWidth)
        }
        onMeasure?.invoke(chatRowData)
        layout(width = chatRowData.parentWidth, height = chatRowData.rowHeight) {
            message.placeRelative(0, 0)
            status.placeRelative(
                chatRowData.parentWidth - status.width,
                chatRowData.rowHeight - status.height
            )
        }
    }
}

data class ChatRowData(
    var text: String = "",
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
    modifier: Modifier = Modifier,
    text: String,
    onTextLayout: (TextLayoutResult) -> Unit,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 16.sp,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        modifier = modifier
            .padding(horizontal = 6.dp)
            .padding(bottom = 4.dp),
        text = text.trim(),
        onTextLayout = onTextLayout,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
    )
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
                text = "Say my name \uD83D\uDE0E",
                messageStat = {
                    MessageTimeText(
                        modifier = Modifier.wrapContentSize(),
                        messageTime = "13:37",
                        messageStatus = MessageStatus.READ
                    )
                }
            )
        }
    )
}