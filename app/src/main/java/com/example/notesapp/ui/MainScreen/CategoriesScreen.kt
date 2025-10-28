package com.example.notesapp.ui.MainScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.notesapp.ui.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: NoteViewModel,
    onBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var confirmRemove by remember { mutableStateOf<String?>(null) }

    // Ensure fixed first two c ategories in display: "Tất cả", "Chưa phân loại"
    val listForDisplay = listOf("Tất cả", "Chưa phân loại") + categories.filter { it != "Tất cả" && it != "Chưa phân loại" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phân loại") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(8.dp)
        ) {
            itemsIndexed(listForDisplay) { idx, cat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectCategory(cat) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = cat, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge,
                        color = if (cat == selectedCategory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    if (cat != "Tất cả" && cat != "Chưa phân loại") {
                        IconButton(onClick = { confirmRemove = cat }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Xóa")
                        }
                    }
                }
                Divider()
            }

            // Always keep the Add button as the last item
            item {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Thêm")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Thêm phân loại")
                }
            }
        }
    }

    if (showAddDialog) {
        var text by remember { mutableStateOf(TextFieldValue()) }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Thêm phân loại") },
            text = {
                TextField(value = text, onValueChange = { text = it }, placeholder = { Text("Tên phân loại") })
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addCategory(text.text)
                    showAddDialog = false
                }) { Text("Thêm") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Hủy") }
            }
        )
    }

    confirmRemove?.let { toRemove ->
        AlertDialog(
            onDismissRequest = { confirmRemove = null },
            title = { Text("Xóa phân loại") },
            text = { Text("Xóa phân loại \"$toRemove\"? Những ghi chú thuộc phân loại này sẽ về 'Chưa phân loại'.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeCategory(toRemove)
                    confirmRemove = null
                }) { Text("Xóa", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmRemove = null }) { Text("Hủy") }
            }
        )
    }
}
