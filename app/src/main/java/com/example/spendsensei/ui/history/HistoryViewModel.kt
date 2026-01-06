package com.example.spendsensei.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsensei.data.local.entity.Expense
import com.example.spendsensei.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    repository: TransactionRepository,
    userId: Int
) : ViewModel() {

    val allTransactions: StateFlow<List<Expense>> =
        repository.allExpenses(userId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}
