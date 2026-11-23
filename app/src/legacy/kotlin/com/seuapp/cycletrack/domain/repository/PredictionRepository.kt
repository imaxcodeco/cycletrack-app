package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.model.PredictionType

/**
 * Interface do repositório de Previsões.
 * Clean-room - define contrato sem expor implementação.
 */
interface PredictionRepository {

    /**
     * Obtém todas as previsões do usuário.
     */
    suspend fun getPredictions(userId: String): List<Prediction>

    /**
     * Obtém previsões por tipo.
     */
    suspend fun getPredictionsByType(userId: String, type: PredictionType): List<Prediction>

    /**
     * Obtém previsões futuras (datas >= hoje).
     */
    suspend fun getUpcomingPredictions(userId: String, fromDate: Long = System.currentTimeMillis()): List<Prediction>

    /**
     * Obtém a próxima previsão de início de menstruação.
     */
    suspend fun getNextPeriodStart(userId: String): Prediction?

    /**
     * Obtém a próxima previsão de ovulação.
     */
    suspend fun getNextOvulation(userId: String): Prediction?

    /**
     * Obtém a próxima janela fértil (início e fim).
     */
    suspend fun getNextFertileWindow(userId: String): Pair<Prediction?, Prediction?>

    /**
     * Obtém previsões que precisam de notificação.
     */
    suspend fun getPredictionsNeedingNotification(userId: String, beforeMillis: Long = System.currentTimeMillis()): List<Prediction>

    /**
     * Obtém previsões expiradas (para limpeza).
     */
    suspend fun getExpiredPredictions(userId: String, beforeDate: Long = System.currentTimeMillis()): List<Prediction>

    /**
     * Insere ou atualiza uma previsão.
     */
    suspend fun upsertPrediction(prediction: Prediction): Prediction

    /**
     * Insere múltiplas previsões em uma transação.
     */
    suspend fun upsertPredictions(predictions: List<Prediction>): List<Prediction>

    /**
     * Deleta uma previsão.
     */
    suspend fun deletePrediction(predictionId: String): Boolean

    /**
     * Deleta todas as previsões de um usuário.
     */
    suspend fun deleteAllPredictions(userId: String): Int

    /**
     * Marca previsão como notificada.
     */
    suspend fun markAsNotified(predictionId: String): Boolean

    /**
     * Atualiza previsão com data real (para rastreamento de precisão).
     */
    suspend fun updateActualDate(predictionId: String, actualDate: Long): Boolean

    /**
     * Obtém estatísticas de precisão das previsões.
     */
    suspend fun getAccuracyStats(userId: String, type: PredictionType): AccuracyStats

    /**
     * Regenera todas as previsões baseadas nos ciclos atuais.
     */
    suspend fun regeneratePredictions(userId: String): List<Prediction>

    /**
     * Obtém previsões para exportação (LGPD/GDPR).
     */
    suspend fun getPredictionsForExport(
        userId: String,
        startDate: Long,
        endDate: Long,
        anonymize: Boolean
    ): List<Prediction>
}

/**
 * Estatísticas de precisão das previsões.
 */
data class AccuracyStats(
    val totalPredictions: Int,
    val averageAccuracyDays: Double,
    val within1DayCount: Int,
    val within3DaysCount: Int,
    val minAccuracyDays: Double,
    val maxAccuracyDays: Double
) {
    val within1DayPercentage: Double
        get() = if (totalPredictions > 0) (within1DayCount * 100.0 / totalPredictions) else 0.0

    val within3DaysPercentage: Double
        get() = if (totalPredictions > 0) (within3DaysCount * 100.0 / totalPredictions) else 0.0

    val accuracyGrade: String
        get() = when {
            within3DaysPercentage >= 80 -> "Excelente"
            within3DaysPercentage >= 60 -> "Boa"
            within3DaysPercentage >= 40 -> "Regular"
            else -> "Baixa"
        }
}