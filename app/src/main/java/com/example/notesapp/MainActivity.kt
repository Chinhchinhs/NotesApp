package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notesapp.data.NoteDatabase
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.model.Note
import com.example.notesapp.ui.NoteViewModel
import com.example.notesapp.ui.NoteViewModelFactory
import com.example.notesapp.ui.screen.CategoriesScreen
import com.example.notesapp.ui.screen.NoteAddScreen
import com.example.notesapp.ui.screen.NoteEditScreen
import com.example.notesapp.ui.screen.NotesHomeScreen
import com.example.notesapp.ui.screen.SettingsScreen
import com.example.notesapp.ui.theme.NotesAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: NoteViewModel by viewModels {
        val dao = NoteDatabase.getDatabase(application).noteDao()
        val repo = NoteRepository(dao)
        NoteViewModelFactory(application, repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode by viewModel.darkMode.collectAsState()
            val fontSizeIndex by viewModel.fontSizeIndex.collectAsState()

            NotesAppTheme(darkTheme = darkMode, fontSizeIndex = fontSizeIndex) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    // Màn hình chính
                    composable("home") {
                        NotesHomeScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }

                    // Màn hình thêm ghi chú
                    composable("addNote") {
                        NoteAddScreen(
                            navController = navController,
                            onSave = { title, content ->
                                viewModel.addNote(
                                    Note(
                                        title = title,
                                        content = content,
                                        category = if (viewModel.selectedCategory.value == "Tất cả")
                                            "Chưa phân loại"
                                        else viewModel.selectedCategory.value
                                    )
                                )
                                navController.popBackStack() // trở về sau khi lưu
                            }
                        )
                    }

                    // Màn hình chỉnh sửa ghi chú
                    composable(
                        route = "editNote/{noteId}",
                        arguments = listOf(
                            navArgument("noteId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getInt("noteId")
                        NoteEditScreen(
                            noteId = noteId ?: 0,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // Cài đặt
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }

                    // Phân loại
                    composable("categories") {
                        CategoriesScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
