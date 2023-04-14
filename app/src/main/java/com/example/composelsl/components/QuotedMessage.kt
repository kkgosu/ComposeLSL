package com.example.composelsl.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composelsl.R
import com.example.composelsl.ui.theme.SentQuoteColor

/**
 * @author Konstantin Koval on 14.04.2023
 */
@Composable
fun QuotedMessage(
    modifier: Modifier = Modifier,
    quotedMessage: String? = null,
    quotedImage: Int? = null,
) {
    Row(modifier = modifier
        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
        .height(IntrinsicSize.Min)
        .background(SentQuoteColor, shape = RoundedCornerShape(8.dp))
        .clip(shape = RoundedCornerShape(8.dp))
        .clickable {}) {
        Surface(
            color = Color.White,
            modifier = Modifier
                .padding(start = 6.dp, top = 4.dp, bottom = 4.dp)
                .fillMaxHeight()
                .width(4.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {}
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (quotedImage != null) {
                Image(
                    painter = painterResource(id = quotedImage),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .layoutId("image")
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
            Column {
                Text(
                    text = "You",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    letterSpacing = 1.sp,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = quotedMessage ?: "Photo",
                    fontSize = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Row for storing quote title, message or image description and image itself.
 * [image] is positioned end of this layout.
 */
@Composable
private fun QuoteImageRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    image: @Composable (() -> Unit)? = null
) {
    val finalContent = @Composable {
        if (image != null) {
            content()
            image.invoke()
        } else {
            content()
        }
    }
    Layout(modifier = modifier, content = finalContent) { measurables, constraints ->
        var imageIndex = -1
        val placeables = measurables.mapIndexed { index, measurable ->
            if (measurable.layoutId == "image") {
                imageIndex = index
            }
            measurable.measure(Constraints(0, constraints.maxWidth, 0, constraints.maxHeight))
        }

        val size = placeables.fold(IntSize.Zero) { current: IntSize, placeable: Placeable ->
            IntSize(
                width = current.width + placeable.width,
                height = maxOf(current.height, placeable.height)
            )
        }
        val width = size.width.coerceAtLeast(constraints.minWidth)
        layout(width, size.height) {
            var x = 0
            placeables.forEachIndexed { index: Int, placeable: Placeable ->
                if (index != imageIndex) {
                    placeable.placeRelative(x, 0)
                    x += placeable.width
                } else {
                    placeable.placeRelative(width - placeable.width, 0)
                }
            }
        }
    }
}

@Preview
@Composable
fun QuotedMessagePreview() {
    QuotedMessage(
        quotedMessage = "Say my name \uD83D\uDE0E",
        quotedImage = null
    )
}

@Preview
@Composable
fun QuotedMessageWithImagePreview() {
    QuotedMessage(
        quotedMessage = "Say my name \uD83D\uDE0E",
        quotedImage = R.drawable.heisenberg
    )
}