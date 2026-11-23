package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.CycleRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CycleDao @Inject constructor() {

    private val realm: Realm
        get() = Realm.getDefaultInstance()

    suspend fun getCycles(userId: String): List<CycleRealm> = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(CycleRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("deletedAt", 0L)
                .sort("startDate", io.realm.Sort.DESCENDING)
                .findAll()
                .let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCycle(cycleId: String): CycleRealm? = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(CycleRealm::class.java)
                .equalTo("id", cycleId)
                .equalTo("deletedAt", 0L)
                .findFirst()
                ?.let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getLatestCycle(userId: String): CycleRealm? = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(CycleRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("deletedAt", 0L)
                .sort("startDate", io.realm.Sort.DESCENDING)
                .findFirst()
                ?.let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCyclesInRange(userId: String, startDate: Long, endDate: Long): List<CycleRealm> {
        return emptyList()
    }

    suspend fun getPredictedCycles(userId: String): List<CycleRealm> {
        return emptyList()
    }

    suspend fun getCyclesNeedingSync(userId: String): List<CycleRealm> {
        return emptyList()
    }

    suspend fun upsertCycle(cycle: CycleRealm) = withContext(Dispatchers.IO) {
        try {
            realm.executeTransaction { it.insertOrUpdate(cycle) }
        } catch (e: Exception) {
            // Falha silenciosa: persistencia local e best-effort
        }
    }

    suspend fun upsertCycles(cycles: List<CycleRealm>) = withContext(Dispatchers.IO) {
        try {
            realm.executeTransaction { it.insertOrUpdate(cycles) }
        } catch (e: Exception) {
            // Falha silenciosa: persistencia local e best-effort
        }
    }

    suspend fun deleteCycle(cycleId: String) {}

    suspend fun hardDeleteCycle(cycleId: String) {}

    suspend fun getCycleStats(userId: String): Map<String, Any> = emptyMap()

    suspend fun getCycleCount(userId: String): Int = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(CycleRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("deletedAt", 0L)
                .count()
                .toInt()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun hasCycleOnDate(userId: String, date: Long): Boolean = false
}
