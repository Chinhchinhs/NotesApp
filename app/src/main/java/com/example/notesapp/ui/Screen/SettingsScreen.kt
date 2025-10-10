package com.example.notesapp.ui.screen

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
