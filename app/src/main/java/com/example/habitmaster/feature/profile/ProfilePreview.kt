package com.example.habitmaster.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onFinish: () -> Unit) {
    // 프로필 목록 관리 (최대 4개)
    // 실제로는 Room DB나 Firebase 등에서 불러와야 하지만, 여기서는 로컬 상태로 관리한다고 가정
    val profiles = remember { mutableStateListOf<ProfileData>() }

    // 프로필 생성 다이얼로그 노출 여부
    var showCreateDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("프로필 선택") },
                navigationIcon = {
                    // 뒤로가기 버튼은 필요에 따라 onFinish 혹은 popBackStack 호출
                    // 여기서는 온보딩에서 넘어왔을 때 앱 종료가 아니면 굳이 필요 없을 수도 있지만
                    // 뒤로가기 동작이 필요하다면 유지
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "사용할 프로필을 선택해주세요",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "최대 4개까지 만들 수 있어요",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 프로필 목록 그리드 (2열)
            // LazyVerticalGrid를 사용하면 좋지만,
            // 전체 화면이 스크롤 가능해야 한다면 Column 안에서 LazyVerticalGrid는 높이 계산 문제가 있을 수 있습니다.
            // 아이템 개수가 적으므로(최대 4개) FlowRow나 간단한 Column/Row 조합을 사용할 수도 있습니다.
            // 여기서는 간단히 Row 2개로 나누거나 Grid를 쓰되 높이를 고정/wrapContent로 합니다.

            // 간단하게 Column + Row 로 2x2 배치 구현
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val chunkedProfiles = (profiles + if (profiles.size < 4) listOf(null) else emptyList())
                    .chunked(2)

                chunkedProfiles.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { profileData ->
                            Box(modifier = Modifier.weight(1f)) {
                                if (profileData != null) {
                                    // 이미 생성된 프로필 카드
                                    ExistingProfileCard(
                                        profile = profileData,
                                        onClick = {
                                            // 프로필 선택 시 메인 화면으로 이동
                                            onFinish()
                                        }
                                    )
                                } else {
                                    // 프로필 추가 버튼 (비어있는 슬롯)
                                    AddProfileCard(
                                        onClick = { showCreateDialog = true }
                                    )
                                }
                            }
                        }
                        // 홀수개일 때 빈 공간 채우기
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // 프로필 생성 다이얼로그
    if (showCreateDialog) {
        CreateProfileDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                if (profiles.size < 4) {
                    profiles.add(ProfileData(name = name))
                }
                showCreateDialog = false
            }
        )
    }
}

// 간단한 프로필 데이터 모델
data class ProfileData(
    val name: String,
    val color: Color = Color(0xFFB1A7F5) // 임시 랜덤 색상 대신 고정
)

@Composable
fun ExistingProfileCard(
    profile: ProfileData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(0.8f) // 정사각형에 가깝게 혹은 세로로 약간 길게
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(profile.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = profile.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun AddProfileCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(0.8f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), // 회색 배경
        // elevation = CardDefaults.cardElevation(0.dp) // 평평하게
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Profile",
                    tint = Color.Gray,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "프로필 추가",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CreateProfileDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새 프로필 만들기") },
        text = {
            Column {
                Text("이름을 입력해주세요.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    placeholder = { Text("이름") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name)
                    }
                }
            ) {
                Text("생성")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
