package com.example.composeplayground.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val StarSize = 50

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CardV2() {
    val animatedState = remember {
        mutableListOf<Animatable<Float, AnimationVector1D>>()
    }
    val offsets = remember {
        mutableStateListOf<Offset>()
    }
    repeat(5) {
        offsets.add(
            Offset(
                x = 200 + 600 * Random.nextFloat(),
                y = 650 + 300 * Random.nextFloat()
            )
        )
        animatedState.add(Animatable(0f))
        LaunchedEffect(animatedState[it]) {
            val size = animatedState[it]
            launch {
                while (size.isRunning) {
                    size.animateTo(
                        1f,
                        animationSpec = repeatable(
                            iterations = 3,
                            animation = tween(
                                durationMillis = 300,
                                delayMillis = Random.nextInt(0, 2500)
                            ),
                            repeatMode = RepeatMode.Reverse,
                            initialStartOffset = StartOffset(
                                Random.nextInt(0, 500),
                                offsetType = StartOffsetType.Delay
                            )
                        )
                    )
                    size.animateTo(
                        0f,
                        animationSpec = tween(
                            durationMillis = 1000
                        )
                    )
                }
            }
        }
    }

    Row {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            drawRoundRect(
                color = Color(0xFF791E1E),
                topLeft = Offset(200f, 600f),
                size = Size(700f, 400f),
                cornerRadius = CornerRadius(20f, 20f)
            )

        }
    }
    Row(modifier = Modifier.blur(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .blur(
                    1000.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
        ) {
            for (i in 0..4) {
                scale(
                    scale = animatedState[i].value,
                    pivot = Offset(offsets[i].x, offsets[i].y)
                ) {
                    drawCircle(
                        color = Color(0xFF66AEC5),
                        radius = 20f,
                        center = offsets[i]
                    )
                }
            }
        }
    }

}