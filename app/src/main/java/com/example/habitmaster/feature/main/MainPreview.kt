package com.example.habitmaster.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    HabitMasterTheme {
        MainScreen(
            profileId = "preview_profile_id", // 더미 ID 추가
            onFinish = {}
        )
    }
}