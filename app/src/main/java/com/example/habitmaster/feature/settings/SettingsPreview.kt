package com.example.habitmaster.feature.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.core.model.Profile // Profile 모델 import
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    HabitMasterTheme {
        var showProfileDialog by remember { mutableStateOf(false) }
        var showPasswordDialog by remember { mutableStateOf(false) }
        var notificationEnabled by remember { mutableStateOf(false) }
        var showNotificationDialog by remember { mutableStateOf(false) }
        var isDarkMode by remember { mutableStateOf(false) }
        var showLogoutDialog by remember { mutableStateOf(false) }
        var showDataResetDialog by remember { mutableStateOf(false) }
        var showDataResetSecondDialog by remember { mutableStateOf(false) }
        var confirmInputText by remember { mutableStateOf("") }

        SettingsScreen(
            onFinish = {},
            onShowProfileEditDialog = { showProfileDialog = true },
            onShowPasswordChangeDialog = { showPasswordDialog = true },
            notificationEnabled = notificationEnabled,
            onNotificationEnabledChange = {
                notificationEnabled = it
                showNotificationDialog = true
            },
            isDarkMode = isDarkMode,
            onDarkModeChange = { isDarkMode = it },
            onLogoutClick = { showLogoutDialog = true },
            onDataResetClick = { showDataResetDialog = true }
        )

        if (showProfileDialog) {
            ProfileEditDialog(
                profile = Profile(id = "preview", name = "기존 이름", passwordHash = "", photoUrl = null, createdAtMillis = 0L),
                onDismiss = { showProfileDialog = false },
                onSave = { _ -> }
            )
        }

        if (showPasswordDialog) {
            PasswordChangeDialog(
                onDismiss = { showPasswordDialog = false },
                onSave = { _, _ -> }
            )
        }

        if (showNotificationDialog) {
            val title = if (notificationEnabled) "알림이 켜졌습니다" else "알림이 꺼졌습니다"
            val text = if (notificationEnabled) "오늘 습관을 완료했는지 확인하는 알림을 보내드립니다." else "알림이 비활성화되었습니다."
            AlertDialog(
                onDismissRequest = { showNotificationDialog = false },
                title = { Text(title) },
                text = { Text(text) },
                confirmButton = { TextButton(onClick = { showNotificationDialog = false }) { Text("확인") } }
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("로그아웃") },
                text = { Text("정말 로그아웃 하시겠습니까?") },
                confirmButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("예") } },
                dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("아니요") } }
            )
        }

        if (showDataResetDialog) {
            AlertDialog(
                onDismissRequest = { showDataResetDialog = false },
                title = { Text("데이터 초기화") },
                text = { Text("정말 모든 데이터를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.") },
                confirmButton = { TextButton(onClick = { showDataResetDialog = false; showDataResetSecondDialog = true; confirmInputText = "" }) { Text("삭제") } },
                dismissButton = { TextButton(onClick = { showDataResetDialog = false }) { Text("취소") } }
            )
        }

        if (showDataResetSecondDialog) {
            AlertDialog(
                onDismissRequest = { showDataResetSecondDialog = false },
                title = { Text("최종 확인") },
                text = { OutlinedTextField(value = confirmInputText, onValueChange = { confirmInputText = it }, label = { Text("'삭제'를 입력하세요") }, singleLine = true) },
                confirmButton = { TextButton(onClick = { showDataResetSecondDialog = false }, enabled = confirmInputText == "삭제") { Text("최종 삭제") } },
                dismissButton = { TextButton(onClick = { showDataResetSecondDialog = false }) { Text("취소") } }
            )
        }
    }
}
