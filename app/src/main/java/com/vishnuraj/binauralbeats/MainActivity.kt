package com.vishnuraj.binauralbeats

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.darkColorScheme

data class Mode(
    val name: String,
    val leftFreq: Double,
    val rightFreq: Double,
    val noiseType: NoiseType,
    val noiseVolume: Double,
    val ambientResId: Int?,
    val ambientVolume: Float,
    val color: Color,
    val waveCount: Int,
    val description: String
)

sealed class Screen {
    object Home : Screen()
    object Solfeggio : Screen()
    data class Player(val mode: Mode) : Screen()
}

private val SpotifyDarkColors = darkColorScheme(
    background = Color(0xFF121212),
    surface = Color(0xFF181818),
    primary = Color(0xFF1ED760),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

class MainActivity : ComponentActivity() {
    private lateinit var audioEngine: AudioEngine
    private val authManager = AuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        var keepSplash = true
        splash.setKeepOnScreenCondition { keepSplash }
        Handler(Looper.getMainLooper()).postDelayed({ keepSplash = false }, 1200)

        audioEngine = AudioEngine(this)

        val modes = listOf(
            Mode("Sleep", 200.0, 202.0, NoiseType.PINK, 0.10, R.raw.sleep_ambient, 0.35f, Color(0xFF4A6FA5), 5, "This mode plays a very low-frequency delta-range binaural beat, layered under a steady rain soundscape. Delta waves are the pattern typically associated with deep, dreamless sleep, so beats in this range are commonly used to help ease the mind into rest. The rain layer beneath the tone is meant to mask sudden noises around you and give your mind something soft and steady to settle into. For best results, use stereo headphones or earbuds, since the binaural effect depends on each ear receiving a slightly different tone. Try dimming the lights and putting your phone aside once playback starts, rather than continuing to scroll. This is meant purely as a relaxation aid, not a substitute for proper sleep hygiene or medical treatment for insomnia."),
            Mode("Relax", 200.0, 210.0, NoiseType.BROWN, 0.08, R.raw.relax_ambient, 0.35f, Color(0xFF4A9B8E), 8, "This mode uses an alpha-range binaural beat, the frequency band most associated with a calm, relaxed, and clear-headed mental state. It is paired with a gentle water ambience to create a soothing backdrop for unwinding after a long day. Alpha activity is common when people are awake but not actively concentrating hard, such as during light meditation or quiet reflection. Many people use tones in this range during breathing exercises, stretching, or simply sitting quietly for a few minutes. As with all modes here, headphones are required for the binaural effect to work properly. Give it at least five to ten minutes of uninterrupted listening to notice any effect, rather than switching modes quickly. This is intended as a relaxation aid only, not a clinical treatment for anxiety or stress disorders."),
            Mode("Study", 200.0, 217.0, NoiseType.BROWN, 0.05, R.raw.study_ambient, 0.25f, Color(0xFFC77B3D), 14, "This mode combines a low-beta binaural beat with a light layer of birdsong ambience, aimed at supporting focused, alert work without feeling overstimulating. Beta activity is generally linked to active thinking, concentration, and engagement with a task, which is why this range is often chosen for studying or work sessions. The birdsong sits quietly in the background rather than demanding attention, unlike music with lyrics or shifting melodies that can compete with your focus. Many people find it helpful to start this mode a few minutes before beginning a study session, so the tone is already playing once they sit down to work. Keep the volume moderate rather than loud, since very loud levels can become distracting rather than helpful. Headphones are necessary for the two ears to receive the slightly different tones that create the binaural effect. This mode is a focus aid, not a replacement for breaks, sleep, or good study habits."),
            Mode("Foundation", 174.0, 174.0, NoiseType.NONE, 0.0, null, 0f, Color(0xFF6B4C93), 3, "This is a pure 174 Hz tone, played identically in both ears rather than as a binaural pair, since Solfeggio frequencies are traditionally used as single steady tones. Within the Solfeggio system, 174 Hz is described as the lowest of the set and is popularly associated with a sense of physical safety and grounding. Some sound-healing practitioners describe it as a kind of anesthetic tone, said to help the body and mind feel more settled and secure. It is worth being clear that these associations come from modern wellness and spiritual traditions rather than from peer-reviewed scientific research. There is no established clinical evidence that a specific frequency like this produces pain relief or physical safety on its own. Many people still find slow, steady tones like this genuinely calming to listen to, independent of the specific claims attached to them. As with the other modes, headphones will give you the clearest listening experience, though a pure tone like this does not rely on the binaural left-right effect the way Sleep, Relax, and Study do."),
            Mode("Healing", 285.0, 285.0, NoiseType.NONE, 0.0, null, 0f, Color(0xFF7B4C93), 4, "This is a pure 285 Hz tone, presented as a single steady frequency in both ears rather than a binaural beat. In Solfeggio-based sound healing traditions, 285 Hz is often described as supporting tissue repair and helping the body\u0027s energy field return to a healthy state after injury or illness. These are long-standing claims within alternative and energy-healing communities, not findings backed by controlled scientific studies. No mainstream medical or audiological research confirms that any specific sound frequency can directly heal tissue. That said, calm, steady tones are commonly used simply as a relaxation aid alongside rest and recovery, which is a reasonable and much more modest use case. If you are dealing with an actual injury or illness, this tone should never replace medical care or a doctor\u0027s advice. Treat this mode as a calming background sound rather than a treatment."),
            Mode("Liberation", 396.0, 396.0, NoiseType.NONE, 0.0, null, 0f, Color(0xFF8B4C93), 5, "This is a pure 396 Hz tone, played as a single steady frequency in both ears. Within the Solfeggio tradition, this tone is associated with releasing feelings of guilt and fear, and is sometimes used during meditation or journaling practices focused on emotional processing. As with the other Solfeggio tones in this app, this association is a belief from modern sound-healing culture rather than an evidence-based psychological finding. No controlled studies demonstrate that a specific frequency can directly reduce guilt or fear on its own. Emotional processing is a genuinely complex process, and a background tone is at most a small aid to a calm environment, not a substitute for therapy or emotional support from people in your life. If you find this tone personally calming while reflecting on difficult feelings, that is a reasonable way to use it. It should not be relied upon as your only coping tool for serious emotional distress."),
            Mode("Change", 417.0, 417.0, NoiseType.NONE, 0.0, null, 0f, Color(0xFF9B4C93), 6, "This is a pure 417 Hz tone, played as a single steady frequency in both ears rather than a binaural pair. Within Solfeggio sound-healing tradition, 417 Hz is associated with facilitating change and clearing away negative patterns or stuck situations in a person\u0027s life. This is a belief drawn from modern spiritual and wellness communities rather than a scientifically tested claim about how sound affects real-world circumstances. No research supports the idea that a specific frequency can directly cause change in your life or clear negative energy in any measurable sense. What a steady, calming tone can reasonably help with is creating a quiet moment to think clearly about a decision or transition you are facing. Many people find that quiet reflection, not the specific frequency itself, is what actually helps with processing change. Use this mode as a calm backdrop for reflection, not as a mechanism that acts on your circumstances by itself."),
            Mode("Miracle Tone", 528.0, 528.0, NoiseType.NONE, 0.0, null, 0f, Color(0xFFAB4C93), 9, "This is a pure 528 Hz tone, one of the most widely discussed Solfeggio frequencies, often marketed as the \u0022Miracle\u0022 or \u0022Love\u0022 frequency. Online wellness content frequently claims this frequency can repair DNA, promote healing, or increase feelings of love and connection. These are popular internet claims without support from peer-reviewed science; there is no credible research showing that any audible sound frequency can alter DNA or biologically heal the body. The frequency\u0027s popularity mostly comes from its prominent place in modern sound-healing and New Age wellness culture rather than from clinical studies. That said, plenty of people report finding this tone pleasant or calming to listen to, which is a perfectly valid reason to use it, separate from the more dramatic claims attached to it. Approach the health claims around this tone with healthy skepticism, the same way you would with any wellness trend you encounter online. As with the other Solfeggio modes, this plays as a steady tone in both ears rather than a binaural beat."),
            Mode("Connection", 639.0, 639.0, NoiseType.NONE, 0.0, null, 0f, Color(0xFFBB4C8C), 10, "This is a pure 639 Hz tone, played as a steady frequency in both ears. In the Solfeggio tradition, this tone is associated with harmonizing relationships and improving communication and connection with other people. As with the other tones in this set, this is a belief rooted in sound-healing and spiritual wellness culture, not a claim verified by controlled research. No study shows that listening to a specific frequency changes how well you get along with other people. Real relationships are built through communication, trust, and time spent together, none of which a background tone can substitute for. If you find this tone calming to listen to before a difficult conversation or while journaling about a relationship, that is a reasonable personal use, distinct from believing the tone itself changes the relationship. Treat it as a calming backdrop, not a relationship fix."),
            Mode("Awakening", 741.0, 741.0, NoiseType.NONE, 0.0, null, 0f, Color(0xFFCB4C7C), 12, "This is a pure 741 Hz tone, played as a steady frequency in both ears. Within the Solfeggio tradition, this tone is linked to self-expression, problem-solving, and what practitioners describe as spiritual awakening or expanded thinking. As with the rest of this set, this is a belief from sound-healing and wellness culture, not something demonstrated by controlled research. No study shows that a specific frequency improves problem-solving ability or triggers a spiritual experience on its own. If you find quiet, steady background sound genuinely helps you think through a problem, that benefit likely comes from having a calm environment rather than the specific frequency itself. Use this mode as a calm backdrop while working through a decision or creative block, not as a guaranteed trigger for insight. As with other Solfeggio modes here, headphones are optional since there is no left-right binaural effect involved."),
            Mode("Intuition", 852.0, 852.0, NoiseType.NONE, 0.0, null, 0f, Color(0xFFDB4C6C), 14, "This is a pure 852 Hz tone, played as a steady frequency in both ears. In Solfeggio tradition, this tone is associated with returning to a sense of spiritual order and awakening intuition, often used during meditation or quiet reflection practices. This association again comes from modern spiritual and wellness communities rather than scientific research into how sound affects intuition or spiritual states. There is no controlled evidence that any specific frequency can awaken intuition or produce a spiritual experience by itself. What is more plausible is that a quiet, steady tone can support a calm mental state during meditation, which many people already find helpful regardless of the specific numbers involved. If meditation or mindfulness practice is something you already do, this mode can simply serve as a pleasant backdrop for that time. Treat any spiritual claims attached to it as personal belief rather than established fact."),
            Mode("God Frequency", 963.0, 963.0, NoiseType.NONE, 0.0, null, 0f, Color(0xFFE8B84C), 18, "This is a pure 963 Hz tone, the highest of the Solfeggio set, often marketed as the \u0022God Frequency\u0022 or \u0022Pure Miracle Tone.\u0022 It is popularly associated with higher consciousness, connection to a divine or universal source, and the crown chakra in some spiritual traditions. As with every tone in this set, this is a belief system from modern spiritual and wellness culture, not a finding backed by scientific study. There is no research demonstrating that any specific frequency connects a listener to higher consciousness or has any measurable spiritual effect. The name itself is a marketing and cultural label rather than a scientific classification of the sound. If you hold personal spiritual or religious beliefs around sound and meditation, you are of course free to use this tone in that context, just as you would choose any other music or sound for reflection. This app presents it as one more available tone, not as a verified spiritual tool.")
        )

        setContent {
            MaterialTheme(colorScheme = SpotifyDarkColors) {
                var currentUser by remember { mutableStateOf(authManager.currentUser) }
                if (currentUser == null) {
                    LoginScreen(authManager = authManager, onAuthenticated = { currentUser = it })
                    return@MaterialTheme
                }

                var selectedTab by remember { mutableStateOf(AppTab.HOME) }
                var currentSession by remember { mutableStateOf<Mode?>(null) }
                var isPlaying by remember { mutableStateOf(false) }
                var showNowPlaying by remember { mutableStateOf(false) }
                var showSolfeggio by remember { mutableStateOf(false) }
                var favorites by remember { mutableStateOf(FavoritesStore.load(this@MainActivity)) }
                var volume by remember { mutableStateOf(1f) }

                val resolvedModes = modes.map { applyOverrides(it, this@MainActivity) }
                val studyMode = resolvedModes.first { it.name == "Study" }
                val sleepMode = resolvedModes.first { it.name == "Sleep" }
                val relaxMode = resolvedModes.first { it.name == "Relax" }
                val solfeggioTones = resolvedModes.drop(3)

                val timerState = remember { SessionTimerState() }

               fun openSession(mode: Mode) {
                    timerState.cancel()
                    audioEngine.stop()
                    audioEngine.start(mode.leftFreq, mode.rightFreq, mode.noiseType, mode.noiseVolume, mode.ambientResId, mode.ambientVolume)
                    currentSession = mode
                    isPlaying = true
                    showNowPlaying = true
                }

                fun togglePlayback() {
                    if (isPlaying) {
                        audioEngine.stop()
                        isPlaying = false
                    } else {
                        currentSession?.let { mode ->
                            audioEngine.start(mode.leftFreq, mode.rightFreq, mode.noiseType, mode.noiseVolume, mode.ambientResId, mode.ambientVolume)
                            isPlaying = true
                        }
                    }
                }

                fun selectBackground(index: Int) {
                    val session = currentSession ?: return
                    SettingsStore.setAmbientChoice(this@MainActivity, session.name, index)
                    val res = ambientResFromIndex(index)
                    val updated = session.copy(
                        ambientResId = res,
                        ambientVolume = if (res != null) (if (session.ambientVolume > 0f) session.ambientVolume else 0.3f) else 0f
                    )
                    currentSession = updated
                    if (isPlaying) {
                        audioEngine.stop()
                        audioEngine.start(updated.leftFreq, updated.rightFreq, updated.noiseType, updated.noiseVolume, updated.ambientResId, updated.ambientVolume)
                    }
                }

                fun toggleFavorite(name: String) {
                    favorites = if (favorites.contains(name)) favorites - name else favorites + name
                    FavoritesStore.save(this@MainActivity, favorites)
                }

                TimerEffects(timerState) { if (isPlaying) togglePlayback() }

               BackHandler(enabled = showNowPlaying || showSolfeggio) {
                    showNowPlaying = false
                    showSolfeggio = false
                }

                val activeSession = currentSession
                val featured = activeSession ?: studyMode

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        showNowPlaying && currentSession != null -> {
                            val session = currentSession!!
                            NowPlayingScreen(
                                timerState = timerState,
                                mode = session,
                                
                                palette = paletteFor(session.name),
                                frequencyLabel = frequencyLabelFor(session),
                                isPlaying = isPlaying,
                                isFavorite = favorites.contains(session.name),
                                onTogglePlay = { togglePlayback() },
                                onToggleFavorite = { toggleFavorite(session.name) },
                                volume = volume,
                                onVolumeChange = { volume = it; audioEngine.setMasterVolume(it) },
                                backgroundChoice = ambientIndexFromRes(session.ambientResId),
                                onBackgroundSelected = { selectBackground(it) },
                                story = storyFor(session),
                                onBack = { showNowPlaying = false }
                            )
                        }
                        showSolfeggio -> {
                            SolfeggioListScreen(
                                tones = solfeggioTones,
                                onToneClick = { mode -> openSession(mode); showSolfeggio = false },
                                onBack = { showSolfeggio = false }
                            )
                        }
                        else -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.weight(1f)) {
                                    if (selectedTab == AppTab.FAVORITES) {
                                        FavoritesScreen(
                                            sessions = resolvedModes.filter { favorites.contains(it.name) },
                                            labelFor = { frequencyLabelFor(it) },
                                            onSessionClick = { openSession(it) },
                                            onRemove = { toggleFavorite(it.name) }
                                        )
                                    } else HomeScreen(
                                        featuredLabel = if (isPlaying && activeSession != null) "NOW PLAYING" else "FEATURED SESSION",
                                        featuredTitle = featured.name,
                                        featuredMeta = frequencyMetaFor(featured),
                                        featuredDescription = featured.description.substringBefore(". ").plus("."),
                                        featuredPalette = paletteFor(featured.name),
                                        isFeaturedPlaying = isPlaying && activeSession != null,
                                        onHeroAction = { if (isPlaying && activeSession != null) togglePlayback() else openSession(featured) },
                                        gridSessions = listOf(
                                            GridSession(1, "Solfeggio Frequencies", "${solfeggioTones.size} pure tones", "https://images.unsplash.com/photo-1635776062127-d379bfcba9f8?auto=format&fit=crop&w=900&q=85", VioletPalette) { showSolfeggio = true },
                                            GridSession(2, sleepMode.name, frequencyLabelFor(sleepMode), "https://images.unsplash.com/photo-1557683304-673a23048d34?auto=format&fit=crop&w=900&q=85", BluePalette) { openSession(sleepMode) },
                                            GridSession(3, relaxMode.name, frequencyLabelFor(relaxMode), "https://images.unsplash.com/photo-1618397746666-63405ce5d015?auto=format&fit=crop&w=900&q=85", AmberPalette) { openSession(relaxMode) },
                                                            GridSession(4, studyMode.name, frequencyLabelFor(studyMode), "https://images.unsplash.com/photo-1668853853439-923e013afff1?auto=format&fit=crop&w=900&q=85", GreenPalette) { openSession(studyMode) }
                                        ),
                                        userEmail = currentUser?.email ?: "",
                                        onSignOut = {
                                            authManager.signOut()
                                            currentUser = null
                                        }
                                    )
                                }
                                if (activeSession != null) {
                                    MiniPlayerBar(
                                        title = activeSession.name,
                                        subtitle = frequencyLabelFor(activeSession),
                                        isPlaying = isPlaying,
                                        isFavorite = favorites.contains(activeSession.name),
                                        onToggleFavorite = { toggleFavorite(activeSession.name) },
                                        onTogglePlay = { togglePlayback() },
                                        onOpenNowPlaying = { showNowPlaying = true }
                                    )
                                }
                                BottomNavBar(selected = selectedTab, onSelect = { selectedTab = it })
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine.stop()
    }
}

private fun waveNameFor(beatHz: Double): String = when {
    beatHz < 4 -> "Delta"
    beatHz < 8 -> "Theta"
    beatHz < 12 -> "Alpha"
    beatHz < 30 -> "Beta"
    else -> "Gamma"
}

private fun frequencyLabelFor(mode: Mode): String {
    return if (mode.leftFreq == mode.rightFreq) {
        "${mode.leftFreq.toInt()} Hz • Solfeggio"
    } else {
        val beat = kotlin.math.abs(mode.rightFreq - mode.leftFreq)
        "${beat.toInt()} Hz • ${waveNameFor(beat)}"
    }
}

private fun frequencyMetaFor(mode: Mode): String {
    if (mode.leftFreq == mode.rightFreq) {
        return "${mode.leftFreq.toInt()} Hz \u2022 60 min \u2022 Solfeggio tone"
    }
    val beat = kotlin.math.abs(mode.rightFreq - mode.leftFreq)
    return "${beat.toInt()} Hz \u2022 60 min \u2022 ${waveNameFor(beat)} waves"
}
