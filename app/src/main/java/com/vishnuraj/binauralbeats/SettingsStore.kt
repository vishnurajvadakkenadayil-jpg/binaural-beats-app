package com.vishnuraj.binauralbeats

import android.content.Context

object SettingsStore {
    private const val PREFS_NAME = "binaural_settings"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getFrequency(context: Context, modeName: String, default: Double): Double {
        return prefs(context).getFloat("freq_$modeName", default.toFloat()).toDouble()
    }

    fun setFrequency(context: Context, modeName: String, value: Double) {
        prefs(context).edit().putFloat("freq_$modeName", value.toFloat()).apply()
    }

    fun getAmbientChoice(context: Context, modeName: String, default: Int): Int {
        return prefs(context).getInt("ambient_$modeName", default)
    }

    fun setAmbientChoice(context: Context, modeName: String, value: Int) {
        prefs(context).edit().putInt("ambient_$modeName", value).apply()
    }
}

fun ambientResFromIndex(index: Int): Int? = when (index) {
    1 -> R.raw.sleep_ambient
    2 -> R.raw.relax_ambient
    3 -> R.raw.study_ambient
    else -> null
}

fun ambientIndexFromRes(resId: Int?): Int = when (resId) {
    R.raw.sleep_ambient -> 1
    R.raw.relax_ambient -> 2
    R.raw.study_ambient -> 3
    else -> 0
}

fun applyOverrides(mode: Mode, context: Context): Mode {
    val isPureTone = mode.leftFreq == mode.rightFreq
    val defaultAmbientIndex = ambientIndexFromRes(mode.ambientResId)
    val ambientIndex = SettingsStore.getAmbientChoice(context, mode.name, defaultAmbientIndex)
    val ambientRes = ambientResFromIndex(ambientIndex)
    val ambientVol = if (ambientRes != null) (if (mode.ambientVolume > 0f) mode.ambientVolume else 0.3f) else 0f

    return if (isPureTone) {
        val freq = SettingsStore.getFrequency(context, mode.name, mode.leftFreq)
        mode.copy(leftFreq = freq, rightFreq = freq, ambientResId = ambientRes, ambientVolume = ambientVol)
    } else {
        val defaultBeat = mode.rightFreq - mode.leftFreq
        val beat = SettingsStore.getFrequency(context, mode.name, defaultBeat)
        mode.copy(rightFreq = mode.leftFreq + beat, ambientResId = ambientRes, ambientVolume = ambientVol)
    }
}
