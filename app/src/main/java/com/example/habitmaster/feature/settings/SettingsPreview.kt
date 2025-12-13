package com.example.habitmaster.feature.settings

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    HabitMasterTheme {
        var showProfileDialog by remember { mutableStateOf(false) }
        var showPasswordDialog by remember { mutableStateOf(false) }

        SettingsScreen(
            onFinish = {},
            onShowProfileEditDialog = { showProfileDialog = true },
            onShowPasswordChangeDialog = { showPasswordDialog = true }
        )

        if (showProfileDialog) {
            ProfileEditDialog(onDismiss = { showProfileDialog = false })
        }

        if (showPasswordDialog) {
            PasswordChangeDialog(onDismiss = { showPasswordDialog = false })
        }
    }
}
