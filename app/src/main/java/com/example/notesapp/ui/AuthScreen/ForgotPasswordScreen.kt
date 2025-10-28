package com.example.notesapp.ui.AuthScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesapp.util.AuthManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Khôi phục mật khẩu", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email hoặc số điện thoại") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                loading = true
                message = null
                scope.launch {
                    val res = AuthManager.resetPassword(email.trim())
                    loading = false
                    res.onSuccess {
                        message = "Đã gửi liên kết khôi phục tới $email"
                    }.onFailure {
                        message = it.message ?: "Khôi phục thất bại"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text(if (loading) "Đang gửi..." else "Gửi yêu cầu")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Quay lại đăng nhập")
        }

        message?.let {
            Spacer(Modifier.height(12.dp))
            Text(it)
        }
    }
}
