package com.seuapp.cycletrack.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * Modelo de domínio para Perfil do Usuário.
 * Clean-room com campos LGPD/GDPR.
 */
@Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String = "",
    val dateOfBirth: LocalDate?,
    val heightCm: Int = 0,
    val weightKg: Float = 0.0f,
    val gender: Gender = Gender.FEMALE,

    // Características do ciclo
    val averageCycleLength: Int = 28,
    val averagePeriodLength: Int = 5,
    val averageLutealPhaseLength: Int = 14,
    val cycleRegularity: CycleRegularity = CycleRegularity.REGULAR,

    // Contracepção
    val contraceptionMethod: ContraceptionMethod = ContraceptionMethod.NONE,
    val contraceptionStartDate: LocalDate? = null,

    // Última menstruação
    val lastPeriodDate: LocalDate? = null,
    val periodDurationDays: Int = 5,

    // Gravidez/Perimenopausa
    val pregnancyStatus: PregnancyStatus = PregnancyStatus.NOT_PREGNANT,
    val pregnancyDueDate: LocalDate? = null,
    val pregnancyStartDate: LocalDate? = null,
    val childrenCount: Int = 0,
    val lastPregnancyOutcome: PregnancyOutcome? = null,

    // Condições médicas
    val hasPcos: Boolean = false,
    val hasEndometriosis: Boolean = false,
    val hasThyroidCondition: Boolean = false,
    val hasDiabetes: Boolean = false,
    val medicalConditions: List<String> = emptyList(),

    // Notificações
    val periodReminderEnabled: Boolean = true,
    val periodReminderDaysBefore: Int = 2,
    val fertileWindowReminderEnabled: Boolean = true,
    val ovulationReminderEnabled: Boolean = true,
    val pillReminderEnabled: Boolean = false,
    val pillReminderTime: String = "21:00",

    // Preferências de exibição
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: String = "pt_BR",
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val heightUnit: HeightUnit = HeightUnit.CM,
    val firstDayOfWeek: Int = 1, // 1=Segunda, 7=Domingo
    val calendarViewMode: CalendarViewMode = CalendarViewMode.MONTH,

    // Privacidade e consentimento
    val analyticsOptIn: Boolean = true,
    val crashReportingOptIn: Boolean = true,
    val marketingOptIn: Boolean = false,
    val healthConnectEnabled: Boolean = false,
    val healthConnectPermissionsGranted: Boolean = false,
    val dataSharingConsentVersion: String = "1.0",
    val dataSharingConsentDate: Long = 0L,

    // Conta
    val isAnonymousMode: Boolean = false,
    val accountCreatedAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = 0L,
    val emailVerified: Boolean = false,
    val phoneVerified: Boolean = false,
    val biometricEnabled: Boolean = false,
    val autoLockEnabled: Boolean = true,
    val autoLockTimeoutMinutes: Int = 5,

    // Assinatura
    val subscriptionStatus: SubscriptionStatus = SubscriptionStatus.FREE,
    val subscriptionProductId: String = "",
    val subscriptionExpiresAt: Long = 0L,
    val subscriptionAutoRenew: Boolean = true,
    val subscriptionPlatform: String = "",
    val trialUsed: Boolean = false,

    // Modo parceiro
    val partnerModeEnabled: Boolean = false,
    val partnerId: String = "",
    val partnerShareLevel: PartnerShareLevel = PartnerShareLevel.FULL,
    val partnerInvitationSent: Boolean = false,
    val partnerInvitationAcceptedAt: Long = 0L,

    // Sincronização e backup
    val autoSyncEnabled: Boolean = true,
    val syncFrequencyMinutes: Int = 15,
    val lastBackupAt: Long = 0L,
    val backupFrequencyDays: Int = 7,
    val exportFormat: ExportFormat = ExportFormat.JSON,

    // Metadados
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: String = "",
    val lastSyncedAt: Long = 0L,
    val version: Int = 1
) {
    val age: Int?
        get() = dateOfBirth?.let { Clock.System.todayIn(TimeZone.currentSystemDefault()).yearsUntil(it).years }

    val bmi: Float?
        get() = if (heightCm > 0 && weightKg > 0) {
            val heightM = heightCm / 100f
            weightKg / (heightM * heightM)
        } else null

    val isPremium: Boolean
        get() = subscriptionStatus == SubscriptionStatus.PREMIUM || subscriptionStatus == SubscriptionStatus.TRIAL

    val isSubscriptionActive: Boolean
        get() = subscriptionStatus == SubscriptionStatus.PREMIUM && subscriptionExpiresAt > System.currentTimeMillis()

    val isTrialActive: Boolean
        get() = subscriptionStatus == SubscriptionStatus.TRIAL && subscriptionExpiresAt > System.currentTimeMillis()
}

