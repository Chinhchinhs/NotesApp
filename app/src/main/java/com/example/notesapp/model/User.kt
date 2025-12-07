package com.example.notesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

data class User(
    val userId: String,
    val supaUserId:String?,
    val isGuest: Boolean
)
