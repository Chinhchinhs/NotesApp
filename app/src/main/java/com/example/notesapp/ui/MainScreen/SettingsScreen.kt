package com.example.notesapp.ui.MainScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesapp.ui.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: NoteViewModel
) {
    val darkMode by viewModel.darkMode.collectAsState()
    val layoutIsGrid by viewModel.layoutIsGrid.collectAsState()
    val fontSizeIndex by viewModel.fontSizeIndex.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Divider(Modifier.padding(vertical = 8.dp))

// Lấy dữ liệu user từ ViewModel
            val userName by viewModel.userName
            val userId by viewModel.userId

            Text(
                text = "Thông tin tài khoản",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Tên: $userName")
                    Text("ID: ${userId ?: "Chưa đăng nhập"}")

                    Spacer(Modifier.height(8.dp))

                    if (userId != null) {
                        Button(
                            onClick = {
                                viewModel.logoutUser()
                                navController.navigate("home") {
                                    popUpTo("settings") { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Đăng xuất")
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("changePassword") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Đổi mật khẩu")
                        }
                    } else {
                        Button(
                            onClick = { navController.navigate("login") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Đăng nhập / Đăng ký")
                        }
                    }
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Chế độ tối")
                Switch(checked = darkMode, onCheckedChange = { viewModel.setDarkMode(it) })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Hiển thị dạng lưới")
                Switch(checked = layoutIsGrid, onCheckedChange = { viewModel.setLayoutIsGrid(it) })
            }

            Text("Cỡ chữ:")
            Slider(
                value = fontSizeIndex.toFloat(),
                onValueChange = { viewModel.setFontSizeIndex(it.toInt()) },
                steps = 1,
                valueRange = 0f..2f
            )

        }
    }
}
