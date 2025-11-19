package com.example.habitmaster.feature.habitDetail

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun HabitDetailPreview() {
    HabitMasterTheme {
        HabitDetailScreen(onFinish = {})
    }
}
