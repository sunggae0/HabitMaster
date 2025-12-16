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
            onNavigateToSettings = { navController.navigate("settings/$profileId") },
            onNavigateToMypage = { navController.navigate("mypage/$profileId") },
            onNavigateToHabitCreate = {
                // 습관 생성 시에도 현재 프로필 ID를 넘겨줘야 한다면 URL 파라미터로 전달하거나,
                // HabitCreateScreen 내부에서 현재 프로필을 추적할 수 있어야 함.
                // 여기서는 HabitCreateScreen이 별도로 마지막 프로필을 감지하는 로직을 갖고 있으므로 일단 그대로 둠.
                // 하지만 정확성을 위해 profileId를 넘겨주는 것이 좋음.
                navController.navigate("habit_create/$profileId")
            },
            onNavigateToHabitEdit = { habitId ->
                // 수정 시에도 profileId가 필요할 수 있으나, HabitEditScreen은 habitId로 프로필을 찾을 수 없으므로(구조상),
                // profileId도 같이 넘겨주는 것이 효율적임.
                // 일단 기존 HabitEditScreen은 habitId만 받고 프로필 전체를 뒤져서 찾도록 되어 있음.
                navController.navigate("habit_edit/$habitId/$profileId")
            }
        )
    }
}
