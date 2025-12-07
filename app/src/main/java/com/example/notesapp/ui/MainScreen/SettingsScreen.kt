package com.example.notesapp.ui.MainScreen

import android.R
import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesapp.ui.NoteViewModel
import androidx.compose.ui.graphics.vector.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import com.example.notesapp.data.NoteDao
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.data.supaBaseClientProvider
import com.example.notesapp.model.Note
import com.example.notesapp.ui.theme.authViewModel
import io.github.jan.supabase.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

val supabase = supaBaseClientProvider
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    authVM: authViewModel
) {
    val darkMode by noteViewModel.darkMode.collectAsState()
    val layoutIsGrid by noteViewModel.layoutIsGrid.collectAsState()
    val fontSizeIndex by noteViewModel.fontSizeIndex.collectAsState()

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
            HorizontalDivider(
                Modifier.padding(vertical = 8.dp),
                DividerDefaults.Thickness,
                DividerDefaults.color
            )

// Lấy dữ liệu user từ ViewModel
            val userName by authVM.userName
            val userId by authVM.userId

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

                    if (userId !="guest") {
                        Button(
                            onClick = {
                                authVM.logout()
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
                    var showalert by remember { mutableStateOf(false) }
                    Button(
                        onClick ={
//                            if (userId!=null){
//
//                            }
//                            else{
//                               showalert=true
//                            }
                                //temp

                                val allNote: Flow<List<Note>> = noteViewModel.notes
                            CoroutineScope(Dispatchers.IO).launch {
                                allNote.collect { notesList ->
                                    println("Received list:")
                                    notesList.forEach { note ->
                                        println("→ ${note.title}: ${note.content}")
                                    }
                                }
                            }
                                syncNote(allNote)


                            //endtemp
                        } ,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                            Text("Đồng bộ dữ liệu")
                    }
                    Alert_ChuaDangNhap(
                        {showalert=false},
                        "Lỗi",
                        "Bạn cần đăng nhập để đồng bộ hóa",
                        Icons.Default.Warning,
                        showalert)
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Chế độ tối")
                Switch(checked = darkMode, onCheckedChange = { noteViewModel.setDarkMode(it) })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Hiển thị dạng lưới")
                Switch(checked = layoutIsGrid, onCheckedChange = { noteViewModel.setLayoutIsGrid(it) })
            }

            Text("Cỡ chữ:")
            Slider(
                value = fontSizeIndex.toFloat(),
                onValueChange = { noteViewModel.setFontSizeIndex(it.toInt()) },
                steps = 1,
                valueRange = 0f..2f
            )

        }
    }
}
@Composable
fun Alert_ChuaDangNhap(
    onDismiss: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    showAlert: Boolean

) { if (showAlert){
    AlertDialog(

        icon = {
            Icon(icon, contentDescription = " AlertIcon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
    )
}
}
private fun syncNote(ls_note: Flow<List<Note>>){
    CoroutineScope(Dispatchers.IO).launch {
        ls_note.collect { notesList->
            try {

                val result= supabase.client.from("tbl_note").insert(notesList)

            }
            catch (e: Exception){
                println(e.message)
            }
        }

    }
    }
