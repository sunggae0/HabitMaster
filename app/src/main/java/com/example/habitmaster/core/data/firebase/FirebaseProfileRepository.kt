package com.example.habitmaster.core.data.firebase

import android.net.Uri
import com.example.habitmaster.core.model.Profile
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