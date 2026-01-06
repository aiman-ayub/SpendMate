package com.example.spendsensei.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spendsensei.data.local.entity.Expense
import com.example.spendsensei.data.local.entity.Income
import com.example.spendsensei.data.repository.TransactionRepository
import com.example.spendsensei.ui.dashboard.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat

import java.util.Locale
import kotlinx.coroutines.flow.first

data class AddTransactionState(
    val amount: String = "",
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "Others", // Default category
    val isSaved: Boolean = false,
    val paymentMethod: String = "",
    val budgetExceeded: Boolean = false
)

class AddTransactionViewModel(
    private val repository: TransactionRepository,
    private val userId: Int
) : ViewModel() {

    fun onPaymentMethodChange(newMethod: String) {
        _state.update { it.copy(paymentMethod = newMethod) }
    }

    private val _state = MutableStateFlow(AddTransactionState())
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    fun onAmountChange(newAmount: String) {
        // Basic validation to allow only numbers and one decimal point
        if (newAmount.all { it.isDigit() || it == '.' } && newAmount.count { it == '.' } <= 1) {
            _state.update { it.copy(amount = newAmount) }
        }
    }

    fun onNoteChange(newNote: String) {
        _state.update { it.copy(note = newNote) }
    }

    fun onDateChange(newDate: Long) {
        _state.update { it.copy(date = newDate) }
    }

    fun onTypeChange(newType: TransactionType) {
        _state.update { it.copy(type = newType) }
    }

    fun onCategoryChange(newCategory: String) {
        _state.update { it.copy(category = newCategory) }
    }

    fun saveTransaction() {
        val currentState = _state.value
        val amountValue = currentState.amount.toDoubleOrNull() ?: return

        viewModelScope.launch {

            if (currentState.type == TransactionType.EXPENSE) {

                // ---- MONTH STRING ----
                val monthStr = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                    .format(Date(currentState.date))

                // ---- GET TOTAL EXPENSE & BUDGET ----
                val totalExpense = repository.getTotalExpenseForMonth(userId, monthStr)
                val budget = repository.getBudgetForMonth(userId, monthStr).first()


                // ---- BUDGET CHECK ----
                if (budget != null && totalExpense + amountValue > budget.limitAmount) {
                    _state.update { it.copy(budgetExceeded = true) }
                    return@launch
                }

                // ---- SAVE EXPENSE ----
                val expense = Expense(
                    amount = amountValue,
                    category = currentState.category,
                    date = currentState.date,
                    note = currentState.note.takeIf { it.isNotBlank() },
                    userId = userId,
                    paymentMethod = currentState.paymentMethod.takeIf { it.isNotBlank() }
                )

                repository.insertExpense(expense)

            } else {
                val income = Income(
                    amount = amountValue,
                    source = currentState.category,
                    date = currentState.date,
                    note = currentState.note.takeIf { it.isNotBlank() },
                    userId = userId
                )
                repository.insertIncome(income)
            }

            _state.update { it.copy(isSaved = true, budgetExceeded = false) }
        }
    }

    class AddTransactionViewModelFactory(
        private val repository: TransactionRepository,
        private val userId: Int
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
                return AddTransactionViewModel(repository, userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
