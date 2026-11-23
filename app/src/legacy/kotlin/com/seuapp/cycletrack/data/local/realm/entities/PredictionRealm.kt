package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity representing cycle predictions (period, ovulation, fertile window).
 * Clean-room implementation using weighted average algorithm.
 */
@Serializable
open class PredictionRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var userId: String = ""

    @Index
    var predictionType: String = "" // period_start, period_end, ovulation, fertile_window_start, fertile_window_end

    @Index
    var predictedDate: Long = 0L

    var confidenceScore: Float = 0.0f // 0.0 - 1.0

    var algorithmVersion: String = "1.0" // Track algorithm version for re-prediction

    var cycleCountUsed: Int = 0 // Number of cycles used in calculation

    var standardDeviation: Float = 0.0f // Cycle length standard deviation

    var metadata: String = "" // JSON with additional params (weights, adjustments, etc.)

    var source: String = "local" // local, server, hybrid

    var isNotified: Boolean = false

    var notifiedAt: Long = 0L

    var createdAt: Long = System.currentTimeMillis()

    var updatedAt: Long = System.currentTimeMillis()

    var expiresAt: Long = 0L // When prediction becomes stale

    var actualDate: Long = 0L // When actual event occurred (for accuracy tracking)

    var accuracyDays: Int = 0 // Difference in days (positive = late, negative = early)

    companion object {
        const val TYPE_PERIOD_START = "period_start"
        const val TYPE_PERIOD_END = "period_end"
        const val TYPE_OVULATION = "ovulation"
        const val TYPE_FERTILE_WINDOW_START = "fertile_window_start"
        const val TYPE_FERTILE_WINDOW_END = "fertile_window_end"
        const val TYPE_PMS_START = "pms_start"
        const val TYPE_PMS_END = "pms_end"

        const val SOURCE_LOCAL = "local"
        const val SOURCE_SERVER = "server"
        const val SOURCE_HYBRID = "hybrid"

        const val ALGORITHM_VERSION = "1.0.0"
    }
}