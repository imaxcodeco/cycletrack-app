package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Cycle
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de Ciclos.
 * Clean-room - define contrato sem expor implementação.
 */
interface CycleRepository {

    /**
     * Obtém todos os ciclos do usuário (não deletados), ordenados por data decrescente.
     */
    fun getCycles(userId: String): Flow<List<Cycle>>

    /**
     * Obtém um ciclo específico por ID.
     */
    suspend fun getCycle(cycleId: String): Cycle?

    /**
     * Obtém o ciclo mais recente do usuário.
     */
    suspend fun getLatestCycle(userId: String): Cycle?

    /**
     * Obtém ciclos em um intervalo de datas.
     */
    suspend fun getCyclesInRange(userId: String, startDate: Long, endDate: Long): List<Cycle>

    /**
     * Obtém ciclos previstos (gerados pelo algoritmo).
     */
    suspend fun getPredictedCycles(userId: String): List<Cycle>

    /**
     * Obtém ciclos que precisam de sincronização com o servidor.
     */
    suspend fun getCyclesNeedingSync(userId: String): List<Cycle>

    /**
     * Insere ou atualiza um ciclo.
     */
    suspend fun upsertCycle(cycle: Cycle): Cycle

    /**
     * Insere múltiplos ciclos em uma transação.
     */
    suspend fun upsertCycles(cycles: List<Cycle>): List<Cycle>

    /**
     * Soft delete (marca como deletado).
     */
    suspend fun deleteCycle(cycleId: String): Boolean

    /**
     * Hard delete (remove permanentemente - LGPD/GDPR).
     */
    suspend fun hardDeleteCycle(cycleId: String): Boolean

    /**
     * Obtém estatísticas dos ciclos do usuário.
     */
    suspend fun getCycleStats(userId: String): CycleStats

    /**
     * Obtém contagem de ciclos.
     */
    suspend fun getCycleCount(userId: String): Int

    /**
     * Verifica se existe ciclo em uma data específica.
     */
    suspend fun hasCycleOnDate(userId: String, date: Long): Boolean

    /**
     * Obtém ciclos para exportação (LGPD/GDPR).
     */
    suspend fun getCyclesForExport(
        userId: String,
        startDate: Long,
        endDate: Long,
        anonymize: Boolean
    ): List<Cycle>
}

/**
 * Estatísticas dos ciclos.
 */
data class CycleStats(
    val count: Int,
    val averageLength: Int,
    val averagePeriodLength: Int,
    val minLength: Int,
    val maxLength: Int,
    val standardDeviation: Float
) {
    val hasEnoughDataForPrediction: Boolean
        get() = count >= 3

    val regularityLabel: String
        get() = when {
            standardDeviation <= 2 -> "Muito Regular"
            standardDeviation <= 4 -> "Regular"
            standardDeviation <= 7 -> "Irregular"
            else -> "Muito Irregular"
        }
}