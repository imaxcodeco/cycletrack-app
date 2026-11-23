package com.seuapp.cycletrack.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * DTOs para requisições de ciclos.
 */
@Serializable
data class CreateCycleRequest(
    val startDate: String, // ISO date
    val endDate: String?, // ISO date
    val periodLength: Int,
    val cycleLength: Int,
    val lutealPhaseLength: Int = 14,
    val ovulationDate: String?, // ISO date
    val fertileWindowStart: String?, // ISO date
    val fertileWindowEnd: String?, // ISO date
    val notes: String = "",
    val source: String = "manual"
)

@Serializable
data class UpdateCycleRequest(
    val endDate: String?, // ISO date
    val periodLength: Int? = null,
    val cycleLength: Int? = null,
    val lutealPhaseLength: Int? = null,
    val ovulationDate: String?, // ISO date
    val fertileWindowStart: String?, // ISO date
    val fertileWindowEnd: String?, // ISO date
    val notes: String? = null
)