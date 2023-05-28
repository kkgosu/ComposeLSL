package com.example.composelsl.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SimpleLayout(modifier: Modifier = Modifier) {
    Layout(modifier = modifier, content = { /*TODO*/ }, measurePolicy = { measurables, constrains ->
        //measure
        val placeables = measurables.map { it.measure(constrains) }
        layout(500, 500) {
            //place
            placeables.forEach { it.place(0, 0) }
        }
    })
}

@Composable
@Preview
fun SimpleLayout1_1(modifier: Modifier = Modifier) {
    Layout(modifier = modifier, content = {
        Text(text = "Top left")
        Text(text = "Top right")
        Text(text = "Bottom left")
        Text(text = "Bottom right")
    }, measurePolicy = { measurables, constrains ->
        val placeables = measurables.map { it.measure(constrains) }
        layout(constrains.maxWidth, constrains.maxHeight) {
            placeables.forEachIndexed { index, it ->
                when (index) {
                    0 -> it.place(0, 0)
                    1 -> it.place(constrains.maxWidth - it.width, 0)
                    2 -> it.place(0, constrains.maxHeight - it.height)
                    3 -> it.place(constrains.maxWidth - it.width, constrains.maxHeight - it.height)
                }
            }
        }
    })
}

@Composable
@Preview
fun SimpleLayout1_2(modifier: Modifier = Modifier) {
    Layout(modifier = modifier, content = {
        Text(text = "Top left", modifier = Modifier.layoutId(0))
        Text(text = "Bottom left", modifier = Modifier.layoutId(2))
        Text(text = "Bottom right", modifier = Modifier.layoutId(3))
        Text(text = "Top right", modifier = Modifier.layoutId(1))
    }, measurePolicy = { measurables, constrains ->
        val placeables = measurables.sortedBy { it.layoutId as Int }.map { it.measure(constrains) }
        layout(constrains.maxWidth, constrains.maxHeight) {
            placeables.forEachIndexed { index, it ->
                when (index) {
                    0 -> it.place(0, 0)
                    1 -> it.place(constrains.maxWidth - it.width, 0)
                    2 -> it.place(0, constrains.maxHeight - it.height)
                    3 -> it.place(constrains.maxWidth - it.width, constrains.maxHeight - it.height)
                }
            }
        }
    })
}