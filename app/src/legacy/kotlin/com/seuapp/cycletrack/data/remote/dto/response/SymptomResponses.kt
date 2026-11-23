package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de sintomas.
 */
@Serializable
data class SymptomResponse(
    val id: String,
    val userId: String,
    val cycleId: String,
    val date: String,
    val category: String,
    val name: String,
    val severity: Int,
    val notes: String,
    val source: String,
    val createdAt: Long,
    val updatedAt: Long,
    val serverId: String,
    val lastSyncedAt: Long,
    val version: Int
)

@Serializable
data class SymptomListResponse(
    val symptoms: List<SymptomResponse>,
    val total: Int,
    val limit: Int,
    val offset: Int
)