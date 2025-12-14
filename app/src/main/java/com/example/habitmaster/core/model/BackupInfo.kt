package com.example.habitmaster.core.model

import com.google.firebase.firestore.Exclude

/**
 * Firestore에 저장된 백업 정보(스냅샷)를 나타내는 데이터 클래스입니다.
 *
 * @property id Firestore 문서의 고유 ID. 코드를 통해 채워집니다.
 * @property createdAt 백업이 생성된 시간 (타임스탬프).
 */
data class BackupInfo(
    @get:Exclude val id: String = "",
    val createdAt: Long = 0L
)
