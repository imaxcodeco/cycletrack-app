package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.CycleRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.seuapp.cycletrack.data.local.realm.detached
import kotlin.math.pow

/**
 * DAO for CycleRealm operations.
 * Clean-room implementation with reactive queries.
 */
class CycleDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get all non-deleted cycles for a user, sorted by start date descending.
     */
    suspend fun getCycles(userId: String): List<CycleRealm> = withContext(Dispatchers.IO) {
        realm.where(CycleRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .sort("startDate", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get a specific cycle by ID.
     */
    suspend fun getCycle(cycleId: String): CycleRealm? = withContext(Dispatchers.IO) {
        realm.where(CycleRealm::class.java)
            .equalTo("id", cycleId)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Get the most recent cycle for a user.
     */
    suspend fun getLatestCycle(userId: String): CycleRealm? = withContext(Dispatchers.IO) {
        realm.where(CycleRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .sort("startDate", io.realm.Sort.DESCENDING)
            .findFirst()
            .detached()
    }

    /**
     * Get cycles within a date range.
     */
    suspend fun getCyclesInRange(userId: String, startDate: Long, endDate: Long): List<CycleRealm> = withContext(Dispatchers.IO) {
        realm.where(CycleRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .greaterThanOrEqualTo("startDate", startDate)
            .lessThanOrEqualTo("startDate", endDate)
            .sort("startDate", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get predicted cycles for a user.
     */
    suspend fun getPredictedCycles(userId: String): List<CycleRealm> = withContext(Dispatchers.IO) {
        realm.where(CycleRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("isPredicted", true)
            .sort("startDate", io.realm.Sort.ASCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get cycles needing sync.
     */
    suspend fun getCyclesNeedingSync(userId: String): List<CycleRealm> = withContext(Dispatchers.IO) {
        realm.where(CycleRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .findAll()
            .filter { it.serverId.isEmpty() || it.updatedAt > it.lastSyncedAt }
            .detached()
    }

    /**
     * Insert or update a cycle.
     */
    suspend fun upsertCycle(cycle: CycleRealm): CycleRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            cycle.updatedAt = System.currentTimeMillis()
            val managed = realm.copyToRealmOrUpdate(cycle)
            managed
        }
    }

    /**
     * Insert multiple cycles in a transaction.
     */
    suspend fun upsertCycles(cycles: List<CycleRealm>): List<CycleRealm> = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            cycles.forEach { it.updatedAt = System.currentTimeMillis() }
            realm.copyToRealmOrUpdate(cycles)
        }
    }

    /**
     * Soft delete a cycle.
     */
    suspend fun deleteCycle(cycleId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val cycle = realm.where(CycleRealm::class.java)
                .equalTo("id", cycleId)
                .findFirst()
            cycle?.let {
                it.deletedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Hard delete a cycle (for GDPR/LGPD).
     */
    suspend fun hardDeleteCycle(cycleId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val cycle = realm.where(CycleRealm::class.java)
                .equalTo("id", cycleId)
                .findFirst()
            cycle?.deleteFromRealm() ?: false
        }
    }

    /**
     * Get cycle statistics for a user.
     */
    suspend fun getCycleStats(userId: String): CycleStats = withContext(Dispatchers.IO) {
        val cycles = realm.where(CycleRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("isPredicted", false)
            .findAll()

        if (cycles.isEmpty()) {
            return CycleStats(
                count = 0,
                averageLength = 0,
                averagePeriodLength = 0,
                minLength = 0,
                maxLength = 0,
                standardDeviation = 0.0
            )
        }

        val lengths = cycles.map { it.cycleLength }.filter { it > 0 }
        val periodLengths = cycles.map { it.periodLength }.filter { it > 0 }

        val avgLength = if (lengths.isNotEmpty()) lengths.average().toInt() else 0
        val avgPeriod = if (periodLengths.isNotEmpty()) periodLengths.average().toInt() else 0
        val minLen = lengths.minOrNull() ?: 0
        val maxLen = lengths.maxOrNull() ?: 0

        val stdDev = if (lengths.size > 1) {
            val mean = lengths.average()
            val variance = lengths.map { (it - mean).pow(2) }.average()
            Math.sqrt(variance).toFloat()
        } else 0.0f

        CycleStats(
            count = cycles.size,
            averageLength = avgLength,
            averagePeriodLength = avgPeriod,
            minLength = minLen,
            maxLength = maxLen,
            standardDeviation = stdDev
        )
    }

    /**
     * Get cycle count for a user.
     */
    suspend fun getCycleCount(userId: String): Int = withContext(Dispatchers.IO) {
        realm.where(CycleRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .count()
            .toInt()
    }

    /**
     * Check if a cycle exists for a given date.
     */
    suspend fun hasCycleOnDate(userId: String, date: Long): Boolean = withContext(Dispatchers.IO) {
        realm.where(CycleRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .lessThanOrEqualTo("startDate", date)
            .greaterThanOrEqualTo("endDate", date)
            .findFirst() != null
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

/**
 * Data class for cycle statistics.
 */
data class CycleStats(
    val count: Int,
    val averageLength: Int,
    val averagePeriodLength: Int,
    val minLength: Int,
    val maxLength: Int,
    val standardDeviation: Float
)