package com.seuapp.cycletrack.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * Modelo de domínio para Ciclo Menstrual.
 * Clean-room implementation baseada em padrões médicos (OMS, ACOG).
 */
@Serializable
data class Cycle(
    val id: String,
    val userId: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val cycleLength: Int,
    val periodLength: Int,
    val lutealPhaseLength: Int = 14,
    val ovulationDate: LocalDate?,
    val fertileWindowStart: LocalDate?,
    val fertileWindowEnd: LocalDate?,
    val isPredicted: Boolean = false,
    val confidenceScore: Float = 0.0f,
    val source: String = Cycle.SOURCE_MANUAL,
    val notes: String = "",
    val symptoms: List<Symptom> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: String = "",
    val lastSyncedAt: Long = 0L,
    val version: Int = 1
) {
    companion object {
        const val SOURCE_MANUAL = "manual"
        const val SOURCE_PREDICTED = "predicted"
        val SOURCE_SERVER = "server"
        val SOURCE_HEALTH_CONNECT = "health_connect"

        fun createManual(
            userId: String,
            startDate: LocalDate,
            periodLength: Int,
            previousCycleLength: Int? = null,
            notes: String = ""
        ): Cycle {
            val endDate = startDate.plusDays(periodLength - 1)
            val cycleLength = previousCycleLength ?: 28
            val lutealPhase = 14
            val ovulationDate = startDate.plusDays(cycleLength - lutealPhase - 1)
            val fertileStart = ovulationDate.minusDays(5)
            val fertileEnd = ovulationDate.plusDays(1)

            return Cycle(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                cycleLength = cycleLength,
                periodLength = periodLength,
                lutealPhaseLength = lutealPhase,
                ovulationDate = ovulationDate,
                fertileWindowStart = fertileStart,
                fertileWindowEnd = fertileEnd,
                isPredicted = false,
                confidenceScore = 1.0f,
                source = SOURCE_MANUAL,
                notes = notes
            )
        }
    }

    val isCurrent: Boolean
        get() = endDate == null || endDate! >= Clock.System.todayIn(TimeZone.currentSystemDefault())

    val isPast: Boolean
        get() = endDate != null && endDate! < Clock.System.todayIn(TimeZone.currentSystemDefault())

    val daysSinceStart: Int
        get() = Clock.System.todayIn(TimeZone.currentSystemDefault()).difference(startDate).inDays

    val daysUntilNextPeriod: Int?
        get() = if (isCurrent) null else cycleLength - daysSinceStart
}