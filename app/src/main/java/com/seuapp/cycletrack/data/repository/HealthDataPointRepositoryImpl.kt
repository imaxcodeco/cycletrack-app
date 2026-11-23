package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.data.local.realm.dao.HealthDataPointDao
import com.seuapp.cycletrack.domain.repository.HealthDataPointRepository
import javax.inject.Inject

class HealthDataPointRepositoryImpl @Inject constructor(
    private val healthDataPointDao: HealthDataPointDao
) : HealthDataPointRepository {
    override suspend fun getHealthDataPoints(userId: String) = emptyList<Any>()
    override suspend fun upsertHealthDataPoint(dataPoint: Any) {}
    override suspend fun deleteHealthDataPoint(id: String) {}
}
