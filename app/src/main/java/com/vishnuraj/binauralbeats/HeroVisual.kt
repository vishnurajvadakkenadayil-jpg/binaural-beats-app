package com.vishnuraj.binauralbeats

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HeroVisual(palette: SessionPalette, sizeDp: Int = 230) {
    val infinite = rememberInfiniteTransition(label = "hero")
    val breathe by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    val orbitOneAngle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(13000, easing = LinearEasing)),
        label = "orbitOne"
    )
    val orbitTwoAngle by infinite.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(19000, easing = LinearEasing)),
        label = "orbitTwo"
    )

    Canvas(modifier = Modifier.size(sizeDp.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f

        // wave rings (static, faint)
        listOf(0.70f, 0.82f, 0.94f).forEach { fraction ->
            drawCircle(
                color = palette.accent.copy(alpha = 0.22f),
                radius = maxRadius * fraction,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // breathing disc
        val discRadius = maxRadius * 0.62f * breathe
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(palette.mid, palette.deep, palette.deep.copy(alpha = 0.9f)),
                center = Offset(center.x - discRadius * 0.2f, center.y - discRadius * 0.25f),
                radius = discRadius * 1.3f
            ),
            radius = discRadius,
            center = center
        )
        drawCircle(
            color = palette.accent,
            radius = discRadius * 0.045f,
            center = center
        )

        // orbit one
        val orbitOneRadius = maxRadius * 1.0f
        drawCircle(
            color = palette.accent.copy(alpha = 0.14f),
            radius = orbitOneRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        val orbitOneRad = Math.toRadians(orbitOneAngle.toDouble())
        drawCircle(
            color = palette.accent,
            radius = 4.dp.toPx(),
            center = Offset(
                center.x + (orbitOneRadius * cos(orbitOneRad)).toFloat(),
                center.y + (orbitOneRadius * sin(orbitOneRad)).toFloat()
            )
        )

        // orbit two (reverse, larger)
        val orbitTwoRadius = maxRadius * 0.92f
        drawCircle(
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.08f),
            radius = orbitTwoRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        val orbitTwoRad = Math.toRadians(orbitTwoAngle.toDouble())
        drawCircle(
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
            radius = 2.5.dp.toPx(),
            center = Offset(
                center.x + (orbitTwoRadius * cos(orbitTwoRad)).toFloat(),
                center.y + (orbitTwoRadius * sin(orbitTwoRad)).toFloat()
            )
        )
    }
}
