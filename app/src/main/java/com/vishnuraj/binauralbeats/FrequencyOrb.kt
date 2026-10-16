package com.vishnuraj.binauralbeats

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FrequencyOrb(
    modifier: Modifier = Modifier,
    color: Color,
    waveCount: Int,
    waviness: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = minOf(size.width, size.height) / 2f
        val ringCount = 24

        for (ring in 1..ringCount) {
            val baseRadius = maxRadius * (ring.toFloat() / ringCount)
            val path = Path()

            for (angleDeg in 0..360 step 2) {
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val wobble = sin(waveCount * angleRad + phase.toDouble()) * waviness * (ring.toFloat() / ringCount)
                val r = baseRadius + wobble.toFloat()
                val x = centerX + r * cos(angleRad).toFloat()
                val y = centerY + r * sin(angleRad).toFloat()

                if (angleDeg == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()

            drawPath(
                path = path,
                color = color.copy(alpha = 0.15f + (ring.toFloat() / ringCount) * 0.5f),
                style = Stroke(width = 1.5f)
            )
        }
    }
}
