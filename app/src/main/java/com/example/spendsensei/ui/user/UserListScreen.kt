package com.example.spendsensei.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserListScreen(viewModel: UserListViewModel) {

    val users by viewModel.users.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Registered Users")

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(users) { user ->
                Text(text = "ID: ${user.id}")
                Text(text = "Name: ${user.name}")
                Text(text = "Email: ${user.email}")
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
