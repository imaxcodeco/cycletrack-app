package com.seuapp.cycletrack.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * DTOs para requisições de perfil/configurações.
 */
@Serializable
data class UpdateProfileRequest(
    val displayName: String? = null,
    val dateOfBirth: String? = null, // ISO date
    val heightCm: Int? = null,
    val weightKg: Float? = null,
    val gender: String? = null
)

@Serializable
data class UpdateCycleCharacteristicsRequest(
    val averageCycleLength: Int,
    val averagePeriodLength: Int,
    val cycleRegularity: String, // regular, irregular, very_irregular
    val lastPeriodDate: String // ISO date
)

@Serializable
data class UpdateContraceptionRequest(
    val contraceptionMethod: String, // none, pill, iud_hormonal, iud_copper, implant, patch, ring, injection, condom, withdrawal, fertility_awareness, sterilization, other
    val contraceptionStartDate: String? = null // ISO date
)

@Serializable
data class UpdatePregnancyStatusRequest(
    val pregnancyStatus: String, // not_pregnant, trying, pregnant, postpartum, perimenopause, menopause
    val pregnancyDueDate: String? = null, // ISO date
    val pregnancyStartDate: String? = null // ISO date
)

@Serializable
data class UpdateNotificationPreferencesRequest(
    val periodReminderEnabled: Boolean? = null,
    val periodReminderDaysBefore: Int? = null,
    val fertileWindowReminderEnabled: Boolean? = null,
    val ovulationReminderEnabled: Boolean? = null,
    val pillReminderEnabled: Boolean? = null,
    val pillReminderTime: String? = null // HH:mm
)

@Serializable
data class UpdateDisplayPreferencesRequest(
    val themeMode: String? = null, // light, dark, system
    val language: String? = null,
    val temperatureUnit: String? = null, // celsius, fahrenheit
    val weightUnit: String? = null, // kg, lbs
    val heightUnit: String? = null, // cm, ft_in
    val firstDayOfWeek: Int? = null, // 1-7
    val calendarViewMode: String? = null // month, week, list
)

@Serializable
data class UpdateConsentPreferencesRequest(
    val analyticsOptIn: Boolean? = null,
    val crashReportingOptIn: Boolean? = null,
    val marketingOptIn: Boolean? = null,
    val healthConnectEnabled: Boolean? = null,
    val dataSharingConsentVersion: String? = null
)

@Serializable
data class UpdateSecuritySettingsRequest(
    val biometricEnabled: Boolean? = null,
    val autoLockEnabled: Boolean? = null,
    val autoLockTimeoutMinutes: Int? = null
)

@Serializable
data class MarkReadRequest(
    val progress: Float = 1.0f
)

@Serializable
data class ExportRequest(
    val format: String = "json", // json, csv, pdf
    val includeCycles: Boolean = true,
    val includeSymptoms: Boolean = true,
    val includePredictions: Boolean = true,
    val includePregnancy: Boolean = true,
    val includePartnerData: Boolean = false,
    val includeHealthConnectData: Boolean = false,
    val includeSettings: Boolean = true,
    val includeContent: Boolean = false,
    val dateRangeStart: String? = null, // ISO date
    val dateRangeEnd: String? = null, // ISO date
    val anonymize: Boolean = false
)