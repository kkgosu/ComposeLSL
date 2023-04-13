package com.example.composelsl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * @author Konstantin Koval on 13.04.2023
 */
@Composable
fun ChipFlowLayout(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 2.dp,
    verticalSpacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(content, modifier) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var horizontalSpace = 0
        var verticalSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentHorizontalSize = 0
        var currentVerticalSize = 0

        // Можно ли добавить к текущей строке
        fun canAddToCurrentSequence(placeable: Placeable) =
            currentSequence.isEmpty() || currentHorizontalSize + horizontalSpacing.roundToPx() +
                    placeable.width <= constraints.maxWidth

        // Сохраняет информацию о текущей строке и стартует новую
        fun startNewSequence() {
            if (sequences.isNotEmpty()) {
                verticalSpace += verticalSpacing.roundToPx()
            }
            sequences += currentSequence.toList()
            crossAxisSizes += currentVerticalSize
            crossAxisPositions += verticalSpace

            verticalSpace += currentVerticalSize
            horizontalSpace = max(horizontalSpace, currentHorizontalSize)

            currentSequence.clear()
            currentHorizontalSize = 0
            currentVerticalSize = 0
        }

        for (measurable in measurables) {
            val placeable = measurable.measure(constraints)
            if (!canAddToCurrentSequence(placeable)) startNewSequence()
            if (currentSequence.isNotEmpty()) {
                currentHorizontalSize += horizontalSpacing.roundToPx()
            }
            currentSequence.add(placeable)
            currentHorizontalSize += placeable.width
            currentVerticalSize = max(currentVerticalSize, placeable.height)
        }

        if (currentSequence.isNotEmpty()) startNewSequence()

        val width = max(horizontalSpace, constraints.minWidth)
        val height = max(verticalSpace, constraints.minHeight)

        layout(width, height) {
            sequences.forEachIndexed { i, placeables ->
                val childrenMainAxisSizes = IntArray(placeables.size) { j ->
                    placeables[j].width + if (j < placeables.lastIndex) horizontalSpacing.roundToPx() else 0
                }
                val mainAxisPositions = IntArray(childrenMainAxisSizes.size) { 0 }
                with(Arrangement.Top) {
                    arrange(width, childrenMainAxisSizes, mainAxisPositions)
                }
                placeables.forEachIndexed { j, placeable ->
                    val crossAxis = 0
                    placeable.place(
                        x = mainAxisPositions[j],
                        y = crossAxisPositions[i] + crossAxis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ChipPreview() {
    ChipFlowLayout {
        FilterChip(selected = false, onClick = { /*TODO*/ }, label = {
            Text(text = "Breaking Bad")
        })
        FilterChip(selected = false, onClick = { /*TODO*/ }, label = {
            Text(text = "Walter White")
        })
        FilterChip(selected = false, onClick = { /*TODO*/ }, label = {
            Text(text = "Jesse Pinkman")
        })
        FilterChip(selected = false, onClick = { /*TODO*/ }, label = {
            Text(text = "Saul Goodman")
        })
        FilterChip(selected = false, onClick = { /*TODO*/ }, label = {
            Text(text = "Gus")
        })
        FilterChip(selected = false, onClick = { /*TODO*/ }, label = {
            Text(text = "Los Pollos Hermanos")
        })
        FilterChip(selected = false, onClick = { /*TODO*/ }, label = {
            Text(text = "Quick maths")
        })
    }
}