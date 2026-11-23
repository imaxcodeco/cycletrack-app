package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.data.local.realm.dao.UserProfileDao
import com.seuapp.cycletrack.data.local.realm.entities.UserProfileRealm
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.repository.UserProfileRepository
import java.time.LocalDate
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override suspend fun getProfile(userId: String): UserProfile? {
        val realmProfile = userProfileDao.getProfile(userId) ?: return null
        return realmProfile.toDomain()
    }

    override suspend fun getProfileByEmail(email: String): UserProfile? {
        val realmProfile = userProfileDao.getProfileByEmail(email) ?: return null
        return realmProfile.toDomain()
    }

    override suspend fun upsertProfile(profile: UserProfile) {
        val realmProfile = profile.toRealm()
        userProfileDao.upsertProfile(realmProfile)
    }

    override suspend fun deleteProfile(userId: String) {
        userProfileDao.deleteProfile(userId)
    }

    override suspend fun profileExists(userId: String): Boolean {
        return userProfileDao.profileExists(userId)
    }

    override suspend fun emailExists(email: String): Boolean {
        return userProfileDao.emailExists(email)
    }

    private fun UserProfile.toRealm(): UserProfileRealm {
        val realm = UserProfileRealm()
        realm.id = this.id
        realm.email = this.email
        realm.displayName = this.displayName
        realm.goal = this.goal
        realm.dateOfBirth = this.dateOfBirth?.toEpochDay() ?: 0L
        realm.averageCycleLength = this.averageCycleLength
        realm.averagePeriodLength = this.averagePeriodLength
        realm.lastPeriodStartDate = this.lastPeriodStartDate?.toEpochDay() ?: 0L
        realm.heightCm = this.heightCm
        realm.weightKg = this.weightKg
        realm.onboardingCompleted = this.onboardingCompleted
        return realm
    }

    private fun UserProfileRealm.toDomain(): UserProfile = UserProfile(
        id = this.id,
        email = this.email,
        displayName = this.displayName,
        goal = this.goal,
        dateOfBirth = if (this.dateOfBirth != 0L) LocalDate.ofEpochDay(this.dateOfBirth) else null,
        averageCycleLength = this.averageCycleLength,
        averagePeriodLength = this.averagePeriodLength,
        lastPeriodStartDate = if (this.lastPeriodStartDate != 0L) {
            LocalDate.ofEpochDay(this.lastPeriodStartDate)
        } else {
            null
        },
        heightCm = this.heightCm,
        weightKg = this.weightKg,
        onboardingCompleted = this.onboardingCompleted
    )
}
