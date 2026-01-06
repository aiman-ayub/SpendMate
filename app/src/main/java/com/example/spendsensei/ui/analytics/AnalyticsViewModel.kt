package com.example.spendsensei.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spendsensei.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState

import androidx.compose.material3.Text

import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import androidx.compose.foundation.verticalScroll

import androidx.compose.ui.unit.Dp


data class CategoryExpense(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val color: Color
)

data class AnalyticsState(
    val totalExpense: Double = 0.0,
    val categoryExpenses: List<CategoryExpense> = emptyList()
)

class AnalyticsViewModel(
    private val repository: TransactionRepository,
    private val userId: Int
) : ViewModel() {
    val state: StateFlow<AnalyticsState> = repository.allExpenses(userId)

        .map { expenses ->
            val totalExpense = expenses.sumOf { it.amount }
            val categoryMap = expenses.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val colors = listOf(
                Color(0xFFE57373),
                Color(0xFF64B5F6),
                Color(0xFFFFB74D),
                Color(0xFF81C784),
                Color(0xFFBA68C8),
                Color(0xFFFF8A65)
            )

            val categoryExpenses = categoryMap.entries.mapIndexed { index, entry ->
                val category: String = entry.key
                val amount: Double = entry.value
                CategoryExpense(
                    category = category,
                    amount = amount,
                    percentage = if (totalExpense > 0) (amount / totalExpense).toFloat() else 0f,
                    color = colors[index % colors.size]
                )
            }.sortedByDescending { it.amount }



            AnalyticsState(
                totalExpense = totalExpense,
                categoryExpenses = categoryExpenses
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AnalyticsState()
        )
}

class AnalyticsViewModelFactory(
    private val repository: TransactionRepository,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnalyticsViewModel(repository, userId) as T
    }
}


@Composable
fun SimplePieChart(
    data: List<CategoryExpense>,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp
) {
    val colors = listOf(
        Color(0xFFE57373),
        Color(0xFF64B5F6),
        Color(0xFFFFB74D),
        Color(0xFF81C784),
        Color(0xFFBA68C8),
        Color(0xFFFF8A65)
    )

    if (data.isEmpty()) return

    // Compute sweep angles
    val angles = data.map { it.percentage * 360f }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // LEFT LABELS
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            data.take(data.size / 2).forEachIndexed { index, cat ->
                LabelItem(cat, colors[index % colors.size])
            }
        }

        // PIE CHART IN CENTER
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var start = -90f
                angles.forEachIndexed { i, sweep ->
                    drawArc(
                        color = colors[i % colors.size],
                        startAngle = start,
                        sweepAngle = sweep,
                        useCenter = true
                    )
                    start += sweep
                }
            }
        }

        // RIGHT LABELS
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            data.drop(data.size / 2).forEachIndexed { index, cat ->
                LabelItem(cat, colors[(index + data.size / 2) % colors.size])
            }
        }
    }
}

@Composable
fun LabelItem(cat: CategoryExpense, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("${cat.category} (${(cat.percentage * 100).toInt()}%)", fontSize = 13.sp)
    }
}
@Composable
private fun LabelRow(
    cat: CategoryExpense,
    color: Color,
    alignEnd: Boolean = false
) {
    Row(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .fillMaxWidth()
            .wrapContentWidth(align = if (alignEnd) Alignment.End else Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Color box
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${cat.category} (${String.format("%.1f%%", cat.percentage * 100)})",
            fontSize = 8.sp,
            softWrap = false,                 // ❗ PREVENT WRAPPING
            maxLines = 1,                     // ❗ FORCE SINGLE LINE
            modifier = Modifier.widthIn(min = 90.dp) // ❗ ENOUGH SPACE FOR TEXT (fixes "Shop"/"ping")
        )
    }
}
