package com.example.spendsensei.data.repository

import com.example.spendsensei.data.local.dao.UserDao
import com.example.spendsensei.data.local.entity.UserEntity
import com.example.spendsensei.data.local.entity.UserListItem

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun signup(user: UserEntity): UserEntity? {
        // Check if email already exists
        val existing = userDao.getUserByEmail(user.email)
        if (existing != null) return null

        // Insert user
        userDao.insertUser(user)

        // Return the same user object with ID fetched from DB
        // This fetch ensures the ID is populated
        return userDao.getUserByEmail(user.email)
    }


    suspend fun loginAndGetUser(
        email: String,
        password: String
    ): UserEntity? {
        return userDao.login(email, password)
    }
    suspend fun getAllUsers(): List<UserListItem> {
        return userDao.getAllUsers()
    }

}
