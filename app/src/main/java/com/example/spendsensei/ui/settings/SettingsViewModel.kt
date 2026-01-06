package com.example.spendsensei.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spendsensei.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val database: AppDatabase
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(false) // default light
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            database.clearAllTables()
        }
    }
}

class SettingsViewModelFactory(private val database: AppDatabase) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
