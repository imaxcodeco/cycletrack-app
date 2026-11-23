package com.seuapp.cycletrack.data.remote.mappers

import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.data.remote.dto.request.ProfileRequests
import com.seuapp.cycletrack.data.remote.dto.response.ProfileResponses
import com.seuapp.cycletrack.data.local.realm.entities.UserProfileRealm
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Instant
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.weight
import import com.seuapp.cycletrack.util.toEpochMilliseconds

/**
 * Mapeadores para Perfil do Usuário entre Domain, Realm, DTOs Request/Response.
 * Clean-room - implementação própria.
 */
object UserProfileMapper {

    // Domain <-> Realm
    fun toRealm(domain: UserProfile): UserProfileRealm = UserProfileRealm().apply {
        id = domain.id
        email = domain.email
        displayName = domain.displayName
        dateOfBirth = domain.dateOfBirth?.toEpochMilliseconds()
        height = domain.height
        weight = domain.weight
        gender = domain.gender
        averageCycleLength = domain.averageCycleLength
        averagePeriodLength = domain.averagePeriodLength
        averageLutealPhaseLength = domain.averageLutealPhaseLength
        cycleRegularity = domain.cycleRegularity
        contraceptionMethod = domain.contraceptionMethod
        lastPeriodDate = domain.lastPeriodDate?.toEpochMilliseconds()
        subscriptionStatus = domain.subscriptionStatus
        subscriptionProductId = domain.subscriptionProductId
        subscriptionExpiresAt = domain.subscriptionExpiresAt
        subscriptionAutoRenew = domain.subscriptionAutoRenew
        subscriptionPlatform = domain.subscriptionPlatform
        partnerId = domain.partnerId
        partnerShareLevel = domain.partnerShareLevel
        partnerInvitationSent = domain.partnerInvitationSent
        themeMode = domain.themeMode
        language = domain.language
        temperatureUnit = domain.temperatureUnit
        weightUnit = domain.weightUnit
        heightUnit = domain.heightUnit
        firstDayOfWeek = domain.firstDayOfWeek
        calendarViewMode = domain.calendarViewMode
        notificationsEnabled = domain.notificationsEnabled
        periodReminderEnabled = domain.periodReminderEnabled
        periodReminderDaysBefore = domain.periodReminderDaysBefore
        fertileWindowReminderEnabled = domain.fertileWindowReminderEnabled
        ovulationReminderEnabled = domain.ovulationReminderEnabled
        pmsReminderEnabled = domain.pmsReminderEnabled
        pillReminderEnabled = domain.pillReminderEnabled
        pillReminderTime = domain.pillReminderTime
        partnerNotificationsEnabled = domain.partnerNotificationsEnabled
        analyticsOptIn = domain.analyticsOptIn
        crashReportingOptIn = domain.crashReportingOptIn
        marketingOptIn = domain.marketingOptIn
        healthConnectEnabled = domain.healthConnectEnabled
        dataSharingConsentVersion = domain.dataSharingConsentVersion
        biometricEnabled = domain.biometricEnabled
        autoLockEnabled = domain.autoLockEnabled
        autoLockTimeoutMinutes = domain.autoLockTimeoutMinutes
        createdAt = domain.createdAt
        updatedAt = domain.updatedAt
        lastLoginAt = domain.lastLoginAt
        deletedAt = 0L
    }

