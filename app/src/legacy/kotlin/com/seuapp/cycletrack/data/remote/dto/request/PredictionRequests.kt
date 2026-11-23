package com.seuapp.cycletrack.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * DTOs para requisições de previsões.
 */
@Serializable
data class UpdatePredictionActualRequest(
    val actualDate: String, // ISO date
    val accuracyDays: Int
)