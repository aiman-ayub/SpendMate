package com.example.spendsensei

import android.app.Application
import androidx.room.Room
import com.example.spendsensei.data.local.AppDatabase
import com.example.spendsensei.data.repository.TransactionRepository
import com.example.spendsensei.data.repository.UserRepository
import com.example.spendsensei.data.local.entity.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class SpendSenseApplication : Application() {
    lateinit var currencyStore: com.example.spendsensei.ui.settings.CurrencyStore
        private set

    override fun onCreate() {
        super.onCreate()
        currencyStore = com.example.spendsensei.ui.settings.CurrencyStore(this)
    }

    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "spendsense_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }


    val transactionRepository by lazy {
        TransactionRepository(
            database.expenseDao(),
            database.incomeDao(),
            database.budgetDao()
        )
    }


    // ✅ THIS WAS FAILING BEFORE
    val userRepository by lazy {
        UserRepository(database.userDao())
    }

    // GLOBAL LOGGED-IN USER
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    fun setLoggedInUser(user: UserEntity) {
        _currentUser.value = user
    }

    fun logoutUser() {
        _currentUser.value = null
    }
}
