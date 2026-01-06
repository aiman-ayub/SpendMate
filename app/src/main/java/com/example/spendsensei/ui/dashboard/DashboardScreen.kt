package com.example.spendsensei.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendsensei.SpendSenseApplication
import com.example.spendsensei.ui.settings.AppCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToUsers: () -> Unit

) {
    val context = LocalContext.current
    val application = context.applicationContext as SpendSenseApplication
    val repository = application.transactionRepository
    val currentUser by application.currentUser.collectAsState()
    val selectedCurrency by application.currencyStore.currencyFlow
        .collectAsState(initial = AppCurrency.INR)

    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val viewModel: DashboardViewModel = viewModel(
        key = "dashboard_${currentUser!!.id}",
        factory = DashboardViewModelFactory(repository, currentUser!!.id)
    )

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SpendMate")
                        Text(
                            text = "Welcome, ${currentUser!!.name}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTransaction) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        bottomBar = {
            DashboardBottomBar(
                onDashboard = { /* already here */ },
                onHistory = onNavigateToHistory,
                onAnalytics = onNavigateToAnalytics,
                onBudget = onNavigateToBudget
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            SummaryCard(
                totalIncome = state.totalIncome,
                totalExpense = state.totalExpense,
                balance = state.balance,
                currency = selectedCurrency
            )

            QuickActionSection(
                onNavigateToAnalytics = onNavigateToAnalytics,
                onNavigateToBudget = onNavigateToBudget,
                onNavigateToHistory = onNavigateToHistory
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "View All",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToHistory() }
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(state.recentTransactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        selectedCurrency = selectedCurrency, // pass it here
                        onDelete = { viewModel.deleteTransaction(it) }
                    )
                }

                if (state.recentTransactions.isEmpty()) {
                    item { EmptyTransactionState() }
                }
            }
        }
    }
}

/* -------------------- BOTTOM BAR -------------------- */
@Composable
private fun DashboardBottomBar(
    onDashboard: () -> Unit,
    onHistory: () -> Unit,
    onAnalytics: () -> Unit,
    onBudget: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        BottomBarItem(Icons.Default.Home, "Home", onDashboard)
        BottomBarItem(Icons.Default.History, "History", onHistory)
        BottomBarItem(Icons.Default.BarChart, "Analytics", onAnalytics)
        BottomBarItem(Icons.Default.AccountBalanceWallet, "Budget", onBudget)
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label)
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
