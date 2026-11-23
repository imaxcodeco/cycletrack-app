package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun getProfile(userId: String): UserProfile?
    suspend fun getProfileByEmail(email: String): UserProfile?
    suspend fun upsertProfile(profile: UserProfile)
    suspend fun deleteProfile(userId: String)
    suspend fun profileExists(userId: String): Boolean
    suspend fun emailExists(email: String): Boolean
}
