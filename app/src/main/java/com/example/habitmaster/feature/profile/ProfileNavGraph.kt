package com.example.habitmaster.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {
    composable("profile") {
        ProfileScreen(
            onFinish = { profileId -> 
                navController.navigate("main/$profileId") {
                    // 프로필 선택 화면을 스택에서 제거 (뒤로가기 시 다시 안 나오게)
                    popUpTo("profile") { inclusive = true }
                }
            }
        )
    }
}