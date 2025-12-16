package com.example.habitmaster.feature.settings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.habitmaster.core.data.firebase.FirebaseProfileRepository
import com.example.habitmaster.core.model.BackupInfo
import com.example.habitmaster.core.model.Profile
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    composable("settings/{profileId}") { backStackEntry ->
        val profileId = backStackEntry.arguments?.getString("profileId") ?: return@composable

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
        var showDataRestoreDialog by remember { mutableStateOf(false) }
        var backupList by remember { mutableStateOf<List<BackupInfo>>(emptyList()) }
        var selectedBackup by remember { mutableStateOf<BackupInfo?>(null) }
        var showRestoreConfirmDialog by remember { mutableStateOf(false) }
        var showPasswordResultDialog by remember { mutableStateOf<Boolean?>(null) } // 비밀번호 변경 결과 다이얼로그 상태

        val coroutineScope = rememberCoroutineScope()
        val repository = remember { FirebaseProfileRepository() }

        val currentProfile by produceState<Profile?>(initialValue = null, profileId) {
            repository.observeProfiles().collect { profiles ->
                value = profiles.find { it.id == profileId }
            }
        }

        SettingsScreen(
            onFinish = { navController.popBackStack() },
            onShowProfileEditDialog = { showProfileDialog = true },
            onShowPasswordChangeDialog = { showPasswordDialog = true },
            notificationEnabled = notificationEnabled,
            onNotificationEnabledChange = { notificationEnabled = it; showNotificationDialog = true },
            isDarkMode = isDarkMode,
            onDarkModeChange = { isDarkMode = it },
            onLogoutClick = { showLogoutDialog = true },
            onDataResetClick = { showDataResetDialog = true },
        )

        if (showProfileDialog) {
            ProfileEditDialog(
                profile = currentProfile,
                onDismiss = { showProfileDialog = false },
                onSave = { newName ->
                    coroutineScope.launch {
                        repository.updateProfileName(profileId, newName)
                        showProfileDialog = false
                    }
                }
            )
        }

        if (showPasswordDialog) {
            PasswordChangeDialog(
                onDismiss = { showPasswordDialog = false },
                onSave = { currentPassword, newPassword ->
                    coroutineScope.launch {
                        val success = repository.updatePassword(profileId, currentPassword, newPassword)
                        showPasswordDialog = false
                        showPasswordResultDialog = success
                    }
                }
            )
        }

        // 비밀번호 변경 결과 다이얼로그
        showPasswordResultDialog?.let { success ->
            val title = if (success) "성공" else "오류"
            val text = if (success) "비밀번호가 성공적으로 변경되었습니다." else "현재 비밀번호가 일치하지 않습니다."
            AlertDialog(
                onDismissRequest = { showPasswordResultDialog = null },
                title = { Text(title) },
                text = { Text(text) },
                confirmButton = { TextButton(onClick = { showPasswordResultDialog = null }) { Text("확인") } }
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
                title = { Text("프로필 선택으로") },
                text = { Text("프로필 선택 화면으로 돌아가시겠습니까?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            navController.navigate("profile") { popUpTo(navController.graph.startDestinationId) { inclusive = true } }
                        }
                    ) { Text("예") }
                },
                dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("아니요") } }
            )
        }

        if (showDataResetDialog) {
            AlertDialog(
                onDismissRequest = { showDataResetDialog = false },
                title = { Text("데이터 초기화") },
                text = { Text("정말 모든 데이터를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.") },
                confirmButton = {
                    TextButton(
                        onClick = { showDataResetDialog = false; showDataResetSecondDialog = true; confirmInputText = "" }
                    ) { Text("삭제") }
                },
                dismissButton = { TextButton(onClick = { showDataResetDialog = false }) { Text("취소") } }
            )
        }

        if (showDataResetSecondDialog) {
            AlertDialog(
                onDismissRequest = { showDataResetSecondDialog = false },
                title = { Text("최종 확인") },
                text = { OutlinedTextField(value = confirmInputText, onValueChange = { confirmInputText = it }, label = { Text("'삭제'를 입력하세요") }, singleLine = true) },
                confirmButton = {
                    TextButton(
                        onClick = { showDataResetSecondDialog = false; coroutineScope.launch { repository.deleteAllUserData() } },
                        enabled = confirmInputText == "삭제"
                    ) { Text("최종 삭제") }
                },
                dismissButton = { TextButton(onClick = { showDataResetSecondDialog = false }) { Text("취소") } }
            )
        }

        if (showDataSaveDialog) {
            AlertDialog(
                onDismissRequest = { showDataSaveDialog = false },
                title = { Text("데이터 저장") },
                text = { Text("현재 데이터를 서버에 백업하시겠습니까?") },
                confirmButton = {
                    TextButton(
                        onClick = { showDataSaveDialog = false; coroutineScope.launch { repository.backupUserData(); showDataSaveSuccessDialog = true } }
                    ) { Text("예") }
                },
                dismissButton = { TextButton(onClick = { showDataSaveDialog = false }) { Text("아니요") } }
            )
        }

        if (showDataSaveSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showDataSaveSuccessDialog = false },
                title = { Text("완료") },
                text = { Text("데이터가 성공적으로 저장되었습니다.") },
                confirmButton = { TextButton(onClick = { showDataSaveSuccessDialog = false }) { Text("확인") } }
            )
        }

        if (showDataRestoreDialog) {
            AlertDialog(
                onDismissRequest = { showDataRestoreDialog = false },
                title = { Text("복구할 시점 선택") },
                text = {
                    LazyColumn {
                        items(backupList) { backup ->
                            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(backup.createdAt))
                            TextButton(onClick = { selectedBackup = backup; showDataRestoreDialog = false; showRestoreConfirmDialog = true }) { Text("백업: $date") }
                        }
                    }
                },
                confirmButton = { },
                dismissButton = { TextButton(onClick = { showDataRestoreDialog = false }) { Text("취소") } }
            )
        }

        if (showRestoreConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showRestoreConfirmDialog = false },
                title = { Text("데이터 복구") },
                text = { Text("현재 모든 데이터를 덮어쓰고 선택한 시점으로 복구하시겠습니까?") },
                confirmButton = {
                    TextButton(
                        onClick = { showRestoreConfirmDialog = false; coroutineScope.launch { selectedBackup?.let { repository.restoreFromBackup(it.id) } } }
                    ) { Text("복구") }
                },
                dismissButton = { TextButton(onClick = { showRestoreConfirmDialog = false }) { Text("취소") } }
            )
        }
    }
}
