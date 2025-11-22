package com.example.habitmaster.feature.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.designsystem.PretendardFamily
import com.example.habitmaster.R
import com.example.habitmaster.core.data.habitDummyData
import java.text.DecimalFormat


@Composable
fun MainScreen(onFinish: () -> Unit) {
    Column{
        InformationArea()
        HabitList()
    }
}


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
fun Clock(h:Int, m:Int){
    Row(verticalAlignment = Alignment.Bottom) {
        val clockFormat = DecimalFormat("00")

        Text(
            text = "${clockFormat.format(h)}:${clockFormat.format(m)}",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 64.sp
        )
        Text(
            text = "AM",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
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

@Composable
fun DateIndex(date:Int, day:String){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text="$date",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
        Text(
            text=day,
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp
        )
    }
}

@Composable
fun HabitCard(habitName:String, achievementRate: Float, habitCompleteList: List<Boolean>) {
    val percentage = achievementRate * 100

    Box(
        modifier = Modifier
            .width(360.dp)
            .wrapContentHeight()
            .graphicsLayer {
                shape = RoundedCornerShape(15.dp)
                clip = true
            }
            .background(Color(0xFFE3E3E3))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    drawRect(
                        color = Color(0xFF6A86F7),
                        size = Size(size.width * achievementRate, size.height)
                    )
                }
        )
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                    Text(
                        "${percentage.toInt()}%",
                        fontFamily = PretendardFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    habitCompleteList.forEach { status ->
                        Spacer(modifier = Modifier.width(25.dp))
                        Icon(
                            painter = painterResource(
                                id = if (status) R.drawable.ic_checkicon_true else R.drawable.ic_checkicon_false
                            ),
                            contentDescription = if (status) "O" else "X",
                            //modifier = Modifier.size(8.dp) // 아이콘 크기
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(13.dp))
            Text(
                habitName,
                fontFamily = PretendardFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp
            )
        }
    }
}