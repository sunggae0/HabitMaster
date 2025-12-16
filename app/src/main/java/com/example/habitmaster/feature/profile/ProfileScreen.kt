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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.habitmaster.core.data.firebase.FirebaseProfileRepository
import com.example.habitmaster.core.model.Profile
import com.example.habitmaster.ui.theme.HabitMasterTheme
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onFinish: (String) -> Unit) {
    val repository = remember { FirebaseProfileRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val profiles = remember { mutableStateListOf<Profile>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    var showCreateDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("프로필 선택") },
                navigationIcon = {
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

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                                    ExistingProfileCard(
                                        profile = profileItem,
                                        onClick = {
                                            // 선택된 프로필 ID 전달
                                            onFinish(profileItem.id)
                                        }
                                    )
                                } else {
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
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateProfileDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, password, photoUri ->
                scope.launch {
                    try {
                        val newProfile = repository.createProfile(name, password)
                        if (photoUri != null) {
                            val downloadUrl = repository.uploadProfilePhoto(newProfile.id, photoUri)
                            repository.updateProfilePhotoUrl(newProfile.id, downloadUrl)
                        }
                        // 생성 후 바로 해당 프로필로 진입하려면 여기서 onFinish(newProfile.id) 호출 가능
                        // 하지만 목록에서 선택하도록 유도하는 것이 일반적일 수 있음. 여기서는 다이얼로그만 닫음.
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
                if (profile.photoUrl != null) {
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
                        Text("사진\n선택됨", fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Select Photo",
                            tint = Color.White
                        )
                    }
                }

                TextButton(onClick = { photoUri = null }) {
                    Text("사진 건너뛰기 (초기화)", fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text("이름") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

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

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun ProfilePreview() {
    HabitMasterTheme {
        ProfileScreen(onFinish = {})
    }
}