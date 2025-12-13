package com.example.habitmaster.feature.habitEdit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.habitEditNavGraph(navController: NavHostController) {
    composable(
        route = "habit_edit/{habitId}",
        arguments = listOf(navArgument("habitId") { type = NavType.StringType })
    ) { backStackEntry ->
        val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
        HabitEditScreen(
            habitId = habitId,
            onFinish = {
                if (!navController.popBackStack()) {
                    navController.navigate("main") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        )
    }
}
