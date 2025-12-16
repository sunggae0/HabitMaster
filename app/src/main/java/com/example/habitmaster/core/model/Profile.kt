package com.example.habitmaster.core.model

import com.example.habitmaster.core.data.Habit

data class Profile(
    val id: String,
    val name: String,
    val passwordHash: String,
    val photoUrl: String?,
    val habits: List<Habit> = emptyList(),
    val createdAtMillis: Long
)