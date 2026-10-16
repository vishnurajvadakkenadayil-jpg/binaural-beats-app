package com.vishnuraj.binauralbeats

import android.content.Context

object FavoritesStore {
    private const val PREFS = "binaural_favorites"
    private const val KEY = "names"

    fun load(context: Context): Set<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(KEY, emptySet())?.toSet() ?: emptySet()

    fun save(context: Context, names: Set<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putStringSet(KEY, names).apply()
    }
}
