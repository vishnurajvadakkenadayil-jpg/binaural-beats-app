package com.vishnuraj.binauralbeats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PlayerScreen(
    mode: Mode,
    audioEngine: AudioEngine,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentMode by remember(mode) { mutableStateOf(mode) }
    var showSettings by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            IconButton(onClick = { showSettings = true }) {
                Icon(Icons.Filled.Settings, contentDescription = "Mode Settings")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(currentMode.name, style = MaterialTheme.typography.headlineMedium)

            Box(modifier = Modifier.size(280.dp).padding(24.dp)) {
                FrequencyOrb(
                    modifier = Modifier.fillMaxSize(),
                    color = currentMode.color,
                    waveCount = currentMode.waveCount,
                    waviness = if (isPlaying) 18f else 6f
                )
            }

            Button(onClick = {
                if (isPlaying) {
                    audioEngine.stop()
                    isPlaying = false
                } else {
                    audioEngine.start(
                        currentMode.leftFreq,
                        currentMode.rightFreq,
                        currentMode.noiseType,
                        currentMode.noiseVolume,
                        currentMode.ambientResId,
                        currentMode.ambientVolume
                    )
                    isPlaying = true
                }
            }) {
                Text(if (isPlaying) "Stop" else "Start")
            }

            Text(
                currentMode.description,
                modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)
            )
        }
    }

    if (showSettings) {
        Dialog(onDismissRequest = {
            showSettings = false
            if (isPlaying) {
                audioEngine.stop()
                isPlaying = false
            }
            currentMode = applyOverrides(mode, context)
        }) {
            Surface {
                ModeSettingsCard(mode = mode, context = context)
            }
        }
    }
}
