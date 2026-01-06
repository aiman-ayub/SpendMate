package com.example.spendsensei.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spendsensei.data.local.entity.Budget
import com.example.spendsensei.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BudgetState(
    val currentBudget: Double = 0.0,
    val totalExpense: Double = 0.0,
    val progress: Float = 0f,
    val isOverBudget: Boolean = false,
    val budgetInput: String = ""
)

class BudgetViewModel(
    private val repository: TransactionRepository,
    private val userId: Int
) : ViewModel() {

    private val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

    private val _budgetInput = MutableStateFlow("")

    val state: StateFlow<BudgetState> = combine(
        repository.getBudgetForMonth(userId, currentMonth),
        repository.allExpenses(userId), // Ideally filter by month in Repository, but for now filter here
        _budgetInput
    ) { budget, expenses, input ->
        val budgetLimit = budget?.limitAmount ?: 0.0
        
        // Filter expenses for current month
        val currentMonthExpenses = expenses.filter {
            val expenseMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(it.date))
            expenseMonth == currentMonth
        }.sumOf { it.amount }

        val progress = if (budgetLimit > 0) (currentMonthExpenses / budgetLimit).toFloat() else 0f
        val isOverBudget = currentMonthExpenses > budgetLimit && budgetLimit > 0

        BudgetState(
            currentBudget = budgetLimit,
            totalExpense = currentMonthExpenses,
            progress = progress.coerceIn(0f, 1f),
            isOverBudget = isOverBudget,
            budgetInput = input
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetState()
    )

    fun onBudgetInputChange(newInput: String) {
        if (newInput.all { it.isDigit() || it == '.' }) {
            _budgetInput.value = newInput
        }
    }

    fun saveBudget() {
        val amount = _budgetInput.value.toDoubleOrNull() ?: return
        viewModelScope.launch {
            val budget = Budget(
                userId = userId,
                month = currentMonth,
                limitAmount = amount
            )
            repository.insertBudget(budget)
            _budgetInput.value = ""
        }
    }

}

class BudgetViewModelFactory(
    private val repository: TransactionRepository,
    private val userId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            return BudgetViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


