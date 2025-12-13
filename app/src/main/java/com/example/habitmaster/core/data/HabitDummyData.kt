package com.example.habitmaster.core.data

import java.util.UUID

// Boolean -> Boolean? 타입 변경에 맞춤
var dummyCompleteList: MutableList<Boolean?> = mutableListOf(false,true,true,false,false,true,true)

val habitDummyData = MutableList(30) { i->
    Habit(
        id = UUID.randomUUID().toString(),
        title = "habit test$i",
        achievementRate = 0.1f*(i%10),
        completeList = ArrayList(dummyCompleteList) // 각 아이템별로 별도의 리스트 인스턴스 생성
    )
}
