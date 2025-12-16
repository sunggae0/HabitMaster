package com.example.habitmaster.feature.mypage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.habitmaster.core.data.firebase.FirebaseProfileRepository
import com.example.habitmaster.core.model.UserStatus // 공용 UserStatus 모델 import
import kotlinx.coroutines.flow.firstOrNull

fun NavGraphBuilder.mypageNavGraph(navController: NavHostController) {
    composable("mypage") {
        val repository = remember { FirebaseProfileRepository() }
        var userStatus by remember { mutableStateOf<UserStatus?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(key1 = Unit) {
            val profiles = repository.observeProfiles().firstOrNull()
            val activeProfileId = profiles?.firstOrNull()?.id

            if (activeProfileId != null) {
                repository.observeUserStatus(activeProfileId).collect { status ->
                    userStatus = status ?: UserStatus()
                    isLoading = false
                }
            } else {
                userStatus = UserStatus()
                isLoading = false
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            MypageScreen(
                stats = userStatus!!,
                onFinish = { navController.popBackStack() } // 이전 화면으로 돌아가도록 수정
            )
        }
    }
}
