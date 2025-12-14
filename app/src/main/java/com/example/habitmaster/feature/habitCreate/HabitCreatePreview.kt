package com.example.habitmaster.feature.habitCreate

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun HabitCreatePreview() {
    HabitMasterTheme {
        HabitCreateScreen(
            profileId = "preview_profile_id", // 더미 값 전달
            onFinish = {}
        )
    }
}
