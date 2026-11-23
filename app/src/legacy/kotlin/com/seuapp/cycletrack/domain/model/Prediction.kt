package com.seuapp.cycletrack.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import import com.seuapp.cycletrack.util.toEpochMilliseconds
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * Modelo de domínio para Previsão de Ciclo.
 * Clean-room com pontuação de confiança e rastreamento de precisão.
 */
@Serializable
data class Prediction(
    val id: String,
    val userId: String,
    val type: PredictionType,
    val predictedDate: LocalDate,
    val confidenceScore: Float = 0.0f, // 0.0 - 1.0
    val algorithmVersion: String = Prediction.ALGORITHM_VERSION,
    val cycleCountUsed: Int = 0,
    val standardDeviation: Float = 0.0f,
    val metadata: String = "", // JSON com parâmetros extras
    val source: PredictionSource = PredictionSource.LOCAL,
    val isNotified: Boolean = false,
    val notifiedAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = 0L,
    val actualDate: LocalDate? = null,
    val accuracyDays: Int = 0 // positivo = atrasado, negativo = adiantado
) {
    companion object {
        const val ALGORITHM_VERSION = "1.0.0"

        fun createPeriodStart(
            userId: String,
            predictedDate: LocalDate,
            confidence: Float,
            cycleCount: Int,
            stdDev: Float,
            source: PredictionSource = PredictionSource.LOCAL
        ): Prediction {
            return Prediction(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                type = PredictionType.PERIOD_START,
                predictedDate = predictedDate,
                confidenceScore = confidence,
                cycleCountUsed = cycleCount,
                standardDeviation = stdDev,
                source = source,
                expiresAt = predictedDate.plusDays(7).atStartOfDay().toEpochMilliseconds()
            )
        }

        fun createOvulation(
            userId: String,
            predictedDate: LocalDate,
            confidence: Float,
            cycleCount: Int,
            stdDev: Float,
            source: PredictionSource = PredictionSource.LOCAL
        ): Prediction {
            return Prediction(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                type = PredictionType.OVULATION,
                predictedDate = predictedDate,
                confidenceScore = confidence,
                cycleCountUsed = cycleCount,
                standardDeviation = stdDev,
                source = source,
                expiresAt = predictedDate.plusDays(3).atStartOfDay().toEpochMilliseconds()
            )
        }

        fun createFertileWindow(
            userId: String,
            startDate: LocalDate,
            endDate: LocalDate,
            confidence: Float,
            cycleCount: Int,
            stdDev: Float,
            source: PredictionSource = PredictionSource.LOCAL
        ): Pair<Prediction, Prediction> {
            val start = Prediction(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                type = PredictionType.FERTILE_WINDOW_START,
                predictedDate = startDate,
                confidenceScore = confidence,
                cycleCountUsed = cycleCount,
                standardDeviation = stdDev,
                source = source,
                expiresAt = endDate.plusDays(1).atStartOfDay().toEpochMilliseconds()
            )
            val end = Prediction(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                type = PredictionType.FERTILE_WINDOW_END,
                predictedDate = endDate,
                confidenceScore = confidence,
                cycleCountUsed = cycleCount,
                standardDeviation = stdDev,
                source = source,
                expiresAt = endDate.plusDays(1).atStartOfDay().toEpochMilliseconds()
            )
            return Pair(start, end)
        }
    }

    val isExpired: Boolean
        get() = expiresAt > 0 && expiresAt < System.currentTimeMillis()

    val daysUntilPredicted: Int
        get() = Clock.System.todayIn(TimeZone.currentSystemDefault()).difference(predictedDate).inDays

    val isUpcoming: Boolean
        get() = daysUntilPredicted >= 0 && daysUntilPredicted <= 7

    val isToday: Boolean
        get() = daysUntilPredicted == 0

    val isPast: Boolean
        get() = daysUntilPredicted < 0

    val accuracyLabel: String
        get() = when {
            accuracyDays == 0 -> "Exato"
            accuracyDays == 1 -> "1 dia atrasado"
            accuracyDays == -1 -> "1 dia adiantado"
            accuracyDays > 0 -> "$accuracyDays dias atrasado"
            else -> "${Math.abs(accuracyDays)} dias adiantado"
        }
}

enum class PredictionType(val key: String, val label: String, val icon: String) {
    PERIOD_START("period_start", "Início da Menstruação", "🩸"),
    PERIOD_END("period_end", "Fim da Menstruação", "✅"),
    OVULATION("ovulation", "Ovulação", "🥚"),
    FERTILE_WINDOW_START("fertile_window_start", "Início Janela Fértil", "🌱"),
    FERTILE_WINDOW_END("fertile_window_end", "Fim Janela Fértil", "🌿"),
    PMS_START("pms_start", "Início TPM", "😔"),
    PMS_END("pms_end", "Fim TPM", "😊")
}

enum class PredictionSource(val key: String, val label: String) {
    LOCAL("local", "Local (no dispositivo)"),
    SERVER("server", "Servidor"),
    HYBRID("hybrid", "Híbrido (local + servidor)")
}