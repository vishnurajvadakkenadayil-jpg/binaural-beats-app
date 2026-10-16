package com.vishnuraj.binauralbeats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class AppTab(val label: String) {
    HOME("Home"),
    FAVORITES("Favourites")
}

@Composable
fun BottomNavBar(selected: AppTab, onSelect: (AppTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF0A0B0C))
    ) {
        NavTabItem(AppTab.HOME, Icons.Filled.School, selected == AppTab.HOME, onSelect, Modifier.weight(1f))
        NavTabItem(AppTab.FAVORITES, Icons.Filled.Favorite, selected == AppTab.FAVORITES, onSelect, Modifier.weight(1f))
    }
}

@Composable
private fun NavTabItem(
    tab: AppTab,
    icon: ImageVector,
    isActive: Boolean,
    onSelect: (AppTab) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { onSelect(tab) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = tab.label,
                tint = if (isActive) Color(0xFFC8FF58) else Color(0xFF85878B),
                modifier = Modifier.size(22.dp)
            )
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(Color(0xFFC8FF58), CircleShape)
                )
            }
        }
    }
}
