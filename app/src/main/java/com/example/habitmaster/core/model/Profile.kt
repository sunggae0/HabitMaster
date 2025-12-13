package com.example.habitmaster.core.model

data class Profile(
    val id: String,
    val name: String,
    val passwordHash: String,
    val photoUrl: String?,
    val createdAtMillis: Long
)