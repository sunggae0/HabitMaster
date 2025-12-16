package com.example.habitmaster.feature.mypage

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitmaster.ui.theme.HabitMasterTheme

@Preview(showBackground = true)
@Composable
fun MypageScreenPreview() {
    HabitMasterTheme {
        // [수정] stats 대신 profileId를 전달하도록 변경
        MypageScreen(
            profileId = "preview-profile-id",
            onFinish = {}
        )
    }
}
