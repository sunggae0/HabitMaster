package com.example.habitmaster.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmaster.core.model.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onFinish: () -> Unit,
    onShowProfileEditDialog: () -> Unit,
    onShowPasswordChangeDialog: () -> Unit,
    notificationEnabled: Boolean,
    onNotificationEnabledChange: (Boolean) -> Unit,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onLogoutClick: () -> Unit,
    onDataResetClick: () -> Unit,
    onDataSaveClick: () -> Unit,
    onDataRestoreClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsSection(title = "Account") {
                SettingsItem(icon = Icons.Outlined.Person, text = "프로필 설정", onClick = onShowProfileEditDialog)
                SettingsItem(icon = Icons.Outlined.Lock, text = "비밀번호 변경", onClick = onShowPasswordChangeDialog)
            }
            SettingsSection(title = "일반") {
                SettingsItem(
                    icon = Icons.Outlined.Notifications,
                    text = "알림 설정",
                    trailingContent = {
                        Switch(
                            checked = notificationEnabled,
                            onCheckedChange = onNotificationEnabledChange
                        )
                    }
                ) { onNotificationEnabledChange(!notificationEnabled) }
                SettingsItem(
                    icon = Icons.Outlined.BrightnessMedium,
                    text = "라이트/다크 모드",
                    trailingContent = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onDarkModeChange
                        )
                    }
                ) { onDarkModeChange(!isDarkMode) }
            }
            SettingsSection(title = "데이터 지원") {
                SettingsItem(icon = Icons.Outlined.CloudUpload, text = "데이터 저장", onClick = onDataSaveClick)
                SettingsItem(icon = Icons.Outlined.Restore, text = "데이터 복구", onClick = onDataRestoreClick)
                SettingsItem(
                    icon = Icons.Outlined.Delete,
                    text = "데이터 초기화",
                    textColor = MaterialTheme.colorScheme.error,
                    onClick = onDataResetClick
                )
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    SettingsItem(
                        icon = Icons.AutoMirrored.Outlined.Logout,
                        text = "로그아웃",
                        textColor = MaterialTheme.colorScheme.error,
                        onClick = onLogoutClick
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileEditDialog(
    profile: Profile?,
    onDismiss: () -> Unit,
    onSave: (newName: String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    LaunchedEffect(profile) {
        if (profile != null) {
            name = profile.name
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("프로필 수정") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        imageVector = Icons.Default.Person, // Placeholder
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { /* TODO: Change picture */ },
                        contentScale = ContentScale.Crop
                    )
                    Text("변경", modifier = Modifier.padding(top = 80.dp))
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("이름") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { TextButton(onClick = { onSave(name) }) { Text("저장") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
    )
}

// [수정] onSave 파라미터 추가
@Composable
fun PasswordChangeDialog(
    onDismiss: () -> Unit,
    onSave: (currentPassword: String, newPassword: String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val isError = remember(newPassword, confirmPassword) { newPassword.isNotEmpty() && newPassword != confirmPassword }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("비밀번호 변경") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("현재 비밀번호") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("새 비밀번호") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("새 비밀번호 확인") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = isError, // 에러 상태 표시
                    supportingText = { if (isError) Text("새 비밀번호가 일치하지 않습니다.") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(currentPassword, newPassword) },
                enabled = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && !isError // 저장 버튼 활성화 조건
            ) {
                Text("저장")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
    )
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) { content() }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    text: String,
    textColor: Color = Color.Unspecified,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = if (textColor != Color.Unspecified) textColor else LocalContentColor.current
        )
        Text(text = text, color = textColor, style = MaterialTheme.typography.bodyLarge)
        if (trailingContent != null) {
            Spacer(modifier = Modifier.weight(1f))
            trailingContent()
        }
    }
}
