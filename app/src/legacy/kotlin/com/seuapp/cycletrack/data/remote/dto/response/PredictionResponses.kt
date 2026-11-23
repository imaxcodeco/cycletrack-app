package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de previsões.
 */
@Serializable
data class PredictionResponse(
    val id: String,
    val userId: String,
    val predictionType: String,
    val predictedDate: String,
    val confidenceScore: Float,
    val algorithmVersion: String,
    val cycleCountUsed: Int,
    val standardDeviation: Float,
    val metadata: String,
    val source: String,
    val isNotified: Boolean,
    val notifiedAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val expiresAt: Long,
    val actualDate: String?,
    val accuracyDays: Int
)

@Serializable
data class PredictionListResponse(
    val predictions: List<PredictionResponse>,
    val total: Int
)