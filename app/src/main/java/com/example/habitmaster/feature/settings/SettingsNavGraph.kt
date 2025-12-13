package com.example.habitmaster.feature.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    composable("settings") {
        var showProfileDialog by remember { mutableStateOf(false) }
        var showPasswordDialog by remember { mutableStateOf(false) }

        SettingsScreen(
            onFinish = { navController.popBackStack() },
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
