package com.vishnuraj.binauralbeats

import androidx.compose.ui.graphics.Color

data class SessionPalette(
    val accent: Color,
    val glow: Color,
    val mid: Color,
    val deep: Color,
    val gradientStart: Color,
    val gradientEnd: Color
)

val GreenPalette = SessionPalette(
    accent = Color(0xFFC8FF58),
    glow = Color(0xA6C8FF58),
    mid = Color(0xFF4CAD97),
    deep = Color(0xFF183F3B),
    gradientStart = Color(0xFF244F47),
    gradientEnd = Color(0xFF80618B)
)

val VioletPalette = SessionPalette(
    accent = Color(0xFFD98CFF),
    glow = Color(0xA6C765FF),
    mid = Color(0xFF9454C6),
    deep = Color(0xFF39214C),
    gradientStart = Color(0xFF6426A8),
    gradientEnd = Color(0xFFD157C5)
)

val BluePalette = SessionPalette(
    accent = Color(0xFF78C7FF),
    glow = Color(0xA646A4FF),
    mid = Color(0xFF397CC8),
    deep = Color(0xFF142F56),
    gradientStart = Color(0xFF092B6E),
    gradientEnd = Color(0xFF247BD1)
)

val AmberPalette = SessionPalette(
    accent = Color(0xFFFFC66D),
    glow = Color(0xA6FFAB41),
    mid = Color(0xFFC47A38),
    deep = Color(0xFF4E2E18),
    gradientStart = Color(0xFF9C4C17),
    gradientEnd = Color(0xFFE0A547)
)

val PinkPalette = SessionPalette(
    accent = Color(0xFFFF8DBB),
    glow = Color(0xA6FF579A),
    mid = Color(0xFFC2487A),
    deep = Color(0xFF4E1D35),
    gradientStart = Color(0xFF792248),
    gradientEnd = Color(0xFFCF3E78)
)

fun paletteFor(modeName: String): SessionPalette = when (modeName) {
    "Study" -> GreenPalette
    "Sleep" -> BluePalette
    "Relax" -> AmberPalette
    "Miracle Tone" -> PinkPalette
    else -> VioletPalette
}
