package com.example.habitmaster.core.data

data class Habit(
    var id: String = "",
    var title: String = "",
    var achievementRate: Float = 0f,
    var completeList: MutableList<Boolean?> = mutableListOf(), 
    var targetCount: Int = 0,
    var periodValue: Int = 1,
    var periodUnit: String = "일마다",
    var startDate: Long = 0L,
    var isActive: Boolean = true // 활성화 여부 필드 추가
)
