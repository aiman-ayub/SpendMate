package com.example.spendsensei.data.repository

import com.example.spendsensei.data.local.dao.BudgetDao
import com.example.spendsensei.data.local.dao.ExpenseDao
import com.example.spendsensei.data.local.dao.IncomeDao
import com.example.spendsensei.data.local.entity.Budget
import com.example.spendsensei.data.local.entity.Expense
import com.example.spendsensei.data.local.entity.Income
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.first

class TransactionRepository(
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao,
    private val budgetDao: BudgetDao
) {

    fun allExpenses(userId: Int): Flow<List<Expense>> =
        expenseDao.getAllExpenses(userId)

    fun totalExpense(userId: Int): Flow<Double?> =
        expenseDao.getTotalExpense(userId)

    fun allIncomes(userId: Int): Flow<List<Income>> =
        incomeDao.getAllIncomes(userId)

    fun totalIncome(userId: Int): Flow<Double?> =
        incomeDao.getTotalIncome(userId)

    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    suspend fun insertIncome(income: Income) = incomeDao.insertIncome(income)
    suspend fun deleteIncome(income: Income) = incomeDao.deleteIncome(income)

    // Budget Operations
    // Budget Operations
    fun getBudgetForMonth(
        userId: Int,
        month: String
    ): Flow<Budget?> =
        budgetDao.getBudgetForMonth(userId, month)

    suspend fun insertBudget(budget: Budget) =
        budgetDao.insertBudget(budget)

    suspend fun getTotalExpenseForMonth(userId: Int, month: String): Double {
        // Use Flow.first() to get current value from Flow
        return expenseDao.getAllExpenses(userId)
            .first() // get list of expenses
            .filter {
                val expenseMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                    .format(Date(it.date))
                expenseMonth == month
            }
            .sumOf { it.amount }
    }

}
