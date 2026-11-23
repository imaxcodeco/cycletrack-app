package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity representing user profile and preferences.
 * Clean-room implementation with LGPD/GDPR compliance fields.
 */
@Serializable
open class UserProfileRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var email: String = ""

    var displayName: String = ""

    var dateOfBirth: Long = 0L

    var heightCm: Int = 0

    var weightKg: Float = 0.0f

    var gender: String = "female" // female, male, non_binary, prefer_not_to_say

    // Cycle characteristics
    var averageCycleLength: Int = 28
    var averagePeriodLength: Int = 5
    var averageLutealPhaseLength: Int = 14
    var cycleRegularity: String = "regular" // regular, irregular, very_irregular

    // Reproductive health
    var contraceptionMethod: String = "none" // none, pill, iud_hormonal, iud_copper, implant, patch, ring, injection, condom, withdrawal, fertility_awareness, sterilization, other
    var contraceptionStartDate: Long = 0L
    var lastPeriodDate: Long = 0L
    var periodDurationDays: Int = 5

    // Pregnancy/Postpartum
    var pregnancyStatus: String = "not_pregnant" // not_pregnant, trying, pregnant, postpartum, perimenopause, menopause
    var pregnancyDueDate: Long = 0L
    var pregnancyStartDate: Long = 0L
    var childrenCount: Int = 0
    var lastPregnancyOutcome: String = "" // live_birth, miscarriage, abortion, stillbirth, ectopic

    // Medical conditions (for insights)
    var hasPcos: Boolean = false
    var hasEndometriosis: Boolean = false
    var hasThyroidCondition: Boolean = false
    var hasDiabetes: Boolean = false
    var medicalConditions: RealmList<String> = RealmList()

    // Notifications & Reminders
    var periodReminderEnabled: Boolean = true
    var periodReminderDaysBefore: Int = 2
    var fertileWindowReminderEnabled: Boolean = true
    var ovulationReminderEnabled: Boolean = true
    var pillReminderEnabled: Boolean = false
    var pillReminderTime: String = "21:00"
    var customReminders: RealmList<String> = RealmList()

    // Display preferences
    var themeMode: String = "system" // light, dark, system
    var language: String = "pt_BR"
    var temperatureUnit: String = "celsius" // celsius, fahrenheit
    var weightUnit: String = "kg" // kg, lbs
    var heightUnit: String = "cm" // cm, ft_in
    var firstDayOfWeek: Int = 1 // 1=Monday, 7=Sunday
    var calendarViewMode: String = "month" // month, week, list

    // Privacy & Data
    var analyticsOptIn: Boolean = true
    var crashReportingOptIn: Boolean = true
    var marketingOptIn: Boolean = false
    var healthConnectEnabled: Boolean = false
    var healthConnectPermissionsGranted: Boolean = false
    var dataSharingConsentVersion: String = "1.0"
    var dataSharingConsentDate: Long = 0L

    // Account
    var isAnonymousMode: Boolean = false
    var accountCreatedAt: Long = System.currentTimeMillis()
    var lastLoginAt: Long = 0L
    var emailVerified: Boolean = false
    var phoneVerified: Boolean = false
    var biometricEnabled: Boolean = false
    var autoLockEnabled: Boolean = true
    var autoLockTimeoutMinutes: Int = 5

    // Premium/Subscription
    var subscriptionStatus: String = "free" // free, trial, premium, expired, cancelled
    var subscriptionProductId: String = ""
    var subscriptionExpiresAt: Long = 0L
    var subscriptionAutoRenew: Boolean = true
    var subscriptionPlatform: String = "" // google_play, apple_app_store, promo
    var trialUsed: Boolean = false

    // Partner mode
    var partnerModeEnabled: Boolean = false
    var partnerId: String = ""
    var partnerShareLevel: String = "full" // full, cycle_only, fertility_only, insights_only
    var partnerInvitationSent: Boolean = false
    var partnerInvitationAcceptedAt: Long = 0L

    // Sync & Backup
    var autoSyncEnabled: Boolean = true
    var syncFrequencyMinutes: Int = 15
    var lastBackupAt: Long = 0L
    var backupFrequencyDays: Int = 7
    var exportFormat: String = "json" // json, csv, pdf

    // Metadata
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var deletedAt: Long = 0L
    var serverId: String = ""
    var lastSyncedAt: Long = 0L
    var version: Int = 1

    companion object {
        const val GENDER_FEMALE = "female"
        const val GENDER_MALE = "male"
        const val GENDER_NON_BINARY = "non_binary"
        const val GENDER_PREFER_NOT_TO_SAY = "prefer_not_to_say"

        const val CONTRACEPTION_NONE = "none"
        const val CONTRACEPTION_PILL = "pill"
        const val CONTRACEPTION_IUD_HORMONAL = "iud_hormonal"
        const val CONTRACEPTION_IUD_COPPER = "iud_copper"
        const val CONTRACEPTION_IMPLANT = "implant"
        const val CONTRACEPTION_PATCH = "patch"
        const val CONTRACEPTION_RING = "ring"
        const val CONTRACEPTION_INJECTION = "injection"
        const val CONTRACEPTION_CONDOM = "condom"
        const val CONTRACEPTION_WITHDRAWAL = "withdrawal"
        const val CONTRACEPTION_FERTILITY_AWARENESS = "fertility_awareness"
        const val CONTRACEPTION_STERILIZATION = "sterilization"
        const val CONTRACEPTION_OTHER = "other"

        const val PREGNANCY_NOT_PREGNANT = "not_pregnant"
        const val PREGNANCY_TRYING = "trying"
        const val PREGNANCY_PREGNANT = "pregnant"
        const val PREGNANCY_POSTPARTUM = "postpartum"
        const val PREGNANCY_PERIMENOPAUSE = "perimenopause"
        const val PREGNANCY_MENOPAUSE = "menopause"

        const val SUBSCRIPTION_FREE = "free"
        const val SUBSCRIPTION_TRIAL = "trial"
        const val SUBSCRIPTION_PREMIUM = "premium"
        const val SUBSCRIPTION_EXPIRED = "expired"
        const val SUBSCRIPTION_CANCELLED = "cancelled"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"

        const val SHARE_FULL = "full"
        const val SHARE_CYCLE_ONLY = "cycle_only"
        const val SHARE_FERTILITY_ONLY = "fertility_only"
        const val SHARE_INSIGHTS_ONLY = "insights_only"
    }
}