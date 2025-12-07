package com.example.notesapp.data

import android.content.Context

object GuestManager {
    private const val PREF_NAME = "guest_prefs"
    private const val KEY_IS_GUEST = "is_guest"

    fun setGuestMode(context: Context, isGuest: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_GUEST, isGuest).apply()
    }

    fun isGuest(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_GUEST, true) // default to true
    }

    fun clearGuest(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}