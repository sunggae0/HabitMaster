package com.example.habitmaster.core.data.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await



class FirebaseSession(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun requireUid(): String {
        val current = auth.currentUser
        if (current != null) return current.uid

        // 익명 로그인 1회
        val result = auth.signInAnonymously().await()
        return requireNotNull(result.user).uid
    }
    fun signOut() {
        auth.signOut()
    }
}