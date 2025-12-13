package com.example.habitmaster.feature.profile

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp" // 👈 충분한 세로 공간을 가진 기기를 명시
)
@Composable
fun ProfilePreview() {
    HabitMasterTheme {
        ProfileScreen(onFinish = {})
    }
}