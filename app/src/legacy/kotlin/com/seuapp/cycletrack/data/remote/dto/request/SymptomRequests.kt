package com.seuapp.cycletrack.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * DTOs para requisições de sintomas.
 */
@Serializable
data class CreateSymptomRequest(
    val cycleId: String,
    val date: String, // ISO date
    val category: String,
    val name: String,
    val severity: Int = 1,
    val notes: String = "",
    val source: String = "manual"
)

@Serializable
data class CreateSymptomsBatchRequest(
    val symptoms: List<CreateSymptomRequest>
)

@Serializable
data class UpdateSymptomRequest(
    val date: String?, // ISO date
    val category: String? = null,
    val name: String? = null,
    val severity: Int? = null,
    val notes: String? = null
)