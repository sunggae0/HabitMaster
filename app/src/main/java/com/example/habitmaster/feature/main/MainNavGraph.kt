package com.example.habitmaster.feature.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable("main") {
        MainScreen(
            onFinish = { navController.navigate("habit_list") },
            onNavigateToSettings = { navController.navigate("settings") },
            onNavigateToMypage = { navController.navigate("mypage") },
            onNavigateToHabitCreate = { navController.navigate("habit_create") },
            onNavigateToHabitEdit = { habitId ->
                navController.navigate("habit_edit/$habitId")
            }
        )
    }
}
