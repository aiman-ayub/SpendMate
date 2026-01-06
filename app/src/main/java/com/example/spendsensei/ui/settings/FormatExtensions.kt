package com.example.spendsensei.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.spendsensei.SpendSenseApplication

@Composable
fun formatCurrency(amount: Double): String {
    val app = LocalContext.current.applicationContext as SpendSenseApplication
    val selectedCurrency =
        app.currencyStore.currencyFlow.collectAsState(initial = AppCurrency.INR).value

    return formatCurrency(amount, selectedCurrency)
}

