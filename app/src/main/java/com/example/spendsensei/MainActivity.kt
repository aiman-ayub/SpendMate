package com.example.spendsensei

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.spendsensei.ui.navigation.NavGraph
import com.example.spendsensei.ui.settings.SettingsViewModel
import com.example.spendsensei.ui.settings.SettingsViewModelFactory
import com.example.spendsensei.ui.theme.SpendsenseiTheme
import com.example.spendsensei.ui.user.UserViewModel
import com.example.spendsensei.ui.user.UserViewModelFactory


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val app = application as SpendSenseApplication

            // ✅ SETTINGS VM (COMPOSE-OWNED)
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(app.database)
            )

            // ✅ USER VM (COMPOSE-OWNED) — THIS FIXES SIGNUP
            val userViewModel: UserViewModel = viewModel(
                factory = UserViewModelFactory(app.userRepository)
            )

            val isDark = settingsViewModel.isDarkTheme.collectAsState().value

            SpendsenseiTheme(darkTheme = isDark) {

                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        NavGraph(
                            navController = navController,
                            userViewModel = userViewModel
                        )

                    }
                }
            }
        }
    }
}
