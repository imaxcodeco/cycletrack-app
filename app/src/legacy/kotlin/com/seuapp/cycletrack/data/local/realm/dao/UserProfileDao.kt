package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.UserProfileRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.seuapp.cycletrack.data.local.realm.detached

/**
 * DAO for UserProfileRealm operations.
 * Clean-room implementation with singleton profile per user.
 */
class UserProfileDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get user profile by ID.
     */
    suspend fun getProfile(userId: String): UserProfileRealm? = withContext(Dispatchers.IO) {
        realm.where(UserProfileRealm::class.java)
            .equalTo("id", userId)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Get user profile by email.
     */
    suspend fun getProfileByEmail(email: String): UserProfileRealm? = withContext(Dispatchers.IO) {
        realm.where(UserProfileRealm::class.java)
            .equalTo("email", email)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Insert or update user profile.
     */
    suspend fun upsertProfile(profile: UserProfileRealm): UserProfileRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            profile.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(profile)
        }
    }

    /**
     * Update specific fields of the profile.
     */
    suspend fun updateProfile(userId: String, updateBlock: (UserProfileRealm) -> Unit): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            profile?.let {
                updateBlock(it)
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Soft delete user profile.
     */
    suspend fun deleteProfile(userId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .findFirst()
            profile?.let {
                it.deletedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Hard delete user profile and all associated data (GDPR/LGPD).
     */
    suspend fun hardDeleteProfile(userId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .findFirst()
            profile?.deleteFromRealm() ?: false
        }
    }

    /**
     * Update subscription status.
     */
    suspend fun updateSubscriptionStatus(
        userId: String,
        status: String,
        productId: String = "",
        expiresAt: Long = 0L,
        autoRenew: Boolean = true,
        platform: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            profile?.let {
                it.subscriptionStatus = status
                it.subscriptionProductId = productId
                it.subscriptionExpiresAt = expiresAt
                it.subscriptionAutoRenew = autoRenew
                it.subscriptionPlatform = platform
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Update partner connection info.
     */
    suspend fun updatePartnerInfo(
        userId: String,
        partnerId: String,
        shareLevel: String,
        invitationSent: Boolean = false
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            profile?.let {
                it.partnerModeEnabled = partnerId.isNotBlank()
                it.partnerId = partnerId
                it.partnerShareLevel = shareLevel
                it.partnerInvitationSent = invitationSent
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Update last login timestamp.
     */
    suspend fun updateLastLogin(userId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            profile?.let {
                it.lastLoginAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Update cycle characteristics.
     */
    suspend fun updateCycleCharacteristics(
        userId: String,
        averageCycleLength: Int,
        averagePeriodLength: Int,
        averageLutealPhaseLength: Int,
        cycleRegularity: String,
        lastPeriodDate: Long
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            profile?.let {
                it.averageCycleLength = averageCycleLength
                it.averagePeriodLength = averagePeriodLength
                it.averageLutealPhaseLength = averageLutealPhaseLength
                it.cycleRegularity = cycleRegularity
                it.lastPeriodDate = lastPeriodDate
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Update pregnancy status.
     */
    suspend fun updatePregnancyStatus(
        userId: String,
        status: String,
        dueDate: Long = 0L,
        startDate: Long = 0L
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            profile?.let {
                it.pregnancyStatus = status
                it.pregnancyDueDate = dueDate
                it.pregnancyStartDate = startDate
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Update notification preferences.
     */
    suspend fun updateNotificationPreferences(
        userId: String,
        periodReminderEnabled: Boolean? = null,
        periodReminderDaysBefore: Int? = null,
        fertileWindowReminderEnabled: Boolean? = null,
        ovulationReminderEnabled: Boolean? = null,
        pillReminderEnabled: Boolean? = null,
        pillReminderTime: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            profile?.let {
                periodReminderEnabled?.let { it.periodReminderEnabled = it }
                periodReminderDaysBefore?.let { it.periodReminderDaysBefore = it }
                fertileWindowReminderEnabled?.let { it.fertileWindowReminderEnabled = it }
                ovulationReminderEnabled?.let { it.ovulationReminderEnabled = it }
                pillReminderEnabled?.let { it.pillReminderEnabled = it }
                pillReminderTime?.let { it.pillReminderTime = it }
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Update display preferences.
     */
    suspend fun updateDisplayPreferences(
        userId: String,
        themeMode: String? = null,
        language: String? = null,
        temperatureUnit: String? = null,
        weightUnit: String? = null,
        heightUnit: String? = null,
        firstDayOfWeek: Int? = null,
        calendarViewMode: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            profile?.let {
                themeMode?.let { it.themeMode = it }
                language?.let { it.language = it }
                temperatureUnit?.let { it.temperatureUnit = it }
                weightUnit?.let { it.weightUnit = it }
                heightUnit?.let { it.heightUnit = it }
                firstDayOfWeek?.let { it.firstDayOfWeek = it }
                calendarViewMode?.let { it.calendarViewMode = it }
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Update privacy/consent preferences.
     */
    suspend fun updateConsentPreferences(
        userId: String,
        analyticsOptIn: Boolean? = null,
        crashReportingOptIn: Boolean? = null,
        marketingOptIn: Boolean? = null,
        healthConnectEnabled: Boolean? = null,
        dataSharingConsentVersion: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            profile?.let {
                analyticsOptIn?.let { it.analyticsOptIn = it }
                crashReportingOptIn?.let { it.crashReportingOptIn = it }
                marketingOptIn?.let { it.marketingOptIn = it }
                healthConnectEnabled?.let { it.healthConnectEnabled = it }
                dataSharingConsentVersion?.let {
                    it.dataSharingConsentVersion = it
                    it.dataSharingConsentDate = System.currentTimeMillis()
                }
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Update security settings.
     */
    suspend fun updateSecuritySettings(
        userId: String,
        biometricEnabled: Boolean? = null,
        autoLockEnabled: Boolean? = null,
        autoLockTimeoutMinutes: Int? = null
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val profile = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            profile?.let {
                biometricEnabled?.let { it.biometricEnabled = it }
                autoLockEnabled?.let { it.autoLockEnabled = it }
                autoLockTimeoutMinutes?.let { it.autoLockTimeoutMinutes = it }
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Check if profile exists.
     */
    suspend fun profileExists(userId: String): Boolean = withContext(Dispatchers.IO) {
        realm.where(UserProfileRealm::class.java)
            .equalTo("id", userId)
            .equalTo("deletedAt", 0L)
            .count() > 0
    }

    /**
     * Check if email is already registered.
     */
    suspend fun emailExists(email: String): Boolean = withContext(Dispatchers.IO) {
        realm.where(UserProfileRealm::class.java)
            .equalTo("email", email)
            .equalTo("deletedAt", 0L)
            .count() > 0
    }

    /**
     * Executa uma transacao de escrita devolvendo o valor produzido pelo bloco.
     * A API Java do Realm nao retorna valor de `executeTransaction`.
     */
    private suspend fun <T> writeTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
        var result: T? = null
        realm.executeTransaction { transactionRealm ->
            result = block(transactionRealm)
        }
        @Suppress("UNCHECKED_CAST")
        result as T
    }

}