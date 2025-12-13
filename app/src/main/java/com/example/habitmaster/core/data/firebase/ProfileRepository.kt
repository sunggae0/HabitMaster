package com.example.habitmaster.core.data.firebase

import android.net.Uri
import com.example.habitmaster.core.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfiles(): Flow<List<Profile>>
    suspend fun createProfile(name: String, passwordPlain: String): Profile
    suspend fun uploadProfilePhoto(profileId: String, photoUri: Uri): String // returns downloadUrl
    suspend fun updateProfilePhotoUrl(profileId: String, photoUrl: String)
}