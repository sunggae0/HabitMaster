package com.example.habitmaster.feature.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable(
        route = "main/{profileId}",
        arguments = listOf(navArgument("profileId") { type = NavType.StringType })
    ) { backStackEntry ->
        val profileId = backStackEntry.arguments?.getString("profileId") ?: return@composable

        MainScreen(
            profileId = profileId,
            onFinish = { navController.navigate("habit_list") },
            onNavigateToSettings = { navController.navigate("settings/$profileId") }, // [수정] profileId 전달
            onNavigateToMypage = { navController.navigate("mypage/$profileId") },     // [수정] profileId 전달
            onNavigateToHabitCreate = {
                navController.navigate("habit_create/$profileId")
            },
            onNavigateToHabitEdit = { habitId ->
                navController.navigate("habit_edit/$habitId/$profileId")
            }
        )
    }
}