package com.example.habitmaster.feature.habitCreate

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.habitCreateNavGraph(navController: NavHostController) {
    composable("habit_create") {
        HabitCreateScreen(
            onFinish = {
                navController.navigate("main") {
                    // 시작 destination까지 정리하고 main만 남기기
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )
    }
}