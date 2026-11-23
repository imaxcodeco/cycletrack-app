package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Cycle
import kotlinx.coroutines.flow.Flow

interface CycleRepository {

    fun getCycles(userId: String): Flow<List<Cycle>>

    suspend fun getCycle(cycleId: String): Cycle?

    suspend fun getLatestCycle(userId: String): Cycle?

    suspend fun getCyclesInRange(userId: String, startDate: Long, endDate: Long): List<Cycle>

    suspend fun getPredictedCycles(userId: String): List<Cycle>

    suspend fun getCyclesNeedingSync(userId: String): List<Cycle>

    suspend fun upsertCycle(cycle: Cycle)

    suspend fun upsertCycles(cycles: List<Cycle>)

    suspend fun deleteCycle(cycleId: String)

    suspend fun hardDeleteCycle(cycleId: String)

    suspend fun getCycleStats(userId: String): Map<String, Any>

    suspend fun getCycleCount(userId: String): Int

    suspend fun hasCycleOnDate(userId: String, date: Long): Boolean
}
