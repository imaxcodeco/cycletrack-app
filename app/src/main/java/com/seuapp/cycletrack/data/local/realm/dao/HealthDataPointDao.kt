package com.seuapp.cycletrack.data.local.realm.dao

import javax.inject.Inject

class HealthDataPointDao @Inject constructor() {
    suspend fun getHealthDataPoints(userId: String) = emptyList<Any>()
    suspend fun getHealthDataPointsByType(userId: String, dataType: String) = emptyList<Any>()
    suspend fun getHealthDataPointsInRange(userId: String, startDate: Long, endDate: Long) = emptyList<Any>()
    suspend fun upsertHealthDataPoint(dataPoint: Any) {}
    suspend fun upsertHealthDataPoints(dataPoints: List<Any>) {}
    suspend fun deleteHealthDataPoint(dataPointId: String) {}
    suspend fun hardDeleteHealthDataPoint(dataPointId: String) {}
}
