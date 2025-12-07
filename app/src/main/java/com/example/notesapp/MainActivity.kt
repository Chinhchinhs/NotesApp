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
import com.example.notesapp.ui.MainScreen.CategoriesScreen
import com.example.notesapp.ui.MainScreen.NoteAddScreen
import com.example.notesapp.ui.MainScreen.NoteEditScreen
import com.example.notesapp.ui.MainScreen.NotesHomeScreen
import com.example.notesapp.ui.MainScreen.SettingsScreen
import com.example.notesapp.ui.theme.NotesAppTheme
import androidx.compose.runtime.LaunchedEffect
import com.example.notesapp.ui.AuthScreen.LoginScreen
import com.example.notesapp.ui.theme.authViewModel


class MainActivity : ComponentActivity() {

    private val noteViewModel: NoteViewModel by viewModels {
        val dao = NoteDatabase.getDatabase(application).noteDao()
        val repo = NoteRepository(dao)
        NoteViewModelFactory(application, repo)
    }
    private val authVM: authViewModel by viewModels ( )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteViewModel.loadUserFromPrefs() //  tự động load user offline khi mở app
        setContent {
            val darkMode by noteViewModel.darkMode.collectAsState()
            val fontSizeIndex by noteViewModel.fontSizeIndex.collectAsState()

            NotesAppTheme(darkTheme = darkMode, fontSizeIndex = fontSizeIndex) {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    noteViewModel.loadUserFromPrefs()
                }

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable(route="login"){ LoginScreen(
                        navController=navController,
                        viewModel=authVM
                    ) }
                    // Màn hình chính
                    composable("home") {
                        NotesHomeScreen(
                            navController = navController,
                            viewModel = noteViewModel
                        )
                    }

                    // Màn hình thêm ghi chú
                    composable("addNote") {
                        NoteAddScreen(
                            navController = navController,
                            viewModel = noteViewModel,
                            onSave = { title, content, category ->
                                noteViewModel.addNote(
                                    Note(
                                        title = title,
                                        content = content,
                                        category = category
                                    )
                                )
                                navController.popBackStack()
                            }
                        )
                    }

                    // Màn hình chỉnh sửa ghi chú
                    composable(
                        route = "editNote/{noteId}",
                        arguments = listOf(
                            navArgument("noteId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getString("noteId")
                        NoteEditScreen(
                            noteId = noteId ?: "-1",
                            viewModel = noteViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // Cài đặt
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            noteViewModel =noteViewModel,
                            authVM=authVM
                        )

                    }
                    composable("login") {
                        com.example.notesapp.ui.AuthScreen.LoginScreen(
                            navController = navController,
                            onLoggedIn = { navController.navigate("home") },
                            viewModel = authVM
                        )
                    }
                    composable("register") {
                        com.example.notesapp.ui.AuthScreen.RegisterScreen(
                            navController = navController
                        )
                    }
                    composable("forgot") {
                        com.example.notesapp.ui.AuthScreen.ForgotPasswordScreen(
                            navController = navController
                        )
                    }


                    // Phân loại
                    composable("categories") {
                        CategoriesScreen(
                            viewModel = noteViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
