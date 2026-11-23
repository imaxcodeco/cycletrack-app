package com.seuapp.cycletrack.domain.usecase.auth

import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.model.Gender
import com.seuapp.cycletrack.domain.model.CycleRegularity
import com.seuapp.cycletrack.domain.model.ContraceptionMethod
import com.seuapp.cycletrack.domain.model.PregnancyStatus
import com.seuapp.cycletrack.domain.model.ThemeMode
import com.seuapp.cycletrack.domain.repository.UserProfileRepository
import kotlinx.datetime.LocalDate
import import com.seuapp.cycletrack.util.toEpochMilliseconds

/**
 * Casos de uso para Autenticação e Perfil.
 * Clean-room - gerencia registro, login, perfil e conta.
 */
class AuthUseCases(
    private val userProfileRepository: UserProfileRepository
) {

    /**
     * Registra novo usuário (onboarding completo).
     */
    suspend fun registerUser(
        email: String,
        displayName: String,
        dateOfBirth: LocalDate?,
        gender: Gender = Gender.FEMALE,
        heightCm: Int = 0,
        weightKg: Float = 0.0f,
        averageCycleLength: Int = 28,
        averagePeriodLength: Int = 5,
        lastPeriodDate: LocalDate?,
        contraceptionMethod: ContraceptionMethod = ContraceptionMethod.NONE,
        pregnancyStatus: PregnancyStatus = PregnancyStatus.NOT_PREGNANT,
        language: String = "pt_BR",
        isAnonymousMode: Boolean = false
    ): UserProfile {
        // Verifica se email já existe
        if (userProfileRepository.emailExists(email)) {
            throw IllegalArgumentException("Email já cadastrado")
        }

        val profile = UserProfile(
            id = java.util.UUID.randomUUID().toString(),
            email = email.lowercase(),
            displayName = displayName,
            dateOfBirth = dateOfBirth,
            heightCm = heightCm,
            weightKg = weightKg,
            gender = gender,
            averageCycleLength = averageCycleLength.coerceIn(21, 45),
            averagePeriodLength = averagePeriodLength.coerceIn(2, 10),
            contraceptionMethod = contraceptionMethod.key,
            lastPeriodDate = lastPeriodDate,
            pregnancyStatus = pregnancyStatus.key,
            language = language,
            isAnonymousMode = isAnonymousMode,
            accountCreatedAt = System.currentTimeMillis(),
            themeMode = ThemeMode.SYSTEM
        )

        return userProfileRepository.upsertProfile(profile)
    }

    /**
     * Cria usuário anônimo (modo sem conta).
     */
    suspend fun createAnonymousUser(
        language: String = "pt_BR",
        averageCycleLength: Int = 28,
        averagePeriodLength: Int = 5,
        lastPeriodDate: LocalDate?
    ): UserProfile {
        val profile = UserProfile(
            id = java.util.UUID.randomUUID().toString(),
            email = "",
            displayName = "Usuário Anônimo",
            averageCycleLength = averageCycleLength.coerceIn(21, 45),
            averagePeriodLength = averagePeriodLength.coerceIn(2, 10),
            lastPeriodDate = lastPeriodDate,
            language = language,
            isAnonymousMode = true,
            accountCreatedAt = System.currentTimeMillis(),
            analyticsOptIn = false,
            crashReportingOptIn = false,
            marketingOptIn = false
        )

        return userProfileRepository.upsertProfile(profile)
    }

    /**
     * Obtém perfil do usuário.
     */
    suspend fun getProfile(userId: String): UserProfile? {
        return userProfileRepository.getProfile(userId)
    }

    /**
     * Obtém perfil por email.
     */
    suspend fun getProfileByEmail(email: String): UserProfile? {
        return userProfileRepository.getProfileByEmail(email.lowercase())
    }

    /**
     * Atualiza perfil básico.
     */
    suspend fun updateProfile(
        userId: String,
        displayName: String? = null,
        dateOfBirth: LocalDate? = null,
        heightCm: Int? = null,
        weightKg: Float? = null,
        gender: Gender? = null
    ): Boolean {
        return userProfileRepository.updateProfile(userId) { profile ->
            displayName?.let { profile = profile.copy(displayName = it) }
            dateOfBirth?.let { profile = profile.copy(dateOfBirth = it) }
            heightCm?.let { profile = profile.copy(heightCm = it) }
            weightKg?.let { profile = profile.copy(weightKg = it) }
            gender?.let { profile = profile.copy(gender = it) }
        }
    }

    /**
     * Atualiza características do ciclo.
     */
    suspend fun updateCycleCharacteristics(
        userId: String,
        averageCycleLength: Int,
        averagePeriodLength: Int,
        cycleRegularity: CycleRegularity,
        lastPeriodDate: LocalDate
    ): Boolean {
        return userProfileRepository.updateCycleCharacteristics(
            userId = userId,
            averageCycleLength = averageCycleLength.coerceIn(21, 45),
            averagePeriodLength = averagePeriodLength.coerceIn(2, 10),
            averageLutealPhaseLength = 14,
            cycleRegularity = cycleRegularity.key,
            lastPeriodDate = lastPeriodDate.toEpochMilliseconds()
        )
    }

    /**
     * Atualiza método contraceptivo.
     */
    suspend fun updateContraceptionMethod(
        userId: String,
        method: ContraceptionMethod,
        startDate: LocalDate? = null
    ): Boolean {
        return userProfileRepository.updateProfile(userId) { profile ->
            profile = profile.copy(contraceptionMethod = method.key)
            startDate?.let { profile = profile.copy(contraceptionStartDate = it) }
        }
    }

    /**
     * Atualiza status de gravidez.
     */
    suspend fun updatePregnancyStatus(
        userId: String,
        status: PregnancyStatus,
        dueDate: LocalDate? = null,
        startDate: LocalDate? = null
    ): Boolean {
        return userProfileRepository.updatePregnancyStatus(
            userId = userId,
            status = status.key,
            dueDate = dueDate?.toEpochMilliseconds() ?: 0L,
            startDate = startDate?.toEpochMilliseconds() ?: 0L
        )
    }

    /**
     * Atualiza preferências de notificação.
     */
    suspend fun updateNotificationPreferences(
        userId: String,
        periodReminderEnabled: Boolean? = null,
        periodReminderDaysBefore: Int? = null,
        fertileWindowReminderEnabled: Boolean? = null,
        ovulationReminderEnabled: Boolean? = null,
        pillReminderEnabled: Boolean? = null,
        pillReminderTime: String? = null
    ): Boolean {
        return userProfileRepository.updateNotificationPreferences(
            userId = userId,
            periodReminderEnabled = periodReminderEnabled,
            periodReminderDaysBefore = periodReminderDaysBefore,
            fertileWindowReminderEnabled = fertileWindowReminderEnabled,
            ovulationReminderEnabled = ovulationReminderEnabled,
            pillReminderEnabled = pillReminderEnabled,
            pillReminderTime = pillReminderTime
        )
    }

    /**
     * Atualiza preferências de exibição.
     */
    suspend fun updateDisplayPreferences(
        userId: String,
        themeMode: ThemeMode? = null,
        language: String? = null,
        temperatureUnit: String? = null,
        weightUnit: String? = null,
        heightUnit: String? = null,
        firstDayOfWeek: Int? = null,
        calendarViewMode: String? = null
    ): Boolean {
        return userProfileRepository.updateDisplayPreferences(
            userId = userId,
            themeMode = themeMode?.key,
            language = language,
            temperatureUnit = temperatureUnit,
            weightUnit = weightUnit,
            heightUnit = heightUnit,
            firstDayOfWeek = firstDayOfWeek,
            calendarViewMode = calendarViewMode
        )
    }

    /**
     * Atualiza preferências de privacidade/consentimento.
     */
    suspend fun updateConsentPreferences(
        userId: String,
        analyticsOptIn: Boolean? = null,
        crashReportingOptIn: Boolean? = null,
        marketingOptIn: Boolean? = null,
        healthConnectEnabled: Boolean? = null,
        dataSharingConsentVersion: String? = null
    ): Boolean {
        return userProfileRepository.updateConsentPreferences(
            userId = userId,
            analyticsOptIn = analyticsOptIn,
            crashReportingOptIn = crashReportingOptIn,
            marketingOptIn = marketingOptIn,
            healthConnectEnabled = healthConnectEnabled,
            dataSharingConsentVersion = dataSharingConsentVersion
        )
    }

    /**
     * Atualiza configurações de segurança.
     */
    suspend fun updateSecuritySettings(
        userId: String,
        biometricEnabled: Boolean? = null,
        autoLockEnabled: Boolean? = null,
        autoLockTimeoutMinutes: Int? = null
    ): Boolean {
        return userProfileRepository.updateSecuritySettings(
            userId = userId,
            biometricEnabled = biometricEnabled,
            autoLockEnabled = autoLockEnabled,
            autoLockTimeoutMinutes = autoLockTimeoutMinutes
        )
    }

    /**
     * Atualiza último login.
     */
    suspend fun updateLastLogin(userId: String): Boolean {
        return userProfileRepository.updateLastLogin(userId)
    }

    /**
     * Deleta conta (soft delete).
     */
    suspend fun deleteAccount(userId: String): Boolean {
        return userProfileRepository.deleteProfile(userId)
    }

    /**
     * Hard delete conta e todos os dados (LGPD/GDPR - direito ao esquecimento).
     */
    suspend fun hardDeleteAccount(userId: String): Boolean {
        return userProfileRepository.hardDeleteProfile(userId)
    }

    /**
     * Verifica se email está disponível.
     */
    suspend fun isEmailAvailable(email: String): Boolean {
        return !userProfileRepository.emailExists(email.lowercase())
    }

    /**
     * Obtém perfil para exportação (LGPD/GDPR).
     */
    suspend fun getProfileForExport(userId: String, anonymize: Boolean): UserProfile? {
        return userProfileRepository.getProfileForExport(userId, anonymize)
    }
}