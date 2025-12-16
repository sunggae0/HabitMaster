package com.example.habitmaster.core.data.firebase

import android.net.Uri
import com.example.habitmaster.core.data.Habit // Habit 모델 import
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

    private suspend fun habitsColRef(profileId: String) = 
        profilesColRef().document(profileId).collection("habits")

    fun observeHabits(profileId: String): Flow<List<Habit>> = callbackFlow {
        val ref = habitsColRef(profileId)
        val registration = ref.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val habits = snap?.documents.orEmpty().mapNotNull { doc ->
                doc.toObject(Habit::class.java)?.copy(id = doc.id)
            }
            trySend(habits)
        }
        awaitClose { registration.remove() }
    }

    suspend fun updatePassword(profileId: String, currentPasswordPlain: String, newPasswordPlain: String): Boolean {
        val profileDocRef = profilesColRef().document(profileId)
        val profileDoc = profileDocRef.get().await()

        val storedPasswordHash = profileDoc.getString("passwordHash")
        val currentPasswordHash = PasswordHasher.sha256(currentPasswordPlain)

        if (storedPasswordHash != currentPasswordHash) {
            return false
        }

        val newPasswordHash = PasswordHasher.sha256(newPasswordPlain)
        profileDocRef.update("passwordHash", newPasswordHash).await()
        return true
    }

    suspend fun updateProfileName(profileId: String, newName: String) {
        profilesColRef().document(profileId).update("name", newName).await()
    }

    private suspend fun backupsColRef() =
        firestore.collection("backups")
            .document(session.requireUid())
            .collection("snapshots")

    suspend fun getBackupList(): List<BackupInfo> {
        val snapshot = backupsColRef().orderBy("createdAt", Query.Direction.DESCENDING).get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(BackupInfo::class.java)?.copy(id = it.id)
        }
    }

    suspend fun restoreFromBackup(backupId: String) {
        val backupDoc = backupsColRef().document(backupId).get().await()
        val backupData = backupDoc.get("data") as? Map<String, Any> ?: return

        deleteAllUserData()

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

    // [수정] 습관 데이터까지 모두 삭제하도록 보강
    suspend fun deleteAllUserData() {
        val profilesQuery = profilesColRef().get().await()
        val batch = firestore.batch()

        for (profileDoc in profilesQuery.documents) {
            // 1. stats 하위 컬렉션 삭제
            val statsQuery = profileDoc.reference.collection("stats").get().await()
            for (statDoc in statsQuery.documents) {
                batch.delete(statDoc.reference)
            }
            
            // 2. habits 하위 컬렉션 삭제 (추가된 부분)
            val habitsQuery = profileDoc.reference.collection("habits").get().await()
            for (habitDoc in habitsQuery.documents) {
                batch.delete(habitDoc.reference)
            }

            // 3. 프로필 자체 삭제
            batch.delete(profileDoc.reference)
        }

        batch.commit().await()
    }

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