enum class Gender(val key: String, val label: String) {
    FEMALE("female", "Feminino"),
    MALE("male", "Masculino"),
    NON_BINARY("non_binary", "Não-binário"),
    PREFER_NOT_TO_SAY("prefer_not_to_say", "Prefiro não dizer")
}

enum class CycleRegularity(val key: String, val label: String) {
    REGULAR("regular", "Regular"),
    IRREGULAR("irregular", "Irregular"),
    VERY_IRREGULAR("very_irregular", "Muito Irregular")
}

enum class ContraceptionMethod(val key: String, val label: String, val affectsCycle: Boolean) {
    NONE("none", "Nenhum", false),
    PILL("pill", "Pílula Anticoncepcional", true),
    IUD_HORMONAL("iud_hormonal", "DIU Hormonal", true),
    IUD_COPPER("iud_copper", "DIU de Cobre", false),
    IMPLANT("implant", "Implante", true),
    PATCH("patch", "Adesivo", true),
    RING("ring", "Anel Vaginal", true),
    INJECTION("injection", "Injeção", true),
    CONDOM("condom", "Preservativo", false),
    WITHDRAWAL("withdrawal", "Coito Interrompido", false),
    FERTILITY_AWARENESS("fertility_awareness", "Métodos de Percepção da Fertilidade", false),
    STERILIZATION("sterilization", "Esterilização", false),
    OTHER("other", "Outro", false)
}

enum class PregnancyStatus(val key: String, val label: String) {
    NOT_PREGNANT("not_pregnant", "Não Grávida"),
    TRYING("trying", "Tentando Engravidar"),
    PREGNANT("pregnant", "Grávida"),
    POSTPARTUM("postpartum", "Pós-parto"),
    PERIMENOPAUSE("perimenopause", "Perimenopausa"),
    MENOPAUSE("menopause", "Menopausa")
}

enum class PregnancyOutcome(val key: String, val label: String) {
    LIVE_BIRTH("live_birth", "Nascido Vivo"),
    MISCARRIAGE("miscarriage", "Aborto Espontâneo"),
    ABORTION("abortion", "Aborto Induzido"),
    STILLBIRTH("stillbirth", "Natimorto"),
    ECTOPIC("ectopic", "Gravidez Ectópica")
}

enum class ThemeMode(val key: String, val label: String) {
    LIGHT("light", "Claro"),
    DARK("dark", "Escuro"),
    SYSTEM("system", "Sistema")
}

enum class TemperatureUnit(val key: String, val label: String, val symbol: String) {
    CELSIUS("celsius", "Celsius", "°C"),
    FAHRENHEIT("fahrenheit", "Fahrenheit", "°F")
}

enum class WeightUnit(val key: String, val label: String, val symbol: String) {
    KG("kg", "Quilogramas", "kg"),
    LBS("lbs", "Libras", "lbs")
}

enum class HeightUnit(val key: String, val label: String, val symbol: String) {
    CM("cm", "Centímetros", "cm"),
    FT_IN("ft_in", "Pés/Polegadas", "ft/in")
}

enum class CalendarViewMode(val key: String, val label: String) {
    MONTH("month", "Mês"),
    WEEK("week", "Semana"),
    LIST("list", "Lista")
}

enum class SubscriptionStatus(val key: String, val label: String) {
    FREE("free", "Gratuito"),
    TRIAL("trial", "Período de Teste"),
    PREMIUM("premium", "Premium"),
    EXPIRED("expired", "Expirado"),
    CANCELLED("cancelled", "Cancelado")
}

enum class PartnerShareLevel(val key: String, val label: String) {
    FULL("full", "Completo"),
    CYCLE_ONLY("cycle_only", "Apenas Ciclo"),
    FERTILITY_ONLY("fertility_only", "Apenas Fertilidade"),
    INSIGHTS_ONLY("insights_only", "Apenas Insights")
}

enum class ExportFormat(val key: String, val label: String, val mimeType: String, val extension: String) {
    JSON("json", "JSON", "application/json", "json"),
    CSV("csv", "CSV", "text/csv", "csv"),
    PDF("pdf", "PDF", "application/pdf", "pdf")
}