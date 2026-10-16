package com.vishnuraj.binauralbeats

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

enum class NoiseType {
    NONE, PINK, BROWN
}

class AudioEngine(private val context: Context) {
    private val sampleRate = 44100
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var playbackThread: Thread? = null
    private var mediaPlayer: MediaPlayer? = null
    private var masterVolume = 1f
    private var ambientBaseVolume = 0.3f

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        audioTrack?.setVolume(masterVolume)
        mediaPlayer?.setVolume(ambientBaseVolume * masterVolume, ambientBaseVolume * masterVolume)
    }

    fun start(
        leftFreq: Double,
        rightFreq: Double,
        noiseType: NoiseType = NoiseType.NONE,
        noiseVolume: Double = 0.15,
        ambientResId: Int? = null,
        ambientVolume: Float = 0.3f
    ) {
        if (isPlaying) return
        isPlaying = true

        ambientResId?.let { resId ->
            mediaPlayer = MediaPlayer.create(context, resId).apply {
                isLooping = true
                ambientBaseVolume = ambientVolume
                setVolume(ambientVolume * masterVolume, ambientVolume * masterVolume)
                start()
            }
        }

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = minBufferSize * 2

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack = track
        track.setVolume(masterVolume)
        track.play()

        playbackThread = thread {
            var leftPhase = 0.0
            var rightPhase = 0.0
            val leftInc = 2.0 * PI * leftFreq / sampleRate
            val rightInc = 2.0 * PI * rightFreq / sampleRate
            val chunkFrames = 1024
            val buffer = ShortArray(chunkFrames * 2)

            var b0 = 0.0
            var b1 = 0.0
            var b2 = 0.0
            var b3 = 0.0
            var b4 = 0.0
            var b5 = 0.0
            var b6 = 0.0
            var brownLast = 0.0

            while (isPlaying) {
                for (i in 0 until chunkFrames) {
                    val toneL = sin(leftPhase) * 0.5
                    val toneR = sin(rightPhase) * 0.5

                    var noise = 0.0
                    if (noiseType == NoiseType.PINK) {
                        val white = Random.nextDouble(-1.0, 1.0)
                        b0 = 0.99886 * b0 + white * 0.0555179
                        b1 = 0.99332 * b1 + white * 0.0750759
                        b2 = 0.96900 * b2 + white * 0.1538520
                        b3 = 0.86650 * b3 + white * 0.3104856
                        b4 = 0.55000 * b4 + white * 0.5329522
                        b5 = -0.7616 * b5 - white * 0.0168980
                        val pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
                        b6 = white * 0.115926
                        noise = pink * 0.11
                    } else if (noiseType == NoiseType.BROWN) {
                        val white = Random.nextDouble(-1.0, 1.0)
                        brownLast = (brownLast + (0.02 * white)) / 1.02
                        noise = brownLast * 3.5
                    }

                    val mixedL = toneL + noise * noiseVolume
                    val mixedR = toneR + noise * noiseVolume

                    val l = (mixedL.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
                    val r = (mixedR.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()

                    buffer[i * 2] = l
                    buffer[i * 2 + 1] = r

                    leftPhase += leftInc
                    rightPhase += rightInc
                    if (leftPhase > 2.0 * PI) leftPhase -= 2.0 * PI
                    if (rightPhase > 2.0 * PI) rightPhase -= 2.0 * PI
                }
                track.write(buffer, 0, buffer.size)
            }
        }
    }

    fun stop() {
        isPlaying = false
        playbackThread?.join(500)
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
