package com.example.notesapp.ui.MainScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.notesapp.ui.NoteViewModel
import com.example.notesapp.model.Note
import com.example.notesapp.util.ExportUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Int?,
    viewModel: NoteViewModel,
    onBack: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val note = notes.find { it.id == noteId } ?: run {
        // if note not found, just go back
        onBack(); return
    }

    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa ghi chú") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        ExportUtil.shareNoteAsText(context, note.copy(title = title, content = content))
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Chia sẻ")
                    }

                    TextButton(onClick = {
                        viewModel.updateNote(note.copy(title = title, content = content))
                        onBack()
                    }) {
                        Text("Lưu")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tiêu đề") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Nội dung") }, modifier = Modifier.fillMaxWidth().height(200.dp))
        }
    }
}
