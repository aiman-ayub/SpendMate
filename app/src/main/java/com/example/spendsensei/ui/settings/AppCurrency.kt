package com.example.spendsensei.data.settings

import java.util.Locale

enum class AppCurrency(
    val symbol: String,
    val locale: Locale
) {
    INR("₹", Locale("en", "IN")),
    USD("$", Locale.US),
    EUR("€", Locale.GERMANY),
    GBP("£", Locale.UK)
}
