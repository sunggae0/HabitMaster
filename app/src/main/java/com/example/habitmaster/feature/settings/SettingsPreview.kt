package com.example.habitmaster.feature.settings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.core.model.BackupInfo
import com.example.habitmaster.ui.theme.HabitMasterTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        var showDataSaveDialog by remember { mutableStateOf(false) }
        var showDataSaveSuccessDialog by remember { mutableStateOf(false) }
        var showDataRestoreDialog by remember { mutableStateOf(false) } // 데이터 복구 목록 창

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
            onDataResetClick = { showDataResetDialog = true },
            onDataSaveClick = { showDataSaveDialog = true },
            onDataRestoreClick = { showDataRestoreDialog = true } // 클릭 이벤트 연결
        )

        if (showProfileDialog) {
            ProfileEditDialog(onDismiss = { showProfileDialog = false })
        }

        if (showPasswordDialog) {
            PasswordChangeDialog(onDismiss = { showPasswordDialog = false })
        }

        if (showNotificationDialog) {
            val title = if (notificationEnabled) "알림이 켜졌습니다" else "알림이 꺼졌습니다"
            val text = if (notificationEnabled) "오늘 습관을 완료했는지 확인하는 알림을 보내드립니다." else "알림이 비활성화되었습니다."
            AlertDialog(
                onDismissRequest = { showNotificationDialog = false },
                title = { Text(title) },
                text = { Text(text) },
                confirmButton = {
                    TextButton(onClick = { showNotificationDialog = false }) {
                        Text("확인")
                    }
                }
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("로그아웃") },
                text = { Text("정말 로그아웃 하시겠습니까?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            // TODO: Navigate to onboarding screen
                        }
                    ) {
                        Text("예")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("아니요")
                    }
                }
            )
        }

        // 1차 데이터 초기화 확인 창
        if (showDataResetDialog) {
            AlertDialog(
                onDismissRequest = { showDataResetDialog = false },
                title = { Text("데이터 초기화") },
                text = { Text("정말 모든 데이터를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDataResetDialog = false
                            showDataResetSecondDialog = true
                            confirmInputText = ""
                        }
                    ) {
                        Text("삭제")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDataResetDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }

        // 2차 데이터 초기화 확인 창
        if (showDataResetSecondDialog) {
            AlertDialog(
                onDismissRequest = { showDataResetSecondDialog = false },
                title = { Text("최종 확인") },
                text = {
                    OutlinedTextField(
                        value = confirmInputText,
                        onValueChange = { confirmInputText = it },
                        label = { Text("'삭제'를 입력하세요") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDataResetSecondDialog = false
                        },
                        enabled = confirmInputText == "삭제"
                    ) {
                        Text("최종 삭제")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDataResetSecondDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }

        // 데이터 저장 확인 창
        if (showDataSaveDialog) {
            AlertDialog(
                onDismissRequest = { showDataSaveDialog = false },
                title = { Text("데이터 저장") },
                text = { Text("현재 데이터를 서버에 백업하시겠습니까?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDataSaveDialog = false
                            // TODO: 데이터 저장 로직 실행
                            showDataSaveSuccessDialog = true
                        }
                    ) {
                        Text("예")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDataSaveDialog = false }) {
                        Text("아니요")
                    }
                }
            )
        }

        // 데이터 저장 완료 창
        if (showDataSaveSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showDataSaveSuccessDialog = false },
                title = { Text("완료") },
                text = { Text("데이터가 성공적으로 저장되었습니다.") },
                confirmButton = {
                    TextButton(onClick = { showDataSaveSuccessDialog = false }) {
                        Text("확인")
                    }
                }
            )
        }

        // 데이터 복구 목록 창
        if (showDataRestoreDialog) {
            // 임시 더미 데이터
            val dummyBackups = listOf(
                BackupInfo(id = "1", createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2),
                BackupInfo(id = "2", createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 5)
            )

            AlertDialog(
                onDismissRequest = { showDataRestoreDialog = false },
                title = { Text("복구할 시점 선택") },
                text = {
                    LazyColumn {
                        items(dummyBackups) { backup ->
                            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(backup.createdAt))
                            TextButton(onClick = { /* TODO: Show final confirmation */ }) {
                                Text("백업: $date")
                            }
                        }
                    }
                },
                confirmButton = { },
                dismissButton = { TextButton(onClick = { showDataRestoreDialog = false }) { Text("취소") } }
            )
        }
    }
}
