package com.example.notesapp.ui.theme

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.notesapp.util.AuthManager
import kotlinx.coroutines.launch

class authViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application.applicationContext

    private val _userName = mutableStateOf("Guest")
    val userName: State<String> = _userName

    private val _userId = mutableStateOf("guest")
    val userId: State<String> = _userId

    fun loadUser() {
        val (name, id) = AuthManager.getCurrentUser(ctx)
        _userName.value = name ?: "Guest"
        _userId.value = id ?: "guest"
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        val result = AuthManager.signInWithEmail(ctx, email, password)
        if (result.isSuccess) {
            val (name, id) = AuthManager.getCurrentUser(ctx)
            _userName.value = name ?: "User"
            _userId.value = id ?: "guest"
        }
        return result
    }

    suspend fun loginAsGuest(): String {
        val guestId = AuthManager.signInAnonymously(ctx).getOrThrow()
        _userName.value = "Guest"
        _userId.value = guestId
        return guestId
    }

     fun logout() {
         viewModelScope.launch {

             AuthManager.signOut(ctx)
             _userName.value = "Guest"
             _userId.value = "guest"
         }
    }
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

}