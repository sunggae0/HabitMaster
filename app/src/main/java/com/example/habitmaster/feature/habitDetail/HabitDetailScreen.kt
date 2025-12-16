package com.example.habitmaster.feature.habitDetail

import android.R.attr.fontFamily
import android.R.attr.fontWeight
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.data.Habit
import com.example.habitmaster.core.data.firebase.FirebaseProfileRepository
import com.example.habitmaster.core.designsystem.PretendardFamily
import com.example.habitmaster.feature.habitDetail.components.CompleteRateDisplay
import com.example.habitmaster.feature.habitDetail.components.DetailHeader
import com.example.habitmaster.feature.habitDetail.components.HabitTitleArea
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun HabitDetailScreen(habitId:String, onFinish: () -> Unit) {
    val repository = remember { FirebaseProfileRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 현재 프로필 및 습관 데이터 로드
    var currentProfileId by remember { mutableStateOf<String?>(null) }
    var currentHabit by remember { mutableStateOf<Habit?>(null) }

    // UI 상태
    var habitName by remember { mutableStateOf("") }
    var targetCount by remember { mutableStateOf("") }
    var periodValue by remember { mutableStateOf("1") }
    var periodUnit by remember { mutableStateOf("일마다") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var isActive by remember { mutableStateOf(true) }
    var achievementRate by remember { mutableStateOf(0f) }

    LaunchedEffect(habitId) {
        try {
            val profiles = repository.observeProfiles().firstOrNull() ?: emptyList()
            // 모든 프로필을 뒤져서 해당 habitId를 가진 프로필과 습관을 찾음
            for (profile in profiles) {
                val foundHabit = profile.habits.find { it.id == habitId }
                if (foundHabit != null) {
                    currentProfileId = profile.id
                    currentHabit = foundHabit

                    habitName = foundHabit.title
                    targetCount = foundHabit.targetCount.toString()
                    periodValue = foundHabit.periodValue.toString()
                    periodUnit = foundHabit.periodUnit
                    startDate = foundHabit.startDate
                    isActive = foundHabit.isActive
                    achievementRate = foundHabit.achievementRate
                    break // 찾았으면 종료
                }
            }
            Log.d("HabitDetailScreen", "HabitId: $habitId, ProfileId: $currentProfileId, periodValue: $periodValue, targetCount: $targetCount")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    Scaffold(
        topBar = {
            DetailHeader(onBackClick = { onFinish() })
        }) { paddingValues ->
        Surface(modifier=Modifier.padding(paddingValues)) {
            Column {
                HabitTitleArea(habitName, startDate, periodValue, periodUnit, targetCount)
                HabitDetailArea(achievementRate)
            }
        }
    }
}

@Composable
fun HabitDetailArea(achievementRate: Float){
    Column(modifier = Modifier.padding(24.dp)) {
        CompleteRateDisplay(achievementRate,0.3f,0.2f)
    }
}

