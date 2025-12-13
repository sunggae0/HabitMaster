package com.example.habitmaster.feature.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.data.habitDummyData
import com.example.habitmaster.core.designsystem.PretendardFamily

@Composable
fun InformationArea(
    userName: String = "sunggae0",
    onSettingsClick: () -> Unit = {},
    onMypageClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp)
    ) {
        // 상단 아이콘 영역 (설정, 마이페이지)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            // 마이페이지 아이콘 (사람 상반신 형상)
            IconButton(onClick = onMypageClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "My Page",
                    tint = Color.Black
                )
            }
            
            // 설정 아이콘 (톱니바퀴)
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.Black
                )
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Clock(12, 0)
        Text(
            text = "$userName 님, 좋은 아침입니다!",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Light,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(35.dp))
        Column() {
            Text("아침에 자주 달성하는 습관:")
            Spacer(modifier = Modifier.height(5.dp))
            HabitCard("Habit View", 0.7f, listOf(true, false, true, false, true, false, true))
        }

    }
}

@Composable
fun HabitList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(15.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFDADADA)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp)
        ) {
            Row(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = "습관 목록",
                    fontFamily = PretendardFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )
            }
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                for (i in 1..7) {
                    DateIndex(date = i, day = "MON")
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(start = 25.dp, end = 25.dp, bottom = 25.dp)
        ) {
            items(habitDummyData) { habitData ->
                Spacer(modifier = Modifier.height(10.dp))
                HabitCard(
                    habitData.title,
                    habitData.achievementRate,
                    habitData.completeList
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
