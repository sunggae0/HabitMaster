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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.time.format.DateTimeFormatter
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

fun formatToMonthDay(timeMillis: Long): String {
    val formatter = DateTimeFormatter
        .ofPattern("y년 M월 d일", Locale.KOREAN)

    return Instant.ofEpochMilli(timeMillis)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}
@Composable
fun HabitTitleArea(title:String, startDate:Long?, periodValue:String, periodUnit:String, targetCount:String = "설정 안함",onEditClick: () -> Unit) {
    val formattedDate = startDate?.let {
        remember(startDate) {
            formatToMonthDay(startDate)
        }
    }?:"NaD"
    Box(modifier = Modifier.background(Color(0xFF6A86F7))) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start=25.dp,end=25.dp, bottom = 25.dp,top=10.dp)
        ) {
            HabitDetailTitle(title, formattedDate,onEditClick)
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
            ) {
                Box(modifier = Modifier.weight(1f)) { HabitDetailInfo("주기", periodValue + periodUnit) }
                Box(modifier = Modifier.weight(1f)) { HabitDetailInfo("목표 횟수", targetCount) }
            }
        }
    }
}