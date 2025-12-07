package com.example.notesapp.ui.AuthScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesapp.util.AuthManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Đăng ký tài khoản", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email hoặc số điện thoại") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(mask = '\u25CF')
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
//                    loading = true
//                    message = null
//                    scope.launch {
//                        val res = AuthManager.signUp(navController.context, email.trim(), password)
//                        loading = false
//                        res.onSuccess {
//                            navController.navigate("login") {
//                                popUpTo("register") { inclusive = true }
//                            }
//
//                        }.onFailure {
//                            message = it.message ?: "Đăng ký thất bại"
//                        }
//                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text(if (loading) "Đang đăng ký..." else "Tạo tài khoản")
            }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Đã có tài khoản? Đăng nhập")
            }

            message?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
