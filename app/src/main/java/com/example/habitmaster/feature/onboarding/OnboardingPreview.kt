package com.example.habitmaster.feature.onboarding

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    HabitMasterTheme {
        OnboardingScreen(onFinish = {})
    }
}
