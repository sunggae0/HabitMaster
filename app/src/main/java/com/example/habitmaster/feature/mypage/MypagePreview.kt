package com.example.habitmaster.feature.mypage

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.core.model.UserStatus // 공용 UserStatus 모델 import
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun MypageScreenPreview() {
    HabitMasterTheme {
        MypageScreen(
            stats = UserStatus(),
            onFinish = {}
        )
    }
}
