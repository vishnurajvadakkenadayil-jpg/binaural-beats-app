package com.vishnuraj.binauralbeats

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
private fun PlayerPanel(
    timerState: SessionTimerState,
    mode: Mode,
    palette: SessionPalette,
    frequencyLabel: String,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onTogglePlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    backgroundChoice: Int,
    onBackgroundSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    var showBackgroundPicker by remember { mutableStateOf(false) }
    if (showBackgroundPicker) {
        BackgroundPickerDialog(
            palette = palette,
            selectedIndex = backgroundChoice,
            onSelect = onBackgroundSelected,
            onDismiss = { showBackgroundPicker = false }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07090B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundGlassButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFFA8ABAE))
            }
            RoundGlassButton(onClick = { showBackgroundPicker = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Background sound", tint = Color(0xFFA8ABAE))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                GlobeVisual(palette = palette, sizeDp = 260)
            }

            Text(
                text = "NOW PLAYING",
                color = palette.accent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = mode.name,
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = frequencyLabel,
                color = Color(0xFF9DA1A0),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (mode.name == "Study" || mode.name == "Sleep" || mode.name == "Relax") {
                SessionTimerCard(palette = palette, state = timerState)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundGlassButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) palette.accent else Color(0xFFA8ABAE)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(palette.accent)
                        .clickable { onTogglePlay() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color(0xFF0C100D),
                        modifier = Modifier.size(29.dp)
                    )
                }
                Box(modifier = Modifier.size(42.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.VolumeUp,
                    contentDescription = "Volume",
                    tint = Color(0xFF8A8E90),
                    modifier = Modifier.size(20.dp)
                )
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = palette.accent,
                        activeTrackColor = palette.accent,
                        inactiveTrackColor = Color.White.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                )
            }

            Row(
                modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulsingDot(palette = palette, isPlaying = isPlaying)
                Text(
                    text = if (isPlaying) "BINAURAL SIGNAL ACTIVE" else "SESSION PAUSED",
                    color = Color(0xFF5F6463),
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun RoundGlassButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.035f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun CosmeticProgressBar(palette: SessionPalette, isPlaying: Boolean) {
    Column(modifier = Modifier.padding(top = 36.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Color.White.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.31f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(palette.accent)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("18:24", color = Color(0xFF5F6365), fontSize = 9.sp)
            Text("60:00", color = Color(0xFF5F6365), fontSize = 9.sp)
        }
    }
}

@Composable
private fun PulsingDot(palette: SessionPalette, isPlaying: Boolean) {
    val infinite = rememberInfiniteTransition(label = "pulse")
    val ringScale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = if (isPlaying) 3.2f else 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "pulseScale"
    )
    val ringAlpha by infinite.animateFloat(
        initialValue = if (isPlaying) 0.5f else 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "pulseAlpha"
    )
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .graphicsLayer(scaleX = ringScale, scaleY = ringScale, alpha = ringAlpha)
                .background(palette.accent, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(5.dp)
                .background(
                    if (isPlaying) palette.accent else Color(0xFF777777),
                    CircleShape
                )
        )
    }
}

@Composable
private fun BackgroundPickerDialog(
    palette: SessionPalette,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf("None", "Rain", "Water", "Birds")
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF16181B))
                .padding(vertical = 18.dp)
        ) {
            Text(
                text = "Background sound",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp)
            )
            options.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(index)
                            onDismiss()
                        }
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (selected) palette.accent else Color(0xFF3A3D41), CircleShape)
                    )
                    Text(
                        text = label,
                        color = if (selected) palette.accent else Color(0xFFD0D2D4),
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.padding(start = 14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NowPlayingScreen(
    timerState: SessionTimerState,
    mode: Mode,
    palette: SessionPalette,
    frequencyLabel: String,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onTogglePlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    backgroundChoice: Int,
    onBackgroundSelected: (Int) -> Unit,
    story: SessionStory,
    onBack: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07090B))
    ) {
        val screenHeight = maxHeight
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = screenHeight)
            ) {
                PlayerPanel(
                    timerState = timerState,
                    mode = mode,
                    palette = palette,
                    frequencyLabel = frequencyLabel,
                    isPlaying = isPlaying,
                    isFavorite = isFavorite,
                    onTogglePlay = onTogglePlay,
                    onToggleFavorite = onToggleFavorite,
                    volume = volume,
                    onVolumeChange = onVolumeChange,
                    backgroundChoice = backgroundChoice,
                    onBackgroundSelected = onBackgroundSelected,
                    onBack = onBack
                )
            }
            StorySection(palette = palette, title = mode.name, story = story)
        }
    }
}

@Composable
private fun StorySection(palette: SessionPalette, title: String, story: SessionStory) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.accent.copy(alpha = 0.18f))
        )
        Text(
            text = "BEHIND THE FREQUENCY",
            color = palette.accent,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 70.dp)
        )
        Text(
            text = "About $title",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 38.sp,
            letterSpacing = (-2).sp,
            modifier = Modifier.padding(top = 15.dp)
        )
        Text(
            text = story.about,
            color = Color(0xFFA2A6A5),
            fontSize = 14.sp,
            lineHeight = 25.sp,
            modifier = Modifier.padding(top = 34.dp)
        )
        if (story.guidance != null) {
            Text(
                text = story.guidance,
                color = Color(0xFFA2A6A5),
                fontSize = 14.sp,
                lineHeight = 25.sp,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 36.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Text(
            text = story.note,
            color = Color(0xFF646968),
            fontSize = 11.sp,
            lineHeight = 19.sp,
            modifier = Modifier
                .padding(top = 22.dp, bottom = 90.dp)
        )
    }
}
