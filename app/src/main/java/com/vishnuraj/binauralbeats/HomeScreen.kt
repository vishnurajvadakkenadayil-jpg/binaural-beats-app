package com.vishnuraj.binauralbeats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class GridSession(
    val number: Int,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val palette: SessionPalette,
    val onClick: () -> Unit
)

private const val HeroImageUrl =
    "https://images.unsplash.com/photo-1668853853439-923e013afff1?auto=format&fit=crop&w=1600&q=75"

@Composable
fun HomeScreen(
    featuredLabel: String,
    featuredTitle: String,
    featuredMeta: String,
    featuredDescription: String,
    featuredPalette: SessionPalette,
    isFeaturedPlaying: Boolean,
    onHeroAction: () -> Unit,
    gridSessions: List<GridSession>,
    userEmail: String,
    onSignOut: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101113)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(top = 18.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(41.dp))
                BrandMark()
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color.White.copy(alpha = 0.03f))
                            .clickable { menuExpanded = true }
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(31.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25272A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Account",
                                tint = Color(0xFFD6D7D3)
                            )
                        }
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(userEmail.ifBlank { "Signed in" }) },
                            onClick = { menuExpanded = false }
                        )
                        DropdownMenuItem(text = { Text("Sign out") }, onClick = {
                            menuExpanded = false
                            onSignOut()
                        })
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(RoundedCornerShape(23.dp))
            ) {
                AsyncImage(
                    model = HeroImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFC0F1A1D),
                                    Color(0xED101F20),
                                    Color(0xEB1D1F25)
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                ) {
                    HeroVisual(palette = featuredPalette, sizeDp = 190)
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(26.dp)
                ) {
                    Text(
                        text = featuredLabel,
                        color = featuredPalette.accent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = featuredTitle,
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = featuredDescription,
                        color = Color(0xFFAAADAE),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 8.dp, end = 30.dp)
                    )
                    Text(
                        text = featuredMeta,
                        color = Color(0xFF777C7D),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(featuredPalette.accent)
                            .clickable { onHeroAction() }
                            .padding(start = 6.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(31.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF101411)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFeaturedPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = featuredPalette.accent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (isFeaturedPlaying) "Pause session" else "Start listening",
                            color = Color(0xFF111313),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(top = 30.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "MADE FOR YOUR MIND",
                        color = Color(0xFF87898E),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Find your frequency",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .height(((gridSessions.size / 2 + gridSessions.size % 2) * 230).dp),
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(gridSessions) { session ->
                    SessionCard(
                        number = session.number,
                        title = session.title,
                        subtitle = session.subtitle,
                        imageUrl = session.imageUrl,
                        palette = session.palette,
                        onClick = session.onClick
                    )
                }
            }
        }
    }
}

private fun greetingForNow(): String {
    val hour = java.time.LocalTime.now().hour
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}
