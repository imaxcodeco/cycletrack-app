package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.HealthDataPoint

/**
 * Interface do repositório de Pontos de Dados de Saúde (Health Connect).
 * Clean-room - define contrato sem expor implementação.
 */
interface HealthDataPointRepository {

    /**
     * Obtém todos os pontos de dados do usuário.
     */
    suspend fun getHealthDataPoints(userId: String): List<HealthDataPoint>

    /**
     * Obtém pontos de dados por tipo.
     */
    suspend fun getHealthDataPointsByType(
        userId: String,
        dataType: String
    ): List<HealthDataPoint>

    /**
     * Obtém pontos de dados em intervalo de datas.
     */
    suspend fun getHealthDataPointsInRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): List<HealthDataPoint>

    /**
     * Obtém pontos de dados por tipo e intervalo.
     */
    suspend fun getHealthDataPointsByTypeAndRange(
        userId: String,
        dataType: String,
        startDate: Long,
        endDate: Long
    ): List<HealthDataPoint>

    /**
     * Obtém pontos pendentes de sincronização para Health Connect.
     */
    suspend fun getPendingSyncToHealthConnect(userId: String): List<HealthDataPoint>

    /**
     * Obtém pontos do Health Connect que precisam de merge.
     */
    suspend fun getFromHealthConnectNeedingMerge(userId: String): List<HealthDataPoint>

    /**
     * Obtém ponto por ID.
     */
    suspend fun getHealthDataPoint(dataPointId: String): HealthDataPoint?

    /**
     * Obtém ponto por ID do registro Health Connect.
     */
    suspend fun getByHealthConnectRecordId(userId: String, recordId: String): HealthDataPoint?

    /**
     * Insere ou atualiza ponto de dados.
     */
    suspend fun upsertHealthDataPoint(dataPoint: HealthDataPoint): HealthDataPoint

    /**
     * Insere múltiplos pontos de dados.
     */
    suspend fun upsertHealthDataPoints(dataPoints: List<HealthDataPoint>): List<HealthDataPoint>

    /**
     * Atualiza status de sincronização.
     */
    suspend fun updateSyncStatus(
        dataPointId: String,
        status: String,
        error: String? = null,
        conflictResolution: String? = null
    ): Boolean

    /**
     * Atualização em lote do status de sincronização.
     */
    suspend fun bulkUpdateSyncStatus(
        dataPointIds: List<String>,
        status: String
    ): Int

    /**
     * Resolve conflito com vitória do local.
     */
    suspend fun resolveConflictLocalWins(dataPointId: String): Boolean

    /**
     * Resolve conflito com vitória do remoto.
     */
    suspend fun resolveConflictRemoteWins(dataPointId: String, remoteData: HealthDataPoint): Boolean

    /**
     * Soft delete.
     */
    suspend fun deleteHealthDataPoint(dataPointId: String): Boolean

    /**
     * Hard delete (LGPD/GDPR).
     */
    suspend fun hardDeleteHealthDataPoint(dataPointId: String): Boolean

    /**
     * Hard delete todos os dados do usuário (LGPD/GDPR).
     */
    suspend fun hardDeleteAllForUser(userId: String): Int

    /**
     * Obtém fluxos menstruais para cálculo de ciclo.
     */
    suspend fun getMenstrualFlows(userId: String, startDate: Long, endDate: Long): List<HealthDataPoint>

    /**
     * Obtém temperaturas basais para detecção de ovulação.
     */
    suspend fun getBasalBodyTemperatures(userId: String, startDate: Long, endDate: Long): List<HealthDataPoint>

    /**
     * Obtém resultados de testes de ovulação.
     */
    suspend fun getOvulationTests(userId: String, startDate: Long, endDate: Long): List<HealthDataPoint>

    /**
     * Obtém contagem por tipo.
     */
    suspend fun getCountByType(userId: String): Map<String, Int>

    /**
     * Obtém dados para exportação (LGPD/GDPR).
     */
    suspend fun getHealthDataForExport(
        userId: String,
        startDate: Long,
        endDate: Long,
        anonymize: Boolean
    ): List<HealthDataPoint>
}

/**
 * Modelo de domínio para Ponto de Dado de Saúde.
 */
@Serializable
data class HealthDataPoint(
    val id: String,
    val userId: String,
    val dataType: String,
    val startTime: Long,
    val endTime: Long = 0L,
    val timeZoneOffset: Int = 0,
    val value: String = "",
    val numericValue: Double = 0.0,
    val unit: String = "",
    val source: String = HealthDataPoint.SOURCE_APP,
    val sourcePackageName: String = "",
    val sourceRecordId: String = "",
    val flowLevel: String = "",
    val testResult: String = "",
    val temperatureCelsius: Double = 0.0,
    val mucusConsistency: String = "",
    val pregnancyTestResult: String = "",
    val syncStatus: String = HealthDataPoint.SYNC_PENDING,
    val lastSyncAttempt: Long = 0L,
    val syncError: String = "",
    val conflictResolution: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: String = "",
    val lastSyncedAt: Long = 0L,
    val version: Int = 1
) {
    companion object {
        // Tipos de dados Health Connect
        const val TYPE_MENSTRUAL_FLOW = "menstrual_flow"
        const val TYPE_OVULATION_TEST = "ovulation_test"
        const val TYPE_BASAL_BODY_TEMPERATURE = "basal_body_temperature"
        const val TYPE_CERVICAL_MUCUS = "cervical_mucus"
        const val TYPE_PREGNANCY_TEST = "pregnancy_test"
        const val TYPE_INTERCOURSE = "intercourse"
        const val TYPE_CONTRACEPTION = "contraception"
        const val TYPE_VAGINAL_BLEEDING = "vaginal_bleeding"
        const val TYPE_MENSTRUATION = "menstruation"

        // Níveis de fluxo
        const val FLOW_SPOTTING = "spotting"
        const val FLOW_LIGHT = "light"
        const val FLOW_MEDIUM = "medium"
        const val FLOW_HEAVY = "heavy"

        // Resultados de teste
        const val TEST_POSITIVE = "positive"
        const val TEST_NEGATIVE = "negative"
        const val TEST_INVALID = "invalid"

        // Consistência do muco
        const val MUCUS_DRY = "dry"
        const val MUCUS_STICKY = "sticky"
        const val MUCUS_CREAMY = "creamy"
        const val MUCUS_WATERY = "watery"
        const val MUCUS_EGG_WHITE = "egg_white"

        // Status de sincronização
        const val SYNC_PENDING = "pending"
        const val SYNC_SYNCED = "synced"
        const val SYNC_CONFLICT = "conflict"
        const val SYNC_ERROR = "error"

        // Resolução de conflito
        const val CONFLICT_LOCAL_WINS = "local_wins"
        const val CONFLICT_REMOTE_WINS = "remote_wins"
        const val CONFLICT_MERGE = "merge"
        const val CONFLICT_MANUAL = "manual"

        // Fontes
        const val SOURCE_APP = "app"
        const val SOURCE_HEALTH_CONNECT = "health_connect"
        const val SOURCE_MANUAL = "manual"
        const val SOURCE_PARTNER = "partner"
    }
}