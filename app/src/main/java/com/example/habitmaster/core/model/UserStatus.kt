package com.example.habitmaster.core.model

/**
 * 사용자의 통계 정보를 나타내는 데이터 클래스입니다.
 * 이 클래스는 앱의 여러 부분에서 공통으로 사용됩니다.
 */
data class UserStatus(
    val achievementRate: Int = 75,
    val trendChange: Int = 4,
    val currentStreak: Int = 15,
    val totalAchieved: Int = 234,
    val bestStreak: Int = 43,
    val activeChallenges: Int = 5,
    val monthlyAchievementRate: Int = 60,
    val monthlyTrendChange: Int = -2
)
