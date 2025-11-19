package com.example.habitmaster.feature.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.designsystem.PretendardFamily
import com.example.habitmaster.R


@Composable
fun MainScreen(onFinish: () -> Unit) {
    Text("메인 화면입니다")
    //TODO: ui 구현
}

@Composable
fun HabitList(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(15.dp))
            .border(
                width=1.dp,
                color = Color(0xFFDADADA))
            .padding(25.dp)
    ){
        Box(modifier = Modifier.fillMaxWidth()){

        }
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
                fontSize = 18.sp
            )
        }
    }
}