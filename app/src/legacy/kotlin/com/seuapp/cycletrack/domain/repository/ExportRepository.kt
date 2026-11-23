package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.ExportRequest

/**
 * Interface do repositório de Exportação (LGPD/GDPR).
 * Clean-room - define contrato sem expor implementação.
 */
interface ExportRepository {

    /**
     * Obtém todas as exportações do usuário.
     */
    suspend fun getExports(userId: String): List<ExportRequest>

    /**
     * Obtém exportações por status.
     */
    suspend fun getExportsByStatus(userId: String, status: String): List<ExportRequest>

    /**
     * Obtém exportações pendentes/processando.
     */
    suspend fun getPendingExports(): List<ExportRequest>

    /**
     * Obtém exportação por ID.
     */
    suspend fun getExport(exportId: String): ExportRequest?

    /**
     * Cria nova solicitação de exportação.
     */
    suspend fun createExport(
        userId: String,
        format: String = ExportRequest.FORMAT_JSON,
        includeCycles: Boolean = true,
        includeSymptoms: Boolean = true,
        includePredictions: Boolean = true,
        includePregnancy: Boolean = true,
        includePartnerData: Boolean = false,
        includeHealthConnectData: Boolean = false,
        includeSettings: Boolean = true,
        includeContent: Boolean = false,
        dateRangeStart: Long = 0L,
        dateRangeEnd: Long = Long.MAX_VALUE,
        anonymize: Boolean = false,
        requestedFrom: String = "app",
        ipAddress: String = "",
        userAgent: String = "",
        legalBasis: String = ExportRequest.LEGAL_BASIS_USER_REQUEST,
        retentionDays: Int = ExportRequest.DEFAULT_RETENTION_DAYS
    ): ExportRequest

    /**
     * Marca como processando.
     */
    suspend fun markAsProcessing(exportId: String): Boolean

    /**
     * Atualiza progresso.
     */
    suspend fun updateProgress(exportId: String, progress: Int): Boolean

    /**
     * Marca como concluída.
     */
    suspend fun markAsCompleted(
        exportId: String,
        filePath: String,
        fileSize: Long,
        fileHash: String,
        downloadUrl: String = ""
    ): Boolean

    /**
     * Marca como falhada.
     */
    suspend fun markAsFailed(exportId: String, errorMessage: String): Boolean

    /**
     * Marca como expirada.
     */
    suspend fun markAsExpired(exportId: String): Boolean

    /**
     * Obtém exportações expiradas.
     */
    suspend fun getExpiredExports(): List<ExportRequest>

    /**
     * Limpa exportações expiradas.
     */
    suspend fun cleanupExpiredExports(): Int

    /**
     * Deleta registro de exportação.
     */
    suspend fun deleteExport(exportId: String): Boolean

    /**
     * Obtém estatísticas de exportação.
     */
    suspend fun getExportStats(userId: String): ExportStats
}

/**
 * Modelo de domínio para Solicitação de Exportação.
 */
@Serializable
data class ExportRequest(
    val id: String,
    val userId: String,
    val format: String,
    val includeCycles: Boolean,
    val includeSymptoms: Boolean,
    val includePredictions: Boolean,
    val includePregnancy: Boolean,
    val includePartnerData: Boolean,
    val includeHealthConnectData: Boolean,
    val includeSettings: Boolean,
    val includeContent: Boolean,
    val dateRangeStart: Long,
    val dateRangeEnd: Long,
    val anonymize: Boolean,
    val status: String = ExportRequest.STATUS_PENDING,
    val progress: Int = 0,
    val filePath: String = "",
    val fileSize: Long = 0,
    val fileHash: String = "",
    val downloadUrl: String = "",
    val expiresAt: Long = 0L,
    val errorMessage: String = "",
    val retryCount: Int = 0,
    val requestedAt: Long = System.currentTimeMillis(),
    val startedAt: Long = 0L,
    val completedAt: Long = 0L,
    val requestedFrom: String = "app",
    val ipAddress: String = "",
    val userAgent: String = "",
    val legalBasis: String = ExportRequest.LEGAL_BASIS_USER_REQUEST,
    val retentionDays: Int = ExportRequest.DEFAULT_RETENTION_DAYS,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: String = "",
    val lastSyncedAt: Long = 0L,
    val version: Int = 1
) {
    companion object {
        const val FORMAT_JSON = "json"
        const val FORMAT_CSV = "csv"
        const val FORMAT_PDF = "pdf"

        const val STATUS_PENDING = "pending"
        const val STATUS_PROCESSING = "processing"
        const val STATUS_COMPLETED = "completed"
        const val STATUS_FAILED = "failed"
        const val STATUS_EXPIRED = "expired"

        const val LEGAL_BASIS_USER_REQUEST = "user_request"
        const val LEGAL_BASIS_LEGAL_OBLIGATION = "legal_obligation"
        const val LEGAL_BASIS_VITAL_INTERESTS = "vital_interests"

        const val DEFAULT_RETENTION_DAYS = 7
        const val MAX_RETENTION_DAYS = 30
    }
}

/**
 * Estatísticas de exportação.
 */
data class ExportStats(
    val total: Int,
    val pending: Int,
    val processing: Int,
    val completed: Int,
    val failed: Int,
    val expired: Int,
    val totalSizeBytes: Long,
    val byFormat: Map<String, Int>
) {
    val successRate: Double
        get() = if (total > 0) (completed * 100.0 / total) else 0.0

    val totalSizeMB: Double
        get() = totalSizeBytes / (1024.0 * 1024.0)
}