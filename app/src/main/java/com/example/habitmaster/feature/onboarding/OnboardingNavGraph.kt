package com.example.habitmaster.feature.onboarding

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.onboardingNavGraph(navController: NavHostController) {
    composable("onboarding") {
        OnboardingScreen(
            onFinish = { navController.navigate("habit_list") }
        )
    }
}
