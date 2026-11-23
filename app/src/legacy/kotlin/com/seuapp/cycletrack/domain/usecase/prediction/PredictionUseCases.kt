package com.seuapp.cycletrack.domain.usecase.prediction

import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.model.PredictionType
import com.seuapp.cycletrack.domain.repository.PredictionRepository

/**
 * Casos de uso para Previsões.
 * Clean-room - gerencia consultas e precisão de previsões.
 */
class PredictionUseCases(
    private val predictionRepository: PredictionRepository
) {

    /**
     * Obtém todas as previsões do usuário.
     */
    suspend fun getAllPredictions(userId: String): List<Prediction> {
        return predictionRepository.getPredictions(userId)
    }

    /**
     * Obtém previsões por tipo.
     */
    suspend fun getPredictionsByType(userId: String, type: PredictionType): List<Prediction> {
        return predictionRepository.getPredictionsByType(userId, type)
    }

    /**
     * Obtém previsões futuras (próximos 30 dias).
     */
    suspend fun getUpcomingPredictions(userId: String): List<Prediction> {
        return predictionRepository.getUpcomingPredictions(userId)
    }

    /**
     * Obtém próxima menstruação prevista.
     */
    suspend fun getNextPeriodStart(userId: String): Prediction? {
        return predictionRepository.getNextPeriodStart(userId)
    }

    /**
     * Obtém próxima ovulação prevista.
     */
    suspend fun getNextOvulation(userId: String): Prediction? {
        return predictionRepository.getNextOvulation(userId)
    }

    /**
     * Obtém próxima janela fértil (início e fim).
     */
    suspend fun getNextFertileWindow(userId: String): Pair<Prediction?, Prediction?> {
        return predictionRepository.getNextFertileWindow(userId)
    }

    /**
     * Obtém previsões que precisam de notificação.
     */
    suspend fun getPredictionsNeedingNotification(userId: String): List<Prediction> {
        return predictionRepository.getPredictionsNeedingNotification(userId)
    }

    /**
     * Marca previsão como notificada.
     */
    suspend fun markAsNotified(predictionId: String): Boolean {
        return predictionRepository.markAsNotified(predictionId)
    }

    /**
     * Atualiza previsão com data real (para cálculo de precisão).
     */
    suspend fun updateWithActualDate(predictionId: String, actualDate: Long): Boolean {
        return predictionRepository.updateActualDate(predictionId, actualDate)
    }

    /**
     * Obtém estatísticas de precisão por tipo.
     */
    suspend fun getAccuracyStats(userId: String, type: PredictionType) {
        return predictionRepository.getAccuracyStats(userId, type)
    }

    /**
     * Obtém resumo de precisão de todas as previsões.
     */
    suspend fun getAccuracySummary(userId: String): Map<PredictionType, AccuracySummary> {
        val types = PredictionType.values()
        return types.associateWith { type ->
            val stats = predictionRepository.getAccuracyStats(userId, type)
            AccuracySummary(
                type = type,
                totalPredictions = stats.totalPredictions,
                averageAccuracyDays = stats.averageAccuracyDays,
                within1DayPercentage = stats.within1DayPercentage,
                within3DaysPercentage = stats.within3DaysPercentage,
                grade = stats.accuracyGrade
            )
        }
    }

    /**
     * Força regeneração de previsões.
     */
    suspend fun regeneratePredictions(userId: String): List<Prediction> {
        return predictionRepository.regeneratePredictions(userId)
    }

    /**
     * Obtém previsões para exportação (LGPD/GDPR).
     */
    suspend fun getPredictionsForExport(
        userId: String,
        startDate: Long,
        endDate: Long,
        anonymize: Boolean
    ): List<Prediction> {
        return predictionRepository.getPredictionsForExport(userId, startDate, endDate, anonymize)
    }
}

/**
 * Resumo de precisão para UI.
 */
data class AccuracySummary(
    val type: PredictionType,
    val totalPredictions: Int,
    val averageAccuracyDays: Double,
    val within1DayPercentage: Double,
    val within3DaysPercentage: Double,
    val grade: String
) {
    val displayText: String
        get() = if (totalPredictions == 0) {
            "Sem dados suficientes"
        } else {
            "Precisão: ${grade} (${within3DaysPercentage.roundToInt()}% dentro de 3 dias)"
        }
}