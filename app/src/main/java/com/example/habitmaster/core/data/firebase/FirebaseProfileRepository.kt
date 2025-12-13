package com.example.habitmaster.core.data.firebase

import android.net.Uri
import com.example.habitmaster.core.data.Habit
import com.example.habitmaster.core.model.Profile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
                
                val habits = (d.get("habits") as? List<Map<String, Any>>)?.mapNotNull { habitMap ->
                    val habitId = habitMap["id"] as? String ?: UUID.randomUUID().toString()
                    val title = habitMap["title"] as? String ?: return@mapNotNull null
                    val achievementRate = (habitMap["achievementRate"] as? Number)?.toFloat() ?: 0f
                    val completeListRaw = habitMap["completeList"] as? List<Boolean?>
                    val completeList = completeListRaw?.toMutableList() ?: mutableListOf()
                    val targetCount = (habitMap["targetCount"] as? Number)?.toInt() ?: 0
                    val periodValue = (habitMap["periodValue"] as? Number)?.toInt() ?: 1
                    val periodUnit = habitMap["periodUnit"] as? String ?: "일마다"
                    val startDate = (habitMap["startDate"] as? Number)?.toLong() ?: 0L
                    val isActive = (habitMap["isActive"] as? Boolean) ?: true // 기본값 true

                    Habit(
                        id = habitId,
                        title = title, 
                        achievementRate = achievementRate, 
                        completeList = completeList,
                        targetCount = targetCount,
                        periodValue = periodValue,
                        periodUnit = periodUnit,
                        startDate = startDate,
                        isActive = isActive
                    )
                } ?: emptyList()
                
                val createdAtMillis = d.getLong("createdAtMillis") ?: 0L
                Profile(
                    id = id,
                    name = name,
                    passwordHash = passwordHash,
                    photoUrl = photoUrl,
                    habits = habits,
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
            "habits" to emptyList<Any>(),
            "createdAtMillis" to now
        )

        col.document(profileId).set(data).await()

        return Profile(
            id = profileId,
            name = name,
            passwordHash = passwordHash,
            photoUrl = null,
            habits = emptyList(),
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
    
    // 습관 추가 메서드
    suspend fun addHabitToProfile(profileId: String, habit: Habit) {
        val col = profilesColRef()
        val habitMap = mapOf(
            "id" to habit.id,
            "title" to habit.title,
            "achievementRate" to habit.achievementRate,
            "completeList" to habit.completeList,
            "targetCount" to habit.targetCount,
            "periodValue" to habit.periodValue,
            "periodUnit" to habit.periodUnit,
            "startDate" to habit.startDate,
            "isActive" to habit.isActive
        )
        col.document(profileId).update("habits", FieldValue.arrayUnion(habitMap)).await()
    }

    override suspend fun updateHabit(profileId: String, habit: Habit) {
        val col = profilesColRef()
        
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(col.document(profileId))
            val existingHabits = snapshot.get("habits") as? List<Map<String, Any>> ?: emptyList()
            
            val updatedHabits = existingHabits.map { habitMap ->
                if (habitMap["id"] == habit.id) {
                    mapOf(
                        "id" to habit.id,
                        "title" to habit.title,
                        "achievementRate" to habit.achievementRate,
                        "completeList" to habit.completeList,
                        "targetCount" to habit.targetCount,
                        "periodValue" to habit.periodValue,
                        "periodUnit" to habit.periodUnit,
                        "startDate" to habit.startDate,
                        "isActive" to habit.isActive
                    )
                } else {
                    habitMap
                }
            }
            
            transaction.update(col.document(profileId), "habits", updatedHabits)
        }.await()
    }
}
