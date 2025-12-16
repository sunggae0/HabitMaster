package com.example.habitmaster.feature.habitDetail

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun HabitDetailPreview() {
    HabitMasterTheme {
        HabitDetailScreen("2b4a900f-38c9-4ca5-957a-3e849b213625",
            onFinish = {})
    }
}
