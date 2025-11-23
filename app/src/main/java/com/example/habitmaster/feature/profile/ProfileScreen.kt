package com.example.habitmaster.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(onFinish: () -> Unit) {
    Text("프로필 화면입니다")
    //TODO: ui 구현
    data class ProfileSlotUi(
        val name: String? = null,
        val hasImage: Boolean = false,
    )

    // 4개의 슬롯 상태
    var slots by remember { mutableStateOf(List(4) { ProfileSlotUi() }) }

    // 현재 편집 중인 슬롯 인덱스 (null이면 아무 것도 편집 안 하는 상태)
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    // 편집 단계: 이름/비밀번호 입력 단계인지, 사진 선택 단계인지
    var isPhotoStep by remember { mutableStateOf(false) }

    // 임시 입력 값들 (슬롯 저장 전까지 보관)
    var tempName by remember { mutableStateOf("") }
    var tempPassword by remember { mutableStateOf("") }
    var tempHasPhoto by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 제목
        Text(
            text = "사용하실 프로필을 선택해주세요",
            // fontFamily = AppFontFamily.Title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2 x 2 프로필 그리드
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(2) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(2) { col ->
                        val index = row * 2 + col
                        val slot = slots[index]

                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    when {
                                        slot.name != null -> Color(0xFFE8ECFF) // 선택된 유저 느낌 색
                                        else -> Color(0xFFF5F5F5)             // 빈 슬롯 배경
                                    }
                                )
                                .clickable {
                                    if (slot.name != null) {
                                        // TODO: 해당 프로필로 메인 페이지로 이동
                                    } else {
                                        // 새 프로필 생성 플로우 시작
                                        editingIndex = index
                                        isPhotoStep = false
                                        tempName = ""
                                        tempPassword = ""
                                        tempHasPhoto = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (slot.name != null) {
                                // 생성된 유저 UI
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFDDDDDD))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = slot.name ?: "",
                                        // fontFamily = AppFontFamily.Body,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                }
                            } else {
                                // 빈 슬롯: 플러스 버튼
                                Text(
                                    text = "+",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Light,
                                    color = Color(0xFF888888)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ===================== 편집 영역 (이름/비번 입력 or 사진 선택) =====================
        editingIndex?.let { index ->
            if (!isPhotoStep) {
                // 1단계: 이름 + 패스워드 입력
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "사용하실 이름과 패스워드를 입력해주세요",
                        // fontFamily = AppFontFamily.Body,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text(text = "User Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tempPassword,
                        onValueChange = { tempPassword = it },
                        label = { Text(text = "Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                // 이름이 비어 있지 않을 때만 다음 단계로
                                if (tempName.isNotBlank()) {
                                    isPhotoStep = true
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "다음",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                // 2단계: 프로필 사진 선택 / 건너뛰기
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "사용하실 프로필 사진을 추가해주세요",
                        // fontFamily = AppFontFamily.Body,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 사진 업로드 영역 (실제 기능은 나중에 연결)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFF5F5F5))
                            .clickable {
                                // TODO: 갤러리/카메라 연동
                                // 지금은 UI 확인용으로 토글만
                                tempHasPhoto = !tempHasPhoto
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (tempHasPhoto) {
                            Text(
                                text = "사진이 선택되었습니다",
                                // fontFamily = AppFontFamily.Body,
                                fontSize = 14.sp,
                                color = Color(0xFF444444)
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "+",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Light,
                                    color = Color(0xFF888888)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "사진 추가",
                                    fontSize = 12.sp,
                                    color = Color(0xFF888888)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                // 현재 편집 중인 슬롯을 실제 슬롯 리스트에 반영
                                val updated = slots.toMutableList()
                                updated[index] = ProfileSlotUi(
                                    name = tempName.ifBlank { "User${index + 1}" },
                                    hasImage = tempHasPhoto
                                )
                                slots = updated

                                // 편집 상태 초기화
                                editingIndex = null
                                isPhotoStep = false
                                tempHasPhoto = false
                                tempName = ""
                                tempPassword = ""
                            }
                        ) {
                            Text(
                                text = if (tempHasPhoto) "다음" else "건너뛰기",
                                fontSize = 14.sp,
                                // 건너뛰기/다음 둘 다 링크 느낌의 파란색
                                color = Color(0xFF1E88E5)
                            )
                        }
                    }
                }
            }
        }
    }
}