package com.example.habitmaster.feature.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.habitmaster.core.data.firebase.FirebaseProfileRepository
import com.example.habitmaster.core.model.Profile
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onFinish: () -> Unit) {
    val repository = remember { FirebaseProfileRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Firebase에서 불러온 프로필 목록
    val profiles = remember { mutableStateListOf<Profile>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // 초기 데이터 로딩
    LaunchedEffect(Unit) {
        try {
            repository.observeProfiles()
                .catch { e ->
                    e.printStackTrace()
                    errorMessage = "데이터 로드 실패: ${e.localizedMessage}"
                }
                .collect { list ->
                    profiles.clear()
                    profiles.addAll(list)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "오류 발생: ${e.localizedMessage}"
        }
    }
    
    // 에러 발생 시 스낵바 표시
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    // 프로필 생성 다이얼로그 노출 여부
    var showCreateDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("프로필 선택") },
                navigationIcon = {
                    // 뒤로가기 버튼은 필요에 따라 onFinish 혹은 popBackStack 호출
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
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 아직 로딩 전이거나 데이터가 없어도 profiles 크기에 맞춰서 표시
                // 최대 4개 슬롯을 만들기 위해 profiles 리스트에 가상의 null을 추가하여 chunked
                val displayList = profiles.toList() + if (profiles.size < 4) listOf(null) else emptyList()
                val chunkedProfiles = displayList.chunked(2)

                chunkedProfiles.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { profileItem ->
                            Box(modifier = Modifier.weight(1f)) {
                                if (profileItem != null) {
                                    // 이미 생성된 프로필 카드
                                    ExistingProfileCard(
                                        profile = profileItem,
                                        onClick = {
                                            // TODO: 비밀번호 확인 후 로그인 등의 절차가 필요할 수 있음
                                            onFinish()
                                        }
                                    )
                                } else {
                                    // 프로필 추가 버튼 (비어있는 슬롯)
                                    // 이미 4개 꽉 찼으면 마지막 null이 안들어갔으므로 렌더링 안됨
                                    if (profiles.size < 4) {
                                        AddProfileCard(
                                            onClick = { showCreateDialog = true }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.fillMaxSize())
                                    }
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
            onConfirm = { name, password, photoUri ->
                scope.launch {
                    try {
                        // 1. 프로필 메타데이터 생성 (Firestore)
                        val newProfile = repository.createProfile(name, password)
                        
                        // 2. 사진이 있으면 업로드 (Storage) 후 URL 업데이트 (Firestore)
                        if (photoUri != null) {
                            val downloadUrl = repository.uploadProfilePhoto(newProfile.id, photoUri)
                            repository.updateProfilePhotoUrl(newProfile.id, downloadUrl)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        errorMessage = "프로필 생성 실패: ${e.localizedMessage}"
                    }
                }
                showCreateDialog = false
            }
        )
    }
}


@Composable
fun ExistingProfileCard(
    profile: Profile,
    onClick: () -> Unit
) {
    // 임시 색상 로직 (프로필마다 다르게 보이게)
    val color = Color(0xFFB1A7F5)

    Card(
        modifier = Modifier
            .aspectRatio(0.8f) 
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
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                // 실제 이미지가 있으면 로드해야 하지만, 여기서는 coil 라이브러리 등이 없으므로 
                // photoUrl이 있으면 아이콘 색상을 다르게 하거나 텍스트로 표시하는 정도로 대체
                // (실제 구현 시 Coil의 AsyncImage 사용 권장)
                if (profile.photoUrl != null) {
                   // 이미지가 있다는 표시 (파란색 아이콘)
                   Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Blue, 
                        modifier = Modifier.size(36.dp)
                   )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), 
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
    onConfirm: (String, String, Uri?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    
    var passwordVisible by remember { mutableStateOf(false) }

    // 갤러리 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새 프로필 만들기") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 프로필 사진 선택 영역
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { 
                            galleryLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        // 선택된 이미지가 있으면 표시해야 함 (Coil 필요)
                        // 여기서는 간단히 텍스트로 대체
                        Text("사진\n선택됨", fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Select Photo",
                            tint = Color.White
                        )
                    }
                }
                
                // 사진 건너뛰기 안내 (옵션)
                TextButton(onClick = { photoUri = null }) {
                    Text("사진 건너뛰기 (초기화)", fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // 이름 입력
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text("이름") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 비밀번호 입력
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    label = { Text("비밀번호 (4자리 이상)") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // 유효성 검사: 이름 비어있지 않음, 비밀번호 4자리 이상
                    if (name.isNotBlank() && password.length >= 4) {
                        onConfirm(name, password, photoUri)
                    }
                },
                enabled = name.isNotBlank() && password.length >= 4
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
