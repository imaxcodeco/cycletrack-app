package com.seuapp.cycletrack.domain.model

import java.time.LocalDate

data class Symptom(
    val id: String,
    val userId: String,
    val date: LocalDate,
    val name: String,
    val category: String = "",
    val severity: Int = 2,
    val notes: String = "",
    val value: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)
