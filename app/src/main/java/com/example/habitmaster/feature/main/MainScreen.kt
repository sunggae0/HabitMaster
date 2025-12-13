package com.example.habitmaster.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.habitmaster.feature.main.components.AddHabitFAB
import com.example.habitmaster.feature.main.components.HabitList
import com.example.habitmaster.feature.main.components.InformationArea

// onFinish는 기존에 있었지만 현재는 네비게이션을 위해 navController를 받는 것이 더 유연할 수 있음
// 하지만 기존 파라미터 구조를 유지하면서 콜백을 추가합니다.
@Composable
fun MainScreen(
    onFinish: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMypage: () -> Unit = {},
    onNavigateToHabitCreate: () -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            AddHabitFAB(onClick = {
                onNavigateToHabitCreate()
            })
        },
        floatingActionButtonPosition = FabPosition.End, // 기본 End
        containerColor = Color.White
    ) { paddingValues ->
        // Box를 사용하여 절대 위치 배치가 가능하도록 함 (필요시)
        // 여기서는 Column 구조를 유지하되 InformationArea에 콜백을 전달
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InformationArea(
                userName = "sunggae0", // TODO: 실제 유저 이름 연동 필요 (ViewModel 등)
                onSettingsClick = onNavigateToSettings,
                onMypageClick = onNavigateToMypage
            )
            HabitList()
        }
    }
}
