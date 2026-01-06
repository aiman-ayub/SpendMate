package com.example.spendsensei.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.spendsensei.data.local.entity.UserEntity
import androidx.room.OnConflictStrategy
import com.example.spendsensei.data.local.entity.UserListItem

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT id, name, email FROM users")
    suspend fun getAllUsers(): List<UserListItem>


}

