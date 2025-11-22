package com.example.habitmaster.feature.main.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.R
import com.example.habitmaster.core.designsystem.PretendardFamily
import java.text.DecimalFormat

@Composable
fun AddHabitFAB(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)                              // 버튼 크기
            .background(
                color = Color(0xFFE3E3E3),           // 회색 배경 (제시 이미지)
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            // +
            val strokeWidth = 4.dp.toPx()
            val half = size.minDimension / 2

            // Vertical line
            drawLine(
                color = Color.Black,
                start = Offset(half, 0f),
                end = Offset(half, size.height),
                strokeWidth = strokeWidth
            )
            // Horizontal line
            drawLine(
                color = Color.Black,
                start = Offset(0f, half),
                end = Offset(size.width, half),
                strokeWidth = strokeWidth
            )
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