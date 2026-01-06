package com.example.spendsensei.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsensei.data.local.entity.UserEntity
import com.example.spendsensei.data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    fun login(
        email: String,
        password: String,
        onResult: (UserEntity?) -> Unit
    ) {
        viewModelScope.launch {
            val user = repository.loginAndGetUser(email, password)

            withContext(Dispatchers.Main) {
                onResult(user)
            }
        }
    }


    fun signup(
        name: String,
        email: String,
        password: String,
        onResult: (UserEntity?) -> Unit
    ) {
        viewModelScope.launch {
            // Create new user entity
            val newUser = UserEntity(
                name = name,
                email = email,
                password = password
            )

            // Attempt to insert using repository
            val insertedUser = repository.signup(newUser) // returns UserEntity? if successful

            // Return result on Main thread
            withContext(Dispatchers.Main) {
                onResult(insertedUser)
            }
        }
    }


}
