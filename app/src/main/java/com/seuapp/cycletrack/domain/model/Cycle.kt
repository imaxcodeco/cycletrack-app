package com.seuapp.cycletrack.domain.model

import java.time.LocalDate
import java.util.UUID

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
    val source: String = SOURCE_MANUAL,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val SOURCE_MANUAL = "manual"
        const val SOURCE_PREDICTED = "predicted"
        const val SOURCE_SERVER = "server"
        const val SOURCE_HEALTH_CONNECT = "health_connect"

        fun createManual(
            userId: String,
            startDate: LocalDate,
            periodLength: Int,
            previousCycleLength: Int? = null,
            notes: String = ""
        ): Cycle {
            val endDate = startDate.plusDays((periodLength - 1).toLong())
            val cycleLength = previousCycleLength ?: 28
            val lutealPhase = 14
            val ovulationDate = startDate.plusDays((cycleLength - lutealPhase - 1).toLong())
            val fertileStart = ovulationDate.minusDays(5L)
            val fertileEnd = ovulationDate.plusDays(1L)

            return Cycle(
                id = UUID.randomUUID().toString(),
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                cycleLength = cycleLength,
                periodLength = periodLength,
                lutealPhaseLength = lutealPhase,
                ovulationDate = ovulationDate,
                fertileWindowStart = fertileStart,
                fertileWindowEnd = fertileEnd,
                source = SOURCE_MANUAL,
                notes = notes
            )
        }
    }
}
