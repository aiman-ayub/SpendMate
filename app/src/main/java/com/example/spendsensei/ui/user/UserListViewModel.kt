package com.example.spendsensei.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsensei.data.local.entity.UserEntity
import com.example.spendsensei.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.spendsensei.data.local.entity.UserListItem
class UserListViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserListItem>>(emptyList())
    val users: StateFlow<List<UserListItem>> = _users


    fun loadUsers() {
        viewModelScope.launch {
            _users.value = repository.getAllUsers()
        }
    }
}
