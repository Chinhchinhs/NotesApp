package com.example.notesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

data class User(
    val userId: Int,
    val emailAddress:String,
    val name: String,
)
