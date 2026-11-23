package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.data.local.realm.dao.CycleDao
import com.seuapp.cycletrack.data.local.realm.entities.CycleRealm
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.repository.CycleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import javax.inject.Inject

class CycleRepositoryImpl @Inject constructor(
    private val cycleDao: CycleDao
) : CycleRepository {

    override fun getCycles(userId: String): Flow<List<Cycle>> {
        return flowOf(emptyList()) // TODO: Implementar com Realm Flow
    }

    override suspend fun getCycle(cycleId: String): Cycle? {
        val realmCycle = cycleDao.getCycle(cycleId) ?: return null
        return realmCycle.toDomain()
    }

    override suspend fun getLatestCycle(userId: String): Cycle? {
        return try {
            val realmCycle = cycleDao.getLatestCycle(userId) ?: return null
            realmCycle.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getCyclesInRange(userId: String, startDate: Long, endDate: Long): List<Cycle> {
        val realmCycles = cycleDao.getCyclesInRange(userId, startDate, endDate)
        return realmCycles.map { it.toDomain() }
    }

    override suspend fun getPredictedCycles(userId: String): List<Cycle> {
        val realmCycles = cycleDao.getPredictedCycles(userId)
        return realmCycles.map { it.toDomain() }
    }

    override suspend fun getCyclesNeedingSync(userId: String): List<Cycle> {
        val realmCycles = cycleDao.getCyclesNeedingSync(userId)
        return realmCycles.map { it.toDomain() }
    }

    override suspend fun upsertCycle(cycle: Cycle) {
        val realmCycle = cycle.toRealm()
        cycleDao.upsertCycle(realmCycle)
    }

    override suspend fun upsertCycles(cycles: List<Cycle>) {
        val realmCycles = cycles.map { it.toRealm() }
        cycleDao.upsertCycles(realmCycles)
    }

    override suspend fun deleteCycle(cycleId: String) {
        cycleDao.deleteCycle(cycleId)
    }

    override suspend fun hardDeleteCycle(cycleId: String) {
        cycleDao.hardDeleteCycle(cycleId)
    }

    override suspend fun getCycleStats(userId: String): Map<String, Any> {
        return cycleDao.getCycleStats(userId)
    }

    override suspend fun getCycleCount(userId: String): Int {
        return cycleDao.getCycleCount(userId)
    }

    override suspend fun hasCycleOnDate(userId: String, date: Long): Boolean {
        return cycleDao.hasCycleOnDate(userId, date)
    }

    // Mapper functions
    private fun Cycle.toRealm(): CycleRealm = CycleRealm().apply {
        this.id = this@toRealm.id
        this.userId = this@toRealm.userId
        this.startDate = this@toRealm.startDate.toEpochDay()
        this.endDate = this@toRealm.endDate?.toEpochDay() ?: 0L
        this.cycleLength = this@toRealm.cycleLength
        this.periodLength = this@toRealm.periodLength
        this.lutealPhaseLength = this@toRealm.lutealPhaseLength
        this.ovulationDate = this@toRealm.ovulationDate?.toEpochDay() ?: 0L
        this.fertileWindowStart = this@toRealm.fertileWindowStart?.toEpochDay() ?: 0L
        this.fertileWindowEnd = this@toRealm.fertileWindowEnd?.toEpochDay() ?: 0L
        this.isPredicted = this@toRealm.isPredicted
        this.confidenceScore = this@toRealm.confidenceScore
        this.source = this@toRealm.source
        this.notes = this@toRealm.notes
        this.createdAt = this@toRealm.createdAt
        this.updatedAt = this@toRealm.updatedAt
    }

    private fun CycleRealm.toDomain(): Cycle = Cycle(
        id = this.id,
        userId = this.userId,
        startDate = LocalDate.ofEpochDay(this.startDate),
        endDate = if (this.endDate != 0L) LocalDate.ofEpochDay(this.endDate) else null,
        cycleLength = this.cycleLength,
        periodLength = this.periodLength,
        lutealPhaseLength = this.lutealPhaseLength,
        ovulationDate = if (this.ovulationDate != 0L) LocalDate.ofEpochDay(this.ovulationDate) else null,
        fertileWindowStart = if (this.fertileWindowStart != 0L) LocalDate.ofEpochDay(this.fertileWindowStart) else null,
        fertileWindowEnd = if (this.fertileWindowEnd != 0L) LocalDate.ofEpochDay(this.fertileWindowEnd) else null,
        isPredicted = this.isPredicted,
        confidenceScore = this.confidenceScore,
        source = this.source,
        notes = this.notes,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
