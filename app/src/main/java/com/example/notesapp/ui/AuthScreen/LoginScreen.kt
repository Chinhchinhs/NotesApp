package com.example.notesapp.ui.AuthScreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesapp.util.AuthManager
import com.example.notesapp.ui.NoteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: NoteViewModel,
    onLoggedIn: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val darkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardColor = if (darkTheme) Color(0xFF1E1E1E) else Color.White

    // Animation: offset gradient theo thời gian
    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradientBrush = Brush.radialGradient(
                colors = if (darkTheme) {
                    listOf(Color(0xFF9F7BFF).copy(alpha = 0.4f), Color(0xFF121212))
                } else {
                    listOf(Color(0xFFB388FF).copy(alpha = 0.5f), Color(0xFFF5F5F5))
                },
                center = Offset(size.width / 2 + animatedOffset / 2, size.height / 2 + animatedOffset / 3),
                radius = size.maxDimension
            )
            drawRect(brush = gradientBrush)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        "Đăng nhập",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Nhập Email") },
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
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            loading = true
                            message = null
                            scope.launch {
                                val res = AuthManager.signInWithEmail(navController.context, email.trim(), password)
                                loading = false
                                res.onSuccess {
                                    viewModel.loadUserFromPrefs()
                                    onLoggedIn?.invoke()
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }.onFailure {
                                    message = it.message ?: "Đăng nhập thất bại"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading
                    ) {
                        Text(if (loading) "Đang đăng nhập..." else "Đăng nhập")
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { navController.navigate("register") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Đăng ký tài khoản")
                    }

                    Spacer(Modifier.height(8.dp))

                    TextButton(onClick = { navController.navigate("forgot") }) {
                        Text("Quên mật khẩu")
                    }

                    Spacer(Modifier.height(12.dp))

                    TextButton(onClick = {
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
    }
}
