package com.example.habitmaster.feature.habitCreate

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.habitCreateNavGraph(navController: NavHostController) {
    composable("habit_create") {
        HabitCreateScreen(
            onFinish = { navController.navigate("main") }
        )
    }
}