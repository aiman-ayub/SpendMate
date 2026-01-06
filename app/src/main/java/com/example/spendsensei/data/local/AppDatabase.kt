package com.example.spendsensei.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spendsensei.data.local.dao.BudgetDao
import com.example.spendsensei.data.local.dao.ExpenseDao
import com.example.spendsensei.data.local.dao.IncomeDao
import com.example.spendsensei.data.local.entity.Budget
import com.example.spendsensei.data.local.entity.Expense
import com.example.spendsensei.data.local.entity.Income
import com.example.spendsensei.data.local.entity.UserEntity
import com.example.spendsensei.data.local.dao.UserDao

@Database(
    entities = [Expense::class, Income::class, Budget::class,UserEntity::class],
            version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun budgetDao(): BudgetDao

    abstract fun userDao(): UserDao
}
