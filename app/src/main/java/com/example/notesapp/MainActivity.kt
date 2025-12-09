package com.example.notesapp

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.ui.AuthScreen.LoginScreen
import com.example.notesapp.util.authViewModel
import com.example.notesapp.util.AuthManager
import com.example.notesapp.util.AuthManager.handleDeepLink
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val noteViewModel: NoteViewModel by viewModels {
        val dao = NoteDatabase.getDatabase(application).noteDao()
        val repo = NoteRepository(dao)
        NoteViewModelFactory(application, repo)
    }
    private val authVM: authViewModel by viewModels ( )


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        lifecycleScope.launch { handleDeepLink(intent )}



        authVM.loadUserFromPrefs()//  tự động load user offline khi mở app
        setContent {
            val darkMode by noteViewModel.darkMode.collectAsState()
            val fontSizeIndex by noteViewModel.fontSizeIndex.collectAsState()

            NotesAppTheme(darkTheme = darkMode, fontSizeIndex = fontSizeIndex) {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    noteViewModel.loadUserFromPrefs()
                    if(intent.getBooleanExtra("reset_ready",false)){
                        navController.navigate("newp_password"){
                            popUpTo("login"){inclusive=false}
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = if (authVM.getUserId()=="guest"){"login"}else{"home"}
                ) {
//                    composable(route="login"){ LoginScreen(
//                        navController=navController,
//                        viewModel=authVM,
//                        noteViewModel=noteViewModel
//                    ) }
                    // Màn hình chính
                    composable("home") {

                        NotesHomeScreen(
                            navController = navController,
                            viewModel = noteViewModel,
                            authVM
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
                                        category = category,
                                        userId = authVM.getUserId()
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
                            viewModel = authVM,
                            noteViewModel = noteViewModel
                        )
                    }
                    composable("register") {
                        com.example.notesapp.ui.AuthScreen.RegisterScreen(
                            navController = navController,

                        )
                    }
                    composable("forgot") {
                        com.example.notesapp.ui.AuthScreen.ForgotPasswordScreen(
                            navController = navController
                        )
                    }
                    composable("new_password") {
                        com.example.notesapp.ui.AuthScreen.NewPasswordScreen(navController)
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
     override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lifecycleScope.launch {
            handleDeepLink(intent.data!!)
        }
    }
     fun handleDeepLink(intent: Intent) {
        val uri = intent.data ?: return

        lifecycleScope.launch {
            val ok = AuthManager.handleDeepLink(uri)
            if (ok) {
                startActivity(Intent(this@MainActivity, MainActivity::class.java).apply {
                    putExtra("reset_ready", true)
                })
            }

        }

    }
}


