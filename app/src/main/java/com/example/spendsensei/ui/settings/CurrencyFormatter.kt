package com.example.spendsensei.ui.settings

import java.text.NumberFormat

// 🔹 Base currency = INR
private val conversionRates = mapOf(
    AppCurrency.INR to 1.0,
    AppCurrency.USD to 0.012,
    AppCurrency.EUR to 0.011,
    AppCurrency.GBP to 0.0095
)

fun formatCurrency(
    amountInInr: Double,
    currency: AppCurrency = AppCurrency.INR
): String {
    val rate = conversionRates[currency] ?: 1.0
    val convertedAmount = amountInInr * rate

    val formatter = NumberFormat.getCurrencyInstance(currency.locale)
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 2

    return formatter.format(convertedAmount)
}
