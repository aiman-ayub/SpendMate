package com.example.spendsensei.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.spendsensei.data.local.entity.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: Income)

    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

    @Query("SELECT * FROM incomes WHERE userId = :userId")
    fun getAllIncomes(userId: Int): Flow<List<Income>>

    @Query("SELECT SUM(amount) FROM incomes WHERE userId = :userId")
    fun getTotalIncome(userId: Int): Flow<Double?>


}
