package com.example.composelsl.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.intermediateLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.example.composelsl.ui.theme.Purple80
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author Konstantin Koval on 16.04.2023
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun FromLazyColumnToCenter() {
    val sms = listOf(
        ChatMessage(
            0, "Say my name",
        ),
        ChatMessage(
            1, "Heisenberg",
        ),
        ChatMessage(
            2, "Cool",
        ),
        ChatMessage(
            3, "Yea",
        ),
        ChatMessage(
            4, "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        ),
    )
    LookaheadScope {
        var id by remember { mutableStateOf(-1) }
        val itemMap = remember {
            mutableMapOf<ChatMessage, @Composable (Modifier) -> Unit>()
        }
        val movableItems = sms.mapIndexed { index, item ->
            itemMap.getOrPut(item) {
                movableContentOf<Modifier> {
                    if (index % 2 == 0) {
                        SentMessage(
                            text = item.message,
                            messageTime = "13:37",
                            messageStatus = MessageStatus.READ,
                            modifier = it
                                .animatePlacementInScope()
                                .clickable {
                                    id = if (id == index) -1 else index
                                }
                        )
                    } else {
                        ReceivedMessage(text = item.message, messageTime = "13:37", modifier = it
                            .animatePlacementInScope()
                            .clickable {
                                id = if (id == index) -1 else index
                            })
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Purple80)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(if (id != -1) 10.dp else 0.dp)
            ) {
                itemsIndexed(movableItems) { index, it ->
                    if (index != id) it(Modifier) else Spacer(modifier = Modifier.height(40.dp))
                }
            }
            if (id != -1) {
                movableItems[id](Modifier.align(Alignment.Center))
            }
        }
    }
}


context(LookaheadScope)
        @OptIn(ExperimentalComposeUiApi::class)
fun Modifier.animatePlacementInScope() = composed {
    val offsetAnimation = remember { OffsetAnimation() }
    intermediateLayout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            val coordinates = coordinates
            if (coordinates != null) {
                val targetOffset = lookaheadScopeCoordinates.localLookaheadPositionOf(coordinates)
                val animOffset = offsetAnimation.updateTarget(targetOffset.round())
                val current = lookaheadScopeCoordinates.localPositionOf(coordinates, Offset.Zero).round()
                val (x, y) = animOffset - current
                placeable.place(x, y)
            } else {
                placeable.place(0, 0)
            }
        }
    }
}

private class OffsetAnimation {
    val value: IntOffset?
        get() = animatable?.value ?: target
    var target: IntOffset? by mutableStateOf(null)
        private set
    private var animatable: Animatable<IntOffset, AnimationVector2D>? = null

    context (CoroutineScope)
    fun updateTarget(
        targetValue: IntOffset,
    ): IntOffset {
        target = targetValue
        if (target != null && target != animatable?.targetValue) {
            animatable?.run {
                launch {
                    animateTo(targetValue)
                }
            } ?: Animatable(targetValue, IntOffset.VectorConverter).let {
                animatable = it
            }
        }
        return animatable?.value ?: targetValue
    }
}
