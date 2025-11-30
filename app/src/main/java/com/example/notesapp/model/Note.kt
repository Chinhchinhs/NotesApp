package com.example.notesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId:Int=-1,
    val title: String = "",
    val content: String = "",
    val category: String = "Chưa phân loại",
    val backgroundColor: Long = 0xFFFFFFFF,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
