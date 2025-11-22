package com.example.habitmaster.feature.habitDetail

import android.R.attr.fontFamily
import android.R.attr.fontWeight
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.designsystem.PretendardFamily
import com.example.habitmaster.feature.habitDetail.components.DetailHeader
import com.example.habitmaster.feature.habitDetail.components.HabitTitleArea

@Composable
fun HabitDetailScreen(onFinish: () -> Unit) {
    Scaffold(
        topBar = {
            DetailHeader(onBackClick = { /* 뒤로가기 동작 구현 */ })
        }) { paddingValues ->
        Surface(modifier=Modifier.padding(paddingValues)) {
            Column {
                HabitTitleArea()
                HabitDetailArea()
            }
        }
    }
}

@Composable
fun HabitDetailArea(){
    Column(modifier = Modifier.padding(24.dp)) {
        CompleteRateDisplay(0.5f,0.2f,0.5f)
    }
}

@Composable
fun CompleteRateDisplay(rate:Float, monthly:Float, annually:Float){
    val monthlyRate = (monthly*100).toInt()
    val annuallyRate = (annually*100).toInt()
    Column(){
        Box {
            Text(
                text = "성공률",
                fontFamily = PretendardFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
            Text(
                text = "${(rate * 100).toInt()}%",
                fontFamily = PretendardFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 128.sp,
            )
        }
        Row(){
            Text(
                text="Month ${if (monthlyRate>=0) "+" else "-"}$monthlyRate",
                fontFamily = PretendardFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
            Text(
                text="Year ${if (annuallyRate>=0) "+" else "-"}$annuallyRate",
                fontFamily = PretendardFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        }
    }
}