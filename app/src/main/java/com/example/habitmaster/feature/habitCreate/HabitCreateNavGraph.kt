package com.example.habitmaster.feature.habitCreate

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.habitmaster.feature.onboarding.OnboardingScreen

fun NavGraphBuilder.habitCreateNavGraph(navController: NavHostController) {
    composable("habitCreate") {
        OnboardingScreen(
            onFinish = { navController.navigate("habitList") }
        )
    }
}