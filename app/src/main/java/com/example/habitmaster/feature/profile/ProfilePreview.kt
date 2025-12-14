package com.example.habitmaster.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

// 기본 상태 미리보기
@Preview(name = "기본 상태", showBackground = true)
@Composable
fun ProfileScreenPreview_Default() {
    HabitMasterTheme {
        ProfileScreen(
            stats = UserStatus(), // 기본 더미 데이터 사용
            onFinish = {}
        )
    }
}

// 달성률이 높은 상태 미리보기
@Preview(name = "달성률 높음", showBackground = true)
@Composable
fun ProfileScreenPreview_HighAchievement() {
    HabitMasterTheme {
        ProfileScreen(
            stats = UserStatus(
                achievementRate = 95,
                monthlyAchievementRate = 88,
                currentStreak = 30,
                bestStreak = 50
            ),
            onFinish = {}
        )
    }
}

// 달성률이 낮은 상태 미리보기
@Preview(name = "달성률 낮음", showBackground = true)
@Composable
fun ProfileScreenPreview_LowAchievement() {
    HabitMasterTheme {
        ProfileScreen(
            stats = UserStatus(
                achievementRate = 15,
                monthlyAchievementRate = 25,
                currentStreak = 0,
                trendChange = -5
            ),
            onFinish = {}
        )
    }
}
