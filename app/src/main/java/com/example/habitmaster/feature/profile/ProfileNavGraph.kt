package com.example.habitmaster.feature.profile

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
import kotlinx.coroutines.flow.firstOrNull

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {
    composable("profile") {
        val repository = remember { FirebaseProfileRepository() }
        var userStatus by remember { mutableStateOf<UserStatus?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        // 화면이 처음 나타날 때, 데이터 로딩 로직을 실행합니다.
        LaunchedEffect(key1 = Unit) {
            // 1. 현재 사용자의 프로필 목록을 가져옵니다.
            val profiles = repository.observeProfiles().firstOrNull()
            // (여기서는 첫 번째 프로필을 사용한다고 가정합니다.)
            val activeProfileId = profiles?.firstOrNull()?.id

            if (activeProfileId != null) {
                // 2. 프로필 ID를 사용하여 해당 프로필의 통계 정보를 실시간으로 관찰합니다.
                repository.observeUserStatus(activeProfileId).collect { status ->
                    // Firestore에서 가져온 status가 있으면 사용하고, 없으면(예: 새로 만든 프로필) 기본값을 사용합니다.
                    userStatus = status ?: UserStatus()
                    isLoading = false // 로딩 완료
                }
            } else {
                // 프로필이 없는 경우, 기본값으로 표시하고 로딩을 종료합니다.
                userStatus = UserStatus()
                isLoading = false
            }
        }

        // 로딩 상태에 따라 다른 화면을 보여줍니다.
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // userStatus는 로딩이 끝나면 null이 아니므로, !!를 사용하여 non-null로 전달합니다.
            ProfileScreen(
                stats = userStatus!!,
                onFinish = { navController.navigate("habit_list") }
            )
        }
    }
}
