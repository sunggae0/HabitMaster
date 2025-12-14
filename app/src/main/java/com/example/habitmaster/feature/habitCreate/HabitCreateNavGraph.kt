package com.example.habitmaster.feature.habitCreate

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.habitCreateNavGraph(navController: NavHostController) {
    composable(
        route = "habit_create/{profileId}",
        arguments = listOf(navArgument("profileId") { type = NavType.StringType })
    ) { backStackEntry ->
        val profileId = backStackEntry.arguments?.getString("profileId") ?: return@composable
        
        HabitCreateScreen(
            profileId = profileId,
            onFinish = {
                // 작업 완료 시 해당 프로필의 메인 화면으로 이동하며 백스택 정리
                navController.navigate("main/$profileId") {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}