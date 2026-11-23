package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.UserProfileRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserProfileDao @Inject constructor() {

    private val realm: Realm
        get() = Realm.getDefaultInstance()

    suspend fun getProfile(userId: String): UserProfileRealm? = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
                ?.let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getProfileByEmail(email: String): UserProfileRealm? = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(UserProfileRealm::class.java)
                .equalTo("email", email)
                .equalTo("deletedAt", 0L)
                .findFirst()
                ?.let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun upsertProfile(profile: UserProfileRealm) = withContext(Dispatchers.IO) {
        try {
            realm.executeTransaction { it.insertOrUpdate(profile) }
        } catch (e: Exception) {
            // Falha silenciosa: persistencia local e best-effort
        }
    }

    suspend fun updateProfile(userId: String, block: suspend (UserProfileRealm) -> Unit) {
        val profile = getProfile(userId) ?: return
        block(profile)
    }

    suspend fun deleteProfile(userId: String) = withContext(Dispatchers.IO) {
        try {
            realm.executeTransaction {
                it.where(UserProfileRealm::class.java)
                    .equalTo("id", userId)
                    .findFirst()
                    ?.deletedAt = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            // Falha silenciosa
        }
    }

    suspend fun hardDeleteProfile(userId: String) = withContext(Dispatchers.IO) {
        try {
            realm.executeTransaction {
                it.where(UserProfileRealm::class.java)
                    .equalTo("id", userId)
                    .findFirst()
                    ?.deleteFromRealm()
            }
        } catch (e: Exception) {
            // Falha silenciosa
        }
    }

    suspend fun profileExists(userId: String): Boolean = getProfile(userId) != null

    suspend fun emailExists(email: String): Boolean = getProfileByEmail(email) != null
}
