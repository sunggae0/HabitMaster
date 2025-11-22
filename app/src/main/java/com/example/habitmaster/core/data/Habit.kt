package com.example.habitmaster.core.data

data class Habit(
    var title:String,
    var achievementRate: Float,
    var completeList: MutableList<Boolean>
    )
