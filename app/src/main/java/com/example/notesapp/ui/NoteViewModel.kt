package com.example.notesapp.ui

import android.app.Application
import android.util.Log
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
import com.example.notesapp.data.supaBaseClientProvider
import com.example.notesapp.model.NoteDto
import com.example.notesapp.util.AuthManager
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


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
            note.synced=false
            repository.update(note.copy(category = newCategory))
        }
    }

    // --- Note CRUD ---
    fun addNote(note: Note) {
        viewModelScope.launch { `repository`.insert(note) }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            note.synced=false
            repository.update(note)

        }
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
    fun deleteNoteOffline(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(note.copy(isDeleted = true, synced = false))
        }
    }

    fun Note.toDto(): NoteDto = NoteDto(
        id = id,
        title = title,
        content = content,
        category = category,
        backgroundColor = backgroundColor,
        isPinned = isPinned,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userId = userId
    )


//     fun syncNotesToCloud(ls_note: Flow<List<Note>>,currentUserID:String) {
//
//
//        CoroutineScope(Dispatchers.IO).launch {
//
//            ls_note.collect { notesList ->
//                notesList.forEach { note ->
//                    try {
//                        // If note is locally marked deleted
//                        if (note.isDeleted) {
//                            supaBaseClientProvider.client.from("tbl_note").delete{
//                                filter {
//                                    eq("id",note.id)
//                                    eq("userId",note.userId)
//
//                                }
//
//                            }
//                            deleteNote(note)
//
//                        } else if (!note.synced) {
//                            val dto = note.toDto()
////                            val req_check= supaBaseClientProvider.client.from("tbl_note").select { filter { eq("id",dto.id) } }
////                            val remoteNotes = req_check.decodeAs<List<NoteDto>>()
////                            val remoteNote = remoteNotes.firstOrNull()
////
////                            if( remoteNote==null || dto.updatedAt > remoteNote.updatedAt) {
////                                val request =
////                                    supaBaseClientProvider.client.from("tbl_note").upsert(dto) {
////                                        onConflict = "id"
////                                    }
////                                // mark note as synced in local DB
////                                repository.markDownAsSynced(note.id,true)
////                            }
//
//                                val request =
//                                    supaBaseClientProvider.client.from("tbl_note").upsert(dto) {
//                                        onConflict = "id"
//                                    }
//                                // mark note as synced in local DB
//                                repository.markDownAsSynced(note.id,true)
//
//
//                        }
//                    } catch (e: Exception) {
//                        println(e.message)
//                    }
//                }
//            }
//
//        }
//    }
suspend fun syncNotesToCloud(
    ls_note: Flow<List<Note>>,
    currentUserID: String
) {
    withContext(Dispatchers.IO) {

        val notesList = ls_note.first()

        for (note in notesList) {
            try {
                if (note.isDeleted) {
                    supaBaseClientProvider.client
                        .from("tbl_note")
                        .delete {
                            filter {
                                eq("id", note.id)
                                eq("userId", note.userId)
                            }
                        }

                    repository.delete(note)

                } else if (!note.synced) {

                    val dto = note.toDto()

                    val remoteNotes =
                        supaBaseClientProvider.client
                            .from("tbl_note")
                            .select { filter { eq("id", dto.id) } }
                            .decodeAs<List<NoteDto>>()

                    val remoteNote = remoteNotes.firstOrNull()

                    if (remoteNote == null || dto.updatedAt > remoteNote.updatedAt) {
                        supaBaseClientProvider.client
                            .from("tbl_note")
                            .upsert(dto) {
                                onConflict = "id"
                            }

                        repository.markDownAsSynced(note.id, true)
                    }
                }

            } catch (e: Exception) {
                Log.e("SYNC", "Failed syncing note ${note.id}", e)
            }
        }
    }
}

    fun updateUserAfterLogin(userID:String) {
         viewModelScope.launch {

             repository.updateUserAfterLogin(userID)
         }
     }
    suspend fun fetchNotesFromSupabase(userId: String): List<NoteDto> {
        val result: PostgrestResult = supaBaseClientProvider.client.from("tbl_note").select(){
            filter { eq("userId",userId)}
        }
        val notes= result.decodeList<NoteDto>()
        return notes
    }
    fun NoteDto.toNote(): Note {
        return Note(
            id = id,
            userId = userId,
            title = title,
            content = content,
            category = category,
            backgroundColor = backgroundColor,
            isPinned = isPinned,
            createdAt = createdAt,
            updatedAt = updatedAt,
            synced = true,
            isDeleted = false
        )
    }


    fun mergeSupabaseNotesIntoRoom(notesFromCloud: List<NoteDto>) {
        viewModelScope.launch(Dispatchers.IO) {
            notesFromCloud.forEach { cloudNote ->
                val localNote = repository.getNoteById(cloudNote.id)

                when {
                    localNote == null -> {
                        // note doesn't exist locally then insert
                        repository.insert(cloudNote.toNote())
                    }
                    !localNote.synced -> {
                        // Local note has unsynced changes then skip cloud version
                        return@forEach
                    }
                    else -> {
                        // local note exists and is synced then update fields
                        if(cloudNote.updatedAt>localNote.updatedAt)
                        repository.update(cloudNote.toNote())
                    }
                }
            }
        }
        print(notesFromCloud)
    }
}
