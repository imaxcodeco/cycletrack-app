package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.ExportRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.seuapp.cycletrack.data.local.realm.detached

/**
 * DAO for ExportRealm operations.
 * Clean-room implementation for LGPD/GDPR data export requests.
 */
class ExportDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get all exports for a user.
     */
    suspend fun getExports(userId: String): List<ExportRealm> = withContext(Dispatchers.IO) {
        realm.where(ExportRealm::class.java)
            .equalTo("userId", userId)
            .sort("requestedAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get exports by status.
     */
    suspend fun getExportsByStatus(userId: String, status: String): List<ExportRealm> = withContext(Dispatchers.IO) {
        realm.where(ExportRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("status", status)
            .sort("requestedAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get pending exports.
     */
    suspend fun getPendingExports(): List<ExportRealm> = withContext(Dispatchers.IO) {
        realm.where(ExportRealm::class.java)
            .in("status", arrayOf(ExportRealm.STATUS_PENDING, ExportRealm.STATUS_PROCESSING))
            .sort("requestedAt", io.realm.Sort.ASCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get export by ID.
     */
    suspend fun getExport(exportId: String): ExportRealm? = withContext(Dispatchers.IO) {
        realm.where(ExportRealm::class.java)
            .equalTo("id", exportId)
            .findFirst()
            .detached()
    }

    /**
     * Create a new export request.
     */
    suspend fun createExport(
        userId: String,
        format: String = ExportRealm.FORMAT_JSON,
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
        legalBasis: String = ExportRealm.LEGAL_BASIS_USER_REQUEST,
        retentionDays: Int = ExportRealm.DEFAULT_RETENTION_DAYS
    ): ExportRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val export = ExportRealm().apply {
                id = java.util.UUID.randomUUID().toString()
                this.userId = userId
                this.format = format
                this.includeCycles = includeCycles
                this.includeSymptoms = includeSymptoms
                this.includePredictions = includePredictions
                this.includePregnancy = includePregnancy
                this.includePartnerData = includePartnerData
                this.includeHealthConnectData = includeHealthConnectData
                this.includeSettings = includeSettings
                this.includeContent = includeContent
                this.dateRangeStart = dateRangeStart
                this.dateRangeEnd = dateRangeEnd
                this.anonymize = anonymize
                this.status = ExportRealm.STATUS_PENDING
                this.requestedFrom = requestedFrom
                this.ipAddress = ipAddress
                this.userAgent = userAgent
                this.legalBasis = legalBasis
                this.retentionDays = retentionDays.coerceIn(1, ExportRealm.MAX_RETENTION_DAYS)
                this.requestedAt = System.currentTimeMillis()
                this.createdAt = System.currentTimeMillis()
                this.updatedAt = System.currentTimeMillis()
                this.version = 1
            }
            realm.copyToRealm(export)
        }
    }

    /**
     * Update export status to processing.
     */
    suspend fun markAsProcessing(exportId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val export = realm.where(ExportRealm::class.java)
                .equalTo("id", exportId)
                .findFirst()
            export?.let {
                it.status = ExportRealm.STATUS_PROCESSING
                it.startedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Update export progress.
     */
    suspend fun updateProgress(exportId: String, progress: Int): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val export = realm.where(ExportRealm::class.java)
                .equalTo("id", exportId)
                .findFirst()
            export?.let {
                it.progress = progress.coerceIn(0, 100)
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Mark export as completed.
     */
    suspend fun markAsCompleted(
        exportId: String,
        filePath: String,
        fileSize: Long,
        fileHash: String,
        downloadUrl: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val export = realm.where(ExportRealm::class.java)
                .equalTo("id", exportId)
                .findFirst()
            export?.let {
                it.status = ExportRealm.STATUS_COMPLETED
                it.progress = 100
                it.filePath = filePath
                it.fileSize = fileSize
                it.fileHash = fileHash
                it.downloadUrl = downloadUrl
                it.completedAt = System.currentTimeMillis()
                it.expiresAt = System.currentTimeMillis() + (it.retentionDays * 24 * 60 * 60 * 1000L)
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Mark export as failed.
     */
    suspend fun markAsFailed(exportId: String, errorMessage: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val export = realm.where(ExportRealm::class.java)
                .equalTo("id", exportId)
                .findFirst()
            export?.let {
                it.status = ExportRealm.STATUS_FAILED
                it.errorMessage = errorMessage
                it.retryCount++
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Mark export as expired.
     */
    suspend fun markAsExpired(exportId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val export = realm.where(ExportRealm::class.java)
                .equalTo("id", exportId)
                .findFirst()
            export?.let {
                it.status = ExportRealm.STATUS_EXPIRED
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Get expired exports.
     */
    suspend fun getExpiredExports(): List<ExportRealm> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        realm.where(ExportRealm::class.java)
            .equalTo("status", ExportRealm.STATUS_COMPLETED)
            .lessThan("expiresAt", now)
            .findAll()
            .detached()
    }

    /**
     * Delete expired export files and mark as expired.
     */
    suspend fun cleanupExpiredExports(): Int = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        writeTransaction { realm ->
            val exports = realm.where(ExportRealm::class.java)
                .equalTo("status", ExportRealm.STATUS_COMPLETED)
                .lessThan("expiresAt", now)
                .findAll()
            val count = exports.size
            exports.forEach { it.status = ExportRealm.STATUS_EXPIRED }
            count
        }
    }

    /**
     * Delete export record.
     */
    suspend fun deleteExport(exportId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val export = realm.where(ExportRealm::class.java)
                .equalTo("id", exportId)
                .findFirst()
            export?.deleteFromRealm() ?: false
        }
    }

    /**
     * Get export statistics for a user.
     */
    suspend fun getExportStats(userId: String): ExportStats = withContext(Dispatchers.IO) {
        val exports = realm.where(ExportRealm::class.java)
            .equalTo("userId", userId)
            .findAll()

        val pending = exports.count { it.status == ExportRealm.STATUS_PENDING }
        val processing = exports.count { it.status == ExportRealm.STATUS_PROCESSING }
        val completed = exports.count { it.status == ExportRealm.STATUS_COMPLETED }
        val failed = exports.count { it.status == ExportRealm.STATUS_FAILED }
        val expired = exports.count { it.status == ExportRealm.STATUS_EXPIRED }

        val totalSize = exports
            .filter { it.status == ExportRealm.STATUS_COMPLETED }
            .sumOf { it.fileSize }

        val byFormat = exports
            .groupBy { it.format }
            .mapValues { (_, v) -> v.size }

        ExportStats(
            total = exports.size,
            pending = pending,
            processing = processing,
            completed = completed,
            failed = failed,
            expired = expired,
            totalSizeBytes = totalSize,
            byFormat = byFormat
        )
    }
}

/**
 * Data class for export statistics.
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

    /**
     * Executa uma transacao de escrita devolvendo o valor produzido pelo bloco.
     * A API Java do Realm nao retorna valor de `executeTransaction`.
     */
    private suspend fun <T> writeTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
        var result: T? = null
        realm.executeTransaction { transactionRealm ->
            result = block(transactionRealm)
        }
        @Suppress("UNCHECKED_CAST")
        result as T
    }

}