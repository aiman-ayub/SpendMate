package com.example.spendsensei.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    primaryKeys = ["userId", "month"]
)
data class Budget(
    val userId: Int,
    val month: String,          // YYYY-MM
    val limitAmount: Double,
    val category: String? = null
)
