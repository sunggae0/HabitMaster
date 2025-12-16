package com.example.habitmaster.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.model.UserStatus // 공용 UserStatus 모델 import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MypageScreen(
    stats: UserStatus,
    onFinish: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf("주간") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("마이페이지") },
                navigationIcon = {
                    IconButton(onClick = onFinish) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            TimePeriodToggle(selectedPeriod) { period ->
                selectedPeriod = period
            }

            Spacer(Modifier.height(16.dp))

            if (selectedPeriod == "주간") {
                AchievementCard(stats.achievementRate)
                Spacer(Modifier.height(16.dp))
                TrendCard(stats.trendChange)
            } else {
                MonthlyAchievementCard(stats.monthlyAchievementRate)
                Spacer(Modifier.height(16.dp))
                MonthlyTrendCard(stats.monthlyTrendChange)
            }

            Spacer(Modifier.height(16.dp))

            StatsGrid(stats)

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun MonthlyAchievementCard(rate: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("월간 목표 달성률", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = rate / 100f,
                    modifier = Modifier.size(150.dp),
                    color = Color(0xFF81C784),
                    strokeWidth = 15.dp,
                    trackColor = Color(0xFFF0F0F0)
                )
                Text(
                    text = "$rate%",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun MonthlyTrendCard(trendChange: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("월간 습관 트렌드", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Text(
                    "지난 달 대비 ${if (trendChange >= 0) "+$trendChange" else "$trendChange"}%",
                    color = if (trendChange >= 0) Color.Red else Color.Blue,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("월간 막대 그래프 (준비중)", color = Color.Gray)
            }
        }
    }
}

@Composable
fun TimePeriodToggle(selected: String, onPeriodSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFFF0F0F0)).padding(4.dp)
    ) {
        TogglePill(text = "주간", isSelected = selected == "주간", onClick = { onPeriodSelected("주간") }, modifier = Modifier.weight(1f))
        TogglePill(text = "월간", isSelected = selected == "월간", onClick = { onPeriodSelected("월간") }, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TogglePill(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = if (isSelected) Color(0xFFB1A7F5) else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color.Gray

    Surface(
        onClick = onClick,
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AchievementCard(rate: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("전체 목표 달성률", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                progress = { rate / 100f },
                modifier = Modifier.size(150.dp),
                color = Color(0xFFB1A7F5),
                strokeWidth = 15.dp,
                trackColor = Color(0xFFF0F0F0),
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )
                Text(
                    text = "$rate%",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun TrendCard(trendChange: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("습관 트렌드", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Text(
                    "지난 주 대비 +$trendChange%",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(32.dp))

            val barHeights = listOf(0.6f, 0.5f, 0.65f, 0.8f, 0.6f, 0.55f, 0.7f)
            val days = listOf("일", "월", "화", "수", "목", "금", "토")

            Row(
                modifier = Modifier.fillMaxWidth().height(140.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                barHeights.forEachIndexed { index, heightRatio ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(30.dp).height(90.dp * heightRatio).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(Color(0xFFB1A7F5))
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(days[index], color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatsGrid(stats: UserStatus) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatItem(iconRes = Color.Red, title = "현재 연속 달성", value = "${stats.currentStreak}일", modifier = Modifier.weight(1f))
            StatItem(iconRes = Color.Yellow, title = "총 달성 횟수", value = "${stats.totalAchieved}회", modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatItem(iconRes = Color.Green, title = "최고 연속 달성", value = "${stats.bestStreak}일", modifier = Modifier.weight(1f))
            StatItem(iconRes = Color.Blue, title = "참여 중인 챌린지", value = "${stats.activeChallenges}개", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatItem(iconRes: Color, title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
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
