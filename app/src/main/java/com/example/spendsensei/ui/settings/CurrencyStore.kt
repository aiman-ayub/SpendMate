package com.example.spendsensei.ui.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale


private val Context.dataStore by preferencesDataStore(name = "settings")

enum class AppCurrency(
    val symbol: String,
    val locale: Locale
) {
    INR("₹", Locale("en", "IN")),
    USD("$", Locale.US),
    EUR("€", Locale.GERMANY),
    GBP("£", Locale.UK)
}

class CurrencyStore(private val context: Context) {

    private val CURRENCY_KEY = stringPreferencesKey("currency")

    val currencyFlow: Flow<AppCurrency> =
        context.dataStore.data.map { preferences ->
            AppCurrency.valueOf(
                preferences[CURRENCY_KEY] ?: AppCurrency.INR.name
            )
        }

    suspend fun setCurrency(currency: AppCurrency) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency.name
        }
    }
}
