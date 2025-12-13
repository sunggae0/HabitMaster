package com.example.habitmaster.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.habitmaster.core.data.firebase.FirebaseProfileRepository
import com.example.habitmaster.core.model.Profile
import com.example.habitmaster.feature.main.components.AddHabitFAB
import com.example.habitmaster.feature.main.components.HabitList
import com.example.habitmaster.feature.main.components.InformationArea

@Composable
fun MainScreen(
    onFinish: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMypage: () -> Unit = {},
    onNavigateToHabitCreate: () -> Unit = {},
    onNavigateToHabitEdit: (String) -> Unit = {}
) {
    val repository = remember { FirebaseProfileRepository() }
    var currentProfile by remember { mutableStateOf<Profile?>(null) }
    
    // Firestore 실시간 감지하여 프로필 정보 및 습관 목록 업데이트
    LaunchedEffect(Unit) {
        repository.observeProfiles().collect { profiles ->
            if (profiles.isNotEmpty()) {
                currentProfile = profiles.first() // 첫 번째 프로필 사용 (혹은 선택 로직)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            AddHabitFAB(onClick = {
                onNavigateToHabitCreate()
            })
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = Color.White
    ) { paddingValues ->
        if (currentProfile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                InformationArea(
                    userName = currentProfile!!.name,
                    onSettingsClick = onNavigateToSettings,
                    onMypageClick = onNavigateToMypage
                )
                // 습관 목록 전달
                HabitList(
                    habits = currentProfile!!.habits,
                    onHabitClick = { habitId ->
                        onNavigateToHabitEdit(habitId)
                    }
                )
            }
        }
    }
}
