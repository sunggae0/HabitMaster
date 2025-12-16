        package com.example.habitmaster.feature.habitEdit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.habitEditNavGraph(navController: NavHostController) {
    composable(
        route = "habit_edit/{habitId}/{profileId}",
        arguments = listOf(
            navArgument("habitId") { type = NavType.StringType },
            navArgument("profileId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
        val profileId = backStackEntry.arguments?.getString("profileId") ?: return@composable
        
        HabitEditScreen(
            habitId = habitId,
            onFinish = {
                // 수정 완료 후 메인 화면으로 돌아갈 때 profileId를 사용
                navController.navigate("main/$profileId") {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}