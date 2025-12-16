package com.example.habitmaster.core.data

val habitDummyData = List(30) {
    Habit(
        title = "habit test$it",
        achievementRate = it.toFloat() / 30,
        completeList = mutableListOf(true, false, true, true, false, true, true)
    )
}
