package com.example.composelsl.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.intermediateLayout
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import kotlinx.coroutines.launch

/**
 * @author Konstantin Koval on 16.04.2023
 */
@Composable
@Preview
fun SimpleMove1() {
    var isRow by remember { mutableStateOf(false) }
    val texts = remember {
        movableContentOf {
            Text(text = "Title", style = MaterialTheme.typography.titleSmall)
            Text(text = "Subtitle", style = MaterialTheme.typography.bodyMedium)
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .clickable {
            isRow = !isRow
        }) {
        LogCompositions(msg = "Box")
        if (isRow) {
            Row {
                texts()
            }
        } else {
            Column {
                texts()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun SimpleMove2() {
    LookaheadScope {
        var isRow by remember { mutableStateOf(false) }
        val texts = remember {
            movableContentOf {
                Text(text = "Title", style = MaterialTheme.typography.titleSmall, modifier = Modifier.animatePlacementInScope("title", this))
                Text(text = "Subtitle", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.animatePlacementInScope("subtitle", this))
            }
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                isRow = !isRow
            }) {
            LogCompositions(msg = "Box")
            if (isRow) {
                Row {
                    texts()
                }
            } else {
                Column {
                    texts()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun SimpleMove3() {
    LookaheadScope {
        var isRow by remember { mutableStateOf(false) }
        val texts = remember {
            movableContentOf {
                Text(text = "Title", style = MaterialTheme.typography.titleSmall, modifier = Modifier.animatePlacementInScope("title", this))
                Text(text = "Subtitle", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.animatePlacementInScope("subtitle", this))
            }
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                isRow = !isRow
            }) {
            LogCompositions(msg = "Box")
            if (isRow) {
                Row {
                    texts()
                }
            } else {
                Column(modifier = Modifier.align(Alignment.Center)) {
                    texts()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.animatePlacementInScope(text: String, lookaheadScope: LookaheadScope) = composed {
    var offsetAnimation: Animatable<IntOffset, AnimationVector2D>? by mutableStateOf(null)
    intermediateLayout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            Log.d(TAG, "text: $text")
            // Converts coordinates of the current layout to LookaheadCoordinates
            val coordinates = coordinates
            Log.d(TAG, "coordinates: ${coordinates?.positionInRoot()}")
            if (coordinates != null) {
                // Calculates the target offset within the lookaheadScope
                Log.d(TAG, "lookaheadScopeCoordinates: ${lookaheadScopeCoordinates.positionInRoot()}")
                Log.d(TAG, "localLookaheadPositionOf: ${lookaheadScopeCoordinates.localLookaheadPositionOf(coordinates)}")
                val target = with(lookaheadScope) {
                    lookaheadScopeCoordinates
                        .localLookaheadPositionOf(coordinates)
                        .round()
                }
                Log.d(TAG, "target: $target")
                Log.d(TAG, "offsetAnimation?.targetValue: ${offsetAnimation?.targetValue}")
                // Uses the target offset to start an offset animation
                if (target != offsetAnimation?.targetValue) {
                    offsetAnimation?.run {
                        launch { animateTo(target) }
                    } ?: Animatable(target, IntOffset.VectorConverter).let {
                        offsetAnimation = it
                    }
                }
                Log.d(TAG, "localPositionOf: ${lookaheadScopeCoordinates.localPositionOf(coordinates, Offset.Zero).round()}")
                // Calculates the *current* offset within the given LookaheadScope
                val placementOffset = lookaheadScopeCoordinates.localPositionOf(coordinates, Offset.Zero).round()
                // Calculates the delta between animated position in scope and current
                // position in scope, and places the child at the delta offset. This puts
                // the child layout at the animated position.
                val (x, y) = requireNotNull(offsetAnimation).run { value - placementOffset }
                Log.d(TAG, "(x, y): $x,$y")
                placeable.place(x, y)
                Log.d(TAG, "\n")
            } else {
                placeable.place(0, 0)
                Log.d(TAG, "\n")
            }
        }
    }
}


class Ref(var value: Int)

@Composable
inline fun LogCompositions(msg: String) {
    val ref = remember { Ref(0) }
    SideEffect { ref.value++ }
    Log.d("QQQQ", "Compositions: $msg ${ref.value}")
}

private val TAG = "SimpleMove"