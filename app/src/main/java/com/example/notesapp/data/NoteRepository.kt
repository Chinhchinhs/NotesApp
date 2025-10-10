package com.example.notesapp.data

import com.example.notesapp.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)
    suspend fun insert(note: Note): Long = noteDao.insert(note)
    suspend fun update(note: Note) = noteDao.update(note)
    suspend fun delete(note: Note) = noteDao.delete(note)
    suspend fun updatePinned(noteId: Int, pinned: Boolean) = noteDao.updatePinned(noteId, pinned)
}
