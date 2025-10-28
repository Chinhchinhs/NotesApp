package com.example.notesapp.util

import android.content.Context
import java.util.UUID

object AuthManager {

    private const val PREF_NAME = "auth_prefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"
    private const val KEY_USER_ID = "user_id"

    // Đăng ký tài khoản tạm thời
    suspend fun signUp(context: Context, email: String, password: String): Result<Unit> {
        return if (email.isNotBlank() && password.isNotBlank()) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val userId = UUID.randomUUID().toString()
            prefs.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putString(KEY_USER_ID, userId)
                .apply()
            Result.success(Unit)
        } else {
            Result.failure(Exception("Vui lòng nhập đầy đủ thông tin"))
        }
    }

    // Đăng nhập bằng email & mật khẩu
    suspend fun signInWithEmail(context: Context, email: String, password: String): Result<Pair<String, String>> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedEmail = prefs.getString(KEY_EMAIL, null)
        val savedPass = prefs.getString(KEY_PASSWORD, null)
        val userId = prefs.getString(KEY_USER_ID, null)

        return if (email == savedEmail && password == savedPass && userId != null) {
            Result.success(email to userId)
        } else {
            Result.failure(Exception("Sai thông tin đăng nhập"))
        }
    }

    //  Đăng nhập khách
    suspend fun signInAnonymously(): Result<Pair<String, String>> {
        return Result.success("Khách" to UUID.randomUUID().toString())
    }

    //  Quên mật khẩu (tạm thời mock)
    suspend fun resetPassword(email: String): Result<Unit> {
        return if (email.isNotBlank()) Result.success(Unit)
        else Result.failure(Exception("Vui lòng nhập email"))
    }

    // Lấy thông tin người dùng hiện tại
    fun getCurrentUser(context: Context): Pair<String?, String?> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_EMAIL, null) to prefs.getString(KEY_USER_ID, null)
    }

    // Đăng xuất
    fun signOut(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
