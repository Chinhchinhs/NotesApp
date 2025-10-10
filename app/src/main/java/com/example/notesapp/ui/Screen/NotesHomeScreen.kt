package com.example.notesapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notesapp.model.Note
import com.example.notesapp.ui.NoteViewModel
import com.example.notesapp.util.ExportUtil
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.Label

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesHomeScreen(
    navController: NavController,
    viewModel: NoteViewModel
) {
    val context = LocalContext.current

    // states from ViewModel
    val notes by viewModel.notes.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val fontSizeIndex by viewModel.fontSizeIndex.collectAsState()
    val layoutIsGrid by viewModel.layoutIsGrid.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()

    // UI local states
    var contextMenuNote by remember { mutableStateOf<Note?>(null) }
    var showDeleteConfirmFor by remember { mutableStateOf<Note?>(null) }
    var showMoveDialogFor by remember { mutableStateOf<Note?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    // map font index to sizes (safe)
    val fontSizes = listOf(14.sp, 16.sp, 18.sp)
    val fontSize = fontSizes.getOrElse(fontSizeIndex.coerceIn(0, fontSizes.lastIndex)) { 16.sp }

    // compute displayed list:
    val pinned = notes.filter { it.isPinned }
    val others = notes.filter { !it.isPinned && (
            selectedCategory == "Tất cả"
                    || (selectedCategory == "Chưa phân loại" && it.category.isBlank())
                    || it.category == selectedCategory
            ) }
    val displayed = (pinned + others).distinctBy { it.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ghi chú") },

                actions = {
                    IconButton(onClick = { navController.navigate("categories") }) {
                        Icon(
                            imageVector = Icons.Default.Label, //  biểu tượng nhãn/phân loại
                            contentDescription = "Phân loại"
                        )
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Filled.Add, contentDescription = "Cài đặt")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addNote") }) {
                Icon(Icons.Filled.Add, contentDescription = "Thêm")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- CATEGORY BAR (LazyRow) ---
            // fixed first two items then other categories, then add button at end
            val otherCats = categories.filter { it != "Tất cả" && it != "Chưa phân loại" }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        text = "Tất cả",
                        isSelected = selectedCategory == "Tất cả",
                        onClick = { viewModel.selectCategory("Tất cả") }
                    )
                }
                item {
                    CategoryChip(
                        text = "Chưa phân loại",
                        isSelected = selectedCategory == "Chưa phân loại",
                        onClick = { viewModel.selectCategory("Chưa phân loại") }
                    )
                }

                items(otherCats) { cat ->
                    CategoryChip(
                        text = cat,
                        isSelected = selectedCategory == cat,
                        onClick = { viewModel.selectCategory(cat) }
                    )
                }


            }

            // Add category dialog (simple input)
            if (showAddCategoryDialog) {
                AlertDialog(
                    onDismissRequest = { showAddCategoryDialog = false },
                    title = { Text("Thêm phân loại") },
                    text = {
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            label = { Text("Tên phân loại") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val name = newCategoryName.trim()
                            if (name.isNotEmpty()) {
                                viewModel.addCategory(name)
                                newCategoryName = ""
                                showAddCategoryDialog = false
                            }
                        }) { Text("Thêm") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddCategoryDialog = false }) { Text("Hủy") }
                    }
                )
            }

            // --- NOTES (grid or list) ---
            if (layoutIsGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayed) { note ->
                        NoteCardItem(
                            note = note,
                            fontSize = fontSize,
                            darkMode = darkMode,
                            onClick = { navController.navigate("editNote/${note.id}") },
                            onLongClick = { contextMenuNote = note }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(displayed) { note ->
                        NoteCardItem(
                            note = note,
                            fontSize = fontSize,
                            darkMode = darkMode,
                            onClick = { navController.navigate("editNote/${note.id}") },
                            onLongClick = { contextMenuNote = note }
                        )
                    }
                }
            }
        }
    }

    // --- CONTEXT MENU (long-press) ---
    contextMenuNote?.let { note ->
        AlertDialog(
            onDismissRequest = { contextMenuNote = null },
            title = { Text("Tùy chọn") },
            text = { Text("Bạn muốn làm gì với ghi chú này?") },
            confirmButton = {
                Column {
                    TextButton(onClick = {
                        viewModel.togglePin(note)
                        contextMenuNote = null
                    }) {
                        Icon(Icons.Filled.PushPin, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (note.isPinned) "Bỏ ghim" else "Ghim")
                    }

                    TextButton(onClick = {
                        showMoveDialogFor = note
                        contextMenuNote = null
                    }) { Text("Chuyển tới") }

                    TextButton(onClick = {
                        ExportUtil.shareNoteAsText(context, note)
                        contextMenuNote = null
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Xuất / Chia sẻ")
                    }

                    TextButton(onClick = {
                        showDeleteConfirmFor = note
                        contextMenuNote = null
                    }) {
                        Text("Xóa", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { contextMenuNote = null }) { Text("Đóng") }
            }
        )
    }

    // --- MOVE-TO CATEGORY DIALOG ---
    showMoveDialogFor?.let { note ->
        var chosen by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showMoveDialogFor = null },
            title = { Text("Chuyển tới phân loại") },
            text = {
                Column {
                    val list = listOf("Tất cả", "Chưa phân loại") + categories
                    list.forEach { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { chosen = cat },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (chosen == cat), onClick = { chosen = cat })
                            Spacer(Modifier.width(8.dp))
                            Text(cat)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val dest = when (chosen) {
                        "Tất cả" -> ""
                        "Chưa phân loại" -> ""
                        else -> chosen
                    }
                    if (chosen.isNotBlank()) viewModel.moveNoteToCategory(note, dest)
                    Toast.makeText(context, "Đã chuyển tới ${if (dest.isBlank()) "Chưa phân loại" else dest}", Toast.LENGTH_SHORT).show()
                    showMoveDialogFor = null
                }) { Text("Chuyển") }
            },
            dismissButton = {
                TextButton(onClick = { showMoveDialogFor = null }) { Text("Hủy") }
            }
        )
    }

    // --- DELETE CONFIRM ---
    showDeleteConfirmFor?.let { note ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmFor = null },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc muốn xóa ghi chú này?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNote(note)
                    showDeleteConfirmFor = null
                }) { Text("Xóa", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmFor = null }) { Text("Hủy") }
            }
        )
    }
}

/**
 * Card item for note (used in list & grid)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCardItem(
    note: Note,
    fontSize: androidx.compose.ui.unit.TextUnit,
    darkMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val bg = if (darkMode) Color(0xFF2C2C2C) else Color.White
    val textColor = if (darkMode) Color.White else Color.Black
    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(note.createdAt))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = note.title.ifBlank { "(Không tiêu đề)" },
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
                if (note.isPinned) {
                    Icon(
                        imageVector = Icons.Filled.PushPin,
                        contentDescription = "Ghim",
                        tint = if (darkMode) Color.Yellow else Color(0xFF333333),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = note.content,
                fontSize = (fontSize.value - 1).sp,
                color = textColor,
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = if (note.category.isBlank()) "Chưa phân loại" else note.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor
                )
                Text(text = date, style = MaterialTheme.typography.labelSmall, color = textColor)
            }
        }
    }
}

/**
 * Category chip composable (simple, stable)
 */
@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .wrapContentWidth()
            .heightIn(min = 36.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
