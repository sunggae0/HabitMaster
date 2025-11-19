package com.example.habitmaster.feature.main

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    HabitMasterTheme {
        MainScreen(onFinish = {})
    }
}


@Preview
@Composable
fun HabitCardPreview() {
    HabitMasterTheme {
        HabitCard(
            "sizetest\nsize\nsize",
            0.3f,
            listOf(false, false, false, false, false, true, false)
        )
    }
}