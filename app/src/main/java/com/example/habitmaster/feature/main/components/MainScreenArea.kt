package com.example.habitmaster.feature.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.data.habitDummyData
import com.example.habitmaster.core.designsystem.PretendardFamily

@Composable
fun InformationArea(){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(25.dp)) {
        Clock(12,0)
        Text(
            text="sunggae0 님, 좋은 아침입니다!",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Light,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(35.dp))
        Column() {
            Text("아침에 자주 달성하는 습관:")
            Spacer(modifier=Modifier.height(5.dp))
            HabitCard("Habit View",0.7f, listOf(true,false,true,false,true,false,true))
        }

    }
}

@Composable
fun HabitList(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(15.dp))
            .border(
                width=1.dp,
                color = Color(0xFFDADADA))
    ){
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp)){
            Row(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = "습관 목록",
                    fontFamily = PretendardFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )
            }
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                for (i in 1..7){
                    DateIndex(date = i, day = "MON")
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Spacer(modifier=Modifier.width(2.dp))
            }
        }

        LazyColumn(modifier = Modifier
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