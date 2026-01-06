package com.example.spendsensei.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.spendsensei.SpendSenseApplication
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.DarkMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,// navigate to login
    onNavigateToUsers: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as SpendSenseApplication
    val currentUser by application.currentUser.collectAsState()
    val database = application.database
    val activity = LocalContext.current as ComponentActivity
    val scope = rememberCoroutineScope()

    val selectedCurrency by application.currencyStore
        .currencyFlow
        .collectAsState(initial = AppCurrency.INR)

    var currencyExpanded by remember { mutableStateOf(false) }
    val viewModel: SettingsViewModel = ViewModelProvider(
        activity,
        SettingsViewModelFactory(database)
    ).get(SettingsViewModel::class.java)

    var showClearDataDialog by remember { mutableStateOf(false) }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear All Data") },
            text = { Text("Are you sure you want to delete all expenses, income, and budgets? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData()
                    showClearDataDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ===== CUSTOM COMPOSABLES =====
            @Composable
            fun SettingsItem(
                icon: ImageVector,
                title: String,
                subtitle: String? = null,
                onClick: () -> Unit,
                textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick() }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = textColor)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = title,
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        subtitle?.let {
                            Text(
                                text = it,
                                color = textColor.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            @Composable
            fun ProfileSettingsItem(userName: String, userEmail: String) {
                var expanded by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // ✅ PROFILE ICON (human)
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = if (expanded)
                                Icons.Default.KeyboardArrowDown
                            else
                                Icons.Default.KeyboardArrowRight,
                            contentDescription = "Expand"
                        )
                    }

                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = userName)
                        Text(
                            text = userEmail,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }


            // ===== PROFILE SECTION =====
            currentUser?.let { user ->
                ProfileSettingsItem(userName = user.name, userEmail = user.email)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Clear All Data",
                onClick = { showClearDataDialog = true },
                textColor = MaterialTheme.colorScheme.error
            )
            SettingsItem(
                icon = Icons.Default.Person, // or any icon you like
                title = "View Registered Users",
                onClick = { onNavigateToUsers() } // navigate to UserListScreen
            )
            Box {
                SettingsItem(
                    icon = Icons.Default.AttachMoney,
                    title = "Currency",
                    subtitle = selectedCurrency.name,
                    onClick = { currencyExpanded = true }
                )
                DropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false }
                ) {
                    AppCurrency.values().forEach { currency ->
                        DropdownMenuItem(
                            text = { Text("${currency.symbol} ${currency.name}") },
                            onClick = {
                                scope.launch {
                                    application.currencyStore.setCurrency(currency)
                                }
                                currencyExpanded = false
                            }
                        )
                    }
                }
            }


            SettingsItem(
                icon = Icons.Default.Info,
                title = "About SpendMate",
                subtitle = "Version 1.0",
                onClick = { }
            )

            SettingsItem(
                icon = Icons.Outlined.DarkMode, // 🌙 half moon icon
                title = "Dark Mode",
                subtitle = if (viewModel.isDarkTheme.collectAsState().value) "On" else "Off",
                onClick = { viewModel.toggleTheme() }
            )
            // ===== LOGOUT BUTTON =====
            SettingsItem(
                icon = Icons.AutoMirrored.Outlined.ExitToApp,
                title = "Log Out",
                onClick = {
                    application.logoutUser()
                    onLogout()
                },
                textColor = MaterialTheme.colorScheme.error
            )

        }
    }
}
