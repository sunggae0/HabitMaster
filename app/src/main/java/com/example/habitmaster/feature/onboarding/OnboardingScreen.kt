package com.example.habitmaster.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
) {
    // "Change your life" 먼저 한 번만 fade in
    var titleVisible by remember { mutableStateOf(false) }

    // 아래 소개 문구들 (돌아가면서 노출)
    val introMessages = listOf(
        "작은 습관이 큰 변화를 만든다",
        "매일의 루틴을 기록하며 하루를 돌아봐요.",
        "루틴을 꾸준히 지키고, 달라진 나를 확인해요."
    )

    var subtitleVisible by remember { mutableStateOf(false) }
    var subtitleIndex by remember { mutableStateOf(0) }

    // 앱 설명 팝업
    var showAppInfoDialog by remember { mutableStateOf(false) }

    // 애니메이션 시작 로직
    LaunchedEffect(Unit) {
        delay(300)
        titleVisible = true

        delay(1000)
        while (true) {
            subtitleVisible = true      // fade in
            delay(2200)
            subtitleVisible = false     // fade out
            delay(400)
            subtitleIndex = (subtitleIndex + 1) % introMessages.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // 가운데 텍스트 영역
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = titleVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 900))
                ) {
                    Text(
                        text = "Change your life",
                        // fontFamily = AppFontFamily.Title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color(0x33000000),
                                offset = Offset(0f, 4f),
                                blurRadius = 10f
                            )
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = subtitleVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 700)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 700))
                ) {
                    Text(
                        text = introMessages[subtitleIndex],
                        // fontFamily = AppFontFamily.Body,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF444444),
                        lineHeight = 22.sp
                    )
                }
            }

            // 하단 버튼 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { onFinish() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        text = "시작하기",
                        // fontFamily = AppFontFamily.Button,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "앱 설명",
                    // fontFamily = AppFontFamily.Caption,
                    fontSize = 12.sp,
                    color = Color(0xFF888888),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { showAppInfoDialog = true }
                        .padding(top = 4.dp)
                )
            }
        }

        // 앱 설명 팝업
        if (showAppInfoDialog) {
            AlertDialog(
                onDismissRequest = { showAppInfoDialog = false },
                confirmButton = {
                    TextButton(onClick = { showAppInfoDialog = false }) {
                        Text(text = "확인")
                    }
                },
                title = {
                    Text(
                        text = "HabitMaster 소개",
                        // fontFamily = AppFontFamily.Title,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        text = "HabitMaster는 작은 습관을 시작으로 더 나은 삶을 만들 수 있도록 도와주는 서비스입니다.\n\n" +
                                "- 만들고 싶은 습관을 등록하고, 알림으로 잊지 않게 도와줘요.\n" +
                                "- 매일의 루틴을 체크하며 나의 변화를 기록할 수 있어요.\n" +
                                "- 통계를 통해 내가 얼마나 꾸준히 하고 있는지 한눈에 확인해요.",
                        // fontFamily = AppFontFamily.Body,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            )
        }
    }
}