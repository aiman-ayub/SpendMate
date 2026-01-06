package com.example.spendsensei.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.spendsensei.data.local.entity.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Query("""
        SELECT * FROM budgets 
        WHERE userId = :userId AND month = :month
    """)
    fun getBudgetForMonth(
        userId: Int,
        month: String
    ): Flow<Budget?>
}
