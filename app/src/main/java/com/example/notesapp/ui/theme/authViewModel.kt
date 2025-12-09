package com.example.notesapp.ui.theme

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.notesapp.util.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class authViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application.applicationContext
    private val _signUpResult = MutableStateFlow<Result<Unit>?>(null)

    private val _userName = mutableStateOf<String?>(null)

    val userName: State<String?> = _userName

    private val _userId = mutableStateOf<String?>(null)
    val userId: State<String?> = _userId

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
             _userName.value = "Khách"
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

    fun signUp(email:String, password: String){
        viewModelScope.launch {
            val result = AuthManager.signUp(ctx, email, password)
            _signUpResult.value = result
        }
    }
    fun getUserId(): String{
        val (name, id) = AuthManager.getCurrentUser(ctx)
        return id.toString()
    }
    fun getUserEmail(): String{
        val (name, id) = AuthManager.getCurrentUser(ctx)
        return name.toString()
    }

}