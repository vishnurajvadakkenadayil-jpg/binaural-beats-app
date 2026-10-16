package com.vishnuraj.binauralbeats

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(modes: List<Mode>, onBack: () -> Unit) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = onBack, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(modes) { mode ->
                ModeSettingsCard(mode = mode, context = context)
            }
        }
    }
}

@Composable
fun ModeSettingsCard(mode: Mode, context: Context) {
    val isPureTone = mode.leftFreq == mode.rightFreq
    val defaultFreq = if (isPureTone) mode.leftFreq else (mode.rightFreq - mode.leftFreq)
    var freq by remember { mutableStateOf(SettingsStore.getFrequency(context, mode.name, defaultFreq).toFloat()) }
    var ambientIndex by remember { mutableStateOf(SettingsStore.getAmbientChoice(context, mode.name, ambientIndexFromRes(mode.ambientResId))) }
    var expanded by remember { mutableStateOf(false) }

    val range = if (isPureTone) (mode.leftFreq.toFloat() - 50f)..(mode.leftFreq.toFloat() + 50f) else 1f..30f
    val label = if (isPureTone) "Frequency" else "Beat Frequency"
    val ambientLabels = listOf("None", "Rain", "River", "Birds")

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(mode.name, style = MaterialTheme.typography.titleMedium)

            Text("$label: ${"%.1f".format(freq)} Hz")
            Slider(
                value = freq,
                onValueChange = {
                    freq = it
                    SettingsStore.setFrequency(context, mode.name, it.toDouble())
                },
                valueRange = range
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Background: ${ambientLabels[ambientIndex]}")
                Box {
                    TextButton(onClick = { expanded = true }) {
                        Text("Change")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        ambientLabels.forEachIndexed { index, optionLabel ->
                            DropdownMenuItem(
                                text = { Text(optionLabel) },
                                onClick = {
                                    ambientIndex = index
                                    SettingsStore.setAmbientChoice(context, mode.name, index)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
