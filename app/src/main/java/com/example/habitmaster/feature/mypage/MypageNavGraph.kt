package com.example.habitmaster.feature.mypage

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.mypageNavGraph(navController: NavHostController) {
    composable("mypage/{profileId}") { backStackEntry ->
        val profileId = backStackEntry.arguments?.getString("profileId")
        if (profileId != null) {
            MypageScreen(
                profileId = profileId,
                onFinish = { navController.popBackStack() }
            )
        }
    }
}