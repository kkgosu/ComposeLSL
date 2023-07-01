package com.example.composelsl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Column, который приводит ширину детей к размеру наиболее широкого из них
 *
 * @author Konstantin Koval on 14.04.2023
 */
@Composable
fun SubcomposeColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        var subcomposeIndex = 0
        var placeables: List<Placeable> = subcompose(subcomposeIndex++, content).map {
            it.measure(constraints)
        }
        val columnSize =
            placeables.fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = currentMax.height + placeable.height
                )
            }

        // Remeasure every element using width of longest item using it as min width for
        // every composable
        if (placeables.isNotEmpty() && placeables.size > 1) {
            placeables = subcompose(subcomposeIndex, content).map { measurable: Measurable ->
                measurable.measure(Constraints(columnSize.width, constraints.maxWidth))
            }
        }

        layout(columnSize.width, columnSize.height) {
            var yPos = 0
            placeables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, yPos)
                yPos += placeable.height
            }

        }
    }
}

@Preview
@Composable
fun SubcomposeColumnPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SubcomposeColumn {
            Text(text = "smol", modifier = Modifier.background(Color.LightGray))
            Text(
                text = "Lorem Ipsum is simply dummy text",
                modifier = Modifier.background(Color.Blue)
            )
        }
        SubcomposeColumn {
            Text(text = "smol", modifier = Modifier.background(Color.LightGray))
            Text(
                text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                modifier = Modifier.background(Color.Blue)
            )
        }
    }
}

@Preview
@Composable
fun IntrinsicPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
            Text(text = "smol", modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray))
            Text(
                text = "Lorem Ipsum is simply dummy text",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Blue)
            )
        }
        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
            Text(text = "smol", modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray))
            Text(
                text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Blue)
            )
        }
    }
}