package com.example.notesapp.ui.AuthScreen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.notesapp.util.AuthManager
import kotlinx.coroutines.launch
import com.example.notesapp.ui.NoteViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: NoteViewModel,
    onLoggedIn: (() -> Unit)? = null // optional callback for host wiring
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Đăng nhập", style = MaterialTheme.typography.headlineMedium)
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
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            Button(onClick = {
                loading = true
                message = null
                scope.launch {
                    val res = AuthManager.signInWithEmail(navController.context, email.trim(), password)
                    loading = false
                    res.onSuccess {
                        viewModel.loadUserFromPrefs() //cập nhật ngay
                        onLoggedIn?.invoke()
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }.onFailure {
                        message = it.message ?: "Đăng nhập thất bại"
                    }
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = !loading) {
                Text(if (loading) "Đang đăng nhập..." else "Đăng nhập")
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(onClick = { navController.navigate("register") }, modifier = Modifier.fillMaxWidth()) {
                Text("Đăng ký tài khoản")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("forgot") }) {
                Text("Quên mật khẩu")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = {
                // guest login
                loading = true
                message = null
                scope.launch {
                    val r = AuthManager.signInAnonymously()
                    loading = false
                    r.onSuccess {
                        onLoggedIn?.invoke()
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }.onFailure {
                        message = it.message ?: "Đăng nhập khách thất bại"
                    }
                }
            }) {
                Text("Đăng nhập khách")
            }

            message?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
