package com.example.habitmaster.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.habitmaster.feature.habitCreate.habitCreateNavGraph
import com.example.habitmaster.feature.habitDetail.habitDetailNavGraph
import com.example.habitmaster.feature.habitEdit.habitEditNavGraph
import com.example.habitmaster.feature.main.mainNavGraph
import com.example.habitmaster.feature.mypage.mypageNavGraph
import com.example.habitmaster.feature.onboarding.onboardingNavGraph
import com.example.habitmaster.feature.profile.profileNavGraph
import com.example.habitmaster.feature.settings.settingsNavGraph

@Composable
fun AppRoot() {
    val navController = rememberNavController() //navController 초기화

    NavHost(
        navController = navController,          //navHost에 연결
        startDestination = "onboarding"         //초기화면 지정
    ) {
        onboardingNavGraph(navController)       //feature(기능) 별 NavGraph 호출
        habitCreateNavGraph(navController)
        habitDetailNavGraph(navController)
        habitEditNavGraph(navController)
        mainNavGraph(navController)
        mypageNavGraph(navController)
        profileNavGraph(navController)
        settingsNavGraph(navController)
    }
}
