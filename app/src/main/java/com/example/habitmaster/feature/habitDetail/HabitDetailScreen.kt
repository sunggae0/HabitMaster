package com.example.habitmaster.feature.habitDetail

import android.R.attr.fontFamily
import android.R.attr.fontWeight
import android.util.Log
import android.util.Log.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun HabitDetailScreen(habitId:String, onFinish: () -> Unit,onNavigateToHabitEdit: (habitId:String,profileId:String) -> Unit) {
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
            d("HabitDetailScreen", "HabitId: $habitId, ProfileId: $currentProfileId, periodValue: $periodValue, targetCount: $targetCount")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            DetailHeader(onBackClick = { onFinish() })
        }) { paddingValues ->
        Surface(modifier=Modifier.padding(paddingValues)) {
            Column {
                HabitTitleArea(habitName, startDate, periodValue, periodUnit, targetCount, onEditClick = {onNavigateToHabitEdit(habitId,currentProfileId!!)})
                HabitDetailArea(achievementRate)
                Button(
                    onClick = {
                        Log.d("HabitDetailScreen", "habitId: $habitId, profileId: $currentProfileId")
                        scope.launch {
                            try {
                                val profileId = currentProfileId ?: return@launch
                                val habitToUpdate = currentHabit ?: return@launch

                                val now = System.currentTimeMillis()
                                val lastSuccess = habitToUpdate.lastSuccessDate

                                val isSameDay = if (lastSuccess != null) {
                                    val cal1 =
                                        Calendar.getInstance().apply { timeInMillis = lastSuccess }
                                    val cal2 = Calendar.getInstance().apply { timeInMillis = now }
                                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
                                } else {
                                    false
                                }

                                if (isSameDay) {
                                    Log.d("HabitDetailScreen", "다음 날 자정 이후에 성공버튼을 다시 누를 수 있습니다.")
                                    snackbarHostState.showSnackbar("다음 날 자정 이후에 성공버튼을 다시 누를 수 있습니다.")
                                    return@launch
                                }

                                val updatedList = habitToUpdate.completeList.toMutableList()
                                if (updatedList.size < 7) {
                                    updatedList.add(true)
                                } else {
                                    updatedList.removeAt(0)
                                    updatedList.add(true)
                                }

                                val successCount = updatedList.count { it == true }
                                val target = habitToUpdate.targetCount

                                val newRate = if (target > 0) {
                                    (successCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
                                } else 0f

                                val updatedHabit = habitToUpdate.copy(
                                    completeList = updatedList,
                                    achievementRate = newRate,
                                    lastSuccessDate = now
                                )

                                currentHabit = updatedHabit
                                repository.updateHabit(profileId, updatedHabit)
                                Log.d("HabitDetailScreen", "오늘 습관 달성 완료!")
                                snackbarHostState.showSnackbar("오늘 습관 달성 완료!")
                            } catch (e: Exception) {
                                e.printStackTrace()
                                snackbarHostState.showSnackbar("오류 발생: ${e.message}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A86F7)),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ){
                    Text("달성 완료")
                }
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

fun habitComplite(profileId: String, habitId: String){

}