    fun toDomain(realm: UserProfileRealm): UserProfile = UserProfile(
        id = realm.id,
        email = realm.email,
        displayName = realm.displayName,
        dateOfBirth = realm.dateOfBirth?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        height = realm.height,
        weight = realm.weight,
        gender = realm.gender,
        averageCycleLength = realm.averageCycleLength,
        averagePeriodLength = realm.averagePeriodLength,
        averageLutealPhaseLength = realm.averageLutealPhaseLength,
        cycleRegularity = realm.cycleRegularity,
        contraceptionMethod = realm.contraceptionMethod,
        lastPeriodDate = realm.lastPeriodDate?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        subscriptionStatus = realm.subscriptionStatus,
        subscriptionProductId = realm.subscriptionProductId,
        subscriptionExpiresAt = realm.subscriptionExpiresAt,
        subscriptionAutoRenew = realm.subscriptionAutoRenew,
        subscriptionPlatform = realm.subscriptionPlatform,
        partnerId = realm.partnerId,
        partnerShareLevel = realm.partnerShareLevel,
        partnerInvitationSent = realm.partnerInvitationSent,
        themeMode = realm.themeMode,
        language = realm.language,
        temperatureUnit = realm.temperatureUnit,
        weightUnit = realm.weightUnit,
        heightUnit = realm.heightUnit,
        firstDayOfWeek = realm.firstDayOfWeek,
        calendarViewMode = realm.calendarViewMode,
        notificationsEnabled = realm.notificationsEnabled,
        periodReminderEnabled = realm.periodReminderEnabled,
        periodReminderDaysBefore = realm.periodReminderDaysBefore,
        fertileWindowReminderEnabled = realm.fertileWindowReminderEnabled,
        ovulationReminderEnabled = realm.ovulationReminderEnabled,
        pmsReminderEnabled = realm.pmsReminderEnabled,
        pillReminderEnabled = realm.pillReminderEnabled,
        pillReminderTime = realm.pillReminderTime,
        partnerNotificationsEnabled = realm.partnerNotificationsEnabled,
        analyticsOptIn = realm.analyticsOptIn,
        crashReportingOptIn = realm.crashReportingOptIn,
        marketingOptIn = realm.marketingOptIn,
        healthConnectEnabled = realm.healthConnectEnabled,
        dataSharingConsentVersion = realm.dataSharingConsentVersion,
        biometricEnabled = realm.biometricEnabled,
        autoLockEnabled = realm.autoLockEnabled,
        autoLockTimeoutMinutes = realm.autoLockTimeoutMinutes,
        createdAt = realm.createdAt,
        updatedAt = realm.updatedAt,
        lastLoginAt = realm.lastLoginAt
    )

    // Domain -> Request DTO
    fun toCreateRequest(domain: UserProfile): ProfileRequests.CreateProfileRequest = ProfileRequests.CreateProfileRequest(
        email = domain.email,
        displayName = domain.displayName,
        dateOfBirth = domain.dateOfBirth?.toEpochMilliseconds(),
        height = domain.height,
        weight = domain.weight,
        gender = domain.gender,
        averageCycleLength = domain.averageCycleLength,
        averagePeriodLength = domain.averagePeriodLength,
        averageLutealPhaseLength = domain.averageLutealPhaseLength,
        cycleRegularity = domain.cycleRegularity,
        contraceptionMethod = domain.contraceptionMethod,
        lastPeriodDate = domain.lastPeriodDate?.toEpochMilliseconds(),
        themeMode = domain.themeMode,
        language = domain.language,
        temperatureUnit = domain.temperatureUnit,
        weightUnit = domain.weightUnit,
        heightUnit = domain.heightUnit,
        firstDayOfWeek = domain.firstDayOfWeek,
        calendarViewMode = domain.calendarViewMode,
        notificationsEnabled = domain.notificationsEnabled,
        periodReminderEnabled = domain.periodReminderEnabled,
        periodReminderDaysBefore = domain.periodReminderDaysBefore,
        fertileWindowReminderEnabled = domain.fertileWindowReminderEnabled,
        ovulationReminderEnabled = domain.ovulationReminderEnabled,
        pmsReminderEnabled = domain.pmsReminderEnabled,
        pillReminderEnabled = domain.pillReminderEnabled,
        pillReminderTime = domain.pillReminderTime,
        partnerNotificationsEnabled = domain.partnerNotificationsEnabled,
        analyticsOptIn = domain.analyticsOptIn,
        crashReportingOptIn = domain.crashReportingOptIn,
        marketingOptIn = domain.marketingOptIn,
        healthConnectEnabled = domain.healthConnectEnabled,
        dataSharingConsentVersion = domain.dataSharingConsentVersion,
        biometricEnabled = domain.biometricEnabled,
        autoLockEnabled = domain.autoLockEnabled,
        autoLockTimeoutMinutes = domain.autoLockTimeoutMinutes
    )

