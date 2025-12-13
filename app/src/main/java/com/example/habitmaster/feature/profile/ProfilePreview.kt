package com.example.habitmaster.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

// 임시 데이터
data class UserStatus(
    val achievementRate: Int = 75,
    val trendChange : Int = 4,
    val currentStreak : Int = 15,
    val totalAchieved : Int = 234,
    val bestStreak : Int = 43,
    val activeChallenges : Int = 5
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onFinish: () -> Unit) {
    val stats = UserStatus() // 더미 데이터 설정
    var selectedPeriod by remember { mutableStateOf("주간")}

    val scrollState = rememberScrollState()

    Scaffold (
        topBar = {
            // 1. 상단 바 구현하기 (뒤로가기 버튼, 타이틀)
            CenterAlignedTopAppBar(
                title = {Text("나의 통계")},
                navigationIcon = {
                    IconButton(onClick = onFinish) { // 뒤로가기 버튼
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ){paddingValues ->
        // 2. 메인 컨텐츠 영역
        Column (
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ){
            //3. 주간, 월간 버튼
            TimePeriodToggle(selectedPeriod){ period ->
                selectedPeriod = period
            }

            Spacer(Modifier.height(16.dp))

            //4. 전체 목표 달성률 카드
            AchievementCard(stats.achievementRate)

            Spacer(Modifier.height(16.dp))

            //5. 습관 트렌드 카드 (막대기 표)
            TrendCard(stats.trendChange)

            Spacer(Modifier.height(16.dp))

            //6. 통계 그리드 (4가지 항목)
            StatsGrid(stats)

            Spacer(Modifier.height(32.dp))
        }
    }

}
// 주간, 월간 버튼 만들기
@Composable
fun TimePeriodToggle(selected : String, onPeriodSelected: (String) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF0F0F0))
            .padding(4.dp)
    ){
        //주간 버튼
        TogglePill(
            text ="주간",
            isSelected = selected == "주간",
            onClick = { onPeriodSelected("주간") },
            modifier = Modifier.weight(1f)
        )
        // 월간 버튼
        TogglePill(
            text = "월간",
            isSelected = selected == "월간",
            onClick = { onPeriodSelected("월간") },
            modifier = Modifier.weight(1f)
        )
    }
}
//버튼 개별 컴포넌트
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
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
// 전체 목표 달성률 원형 그래프
@Composable
fun AchievementCard(rate: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("전체 목표 달성률", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))

            // 프로그레스 링을 모방
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Compose의 CircularProgressIndicator를 사용하여 링 모양 구현 가능
                CircularProgressIndicator(
                    progress = rate / 100f,
                    modifier = Modifier.size(150.dp),
                    color = Color(0xFFB1A7F5),
                    strokeWidth = 15.dp,
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

//습관 트렌드 막대 그래프
// 습관 트렌드 카드 Composable (바 차트 모방)
@Composable
fun TrendCard(trendChange: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp), // Row의 전체 높이를 좀 더 여유롭게 확보 (텍스트 공간 포함)
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom // 막대와 요일 텍스트를 바닥에 정렬
            ) {
                barHeights.forEachIndexed { index, heightRatio ->
                    Column(
                        modifier = Modifier.weight(1f), // 각 요일 컬럼이 균등한 공간 차지
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 막대 부분
                        Box(
                            modifier = Modifier
                                .width(30.dp) // 막대의 두께를 30.dp로 명시적으로 지정하여 두껍게 만듭니다.
                                .height(90.dp * heightRatio) // 막대의 최대 높이를 90.dp로 제한하고 비율을 적용
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(Color(0xFFB1A7F5))
                        )
                        // 막대와 요일 텍스트 사이의 간격
                        Spacer(Modifier.height(8.dp))
                        // 요일 텍스트
                        Text(days[index], color = Color.Gray, fontSize = 12.sp) // 글씨 크기 조정
                    }
                }
            }
        }
    }
}

//하단 제일 마지막 4가지 통계 업적 구성하기
// 하단 4가지 통계 항목 그리드 Composable (LazyVerticalGrid를 일반 Column/Row로 변경)
@Composable
fun StatsGrid(stats: UserStatus) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 첫 번째 줄 (상단 두 카드)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // StatItem에 weight(1f)를 적용하여 공간을 균등하게 나눕니다.
            StatItem(
                iconRes = Color.Red, title = "현재 연속 달성", value = "${stats.currentStreak}일",
                modifier = Modifier.weight(1f)
            )
            StatItem(
                iconRes = Color.Yellow, title = "총 달성 횟수", value = "${stats.totalAchieved}회",
                modifier = Modifier.weight(1f)
            )
        }

        // 두 번째 줄 (하단 두 카드)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatItem(
                iconRes = Color.Green, title = "최고 연속 달성", value = "${stats.bestStreak}일",
                modifier = Modifier.weight(1f)
            )
            StatItem(
                iconRes = Color.Blue, title = "참여 중인 챌린지", value = "${stats.activeChallenges}개",
                modifier = Modifier.weight(1f)
            )
        }
    }
}
// 개별 통계 항목 카드 Composable
@Composable
fun StatItem(iconRes: Color, title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        // 외부 Row의 weight에 의해 width가 결정되도록 fillMaxWidth()를 제거
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(iconRes)
                )
                Spacer(Modifier.width(4.dp))
                Text(title, fontSize = 10.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}