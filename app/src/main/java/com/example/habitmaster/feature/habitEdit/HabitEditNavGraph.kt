        package com.example.habitmaster.feature.habitEdit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.habitEditNavGraph(navController: NavHostController) {
    composable("habitEdit") {
        HabitEditScreen(
            onFinish = { navController.navigate("habit_list") }
        )
    }
}
