package com.example.habitmaster.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {
    composable("profile") {
        ProfileScreen(
            onFinish = { navController.navigate("main") }
        )
    }
}