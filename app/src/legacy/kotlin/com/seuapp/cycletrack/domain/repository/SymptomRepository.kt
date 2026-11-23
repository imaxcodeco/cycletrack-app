package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Symptom
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de Sintomas.
 * Clean-room - define contrato sem expor implementação.
 */
interface SymptomRepository {

    /**
     * Obtém todos os sintomas do usuário (não deletados), ordenados por data decrescente.
     */
    fun getSymptoms(userId: String): Flow<List<Symptom>>

    /**
     * Obtém sintomas de um ciclo específico.
     */
    suspend fun getSymptomsForCycle(cycleId: String): List<Symptom>

    /**
     * Obtém sintomas de uma data específica.
     */
    suspend fun getSymptomsForDate(userId: String, date: Long): List<Symptom>

    /**
     * Obtém sintomas em um intervalo de datas.
     */
    suspend fun getSymptomsInRange(userId: String, startDate: Long, endDate: Long): List<Symptom>

    /**
     * Obtém sintomas por categoria.
     */
    suspend fun getSymptomsByCategory(userId: String, category: String): List<Symptom>

    /**
     * Obtém sintomas que precisam de sincronização.
     */
    suspend fun getSymptomsNeedingSync(userId: String): List<Symptom>

    /**
     * Insere ou atualiza um sintoma.
     */
    suspend fun upsertSymptom(symptom: Symptom): Symptom

    /**
     * Insere múltiplos sintomas em uma transação.
     */
    suspend fun upsertSymptoms(symptoms: List<Symptom>): List<Symptom>

    /**
     * Soft delete.
     */
    suspend fun deleteSymptom(symptomId: String): Boolean

    /**
     * Hard delete (LGPD/GDPR).
     */
    suspend fun hardDeleteSymptom(symptomId: String): Boolean

    /**
     * Obtém frequência de sintomas em um período.
     */
    suspend fun getSymptomFrequency(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Map<String, Int>

    /**
     * Obtém distribuição de severidade de um sintoma.
     */
    suspend fun getSymptomSeverityDistribution(
        userId: String,
        symptomName: String,
        startDate: Long,
        endDate: Long
    ): Map<Int, Int>

    /**
     * Obtém os sintomas mais comuns do usuário.
     */
    suspend fun getMostCommonSymptoms(
        userId: String,
        limit: Int = 10,
        startDate: Long = 0L,
        endDate: Long = Long.MAX_VALUE
    ): List<Pair<String, Int>>

    /**
     * Obtém contagem de sintomas.
     */
    suspend fun getSymptomCount(userId: String): Int

    /**
     * Verifica se existe sintoma em uma data com nome específico.
     */
    suspend fun hasSymptomOnDate(userId: String, date: Long, symptomName: String): Boolean

    /**
     * Obtém sintomas para exportação (LGPD/GDPR).
     */
    suspend fun getSymptomsForExport(
        userId: String,
        startDate: Long,
        endDate: Long,
        anonymize: Boolean
    ): List<Symptom>
}