package com.example.habitmaster.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.data.Habit
import com.example.habitmaster.core.data.firebase.FirebaseProfileRepository
import com.example.habitmaster.core.model.UserStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MypageScreen(
    profileId: String,
    onFinish: () -> Unit
) {
    val repository = remember { FirebaseProfileRepository() }
    var habits by remember { mutableStateOf<List<Habit>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(profileId) {
        repository.observeHabits(profileId).collect { fetchedHabits ->
            habits = fetchedHabits
            isLoading = false
        }
    }

    // 전체 달성률 계산
    val overallAchievementRate by remember(habits) {
        derivedStateOf {
            if (habits.isEmpty()) 0 else habits.map { it.achievementRate }.average().toInt()
        }
    }

    // 최고 연속 달성일 계산
    val bestStreak by remember(habits) {
        derivedStateOf {
            habits.maxOfOrNull { habit ->
                var maxStreak = 0
                var currentStreak = 0
                habit.completeList.forEach { completed ->
                    if (completed == true) {
                        currentStreak++
                    } else {
                        if (currentStreak > maxStreak) maxStreak = currentStreak
                        currentStreak = 0
                    }
                }
                if (currentStreak > maxStreak) maxStreak = currentStreak
                maxStreak
            } ?: 0
        }
    }

    // 현재 연속 달성일 계산
    val currentStreak by remember(habits) {
        derivedStateOf {
            habits.maxOfOrNull { habit ->
                var streak = 0
                for (i in habit.completeList.indices.reversed()) {
                    if (habit.completeList[i] == true) streak++ else break
                }
                streak
            } ?: 0
        }
    }

    // 진행중인 습관 개수 계산
    val ongoingHabitsCount by remember(habits) {
        derivedStateOf { habits.count { it.isActive } }
    }

    // 총 달성 횟수 계산
    val totalSuccessCount by remember(habits) {
        derivedStateOf { habits.sumOf { it.completeList.count { s -> s == true } } }
    }

    // [추가] 달성률 등급별 습관 분포도 데이터 계산
    val achievementDistribution by remember(habits) {
        derivedStateOf {
            val counts = MutableList(5) { 0 }
            habits.forEach {
                val ratePercent = (it.achievementRate * 100).toInt()
                val bucketIndex = when {
                    ratePercent in 0..20 -> 0
                    ratePercent in 21..40 -> 1
                    ratePercent in 41..60 -> 2
                    ratePercent in 61..80 -> 3
                    else -> 4
                }
                counts[bucketIndex]++
            }
            counts
        }
    }

    val calculatedStats = UserStatus(
        achievementRate = overallAchievementRate,
        currentStreak = currentStreak,
        totalAchieved = totalSuccessCount,
        bestStreak = bestStreak,
        activeChallenges = ongoingHabitsCount
    )

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("마이페이지") },
                navigationIcon = { IconButton(onClick = onFinish) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                Spacer(Modifier.height(16.dp))
                AchievementCard(calculatedStats.achievementRate)
                Spacer(Modifier.height(16.dp))
                // [추가] 새로운 분포도 차트
                AchievementDistributionChart(distribution = achievementDistribution)
                Spacer(Modifier.height(16.dp))
                StatsGrid(calculatedStats)
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// [추가] 달성률 등급별 습관 분포도 막대 그래프
@Composable
fun AchievementDistributionChart(distribution: List<Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("달성률 분포도", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(32.dp))

            val maxValue = (distribution.maxOrNull()?.toFloat() ?: 1f).coerceAtLeast(1f)
            val barHeights = distribution.map { it / maxValue }
            val labels = listOf("0-20%", "21-40%", "41-60%", "61-80%", "81-100%")

            Row(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                barHeights.forEachIndexed { index, heightRatio ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(distribution[index].toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(90.dp * heightRatio)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(Color(0xFFB1A7F5))
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(labels[index], color = Color.Gray, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCard(rate: Int) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("전체 목표 달성률", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = rate / 100f, modifier = Modifier.size(150.dp), color = Color(0xFFB1A7F5), strokeWidth = 15.dp, trackColor = Color(0xFFF0F0F0))
                Text("$rate%", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            }
        }
    }
}

@Composable
fun StatsGrid(stats: UserStatus) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatItem(Color.Red, "현재 연속 달성", "${stats.currentStreak}일", Modifier.weight(1f))
            StatItem(Color.Yellow, "총 달성 횟수", "${stats.totalAchieved}회", Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatItem(Color.Green, "최고 연속 달성", "${stats.bestStreak}일", Modifier.weight(1f))
            StatItem(Color.Blue, "진행중인 습관", "${stats.activeChallenges}개", Modifier.weight(1f))
        }
    }
}

@Composable
fun StatItem(iconRes: Color, title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(100.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(6.dp)).background(iconRes))
                Spacer(Modifier.width(4.dp))
                Text(title, fontSize = 10.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}
