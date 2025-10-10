package com.example.notesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val category: String = "Chưa phân loại",
    val backgroundColor: Long = 0xFFFFFFFF,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
