package com.example.notesapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.model.Note
import com.example.notesapp.util.CategoryPreferences
import com.example.notesapp.util.SettingsPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.notesapp.util.AuthManager


class NoteViewModel(
    application: Application,
    private val repository: NoteRepository
) : AndroidViewModel(application) {

    private val ctx = application.applicationContext

    // Notes stream from repository
    val notes: StateFlow<List<Note>> = repository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Categories
    private val _categories = MutableStateFlow(CategoryPreferences.getCategories(ctx))
    val categories: StateFlow<List<String>> = _categories

    // Selected category
    val selectedCategory = MutableStateFlow("Tất cả")

    // Layout (grid/list)
    private val _layoutIsGrid = MutableStateFlow(SettingsPreferences.getLayoutIsGrid(ctx))
    val layoutIsGrid: StateFlow<Boolean> = _layoutIsGrid

    // Font size index
    private val _fontSizeIndex = MutableStateFlow(SettingsPreferences.getFontSizeIndex(ctx))
    val fontSizeIndex: StateFlow<Int> = _fontSizeIndex

    // Dark mode
    private val _darkMode = MutableStateFlow(SettingsPreferences.getDarkMode(ctx))
    val darkMode: StateFlow<Boolean> = _darkMode

    // --- Category operations ---
    fun addCategory(category: String) {
        if (category.isBlank()) return
        CategoryPreferences.addCategory(ctx, category.trim())
        _categories.value = CategoryPreferences.getCategories(ctx)
    }

    fun removeCategory(category: String) {
        CategoryPreferences.removeCategory(ctx, category)
        _categories.value = CategoryPreferences.getCategories(ctx)
        if (selectedCategory.value == category) selectedCategory.value = "Tất cả"
    }

    fun selectCategory(category: String) {
        selectedCategory.value = category
    }

    // Move note to category
    fun moveNoteToCategory(note: Note, newCategory: String) {
        viewModelScope.launch {
            repository.update(note.copy(category = newCategory))
        }
    }

    // --- Note CRUD ---
    fun addNote(note: Note) {
        viewModelScope.launch { `repository`.insert(note) }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { repository.update(note) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { repository.delete(note) }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch { repository.updatePinned(note.id, !note.isPinned) }
    }

    // --- Settings persistence ---
    fun setLayoutIsGrid(isGrid: Boolean) {
        _layoutIsGrid.value = isGrid
        SettingsPreferences.setLayoutIsGrid(ctx, isGrid)
    }

    fun setFontSizeIndex(index: Int) {
        _fontSizeIndex.value = index
        SettingsPreferences.setFontSizeIndex(ctx, index)
    }

    fun setDarkMode(enabled: Boolean) {
        _darkMode.value = enabled
        SettingsPreferences.setDarkMode(ctx, enabled)
    }
    // --- User Account Management ---
    fun setUser(name: String, id: String) {
        _userName.value = name
        _userId.value = id
    }

    fun logout() {
        _userName.value = "Khách"
        _userId.value = "guest"
    }



    // User info (for login system)
    private val _userName = mutableStateOf("Khách")
    val userName: State<String> = _userName

    private val _userId = mutableStateOf("")
    val userId: State<String> = _userId

    // Auth tạm thời
    fun loadUserFromPrefs() {
        val (name, id) = AuthManager.getCurrentUser(ctx)
        if (name != null && id != null) {
            _userName.value = name
            _userId.value = id
        } else {
            _userName.value = "Khách"
            _userId.value = "guest"
        }
    }

    suspend fun logoutUser() {
        AuthManager.signOut(ctx)
        _userName.value = "Khách"
        _userId.value = "guest"
    }



}
