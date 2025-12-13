package com.example.habitmaster.core.data

data class Habit(
    var id: String = "",
    var title: String = "",
    var achievementRate: Float = 0f,
    // 상태: 0 = 미완료/대기(-), 1 = 완료(O), 2 = 실패(X)
    // 혹은 Boolean? (true=O, false=X, null=-)도 가능하지만 확장성을 위해 Int나 Enum 권장
    // 여기서는 간단하게 Int 리스트로 변경하거나, 
    // 기존 Boolean을 유지하되 "아직 안 지난 날짜" 표현을 위해 Nullable로 변경
    var completeList: MutableList<Boolean?> = mutableListOf(), 
    var targetCount: Int = 0,
    var periodValue: Int = 1,
    var periodUnit: String = "일마다",
    var startDate: Long = 0L
)
