package com.example.composeplayground.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode.Companion.DstIn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val StarSize = 50

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CardV1() {
    val animatedState = mutableListOf<Float>()
    val offsets = remember {
        mutableStateListOf<Offset>()
    }
    repeat(5) {
        offsets.add(
            Offset(
                x = 200 + 600 * Random.nextFloat(),
                y = 600 + 300 * Random.nextFloat()
            )
        )
        animatedState.add(
            scaleShapeTransition(
                targetValue = 1f
            )
        )
    }
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
        for (i in 0..4) {
            scale(
                scale = animatedState[i],
                pivot = Offset(offsets[i].x + StarSize, offsets[i].y + StarSize)
            ) {
                drawPath(
                    path = drawStar(
                        offset = offsets[i],
                        path = Path()
                    ).also { it.close() },
                    alpha = 0.5f,
                    color = Color(0xFF66AEC5),
                    colorFilter = ColorFilter.tint(Color(0xFF66AEC5), DstIn)
                )
            }
        }
    }
}

private fun drawStar(offset: Offset, path: Path): Path {
    // top left
    path.moveTo(offset.x + StarSize * 0.5f, offset.y + StarSize * 0.84f)
    // top right
    path.lineTo(offset.x + StarSize * 1.5f, offset.y + StarSize * 0.84f)
    // bottom left
    path.lineTo(offset.x + StarSize * 0.68f, offset.y + StarSize * 1.45f)
    // top tip
    path.lineTo(offset.x + StarSize * 1.0f, offset.y + StarSize * 0.5f)
    // bottom right
    path.lineTo(offset.x + StarSize * 1.32f, offset.y + StarSize * 1.45f)
    // top left
    path.lineTo(offset.x + StarSize * 0.5f, offset.y + StarSize * 0.84f)

    return path
}

@Composable
private fun scaleShapeTransition(
    targetValue: Float,
): Float {
    val size = remember {
        Animatable(0f)
    }
    LaunchedEffect(targetValue) {
        launch {
            size.animateTo(
                targetValue,
                animationSpec = repeatable(
                    iterations = 3,
                    animation = tween(
                        durationMillis = 1000,
                        delayMillis = Random.nextInt(0, 5000)
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
    return size.value
}