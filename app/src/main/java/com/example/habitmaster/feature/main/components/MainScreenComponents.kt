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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Locale

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
            val strokeWidth = 2.dp.toPx()
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
fun Clock(h:Int, m:Int){ // 하위 호환성을 위해 파라미터 유지, 하지만 내부에서 현재 시각 사용
    
    // 현재 시각 상태
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

    // 1분마다 시각 업데이트
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance()
            delay(1000L * 60) // 1분 대기 (초 단위 갱신이 필요하다면 줄여야 함)
        }
    }
    
    val hour24 = currentTime.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.get(Calendar.MINUTE)
    
    // 12시간제 변환
    val hour12 = if (hour24 % 12 == 0) 12 else hour24 % 12
    val amPm = if (hour24 < 12) "AM" else "PM"

    Row(verticalAlignment = Alignment.Bottom) {
        val clockFormat = DecimalFormat("00")

        Text(
            text = "${clockFormat.format(hour12)}:${clockFormat.format(minute)}",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 64.sp
        )
        Text(
            text = amPm,
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

// Boolean 리스트를 받는 기존 함수 유지 (호환성)
@Composable
fun HabitCard(
    habitName: String,
    achievementRate: Float,
    habitCompleteList: List<Boolean?>,
    onClick: () -> Unit = {}
) {
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
            .clickable { onClick() } // 클릭 이벤트 추가
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
                    // 최대 7개까지만 표시 (화면 공간상)
                    habitCompleteList.take(7).forEach { status ->
                        Spacer(modifier = Modifier.width(25.dp))
                        when (status) {
                            true -> {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_checkicon_true),
                                    contentDescription = "Success",
                                    tint = Color.Unspecified
                                )
                            }
                            false -> {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_checkicon_false),
                                    contentDescription = "Fail",
                                    tint = Color.Unspecified
                                )
                            }
                            null -> {
                                // 아직 도래하지 않은 날짜 등: 하이픈(-) 혹은 빈 공간
                                // 여기서는 텍스트로 - 표시하거나 별도 아이콘 사용
                                Text(
                                    text = "-",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            }
                        }
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
