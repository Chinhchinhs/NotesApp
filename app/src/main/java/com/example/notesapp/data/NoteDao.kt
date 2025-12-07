package com.example.notesapp.data

import androidx.room.*
import com.example.notesapp.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, createdAt DESC")
    fun getAllNotes(): Flow<List<Note>>
    @Query("SELECT * FROM notes where userId=:userId")
    fun getAllNotesByUserId(userId:String): Flow<List<Note>>
    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: String): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("UPDATE notes SET isPinned = :pinned WHERE id = :noteId")
    suspend fun updatePinned(noteId: String, pinned: Boolean)
}
