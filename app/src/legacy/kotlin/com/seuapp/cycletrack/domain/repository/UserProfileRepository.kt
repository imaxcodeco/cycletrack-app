package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.model.SubscriptionDisplayStatus

/**
 * Interface do repositório de Perfil do Usuário.
 * Clean-room - define contrato sem expor implementação.
 */
interface UserProfileRepository {

    /**
     * Obtém perfil por ID do usuário.
     */
    suspend fun getProfile(userId: String): UserProfile?

    /**
     * Obtém perfil por email.
     */
    suspend fun getProfileByEmail(email: String): UserProfile?

    /**
     * Insere ou atualiza perfil.
     */
    suspend fun upsertProfile(profile: UserProfile): UserProfile

    /**
     * Atualiza campos específicos do perfil.
     */
    suspend fun updateProfile(userId: String, updateBlock: (UserProfile) -> Unit): Boolean

    /**
     * Soft delete.
     */
    suspend fun deleteProfile(userId: String): Boolean

    /**
     * Hard delete e todos os dados associados (LGPD/GDPR).
     */
    suspend fun hardDeleteProfile(userId: String): Boolean

    /**
     * Atualiza status da assinatura.
     */
    suspend fun updateSubscriptionStatus(
        userId: String,
        status: String,
        productId: String = "",
        expiresAt: Long = 0L,
        autoRenew: Boolean = true,
        platform: String = ""
    ): Boolean

    /**
     * Atualiza informações de conexão com parceiro.
     */
    suspend fun updatePartnerInfo(
        userId: String,
        partnerId: String,
        shareLevel: String,
        invitationSent: Boolean = false
    ): Boolean

    /**
     * Atualiza timestamp do último login.
     */
    suspend fun updateLastLogin(userId: String): Boolean

    /**
     * Atualiza características do ciclo.
     */
    suspend fun updateCycleCharacteristics(
        userId: String,
        averageCycleLength: Int,
        averagePeriodLength: Int,
        averageLutealPhaseLength: Int,
        cycleRegularity: String,
        lastPeriodDate: Long
    ): Boolean

    /**
     * Atualiza status de gravidez.
     */
    suspend fun updatePregnancyStatus(
        userId: String,
        status: String,
        dueDate: Long = 0L,
        startDate: Long = 0L
    ): Boolean

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
    ): Boolean

    /**
     * Atualiza preferências de exibição.
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
    ): Boolean

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
    ): Boolean

    /**
     * Atualiza configurações de segurança.
     */
    suspend fun updateSecuritySettings(
        userId: String,
        biometricEnabled: Boolean? = null,
        autoLockEnabled: Boolean? = null,
        autoLockTimeoutMinutes: Int? = null
    ): Boolean

    /**
     * Verifica se perfil existe.
     */
    suspend fun profileExists(userId: String): Boolean

    /**
     * Verifica se email já está cadastrado.
     */
    suspend fun emailExists(email: String): Boolean

    /**
     * Obtém status de exibição da assinatura.
     */
    suspend fun getSubscriptionDisplayStatus(userId: String): SubscriptionDisplayStatus

    /**
     * Obtém perfil para exportação (LGPD/GDPR).
     */
    suspend fun getProfileForExport(userId: String, anonymize: Boolean): UserProfile?
}