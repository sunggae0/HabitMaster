package com.example.habitmaster.core.data

val habitDummyData = List(30) {
    val completeList = mutableListOf<Boolean?>()
    for (i in 0..30) {
        completeList.add(listOf(true, false, null).random())
    }

    Habit(
        id = "dummy_id_$it",
        title = "habit test$it",
        achievementRate = (0..100).random().toFloat() / 100,
        completeList = completeList,
        targetCount = 30,
        periodValue = 1,
        periodUnit = "일마다",
        startDate = System.currentTimeMillis(),
        isActive = true,
        lastSuccessDate = null
    )
}
