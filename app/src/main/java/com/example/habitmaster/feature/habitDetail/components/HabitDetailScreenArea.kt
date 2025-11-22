package com.example.habitmaster.feature.habitDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun HabitTitleArea() {
    Box(modifier = Modifier.background(Color(0xFF6A86F7))) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start=25.dp,end=25.dp, bottom = 25.dp,top=10.dp)
        ) {
            HabitDetailTitle("습관 상세보기 테스트", "2025년 11월 20일")
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
            ) {
                Box(modifier = Modifier.weight(1f)) { HabitDetailInfo("주기", "매일") }
                Box(modifier = Modifier.weight(1f)) { HabitDetailInfo("수행 시간", "17:30") }
            }
        }
    }
}