package com.example.habitmaster.feature.habitDetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.habitmaster.R
import com.example.habitmaster.core.designsystem.PretendardFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailHeader(onBackClick: () -> Unit) {
    // 이미지의 파란색과 유사한 색상 정의 (예: Color(0xFF6A86F7) 또는 Material Blue)
    val customHeaderColor = Color(0xFF6A86F7)
    val titleText = "습관 상세보기"

    CenterAlignedTopAppBar(
        modifier = Modifier.fillMaxWidth(),
        // 1. 타이틀 설정 (중앙 정렬됨)
        title = {
            Text(
                text = titleText,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                color = Color.White // 텍스트 색상을 흰색으로 설정
            )
        },
        // 2. 탐색 아이콘 설정 (왼쪽)
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_btn_backward), // 뒤로가기 화살표 아이콘
                    contentDescription = "뒤로 가기", tint = Color.White // 아이콘 색상을 흰색으로 설정
                )
            }
        },
        // 3. 배경색 및 Content Color 설정
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = customHeaderColor, // 헤더 배경색
            titleContentColor = Color.White,     // 타이틀 기본색 (위에서 Text에 직접 설정했지만, 일관성을 위해)
            navigationIconContentColor = Color.White // 탐색 아이콘 기본색
        )
        // actions 파라미터를 사용하여 오른쪽에 버튼을 추가할 수 있습니다. (예: RowScope.() -> Unit = {})
    )
}

@Composable
fun HabitDetailTitle(title: String, startDate: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White
        )
        Text(
            text = "$startDate 시작했어요.",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Light,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

@Composable
fun HabitDetailInfo(title: String, info: String) {
    Column() {
        Text(
            text = title,
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color.White
        )
        Text(
            text = info,
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            color = Color.White
        )
    }
}