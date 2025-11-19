package com.example.habitmaster.feature.habitDetail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.habitDetailNavGraph(navController: NavHostController) {
    composable("habitDetail") {
        HabitDetailScreen(
            onFinish = { navController.navigate("habit_list") }
        ) //추가 구현 필요할 듯
    }
}
