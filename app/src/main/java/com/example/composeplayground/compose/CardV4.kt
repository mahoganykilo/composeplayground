package com.example.composeplayground.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

private const val loop = 15
private const val initialPosition = 200f

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CardV4() {
    val animatedState = mutableListOf<Float>()
    val offsets = remember {
        mutableStateListOf<Offset>()
    }
    val movingLeft = remember {
        mutableStateOf(1f)
    }


    val sweepPosition = remember {
        mutableStateOf(200f)
    }
    sweepPosition.value = sweep(endPosition = 900f).also {
        if (it == 900f) movingLeft.value = 0f else if (it == 0f) movingLeft.value = 1f
    }
    repeat(loop) {
        offsets.add(
            Offset(
                x = 200 + 600 * Random.nextFloat(),
                y = 650 + 300 * Random.nextFloat()
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
        drawLine(
            color = Color(0xFF66AEC5),
            start = Offset(sweepPosition.value, 600f),
            end = Offset(sweepPosition.value, 1000f),
            strokeWidth = 20f
        )
        val size = min(min(900f - sweepPosition.value, sweepPosition.value - initialPosition), 200f)
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF66AEC5),
                    Color(0xC066AEC5),
                    Color(0x8066AEC5),
                    Color(0x4066AEC5),
                    Color(0x0066AEC5)
                )
            ),
            topLeft = Offset(
                max(
                    initialPosition + max(
                        initialPosition - initialPosition * 2,
                        0f
                    ),
                    initialPosition - (size * movingLeft.value)
                ), 600f
            ),
            size = Size(size, 400f)
        )
        repeat(loop) {
            drawCircles(this, offsets[it], animatedState[it], sweepPosition.value)
        }
    }
}

private fun drawCircles(drawScope: DrawScope, offset: Offset, scale: Float, sweep: Float) {
    if (sweep > offset.x) {
        drawScope.scale(
            scale = scale,
            pivot = offset
        ) {
            drawCircle(
                color = Color(0xFF66AEC5),
                radius = 20f,
                center = offset
            )
        }
    }
}

@Composable
private fun sweep(
    endPosition: Float,
//    update: () -> Unit
): Float {

    val position = remember {
        Animatable(200f)
    }
    LaunchedEffect(endPosition) {
        launch {
            position.animateTo(
                endPosition,
                animationSpec = tween(
                    durationMillis = 1000,
                    delayMillis = 0,
                    easing = LinearEasing

                )
            )
            position.animateTo(
                initialPosition,
                animationSpec = tween(
                    durationMillis = 1000,
                    delayMillis = 250,
                    easing = LinearEasing

                )
            )
        }
    }
//    update.invoke()

    return position.value
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
                animationSpec = tween(
                    durationMillis = 1000
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