package com.example.habitmaster.feature.onboarding

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

// 프로필 화면에서 사용하는 route 문자열과 맞춰줄 것!
private const val ONBOARDING_ROUTE = "onboarding"
private const val PROFILE_ROUTE = "profile"   // ProfileScreen 이 등록된 route

fun NavGraphBuilder.onboardingNavGraph(
    navController: NavHostController,
) {
    composable(route = ONBOARDING_ROUTE) {
        OnboardingScreen(
            onFinish = {
                navController.navigate(PROFILE_ROUTE) {
                    // 온보딩은 스택에서 제거 (뒤로가기 눌렀을 때 다시 안 보이게)
                    popUpTo(ONBOARDING_ROUTE) { inclusive = true }
                }
            }
        )
    }
}