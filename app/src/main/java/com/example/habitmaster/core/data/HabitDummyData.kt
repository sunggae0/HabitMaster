package com.example.habitmaster.core.data

var dummyCompleteList = listOf(false,true,true,false,false,true,true) as MutableList<Boolean>

val habitDummyData = MutableList(30) { i->
    Habit("habit test$i", 0.1f*(i%10), dummyCompleteList)
}