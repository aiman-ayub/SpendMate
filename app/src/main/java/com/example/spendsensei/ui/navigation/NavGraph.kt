package com.example.spendsensei.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.spendsensei.ui.analytics.AnalyticsScreen
import com.example.spendsensei.ui.budget.BudgetScreen
import com.example.spendsensei.ui.dashboard.DashboardScreen
import com.example.spendsensei.ui.transaction.AddTransactionScreen
import com.example.spendsensei.ui.front.SplashScreen
import com.example.spendsensei.ui.user.LoginScreen
import com.example.spendsensei.ui.user.SignupScreen
import com.example.spendsensei.ui.user.UserViewModel
import com.example.spendsensei.ui.history.HistoryScreen
import com.example.spendsensei.ui.history.HistoryViewModel
import com.example.spendsensei.ui.history.HistoryViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    userViewModel: UserViewModel
)
{
    NavHost(
        navController = navController,
        startDestination = Screen.Splash
    ) {
        composable<Screen.Splash> {
            SplashScreen(navController)
        }
        composable<Screen.Login> {
            LoginScreen(
                userViewModel = userViewModel,
                onSignupClick = {
                    navController.navigate(Screen.SignUp)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }
        composable<Screen.SignUp> {
            SignupScreen(
                userViewModel = userViewModel,
                onLoginClick = {
                    navController.navigate(Screen.Login)
                },
                onDashboardNavigate = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(Screen.SignUp) { inclusive = true }
                    }
                }
            )
        }


        composable<Screen.Dashboard> {
            DashboardScreen(
                onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics) },
                onNavigateToBudget = { navController.navigate(Screen.Budget) },
                onNavigateToHistory = { navController.navigate(Screen.History) },
                onNavigateToSettings = { navController.navigate(Screen.Settings) },
                onNavigateToUsers = { navController.navigate(Screen.UserList) }
            )
        }
        composable<Screen.AddTransaction> {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Screen.Analytics> {
            AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Screen.Budget> {
            BudgetScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Screen.Settings> {
            com.example.spendsensei.ui.settings.SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = { navController.navigate("login") { popUpTo("dashboard") { inclusive = true } } },
                onNavigateToUsers = { navController.navigate(Screen.UserList) }
            )
        }
        composable<Screen.History> {
            val app = androidx.compose.ui.platform.LocalContext.current
                .applicationContext as com.example.spendsensei.SpendSenseApplication

            val currentUserState by app.currentUser.collectAsState()
            val currentUser = currentUserState

            if (currentUser != null) {
                val historyViewModel: HistoryViewModel = viewModel(
                    key = "history_${currentUser.id}",
                    factory = HistoryViewModelFactory(
                        app.transactionRepository,
                        currentUser.id
                    )
                )

                HistoryScreen(viewModel = historyViewModel,
                onBack = { navController.popBackStack() })

            }


        }
        composable<Screen.UserList> {

            val app = androidx.compose.ui.platform.LocalContext.current
                .applicationContext as com.example.spendsensei.SpendSenseApplication

            val userListViewModel: com.example.spendsensei.ui.user.UserListViewModel = viewModel(
                factory = com.example.spendsensei.ui.user.UserListViewModelFactory(
                    app.userRepository
                )
            )

            com.example.spendsensei.ui.user.UserListScreen(
                viewModel = userListViewModel
            )
        }

    }
}
