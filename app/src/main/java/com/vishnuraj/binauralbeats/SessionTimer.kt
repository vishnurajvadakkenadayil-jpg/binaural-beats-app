package com.vishnuraj.binauralbeats

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** Plays three short beeps on the alarm audio stream. */
object AlarmBeeper {
    private var track: AudioTrack? = null

    fun playOnce() {
        stop()
        val sampleRate = 22050
        val beep = (0.22 * sampleRate).toInt()
        val gap = (0.10 * sampleRate).toInt()
        val attack = (0.02 * sampleRate).toInt()
        val release = (0.06 * sampleRate).toInt()
        val total = beep * 3 + gap * 2
        val data = ShortArray(total)
        for (b in 0 until 3) {
            val start = b * (beep + gap)
            for (i in 0 until beep) {
                val envelope = minOf(1.0, i.toDouble() / attack) * minOf(1.0, (beep - i).toDouble() / release)
                val sample = Math.sin(2.0 * Math.PI * 880.0 * i / sampleRate) * envelope * 0.5
                data[start + i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
        }
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val format = AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        val newTrack = AudioTrack(
            attributes, format, total * 2,
            AudioTrack.MODE_STATIC, AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        newTrack.write(data, 0, total)
        newTrack.play()
        track = newTrack
    }

    fun stop() {
        track?.let {
            try {
                it.stop()
            } catch (e: Exception) {
            }
            it.release()
        }
        track = null
    }
}

class SessionTimerState {
    var hours by mutableStateOf(0)
    var minutes by mutableStateOf(30)
    var endAt by mutableStateOf<Long?>(null)
    var durationSec by mutableStateOf(0)
    var remainingSec by mutableStateOf(0)
    var finished by mutableStateOf(false)
    var ringing by mutableStateOf(false)

    fun totalMinutes(): Int = hours * 60 + minutes

    fun start() {
        val seconds = totalMinutes() * 60
        if (seconds <= 0) return
        AlarmBeeper.stop()
        finished = false
        ringing = false
        durationSec = seconds
        remainingSec = seconds
        endAt = System.currentTimeMillis() + seconds * 1000L
    }

    fun cancel() {
        AlarmBeeper.stop()
        endAt = null
        remainingSec = 0
        finished = false
        ringing = false
    }

    fun stopAlarm() {
        AlarmBeeper.stop()
        ringing = false
    }
}

/** Runs the countdown and the alarm. Call once, high up in the screen tree, so it keeps running. */
@Composable
fun TimerEffects(state: SessionTimerState, onFinished: () -> Unit) {
    val finishedCallback by rememberUpdatedState(onFinished)

    LaunchedEffect(state.endAt) {
        val end = state.endAt ?: return@LaunchedEffect
        while (true) {
            val left = Math.max(0, Math.ceil((end - System.currentTimeMillis()) / 1000.0).toInt())
            state.remainingSec = left
            if (left == 0) break
            delay(250)
        }
        state.finished = true
        state.ringing = true
        finishedCallback()
        state.endAt = null
    }

    LaunchedEffect(state.ringing) {
        if (!state.ringing) return@LaunchedEffect
        repeat(24) {
            AlarmBeeper.playOnce()
            delay(2500)
        }
        state.stopAlarm()
    }

    DisposableEffect(Unit) {
        onDispose { AlarmBeeper.stop() }
    }
}

private fun clock(seconds: Int): String =
    String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60)

@Composable
fun SessionTimerCard(palette: SessionPalette, state: SessionTimerState) {
    val running = state.endAt != null
    val shape = RoundedCornerShape(12.dp)
    val statusText = when {
        running -> clock(state.remainingSec) + " remaining"
        state.ringing -> "Time is up \u2014 alarm ringing"
        state.finished -> "Time is up"
        else -> "Enter an alarm duration"
    }
    Column(
        modifier = Modifier
            .padding(top = 22.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(if (running) palette.accent.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.035f))
            .border(
                1.dp,
                if (running) palette.accent.copy(alpha = 0.24f) else Color.White.copy(alpha = 0.09f),
                shape
            )
            .padding(horizontal = 14.dp, vertical = 13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SESSION TIMER",
                    color = Color(0xFFE5E6E3),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = statusText,
                    color = if (running) palette.accent else Color(0xFF707578),
                    fontSize = 9.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            when {
                running -> TimerButton("Cancel", palette, outlined = true) { state.cancel() }
                state.ringing -> TimerButton("Stop alarm", palette) { state.stopAlarm() }
                else -> TimerButton(
                    if (state.finished) "Set again" else "Set timer",
                    palette,
                    enabled = state.totalMinutes() > 0
                ) { state.start() }
            }
        }

        if (!running) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeBox("Hours", state.hours, 8, palette) { h ->
                    state.hours = h
                    if (h == 8) state.minutes = 0
                }
                Text(
                    text = ":",
                    color = Color(0xFF777B7E),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 7.dp)
                )
                TimeBox("Minutes", state.minutes, if (state.hours == 8) 0 else 59, palette) { m ->
                    state.minutes = m
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Up to 8 hours", color = Color(0xFF5F6365), fontSize = 8.sp)
            }
        } else {
            val elapsed = Math.max(0, state.durationSec - state.remainingSec)
            val fraction = if (state.durationSec > 0) (elapsed.toFloat() / state.durationSec).coerceIn(0f, 1f) else 0f
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
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
                    Text(text = clock(elapsed), color = Color(0xFF5F6365), fontSize = 8.sp)
                    Text(text = clock(state.durationSec), color = Color(0xFF5F6365), fontSize = 8.sp)
                }
            }
        }
    }
}

@Composable
private fun TimerButton(
    label: String,
    palette: SessionPalette,
    outlined: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(7.dp)
    Box(
        modifier = Modifier
            .height(31.dp)
            .clip(shape)
            .background(
                if (outlined) Color.White.copy(alpha = 0.04f)
                else palette.accent.copy(alpha = if (enabled) 1f else 0.4f)
            )
            .then(if (outlined) Modifier.border(1.dp, Color.White.copy(alpha = 0.11f), shape) else Modifier)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (outlined) Color(0xFFB6B9B8) else Color(0xFF0C100D),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TimeBox(
    label: String,
    value: Int,
    max: Int,
    palette: SessionPalette,
    onChange: (Int) -> Unit
) {
    var text by remember { mutableStateOf(value.toString()) }
    LaunchedEffect(value) {
        if ((text.toIntOrNull() ?: 0) != value) text = value.toString()
    }
    val shape = RoundedCornerShape(7.dp)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label.uppercase(),
            color = Color(0xFF707578),
            fontSize = 8.sp,
            letterSpacing = 0.6.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .size(width = 46.dp, height = 31.dp)
                .clip(shape)
                .background(Color(0xFF171A1C))
                .border(1.dp, Color.White.copy(alpha = 0.11f), shape),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = text,
                onValueChange = { typed ->
                    val digits = typed.filter { it.isDigit() }.take(2)
                    val number = Math.min(max, digits.toIntOrNull() ?: 0)
                    text = if (digits.isEmpty()) "" else number.toString()
                    onChange(number)
                },
                singleLine = true,
                textStyle = TextStyle(
                    color = Color(0xFFE5E6E3),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(palette.accent),
                modifier = Modifier.width(40.dp)
            )
        }
    }
}
