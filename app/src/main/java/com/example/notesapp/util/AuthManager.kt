package com.example.notesapp.util

import android.R
import android.content.Context
import com.example.notesapp.data.GuestManager
import com.example.notesapp.data.GuestManager.setGuestMode
import com.example.notesapp.data.supaBaseClientProvider
import com.example.notesapp.data.supaSessionStorage
import com.example.notesapp.ui.MainScreen.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import java.util.UUID

object AuthManager {

    private const val PREF_NAME = "auth_prefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"
    private const val KEY_USER_ID = "user_id"

    private const val KEY_SESSION = "supa_session"
    private const val KEY_IS_GUEST = "is_guest"
    private val client get() = supaBaseClientProvider.client
    // Đăng ký tài khoản tạm thời
//    suspend fun signUp(context: Context, email: String, password: String): Result<Unit> {
//        return if (email.isNotBlank() && password.isNotBlank()) {
//            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//            val userId = kotlin.random.Random.nextInt()
//            prefs.edit()
//                .putString(KEY_EMAIL, email)
//                .putString(KEY_PASSWORD, password)
//                .putInt(KEY_USER_ID, userId)
//                .apply()
//            Result.success(Unit)
//        } else {
//            Result.failure(Exception("Vui lòng nhập đầy đủ thông tin"))
//        }
//    }

    // Đăng nhập bằng email & mật khẩu
//    suspend fun signInWithEmail(context: Context, email: String, password: String): Result<Pair<String, String>> {
//        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//        val savedEmail = prefs.getString(KEY_EMAIL, null)
//        val savedPass = prefs.getString(KEY_PASSWORD, null)
//        val userId = prefs.getString(KEY_USER_ID, null)
//
//        return if (email == savedEmail && password == savedPass && userId != null) {
//            Result.success(email to userId)
//        } else {
//            Result.failure(Exception("Sai thông tin đăng nhập"))
//        }
//    }
    suspend fun signInWithEmail(context: Context, email: String, password: String): Result<Unit> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val session= client.auth.currentSessionOrNull() ?: return Result.failure(Exception("No session available"))
            supaSessionStorage.saveSession(context,session)
            setGuestMode(context, false)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    //  Đăng nhập khách
    suspend fun signInAnonymously(context:Context): Result<String> {
        val guestId = UUID.randomUUID().toString()  // local guest ID
        GuestManager.setGuestMode(context, true)
        return Result.success(guestId)

    }

    //  Quên mật khẩu (tạm thời mock)
    suspend fun resetPassword(email: String): Result<Unit> {
        return if (email.isNotBlank()) Result.success(Unit)
        else Result.failure(Exception("Vui lòng nhập email"))
    }

    // Lấy thông tin người dùng hiện tại
//    fun getCurrentUser(context: Context): Pair<String?, String?> {
//
//        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//        return prefs.getString(KEY_EMAIL, null) to prefs.getString(KEY_USER_ID, null)
//    }
    fun getCurrentUser(context: Context): Pair<String?, String?> {
        // Try to get current Supabase user
        val user = supaBaseClientProvider.client.auth.currentUserOrNull()
        if (user != null) {
            // Return email and Supabase user ID
            return user.email to user.id
        }

        // Fallback to locally saved session (offline/guest)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val email = prefs.getString(KEY_EMAIL, null)
        val userId = prefs.getString(KEY_USER_ID, null)
        return email to userId
    }//    // Đăng xuất
//    fun signOut(context: Context) {
//        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//        prefs.edit().clear().apply()
//    }

    suspend fun signOut(context: Context) {
        GuestManager.clearGuest(context)
        supaSessionStorage.clearSession(context)
        supaBaseClientProvider.client.auth.signOut()
    }

}
