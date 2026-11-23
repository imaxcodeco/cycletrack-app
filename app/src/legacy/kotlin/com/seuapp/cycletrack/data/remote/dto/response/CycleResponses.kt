package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de ciclos.
 */
@Serializable
data class CycleResponse(
    val id: String,
    val userId: String,
    val startDate: String,
    val endDate: String?,
    val cycleLength: Int,
    val periodLength: Int,
    val lutealPhaseLength: Int,
    val ovulationDate: String?,
    val fertileWindowStart: String?,
    val fertileWindowEnd: String?,
    val isPredicted: Boolean,
    val confidenceScore: Float,
    val source: String,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
    val serverId: String,
    val lastSyncedAt: Long,
    val version: Int
)

@Serializable
data class CycleListResponse(
    val cycles: List<CycleResponse>,
    val total: Int,
    val limit: Int,
    val offset: Int
)