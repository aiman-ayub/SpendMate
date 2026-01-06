package com.example.spendsensei.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spendsensei.data.local.entity.Expense
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import com.example.spendsensei.SpendSenseApplication
import com.example.spendsensei.ui.settings.AppCurrency
import androidx.compose.runtime.getValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as SpendSenseApplication

    val selectedCurrency by app.currencyStore.currencyFlow
        .collectAsState(initial = AppCurrency.INR)

    val transactions = viewModel.allTransactions.collectAsState().value

    // yyyy-MM grouping formatter (API 24 safe)
    val monthFormatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())

    val groupedByMonth = transactions.groupBy { expense ->
        monthFormatter.format(Date(expense.date))


    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
    if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No transactions found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                groupedByMonth.forEach { (month, items) ->

                    item {
                        MonthHeader(month)
                    }

                    items(items) { expense ->
                        HistoryItem(
                            expense = expense,
                            currency = selectedCurrency
                        )
                    }
                }
            }
        }
    }
}
