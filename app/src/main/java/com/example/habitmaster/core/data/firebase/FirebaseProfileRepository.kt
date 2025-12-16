package com.example.habitmaster.core.data.firebase

import android.net.Uri
import com.example.habitmaster.core.model.BackupInfo
import com.example.habitmaster.core.model.Profile
import com.example.habitmaster.core.model.UserStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseProfileRepository(
    private val session: FirebaseSession = FirebaseSession(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
) : ProfileRepository {

    private suspend fun profilesColRef() =
        firestore.collection("accounts")
            .document(session.requireUid())
            .collection("profiles")

    private suspend fun backupsColRef() =
        firestore.collection("backups")
            .document(session.requireUid())
            .collection("snapshots")

    // 백업 목록을 가져오는 함수
    suspend fun getBackupList(): List<BackupInfo> {
        val snapshot = backupsColRef().orderBy("createdAt", Query.Direction.DESCENDING).get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(BackupInfo::class.java)?.copy(id = it.id)
        }
    }

    // 선택한 백업으로 데이터를 복원하는 함수
    suspend fun restoreFromBackup(backupId: String) {
        val backupDoc = backupsColRef().document(backupId).get().await()
        val backupData = backupDoc.get("data") as? Map<String, Any> ?: return

        // 1. 현재 데이터 삭제
        deleteAllUserData()

        // 2. 백업 데이터로 복원
        val batch = firestore.batch()
        backupData.forEach { (profileId, profileData) ->
            val profileDocRef = firestore.collection("accounts").document(session.requireUid()).collection("profiles").document(profileId)
            val data = (profileData as Map<String, Any>).toMutableMap()
            val subCollections = data.remove("sub-collections") as? Map<String, Any>

            batch.set(profileDocRef, data)

            subCollections?.get("stats")?.let { statsData ->
                (statsData as? Map<String, Any>)?.forEach { (statId, statContent) ->
                    val statDocRef = profileDocRef.collection("stats").document(statId)
                    batch.set(statDocRef, statContent as Map<String, Any>)
                }
            }
        }
        batch.commit().await()
    }

    // 현재 사용자의 모든 데이터를 백업하는 함수
    suspend fun backupUserData() {
        val uid = session.requireUid()
        val profilesQuery = profilesColRef().get().await()

        val backupData = mutableMapOf<String, Any>()
        for (profileDoc in profilesQuery.documents) {
            val profileData = profileDoc.data ?: continue
            val profileId = profileDoc.id
            val statsQuery = profileDoc.reference.collection("stats").get().await()
            val statsData = statsQuery.documents.associate { it.id to (it.data ?: emptyMap()) }

            backupData[profileId] = profileData.toMutableMap().apply {
                put("sub-collections", mapOf("stats" to statsData))
            }
        }

        if (backupData.isNotEmpty()) {
            val timestamp = System.currentTimeMillis()
            val backupDocRef = backupsColRef().document(timestamp.toString())
            backupDocRef.set(mapOf("createdAt" to timestamp, "data" to backupData)).await()
        }
    }

    // 모든 사용자 데이터 (프로필, 통계 등)를 삭제하는 함수
    suspend fun deleteAllUserData() {
        val profilesQuery = profilesColRef().get().await()
        val batch = firestore.batch()

        // 각 프로필 문서와 그 하위 컬렉션의 문서를 삭제 목록에 추가
        for (profileDoc in profilesQuery.documents) {
            // stats 하위 컬렉션 삭제
            val statsQuery = profileDoc.reference.collection("stats").get().await()
            for (statDoc in statsQuery.documents) {
                batch.delete(statDoc.reference)
            }
            // 프로필 자체 삭제
            batch.delete(profileDoc.reference)
        }

        batch.commit().await()
    }

    // 특정 프로필의 통계 정보를 실시간으로 관찰하는 함수
    fun observeUserStatus(profileId: String): Flow<UserStatus?> = callbackFlow {
        val statusDocRef = firestore.collection("accounts").document(session.requireUid()).collection("profiles").document(profileId).collection("stats").document("main")
        val registration = statusDocRef.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val status = snap?.toObject(UserStatus::class.java)
            trySend(status)
        }
        awaitClose { registration.remove() }
    }

    // 통계 정보를 Firestore에 저장/업데이트하는 함수
    suspend fun saveUserStatus(profileId: String, status: UserStatus) {
        profilesColRef().document(profileId).collection("stats").document("main").set(status).await()
    }

    override fun observeProfiles(): Flow<List<Profile>> = callbackFlow {
        val uid = session.requireUid()
        val ref = firestore.collection("accounts")
            .document(uid)
            .collection("profiles")
            .orderBy("createdAtMillis", Query.Direction.ASCENDING)

        val registration = ref.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val list = snap?.documents.orEmpty().mapNotNull { d ->
                val id = d.id
                val name = d.getString("name") ?: return@mapNotNull null
                val passwordHash = d.getString("passwordHash") ?: return@mapNotNull null
                val photoUrl = d.getString("photoUrl")
                val createdAtMillis = d.getLong("createdAtMillis") ?: 0L
                Profile(
                    id = id,
                    name = name,
                    passwordHash = passwordHash,
                    photoUrl = photoUrl,
                    createdAtMillis = createdAtMillis
                )
            }
            trySend(list)
        }

        awaitClose { registration.remove() }
    }

    override suspend fun createProfile(name: String, passwordPlain: String): Profile {
        val col = profilesColRef()
        val profileId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val passwordHash = PasswordHasher.sha256(passwordPlain)

        val data = mapOf(
            "name" to name,
            "passwordHash" to passwordHash,
            "photoUrl" to null,
            "createdAtMillis" to now
        )

        col.document(profileId).set(data).await()

        // 새 프로필 생성 시 기본 통계 데이터도 함께 생성
        saveUserStatus(profileId, UserStatus())

        return Profile(
            id = profileId,
            name = name,
            passwordHash = passwordHash,
            photoUrl = null,
            createdAtMillis = now
        )
    }

    override suspend fun uploadProfilePhoto(profileId: String, photoUri: Uri): String {
        val uid = session.requireUid()
        val ref = storage.reference
            .child("accounts/$uid/profiles/$profileId/avatar.jpg")

        ref.putFile(photoUri).await()
        return ref.downloadUrl.await().toString()
    }

    override suspend fun updateProfilePhotoUrl(profileId: String, photoUrl: String) {
        val col = profilesColRef()
        col.document(profileId).update("photoUrl", photoUrl).await()
    }
}