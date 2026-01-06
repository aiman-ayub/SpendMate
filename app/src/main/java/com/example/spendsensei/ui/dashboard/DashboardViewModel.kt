package com.example.spendsensei.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spendsensei.data.local.entity.Expense
import com.example.spendsensei.data.local.entity.Income
import com.example.spendsensei.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed interface TransactionUiModel {
    val id: Int
    val amount: Double
    val date: Long
    val note: String?
    val type: TransactionType
    val categoryOrSource: String
}

enum class TransactionType {
    INCOME, EXPENSE
}

data class ExpenseUiModel(
    val expense: Expense
) : TransactionUiModel {
    override val id = expense.id
    override val amount = expense.amount
    override val date = expense.date
    override val note = expense.note
    override val type = TransactionType.EXPENSE
    override val categoryOrSource = expense.category
}

data class IncomeUiModel(
    val income: Income
) : TransactionUiModel {
    override val id = income.id
    override val amount = income.amount
    override val date = income.date
    override val note = income.note
    override val type = TransactionType.INCOME
    override val categoryOrSource = income.source
}

data class DashboardState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val recentTransactions: List<TransactionUiModel> = emptyList()
)

class DashboardViewModel(
    private val repository: TransactionRepository,
    private val userId: Int
) : ViewModel() {

    val state: StateFlow<DashboardState> = combine(
        repository.totalIncome(userId),
        repository.totalExpense(userId),
        repository.allIncomes(userId),
        repository.allExpenses(userId)
    ) { incomeSum, expenseSum, incomes, expenses ->

        val totalIncome = incomeSum ?: 0.0
        val totalExpense = expenseSum ?: 0.0

        DashboardState(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = totalIncome - totalExpense,
            recentTransactions =
                (incomes.map { IncomeUiModel(it) } +
                        expenses.map { ExpenseUiModel(it) })
                    .sortedByDescending { it.date }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardState()
    )

    fun deleteTransaction(transaction: TransactionUiModel) {
        viewModelScope.launch {
            when (transaction) {
                is ExpenseUiModel -> repository.deleteExpense(transaction.expense)
                is IncomeUiModel -> repository.deleteIncome(transaction.income)
            }
        }
    }
}


class DashboardViewModelFactory(
    private val repository: TransactionRepository,
    private val userId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(repository, userId) as T
    }
}

