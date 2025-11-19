package com.example.habitmaster.feature.mypage

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.mypageNavGraph(navController: NavHostController) {
    composable("mypage") {
        MypageScreen(
            onFinish = { navController.navigate("habit_list") }
        )
    }
}
