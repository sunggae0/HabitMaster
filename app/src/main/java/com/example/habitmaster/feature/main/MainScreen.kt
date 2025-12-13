package com.example.habitmaster.feature.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.habitmaster.feature.main.components.AddHabitFAB
import com.example.habitmaster.feature.main.components.HabitList
import com.example.habitmaster.feature.main.components.InformationArea


@Composable
fun MainScreen(onFinish: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            AddHabitFAB(onClick = {
                // TODO: 습관 추가 화면 이동 또는 다이얼로그 열기
            })
        },
        floatingActionButtonPosition = FabPosition.End, // 기본 End
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InformationArea()
            HabitList()
        }
    }
}

