package com.example.spendsensei.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spendsensei.data.local.entity.Expense
import com.example.spendsensei.ui.settings.AppCurrency
import com.example.spendsensei.ui.settings.formatCurrency
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryItem(
    expense: Expense,
    currency: AppCurrency
) {
    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        .format(Date(expense.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = expense.category)

                expense.note?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = formatCurrency(expense.amount, currency),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
