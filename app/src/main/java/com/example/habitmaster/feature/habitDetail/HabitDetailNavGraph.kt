package com.example.habitmaster.feature.habitDetail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.habitDetailNavGraph(navController: NavHostController) {
    composable(route="habit_detail/{habitId}",
        arguments = listOf(navArgument("habitId") { type = NavType.StringType })) {backStackEntry ->
        val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
        HabitDetailScreen(habitId,
            onFinish = { navController.popBackStack()},
            onNavigateToHabitEdit = {habitId,profileId ->
                navController.navigate("habit_edit/$habitId/$profileId")
            }) //추가 구현 필요할 듯
    }
}
