package com.seuapp.cycletrack.domain.model

import java.time.LocalDate

enum class PredictionType(val key: String) {
    PERIOD_START("period_start"),
    PERIOD_END("period_end"),
    OVULATION("ovulation"),
    FERTILE_WINDOW_START("fertile_window_start"),
    FERTILE_WINDOW_END("fertile_window_end"),
    PMS("pms")
}

data class Prediction(
    val id: String,
    val userId: String,
    val type: String,
    val predictedDate: LocalDate,
    val confidence: Float = 0.0f,
    val createdAt: Long = System.currentTimeMillis()
)
