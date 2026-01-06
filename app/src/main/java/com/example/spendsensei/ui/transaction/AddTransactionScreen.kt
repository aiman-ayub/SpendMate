package com.example.spendsensei.ui.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendsensei.SpendSenseApplication
import com.example.spendsensei.ui.dashboard.TransactionType
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.filled.DateRange
import com.example.spendsensei.ui.settings.AppCurrency
import com.example.spendsensei.ui.transaction.AddTransactionViewModel
import com.example.spendsensei.ui.transaction.AddTransactionViewModel.AddTransactionViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as SpendSenseApplication
    val repository = app.transactionRepository
    val currentUser by app.currentUser.collectAsState()

    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    @Composable
    fun DateInput(
        date: Long,
        onDateChange: (Long) -> Unit
    ) {
        val context = LocalContext.current
        val calendar = remember { java.util.Calendar.getInstance() }

        calendar.timeInMillis = date

        val formattedDate = remember(date) {
            java.text.SimpleDateFormat(
                "dd-MM-yyyy",
                java.util.Locale.getDefault()
            ).format(java.util.Date(date))
        }

        val datePickerDialog = remember {
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    onDateChange(calendar.timeInMillis)
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp) // keeps same width as others
        ) {
            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text("Date") },
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                }
            )
        }

    }

    val viewModel: AddTransactionViewModel = viewModel(
        factory = AddTransactionViewModelFactory(
            repository,
            currentUser!!.id
        )
    )
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    val expenseCategories = listOf("Food", "Travel", "Shopping", "Rent", "Health", "Others")
    val incomeCategories = listOf("Salary", "Freelance", "Gift", "Investment", "Others")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            TransactionTypeSelector(
                selectedType = state.type,
                onTypeSelected = viewModel::onTypeChange
            )

            Spacer(modifier = Modifier.height(16.dp))
            val selectedCurrency by app.currencyStore.currencyFlow
                .collectAsState(initial = AppCurrency.INR)
            AmountInput(
                amount = state.amount,
                currencySymbol = selectedCurrency.symbol,
                onAmountChange = viewModel::onAmountChange
            )
            if (state.budgetExceeded) {
                Text(
                    text = "Budget limit exceeded. Please update your budget to add more expenses.",
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CategorySelector(
                selectedCategory = state.category,
                onCategorySelected = viewModel::onCategoryChange,
                categories = if (state.type == TransactionType.EXPENSE) expenseCategories else incomeCategories
            )

            Spacer(modifier = Modifier.height(16.dp))

            DateInput(
                date = state.date,
                onDateChange = viewModel::onDateChange
            )

            PaymentMethodInput(
                paymentMethod = state.paymentMethod,
                onPaymentMethodChange = viewModel::onPaymentMethodChange
            )

            NoteInput(
                note = state.note,
                onNoteChange = viewModel::onNoteChange
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::saveTransaction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = state.amount.isNotEmpty()
            ) {
                Text("Save Transaction")
            }
        }
    }
}
