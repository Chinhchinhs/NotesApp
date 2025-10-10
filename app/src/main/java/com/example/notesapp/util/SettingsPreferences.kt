package com.example.notesapp.util

import android.content.Context

object SettingsPreferences {
    private const val PREF_NAME = "note_settings_prefs"
    private const val KEY_FONT_SIZE = "font_size"
    private const val KEY_IS_GRID = "is_grid"
    private const val KEY_SORT_NEWEST_FIRST = "sort_newest_first"
    private const val KEY_DARK_MODE = "dark_mode"

    fun getFontSizeIndex(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_FONT_SIZE, 1) // 0=small, 1=medium, 2=large
    }

    fun setFontSizeIndex(context: Context, index: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_FONT_SIZE, index).apply()
    }

    fun getLayoutIsGrid(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_GRID, false)
    }

    fun setLayoutIsGrid(context: Context, isGrid: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_GRID, isGrid).apply()
    }

    fun getSortNewestFirst(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SORT_NEWEST_FIRST, true)
    }

    fun setSortNewestFirst(context: Context, newestFirst: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SORT_NEWEST_FIRST, newestFirst).apply()
    }

    fun getDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }
}
