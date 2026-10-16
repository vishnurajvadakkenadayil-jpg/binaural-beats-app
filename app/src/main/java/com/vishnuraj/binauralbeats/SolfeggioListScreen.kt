package com.vishnuraj.binauralbeats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val Violet = Color(0xFFD98CFF)

private fun toneImageFor(name: String): String {
    val id = when (name) {
        "Foundation" -> "photo-1684162205807-630e5d5904be"
        "Healing" -> "photo-1550275994-cdc89cd1948f"
        "Liberation" -> "photo-1627819098699-d8ae75db1112"
        "Change" -> "photo-1617733033661-aae28b9f36a0"
        "Miracle Tone" -> "photo-1549317336-206569e8475c"
        "Connection" -> "photo-1615223424855-4d710dd844df"
        "Awakening" -> "photo-1618423835718-df9d86abc3bd"
        "Intuition" -> "photo-1652341483339-9ad5eef3ea74"
        else -> "photo-1635776062127-d379bfcba9f8"
    }
    return "https://images.unsplash.com/" + id + "?auto=format&fit=crop&w=900&q=85"
}

@Composable
fun SolfeggioListScreen(
    tones: List<Mode>,
    onToneClick: (Mode) -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090A0D))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x478441B3), Color.Transparent)
                    )
                )
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(74.dp)
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.035f))
                            .border(1.dp, Color.White.copy(alpha = 0.11f), CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFFA8ABAE))
                    }
                    Text(
                        text = "${tones.size} TONES",
                        color = Color(0xFF777A80),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 18.dp)
                        .padding(top = 44.dp, bottom = 42.dp)
                ) {
                    Text(
                        text = "THE COMPLETE COLLECTION",
                        color = Violet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Solfeggio Frequencies",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 46.sp,
                        letterSpacing = (-3.4).sp,
                        modifier = Modifier.padding(top = 14.dp, bottom = 20.dp)
                    )
                    Text(
                        text = "A spectrum of nine tones used in modern sound meditation, from the grounding 174 Hz Foundation to the expansive 963 Hz God Frequency. Select a frequency to open its immersive player and learn about its traditional associations.",
                        color = Color(0xFF8F9297),
                        fontSize = 14.sp,
                        lineHeight = 25.sp
                    )
                }
            }
            items(tones.chunked(2)) { pair ->
                val firstIndex = tones.indexOf(pair[0]) + 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    ToneCard(
                        number = firstIndex,
                        mode = pair[0],
                        onClick = { onToneClick(pair[0]) },
                        modifier = Modifier.weight(1f)
                    )
                    if (pair.size > 1) {
                        ToneCard(
                            number = firstIndex + 1,
                            mode = pair[1],
                            onClick = { onToneClick(pair[1]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 18.dp)
                        .padding(top = 48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Violet.copy(alpha = 0.16f))
                    )
                    Text(
                        text = "ABOUT THE SERIES",
                        color = Violet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 48.dp)
                    )
                    Text(
                        text = "The word Solfeggio comes from a centuries-old system of sung syllables used in medieval church music. The specific frequencies used here, from 174 Hz to 963 Hz, come from a modern sound-meditation tradition rather than from historical music. Today, people use them as atmospheric anchors for reflection, rest, breathwork, and personal rituals.",
                        color = Color(0xFF92969A),
                        fontSize = 14.sp,
                        lineHeight = 26.sp,
                        modifier = Modifier.padding(top = 28.dp)
                    )
                    Text(
                        text = "The names and associations shown here come from sound-meditation communities and are not proven medical effects. Listen at a comfortable volume, and choose the tone that best matches the mood or intention of your session.",
                        color = Color(0xFF92969A),
                        fontSize = 14.sp,
                        lineHeight = 26.sp,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ToneCard(
    number: Int,
    mode: Mode,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val shape = RoundedCornerShape(11.dp)
    Column(modifier = modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.08f)
                .clip(shape)
                .background(Color(0xFF1B1626))
                .border(1.dp, Color.White.copy(alpha = 0.08f), shape)
        ) {
            AsyncImage(
                model = toneImageFor(mode.name),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0x57441569), Color(0x8A100A19))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.45f to Color.Transparent,
                            1f to Color(0x6B000000)
                        )
                    )
            )
            Text(
                text = "0" + number,
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 15.dp, top = 14.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 14.dp, bottom = 14.dp)
                    .size(37.dp)
                    .clip(CircleShape)
                    .background(Violet),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play " + mode.name,
                    tint = Color(0xFF160B1D),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = mode.name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 13.dp)
        )
        Text(
            text = mode.leftFreq.toInt().toString() + " Hz \u2022 Solfeggio",
            color = Color(0xFF70737A),
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
