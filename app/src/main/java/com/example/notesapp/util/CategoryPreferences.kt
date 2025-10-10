package com.example.notesapp.util

import android.content.Context

object CategoryPreferences {
    private const val PREF_NAME = "note_categories_prefs"
    private const val KEY_CATEGORIES = "categories_set"

    private val defaultCategories = linkedSetOf("Tất cả", "Chưa phân loại", "Học tập", "Công việc", "Cá nhân")

    fun getCategories(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_CATEGORIES, null)
        return if (set == null) {
            prefs.edit().putStringSet(KEY_CATEGORIES, defaultCategories).apply()
            defaultCategories.toList()
        } else {
            // keep order: ensure special items first, others after
            val list = set.toList().toMutableList()
            val result = mutableListOf<String>()
            if (!list.contains("Tất cả")) result.add("Tất cả")
            if (!list.contains("Chưa phân loại")) result.add("Chưa phân loại")
            for (c in list) if (c != "Tất cả" && c != "Chưa phân loại") result.add(c)
            result
        }
    }

    fun addCategory(context: Context, newCategory: String) {
        if (newCategory.isBlank()) return
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_CATEGORIES, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        set.add(newCategory.trim())
        prefs.edit().putStringSet(KEY_CATEGORIES, set).apply()
    }

    fun removeCategory(context: Context, category: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_CATEGORIES, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        set.remove(category)
        prefs.edit().putStringSet(KEY_CATEGORIES, set).apply()
    }
}
