package com.example.habitmaster.feature.habitCreate

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.habitCreateNavGraph(navController: NavHostController) {
    composable("habit_create") {
        HabitCreateScreen(
            onFinish = {
                // 무조건 메인으로 이동하며 스택을 클리어
                navController.navigate("main") {
                    popUpTo(0) { inclusive = true } // 모든 백스택 제거하고 메인으로 이동
                }
            }
        )
    }
}