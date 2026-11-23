package com.seuapp.cycletrack.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * Modelo de domínio para Gravidez.
 * Clean-room com acompanhamento semanal de desenvolvimento.
 */
@Serializable
data class Pregnancy(
    val id: String,
    val userId: String,
    val lmpDate: LocalDate, // Data da última menstruação
    val conceptionDate: LocalDate?,
    val dueDate: LocalDate,
    val actualBirthDate: LocalDate?,
    val currentWeek: Int = 0,
    val currentDay: Int = 0,
    val trimester: Int = 1,
    val status: PregnancyStatus = PregnancyStatus.ACTIVE,
    val outcome: PregnancyOutcome? = null,
    val outcomeDate: LocalDate? = null,
    val outcomeNotes: String = "",
    val prePregnancyWeightKg: Float = 0.0f,
    val currentWeightKg: Float = 0.0f,
    val totalWeightGainKg: Float = 0.0f,
    val bloodPressureSystolic: Int = 0,
    val bloodPressureDiastolic: Int = 0,
    val glucoseLevel: Float = 0.0f,
    val appointments: List<PregnancyAppointment> = emptyList(),
    val tests: List<PregnancyTest> = emptyList(),
    val symptoms: List<Symptom> = emptyList(),
    val babyName: String = "",
    val babyGender: BabyGender = BabyGender.UNKNOWN,
    val babyWeightGrams: Int = 0,
    val babyHeightCm: Float = 0.0f,
    val babyApgarScore: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: String = "",
    val lastSyncedAt: Long = 0L,
    val version: Int = 1
) {
    companion object {
        fun create(
            userId: String,
            lmpDate: LocalDate,
            dueDate: LocalDate? = null,
            conceptionDate: LocalDate? = null
        ): Pregnancy {
            val calculatedDueDate = dueDate ?: lmpDate.plusWeeks(40)
            val calculatedConception = conceptionDate ?: lmpDate.plusWeeks(2)

            val now = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val daysSinceLmp = now.difference(lmpDate).inDays
            val currentWeek = (daysSinceLmp / 7) + 1
            val currentDay = (daysSinceLmp % 7) + 1
            val trimester = when {
                currentWeek <= 13 -> 1
                currentWeek <= 27 -> 2
                else -> 3
            }

            return Pregnancy(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                lmpDate = lmpDate,
                conceptionDate = calculatedConception,
                dueDate = calculatedDueDate,
                currentWeek = currentWeek.coerceIn(1, 42),
                currentDay = currentDay.coerceIn(1, 7),
                trimester = trimester,
                status = PregnancyStatus.ACTIVE
            )
        }
    }

    val daysUntilDueDate: Int
        get() = Clock.System.todayIn(TimeZone.currentSystemDefault()).difference(dueDate).inDays

    val isOverdue: Boolean
        get() = daysUntilDueDate < 0 && status == PregnancyStatus.ACTIVE

    val progressPercentage: Float
        get() = (currentWeek / 40f * 100).coerceIn(0f, 100f)

    val gestationalAgeWeeks: Int
        get() = currentWeek

    val gestationalAgeDays: Int
        get() = currentDay

    val trimesterLabel: String
        get() = when (trimester) {
            1 -> "1º Trimestre (Semanas 1-13)"
            2 -> "2º Trimestre (Semanas 14-27)"
            3 -> "3º Trimestre (Semanas 28-40+)"
            else -> "Desconhecido"
        }

    val upcomingAppointments: List<PregnancyAppointment>
        get() = appointments
            .filter { !it.completed && it.appointmentDate >= Clock.System.todayIn(TimeZone.currentSystemDefault()) }
            .sortedBy { it.appointmentDate }

    val overdueAppointments: List<PregnancyAppointment>
        get() = appointments
            .filter { !it.completed && it.appointmentDate < Clock.System.todayIn(TimeZone.currentSystemDefault()) }
            .sortedByDescending { it.appointmentDate }
}

enum class PregnancyStatus(val key: String, val label: String) {
    ACTIVE("active", "Ativa"),
    ENDED("ended", "Finalizada"),
    LOST("lost", "Perdida")
}

enum class BabyGender(val key: String, val label: String) {
    MALE("male", "Menino"),
    FEMALE("female", "Menina"),
    UNKNOWN("unknown", "Não Sabido/Preferimos Não Saber")
}

/**
 * Consulta pré-natal.
 */
@Serializable
data class PregnancyAppointment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val pregnancyId: String,
    val title: String,
    val description: String = "",
    val appointmentDate: LocalDate,
    val appointmentTime: String = "", // HH:mm
    val type: AppointmentType = AppointmentType.PRENATAL,
    val location: String = "",
    val doctorName: String = "",
    val notes: String = "",
    val completed: Boolean = false,
    val reminderEnabled: Boolean = true,
    val reminderMinutesBefore: Int = 60,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class AppointmentType(val key: String, val label: String, val icon: String) {
    PRENATAL("prenatal", "Pré-natal", "👨‍⚕️"),
    ULTRASOUND("ultrasound", "Ultrassom", "📷"),
    BLOOD_TEST("blood_test", "Exame de Sangue", "🩸"),
    GLUCOSE_TEST("glucose_test", "Teste de Glicose", "🍬"),
    GBS_TEST("gbs_test", "Teste GBS", "🧪"),
    OTHER("other", "Outro", "📋")
}

/**
 * Exame/teste durante a gravidez.
 */
@Serializable
data class PregnancyTest(
    val id: String = java.util.UUID.randomUUID().toString(),
    val pregnancyId: String,
    val testType: TestType,
    val testDate: LocalDate,
    val resultValue: String = "",
    val resultUnit: String = "",
    val referenceRange: String = "",
    val isNormal: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class TestType(val key: String, val label: String) {
    HCG("hcg", "Beta-hCG"),
    PROGESTERONE("progesterone", "Progesterona"),
    GLUCOSE("glucose", "Glicose"),
    GBS("gbs", "Estreptococo B"),
    ULTRASOUND("ultrasound", "Ultrassom"),
    NIPT("nipt", "NIPT"),
    OTHER("other", "Outro")
}