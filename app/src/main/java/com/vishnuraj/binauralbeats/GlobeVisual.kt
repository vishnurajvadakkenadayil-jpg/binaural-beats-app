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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GlobeVisual(palette: SessionPalette, sizeDp: Int = 300) {
    val infinite = rememberInfiniteTransition(label = "globe")
    val floatT by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3500, easing = LinearEasing), RepeatMode.Reverse),
        label = "float"
    )
    val auraOneAngle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(24000, easing = LinearEasing)),
        label = "auraOne"
    )
    val auraTwoAngle by infinite.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(35000, easing = LinearEasing)),
        label = "auraTwo"
    )
    val orbitAngle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "orbit"
    )

    Canvas(modifier = Modifier.size(sizeDp.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f
        val floatOffset = -(floatT * 9.dp.toPx())
        val sphereScale = 1f + floatT * 0.012f

        translate(top = floatOffset) {
            scale(sphereScale, pivot = center) {
                val sphereRadius = maxRadius * 0.76f

                // core sphere glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.accent.copy(alpha = 0.55f),
                            palette.mid,
                            palette.deep,
                            Color(0xFF080C10)
                        ),
                        center = Offset(center.x - sphereRadius * 0.25f, center.y - sphereRadius * 0.3f),
                        radius = sphereRadius * 1.6f
                    ),
                    radius = sphereRadius,
                    center = center
                )

                // secondary blob glow (lower right)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(palette.mid.copy(alpha = 0.55f), Color.Transparent),
                        center = Offset(center.x + sphereRadius * 0.3f, center.y + sphereRadius * 0.35f),
                        radius = sphereRadius * 0.7f
                    ),
                    radius = sphereRadius * 0.7f,
                    center = Offset(center.x + sphereRadius * 0.3f, center.y + sphereRadius * 0.35f)
                )

                // rim light
                drawCircle(
                    color = palette.accent.copy(alpha = 0.35f),
                    radius = sphereRadius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // center glow dot
                drawCircle(
                    color = palette.accent,
                    radius = 4.dp.toPx(),
                    center = Offset(center.x - sphereRadius * 0.35f, center.y - sphereRadius * 0.3f)
                )
            }
        }

        // aura one (solid, rotating)
        val auraOneRadius = maxRadius * 0.98f
        drawCircle(
            color = palette.accent.copy(alpha = 0.14f),
            radius = auraOneRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        val a1 = Math.toRadians(auraOneAngle.toDouble())
        drawCircle(
            color = palette.accent,
            radius = 2.5.dp.toPx(),
            center = Offset(
                center.x + (auraOneRadius * cos(a1)).toFloat(),
                center.y + (auraOneRadius * sin(a1)).toFloat()
            )
        )

        // aura two (dashed, reverse, larger)
        val auraTwoRadius = maxRadius * 0.93f
        drawCircle(
            color = Color.White.copy(alpha = 0.09f),
            radius = auraTwoRadius,
            center = center,
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )
        )
        val a2 = Math.toRadians(auraTwoAngle.toDouble())
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = 2.dp.toPx(),
            center = Offset(
                center.x + (auraTwoRadius * cos(a2)).toFloat(),
                center.y + (auraTwoRadius * sin(a2)).toFloat()
            )
        )

        // tilted orbit ring with a bright moving dot
        scale(scaleX = 1f, scaleY = 0.22f, pivot = center) {
            val ringRadius = maxRadius * 1.04f
            drawCircle(
                color = palette.accent.copy(alpha = 0.22f),
                radius = ringRadius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            val oa = Math.toRadians(orbitAngle.toDouble())
            drawCircle(
                color = palette.accent,
                radius = 5.dp.toPx(),
                center = Offset(
                    center.x + (ringRadius * cos(oa)).toFloat(),
                    center.y + (ringRadius * sin(oa)).toFloat()
                )
            )
        }
    }
}