    fun toUpdateRequest(domain: UserProfile): ProfileRequests.UpdateProfileRequest = ProfileRequests.UpdateProfileRequest(
        id = domain.id,
        displayName = domain.displayName,
        dateOfBirth = domain.dateOfBirth?.toEpochMilliseconds(),
        height = domain.height,
        weight = domain.weight,
        gender = domain.gender,
        averageCycleLength = domain.averageCycleLength,
        averagePeriodLength = domain.averagePeriodLength,
        averageLutealPhaseLength = domain.averageLutealPhaseLength,
        cycleRegularity = domain.cycleRegularity,
        contraceptionMethod = domain.contraceptionMethod,
        lastPeriodDate = domain.lastPeriodDate?.toEpochMilliseconds(),
        subscriptionStatus = domain.subscriptionStatus,
        subscriptionProductId = domain.subscriptionProductId,
        subscriptionExpiresAt = domain.subscriptionExpiresAt,
        subscriptionAutoRenew = domain.subscriptionAutoRenew,
        subscriptionPlatform = domain.subscriptionPlatform,
        partnerId = domain.partnerId,
        partnerShareLevel = domain.partnerShareLevel,
        partnerInvitationSent = domain.partnerInvitationSent,
        themeMode = domain.themeMode,
        language = domain.language,
        temperatureUnit = domain.temperatureUnit,
        weightUnit = domain.weightUnit,
        heightUnit = domain.heightUnit,
        firstDayOfWeek = domain.firstDayOfWeek,
        calendarViewMode = domain.calendarViewMode,
        notificationsEnabled = domain.notificationsEnabled,
        periodReminderEnabled = domain.periodReminderEnabled,
        periodReminderDaysBefore = domain.periodReminderDaysBefore,
        fertileWindowReminderEnabled = domain.fertileWindowReminderEnabled,
        ovulationReminderEnabled = domain.ovulationReminderEnabled,
        pmsReminderEnabled = domain.pmsReminderEnabled,
        pillReminderEnabled = domain.pillReminderEnabled,
        pillReminderTime = domain.pillReminderTime,
        partnerNotificationsEnabled = domain.partnerNotificationsEnabled,
        analyticsOptIn = domain.analyticsOptIn,
        crashReportingOptIn = domain.crashReportingOptIn,
        marketingOptIn = domain.marketingOptIn,
        healthConnectEnabled = domain.healthConnectEnabled,
        dataSharingConsentVersion = domain.dataSharingConsentVersion,
        biometricEnabled = domain.biometricEnabled,
        autoLockEnabled = domain.autoLockEnabled,
        autoLockTimeoutMinutes = domain.autoLockTimeoutMinutes,
        version = domain.version
    )

    // Response DTO -> Domain
    fun fromResponse(response: ProfileResponses.ProfileResponse): UserProfile = UserProfile(
        id = response.id,
        email = response.email,
        displayName = response.displayName,
        dateOfBirth = response.dateOfBirth?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        height = response.height,
        weight = response.weight,
        gender = response.gender,
        averageCycleLength = response.averageCycleLength,
        averagePeriodLength = response.averagePeriodLength,
        averageLutealPhaseLength = response.averageLutealPhaseLength,
        cycleRegularity = response.cycleRegularity,
        contraceptionMethod = response.contraceptionMethod,
        lastPeriodDate = response.lastPeriodDate?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        subscriptionStatus = response.subscriptionStatus,
        subscriptionProductId = response.subscriptionProductId,
        subscriptionExpiresAt = response.subscriptionExpiresAt,
        subscriptionAutoRenew = response.subscriptionAutoRenew,
        subscriptionPlatform = response.subscriptionPlatform,
        partnerId = response.partnerId,
        partnerShareLevel = response.partnerShareLevel,
        partnerInvitationSent = response.partnerInvitationSent,
        themeMode = response.themeMode,
        language = response.language,
        temperatureUnit = response.temperatureUnit,
        weightUnit = response.weightUnit,
        heightUnit = response.heightUnit,
        firstDayOfWeek = response.firstDayOfWeek,
        calendarViewMode = response.calendarViewMode,
        notificationsEnabled = response.notificationsEnabled,
        periodReminderEnabled = response.periodReminderEnabled,
        periodReminderDaysBefore = response.periodReminderDaysBefore,
        fertileWindowReminderEnabled = response.fertileWindowReminderEnabled,
        ovulationReminderEnabled = response.ovulationReminderEnabled,
        pmsReminderEnabled = response.pmsReminderEnabled,
        pillReminderEnabled = response.pillReminderEnabled,
        pillReminderTime = response.pillReminderTime,
        partnerNotificationsEnabled = response.partnerNotificationsEnabled,
        analyticsOptIn = response.analyticsOptIn,
        crashReportingOptIn = response.crashReportingOptIn,
        marketingOptIn = response.marketingOptIn,
        healthConnectEnabled = response.healthConnectEnabled,
        dataSharingConsentVersion = response.dataSharingConsentVersion,
        biometricEnabled = response.biometricEnabled,
        autoLockEnabled = response.autoLockEnabled,
        autoLockTimeoutMinutes = response.autoLockTimeoutMinutes,
        createdAt = response.createdAt,
        updatedAt = response.updatedAt,
        lastLoginAt = response.lastLoginAt
    )
}