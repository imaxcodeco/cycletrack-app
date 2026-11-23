package com.seuapp.cycletrack.domain.repository

interface HealthDataPointRepository {
    suspend fun getHealthDataPoints(userId: String): List<Any>
    suspend fun upsertHealthDataPoint(dataPoint: Any)
    suspend fun deleteHealthDataPoint(id: String)
}
