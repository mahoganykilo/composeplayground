package com.example.composeplayground.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.random.Random

private const val loop = 20
private const val yTop = 600f
private const val yBottom = 1000f
private const val initialPosition = 250f
private const val endPosition = 800f
private const val overflow = 200f

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CardV5() {
    val offsets = remember {
        mutableStateListOf<Offset>()
    }
    val movingLeft = remember {
        mutableStateOf(1f)
    }

    val sweepPosition = remember {
        mutableStateOf(0f)
    }

    val position = remember {
        Animatable(0f)
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(sweepPosition) {
        scope.launch {
            animateSweep(position)
        }
    }
    sweepPosition.value = (position.value).also {
        if (it == endPosition) movingLeft.value =
            -1f else if (it == initialPosition) movingLeft.value = 1f
    }
    offsets.addAll(
        MutableList(loop) {
            Offset(
                x = initialPosition + (endPosition - initialPosition) * Random.nextFloat(),
                y = yTop + (yBottom - yTop) * Random.nextFloat()
            )
        }
    )
    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // BG
            val outerBorder = initialPosition - 50
            drawRoundRect(
                color = Color(0xFF000000),
                topLeft = Offset(outerBorder, yTop),
                size = Size((endPosition - initialPosition) + 100, yBottom - yTop),
                cornerRadius = CornerRadius(48f, 48f)
            )
            // Frame
            drawRoundRect(
                color = Color(0xFFFFFFFF),
                topLeft = Offset(initialPosition - 20, yTop + 30),
                size = Size((endPosition - initialPosition) + 40, yBottom - yTop - 60),
                cornerRadius = CornerRadius(24f, 24f),
                style = Stroke(
                    width = 6.dp.value
                )
            )
            // X cancel
            drawRoundRect(
                color = Color(0xFF000000),
                topLeft = Offset(outerBorder, yTop + 75),
                size = Size((endPosition - initialPosition + 100), yBottom - yTop - 150),
                cornerRadius = CornerRadius(24f, 24f),
            )

            // Y Cancel
            drawRect(
                color = Color(0xFF000000),
                topLeft = Offset(initialPosition + 30, yTop),
                size = Size((endPosition - initialPosition - 60), yBottom - yTop),
            )

            val linePosition = max(initialPosition, min(sweepPosition.value, endPosition))
            drawLine(
                color = Color(0xFF66AEC5),
                start = Offset(linePosition, yTop - 40),
                end = Offset(linePosition, yBottom + 40),
                strokeWidth = 5f
            )
            drawGrid(initialPosition, sweepPosition.value, movingLeft.value, this)
            repeat(loop) {
                drawCircles(
                    this, offsets[it],
                    alpha = determineAlpha(
                        sweep = sweepPosition.value,
                        xAxis = offsets[it].x,
                        direction = movingLeft.value
                    )
                )
            }
        }
    }
}

private fun drawCircles(
    drawScope: DrawScope,
    offset: Offset,
    alpha: Float
) {
    drawScope.drawCircle(
        color = Color(0xFF66AEC5),
        radius = 5f,
        center = offset,
        alpha = alpha
    )

}

private suspend fun animateSweep(animatable: Animatable<Float, AnimationVector1D>) {
    val animationLongDuration = 2000
    val animationShortDuration = 100
    repeat(30) {
        animatable.animateTo(
            endPosition,
            animationSpec = tween(
                durationMillis = animationLongDuration,
                delayMillis = 0,
                easing = FastOutSlowInEasing
            )
        )
//        animatable.animateTo(
//            endPosition + overflow,
//            animationSpec = tween(
//                durationMillis = animationShortDuration,
//                delayMillis = 0,
//                easing = LinearEasing
//            )
//        )
//        animatable.animateTo(
//            endPosition,
//            animationSpec = tween(
//                durationMillis = 1,
//                delayMillis = 0,
//                easing = LinearEasing
//            )
//        )
        animatable.animateTo(
            initialPosition,
            animationSpec = tween(
                durationMillis = animationLongDuration,
                delayMillis = 0,
                easing = FastOutSlowInEasing
            )
        )
//        animatable.animateTo(
//            initialPosition - overflow,
//            animationSpec = tween(
//                durationMillis = animationShortDuration,
//                delayMillis = 0,
//                easing = LinearEasing
//            )
//        )
//        animatable.animateTo(
//            initialPosition,
//            animationSpec = tween(
//                durationMillis = 1,
//                delayMillis = 0,
//                easing = LinearEasing
//            )
//        )
    }
}

private fun drawGrid(initialPosition: Float, sweep: Float, direction: Float, drawScope: DrawScope) {
    val color = Color(0xFF66AEC5)
    val xInterval = endPosition - initialPosition
    val yInterval = yBottom - yTop
    val boxSize = 30
    val xLoop = (xInterval / boxSize).toInt()
    val yLoop = (yInterval / boxSize).toInt()

    // vertical lines
    repeat(xLoop) {
        val xAxis = initialPosition + (boxSize * it)
        drawScope.drawLine(
            color = color,
            start = Offset(xAxis, yTop),
            end = Offset(xAxis, yBottom),
            alpha = determineAlpha(sweep, xAxis, direction)
        )
    }

    // horizontal lines
    repeat(yLoop) {
        val yAxis = yTop + (boxSize * it)
        repeat(xLoop) { x ->
            val xAxis = initialPosition + (boxSize * x)
            val xAxis1 = initialPosition + (boxSize * (x + 1))
            drawScope.drawLine(
                color = color,
                start = Offset(xAxis, yAxis),
                end = Offset(xAxis1, yAxis),
                alpha = determineAlpha(sweep, xAxis, direction)
            )
        }
    }
}

private fun determineAlpha(sweep: Float, xAxis: Float, direction: Float): Float {
    val lag = overflow
    val diff = (endPosition - initialPosition) / 2
    val alpha = when {
        direction * (sweep - xAxis) >= lag -> 0f
        direction * (xAxis - sweep) >= 0f -> 0f
        sweep + lag >= endPosition -> {
            1 -  ((abs(sweep - xAxis) / lag) * cos(((endPosition - sweep) / diff) * PI / 2).toFloat())
        }
        sweep - lag <= initialPosition -> {
            1 -  ((abs(sweep - xAxis) / lag) * cos(((sweep - initialPosition) / diff) * PI / 2).toFloat())
        }


        else -> {
            1 - direction * (sweep - xAxis) / lag
        }
    }
    return max(min(alpha, 1.0f), 0.0f)
}