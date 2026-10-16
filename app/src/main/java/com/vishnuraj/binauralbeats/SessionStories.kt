package com.vishnuraj.binauralbeats

const val BINAURAL_NOTE = "Binaural audio is intended for relaxation and personal listening, not as medical treatment. Do not listen while driving or operating machinery."
const val TONE_NOTE = "These associations come from modern sound-meditation traditions and are not proven medical effects. This audio is intended for relaxation and personal listening. Do not listen while driving or operating machinery."

data class SessionStory(val about: String, val guidance: String?, val note: String = BINAURAL_NOTE)

fun storyFor(mode: Mode): SessionStory = when (mode.name) {
    "Study" -> SessionStory(
        about = "Study Flow layers a 40 Hz gamma beat with a soft atmospheric bed to create a consistent space for concentration. Gamma activity is often connected with attention and information processing, making this session a natural companion for reading, writing, or detailed work.",
        guidance = "Set your volume just below the level that demands attention, then begin one clearly defined task. For the binaural effect, use headphones and let the steady pulse become part of the background while you work."
    )
    "Sleep" -> SessionStory(
        about = "This sleep session uses a slow 6 Hz theta beat beneath soft ambient textures. Theta rhythms are commonly associated with the deeply relaxed, dreamlike state that appears as the mind moves from wakefulness toward sleep.",
        guidance = "Listen at a low, comfortable volume as part of your evening wind-down. Dim the room, settle into bed, and allow your breathing to slow naturally while the repeating sound gives your attention a calm place to rest."
    )
    "Relax" -> SessionStory(
        about = "The 10 Hz alpha rhythm in this session is designed for quiet, wakeful rest. Alpha waves are often linked with a relaxed state of awareness\u2014the feeling of taking a pause while remaining present and clear.",
        guidance = "Use this sound during a break, a gentle stretch, or a few minutes of mindful breathing. Release your shoulders, soften your gaze, and let the warm layers create space between you and the noise of the day."
    )
    "Foundation" -> SessionStory(
        about = "174 Hz is often described as the foundation of the expanded Solfeggio set. Its low, grounding character creates a steady listening environment for slowing down, settling the body, and beginning a quiet practice.",
        guidance = "Use this tone when you want to feel anchored before meditation, rest, or focused breathing. Keep the volume gentle and notice the weight and warmth of the lower resonance.",
        note = TONE_NOTE
    )
    "Healing" -> SessionStory(
        about = "285 Hz is commonly associated with renewal and restoration in modern Solfeggio practice. The tone has a calm, rounded quality that many listeners choose for restorative pauses and body-focused meditation.",
        guidance = "Settle into a comfortable position and slowly scan your attention from head to toe. Let the sound accompany the practice without expecting a particular sensation or result.",
        note = TONE_NOTE
    )
    "Liberation" -> SessionStory(
        about = "396 Hz is known in Solfeggio traditions as a tone of liberation. Listeners often use its deep resonance as a backdrop for releasing tension, reflecting on limiting thoughts, and creating a sense of emotional space.",
        guidance = "Breathe out a little longer than you breathe in and allow your shoulders to soften. If a distracting thought appears, acknowledge it and gently return your attention to the tone.",
        note = TONE_NOTE
    )
    "Change" -> SessionStory(
        about = "417 Hz is commonly connected with change and new beginnings. Its clear, gently energizing sound makes it a fitting companion for journaling, setting intentions, or resetting between parts of the day.",
        guidance = "Choose one thing you are ready to approach differently, then listen without forcing an answer. Let the repeating layers create a steady rhythm for reflection.",
        note = TONE_NOTE
    )
    "Miracle Tone" -> SessionStory(
        about = "528 Hz is often called the Miracle Tone and is one of the best-known Solfeggio frequencies. Its bright, resonant character is widely used for positive reflection, restorative listening, and personal rituals.",
        guidance = "Begin with a simple positive intention and breathe evenly. Notice how the sustained tone blends with the surrounding harmonies while allowing your attention to remain open and unhurried.",
        note = TONE_NOTE
    )
    "Connection" -> SessionStory(
        about = "639 Hz is commonly associated with connection, empathy, and harmonious relationships. Its warm midrange quality can support quiet reflection on communication and the people who matter to you.",
        guidance = "Bring one person or relationship to mind and listen with curiosity rather than judgment. Use the session as space to reflect on understanding, gratitude, or a conversation you would like to have.",
        note = TONE_NOTE
    )
    "Awakening" -> SessionStory(
        about = "741 Hz is often called the Awakening frequency. Its crisp, elevated tone is used in contemporary sound practices to accompany clear thinking, self-expression, and moments of creative insight.",
        guidance = "Try this session before writing, making art, or solving a problem. Sit upright, breathe naturally, and let ideas arrive without evaluating them too quickly.",
        note = TONE_NOTE
    )
    "Intuition" -> SessionStory(
        about = "852 Hz is associated with intuition and inner awareness in Solfeggio traditions. The high, luminous quality of the tone makes it well suited to contemplative listening and distraction-free meditation.",
        guidance = "Close your eyes if comfortable and observe thoughts as they pass without following them. Return to the highest layer of the sound whenever you notice your attention drifting.",
        note = TONE_NOTE
    )
    "God Frequency" -> SessionStory(
        about = "963 Hz is sometimes called the God Frequency in modern Solfeggio practice. It is the highest tone in the expanded set and is associated with transcendence, unity, and a broad sense of perspective.",
        guidance = "Listen during a quiet meditation or at the end of a longer practice. Keep the volume soft, relax your focus, and allow the brighter overtones to feel spacious rather than demanding.",
        note = TONE_NOTE
    )
    else -> SessionStory(about = mode.description, guidance = null)
}
