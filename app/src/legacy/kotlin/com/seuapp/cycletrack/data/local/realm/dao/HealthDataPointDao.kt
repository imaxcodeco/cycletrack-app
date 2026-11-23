package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.HealthDataPointRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.seuapp.cycletrack.data.local.realm.detached

/**
 * DAO for HealthDataPointRealm operations.
 * Clean-room implementation for Health Connect bidirectional sync.
 */
class HealthDataPointDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get all health data points for a user.
     */
    suspend fun getHealthDataPoints(userId: String): List<HealthDataPointRealm> = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .sort("startTime", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get health data points by type.
     */
    suspend fun getHealthDataPointsByType(
        userId: String,
        dataType: String
    ): List<HealthDataPointRealm> = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("dataType", dataType)
            .sort("startTime", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get health data points in date range.
     */
    suspend fun getHealthDataPointsInRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): List<HealthDataPointRealm> = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .greaterThanOrEqualTo("startTime", startDate)
            .lessThanOrEqualTo("startTime", endDate)
            .sort("startTime", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get health data points by type and date range.
     */
    suspend fun getHealthDataPointsByTypeAndRange(
        userId: String,
        dataType: String,
        startDate: Long,
        endDate: Long
    ): List<HealthDataPointRealm> = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("dataType", dataType)
            .greaterThanOrEqualTo("startTime", startDate)
            .lessThanOrEqualTo("startTime", endDate)
            .sort("startTime", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get health data points needing sync to Health Connect.
     */
    suspend fun getPendingSyncToHealthConnect(userId: String): List<HealthDataPointRealm> = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("source", HealthDataPointRealm.SOURCE_APP)
            .in("syncStatus", arrayOf(
                HealthDataPointRealm.SYNC_PENDING,
                HealthDataPointRealm.SYNC_ERROR,
                HealthDataPointRealm.SYNC_CONFLICT
            ))
            .sort("lastSyncAttempt", io.realm.Sort.ASCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get health data points from Health Connect needing merge.
     */
    suspend fun getFromHealthConnectNeedingMerge(userId: String): List<HealthDataPointRealm> = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("source", HealthDataPointRealm.SOURCE_HEALTH_CONNECT)
            .equalTo("syncStatus", HealthDataPointRealm.SYNC_CONFLICT)
            .sort("startTime", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get health data point by ID.
     */
    suspend fun getHealthDataPoint(dataPointId: String): HealthDataPointRealm? = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("id", dataPointId)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Get health data point by Health Connect record ID.
     */
    suspend fun getByHealthConnectRecordId(userId: String, recordId: String): HealthDataPointRealm? = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("sourceRecordId", recordId)
            .findFirst()
            .detached()
    }

    /**
     * Insert or update a health data point.
     */
    suspend fun upsertHealthDataPoint(dataPoint: HealthDataPointRealm): HealthDataPointRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            dataPoint.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(dataPoint)
        }
    }

    /**
     * Insert multiple health data points.
     */
    suspend fun upsertHealthDataPoints(dataPoints: List<HealthDataPointRealm>): List<HealthDataPointRealm> = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            dataPoints.forEach { it.updatedAt = System.currentTimeMillis() }
            realm.copyToRealmOrUpdate(dataPoints)
        }
    }

    /**
     * Update sync status.
     */
    suspend fun updateSyncStatus(
        dataPointId: String,
        status: String,
        error: String? = null,
        conflictResolution: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val dataPoint = realm.where(HealthDataPointRealm::class.java)
                .equalTo("id", dataPointId)
                .findFirst()
            dataPoint?.let {
                it.syncStatus = status
                it.lastSyncAttempt = System.currentTimeMillis()
                error?.let { it.syncError = it }
                conflictResolution?.let { it.conflictResolution = it }
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Bulk update sync status.
     */
    suspend fun bulkUpdateSyncStatus(
        dataPointIds: List<String>,
        status: String
    ): Int = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            var count = 0
            dataPointIds.forEach { id ->
                val dataPoint = realm.where(HealthDataPointRealm::class.java)
                    .equalTo("id", id)
                    .findFirst()
                dataPoint?.let {
                    it.syncStatus = status
                    it.lastSyncAttempt = System.currentTimeMillis()
                    it.updatedAt = System.currentTimeMillis()
                    it.version++
                    count++
                }
            }
            count
        }
    }

    /**
     * Resolve conflict with local wins.
     */
    suspend fun resolveConflictLocalWins(dataPointId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val dataPoint = realm.where(HealthDataPointRealm::class.java)
                .equalTo("id", dataPointId)
                .findFirst()
            dataPoint?.let {
                it.syncStatus = HealthDataPointRealm.SYNC_SYNCED
                it.conflictResolution = HealthDataPointRealm.CONFLICT_LOCAL_WINS
                it.lastSyncAttempt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Resolve conflict with remote wins.
     */
    suspend fun resolveConflictRemoteWins(dataPointId: String, remoteData: HealthDataPointRealm): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val dataPoint = realm.where(HealthDataPointRealm::class.java)
                .equalTo("id", dataPointId)
                .findFirst()
            dataPoint?.let {
                // Update with remote data but keep local ID
                val localId = it.id
                val localCreatedAt = it.createdAt
                val localUserId = it.userId

                it.value = remoteData.value
                it.numericValue = remoteData.numericValue
                it.unit = remoteData.unit
                it.flowLevel = remoteData.flowLevel
                it.testResult = remoteData.testResult
                it.temperatureCelsius = remoteData.temperatureCelsius
                it.mucusConsistency = remoteData.mucusConsistency
                it.pregnancyTestResult = remoteData.pregnancyTestResult
                it.startTime = remoteData.startTime
                it.endTime = remoteData.endTime
                it.timeZoneOffset = remoteData.timeZoneOffset

                it.syncStatus = HealthDataPointRealm.SYNC_SYNCED
                it.conflictResolution = HealthDataPointRealm.CONFLICT_REMOTE_WINS
                it.lastSyncAttempt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                it.version++

                // Restore local fields
                it.id = localId
                it.userId = localUserId
                it.createdAt = localCreatedAt

                true
            } ?: false
        }
    }

    /**
     * Soft delete a health data point.
     */
    suspend fun deleteHealthDataPoint(dataPointId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val dataPoint = realm.where(HealthDataPointRealm::class.java)
                .equalTo("id", dataPointId)
                .findFirst()
            dataPoint?.let {
                it.deletedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Hard delete a health data point (GDPR/LGPD).
     */
    suspend fun hardDeleteHealthDataPoint(dataPointId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val dataPoint = realm.where(HealthDataPointRealm::class.java)
                .equalTo("id", dataPointId)
                .findFirst()
            dataPoint?.deleteFromRealm() ?: false
        }
    }

    /**
     * Delete all health data points for a user (GDPR/LGPD).
     */
    suspend fun hardDeleteAllForUser(userId: String): Int = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val dataPoints = realm.where(HealthDataPointRealm::class.java)
                .equalTo("userId", userId)
                .findAll()
            val count = dataPoints.size
            dataPoints.deleteAllFromRealm()
            count
        }
    }

    /**
     * Get menstrual flow data for cycle calculation.
     */
    suspend fun getMenstrualFlows(userId: String, startDate: Long, endDate: Long): List<HealthDataPointRealm> = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("dataType", HealthDataPointRealm.TYPE_MENSTRUAL_FLOW)
            .greaterThanOrEqualTo("startTime", startDate)
            .lessThanOrEqualTo("startTime", endDate)
            .sort("startTime", io.realm.Sort.ASCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get BBT data for ovulation detection.
     */
    suspend fun getBasalBodyTemperatures(userId: String, startDate: Long, endDate: Long): List<HealthDataPointRealm> = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("dataType", HealthDataPointRealm.TYPE_BASAL_BODY_TEMPERATURE)
            .greaterThanOrEqualTo("startTime", startDate)
            .lessThanOrEqualTo("startTime", endDate)
            .sort("startTime", io.realm.Sort.ASCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get ovulation test results.
     */
    suspend fun getOvulationTests(userId: String, startDate: Long, endDate: Long): List<HealthDataPointRealm> = withContext(Dispatchers.IO) {
        realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("dataType", HealthDataPointRealm.TYPE_OVULATION_TEST)
            .greaterThanOrEqualTo("startTime", startDate)
            .lessThanOrEqualTo("startTime", endDate)
            .sort("startTime", io.realm.Sort.ASCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get count of health data points by type.
     */
    suspend fun getCountByType(userId: String): Map<String, Int> = withContext(Dispatchers.IO) {
        val dataPoints = realm.where(HealthDataPointRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .findAll()

        return dataPoints
            .groupBy { it.dataType }
            .mapValues { (_, v) -> v.size }
    }

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