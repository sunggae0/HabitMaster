package com.example.habitmaster.feature.habitEdit

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun HabitEditPreview() {
    HabitMasterTheme {
        HabitEditScreen(onFinish = {})
    }
}
