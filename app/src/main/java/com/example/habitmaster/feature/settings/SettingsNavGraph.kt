package com.example.habitmaster.feature.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    composable("onboarding") {
        SettingsScreen(
            onFinish = { navController.navigate("habit_list") }
        )
    }
}