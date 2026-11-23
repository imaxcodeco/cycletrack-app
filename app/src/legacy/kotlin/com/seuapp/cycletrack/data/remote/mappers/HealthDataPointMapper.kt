package com.seuapp.cycletrack.data.remote.mappers

import com.seuapp.cycletrack.domain.model.HealthDataPoint
import com.seuapp.cycletrack.domain.model.HealthDataType
import com.seuapp.cycletrack.data.local.realm.entities.HealthDataPointRealm
import kotlinx.datetime.Instant

/**
 * Mapeadores para Health Data Points entre Domain, Realm, DTOs.
 * Clean-room - implementação própria.
 */
object HealthDataPointMapper {

    // Domain <-> Realm
    fun toRealm(domain: HealthDataPoint): HealthDataPointRealm = HealthDataPointRealm().apply {
        id = domain.id
        userId = domain.userId
        dataType = domain.dataType.key
        startTime = domain.startTime
        endTime = domain.endTime
        value = domain.value
        flowLevel = domain.flowLevel ?: 0
        ovulationTestResult = domain.ovulationTestResult ?: ""
        source = domain.source
        healthConnectRecordId = domain.healthConnectRecordId ?: ""
        syncStatus = domain.syncStatus
        syncError = domain.syncError ?: ""
        conflictResolution = domain.conflictResolution ?: ""
        createdAt = domain.createdAt
        updatedAt = domain.updatedAt
        lastSyncedAt = domain.lastSyncedAt
        deletedAt = 0L
    }

    fun toDomain(realm: HealthDataPointRealm): HealthDataPoint = HealthDataPoint(
        id = realm.id,
        userId = realm.userId,
        dataType = HealthDataType.fromKey(realm.dataType),
        startTime = realm.startTime,
        endTime = realm.endTime,
        value = realm.value,
        flowLevel = if (realm.flowLevel > 0) realm.flowLevel else null,
        ovulationTestResult = if (realm.ovulationTestResult.isNotBlank()) realm.ovulationTestResult else null,
        source = realm.source,
        healthConnectRecordId = if (realm.healthConnectRecordId.isNotBlank()) realm.healthConnectRecordId else null,
        syncStatus = realm.syncStatus,
        syncError = if (realm.syncError.isNotBlank()) realm.syncError else null,
        conflictResolution = if (realm.conflictResolution.isNotBlank()) realm.conflictResolution else null,
        createdAt = realm.createdAt,
        updatedAt = realm.updatedAt,
        lastSyncedAt = realm.lastSyncedAt
    )

    // Domain -> Request DTO (for server sync)
    // Health data points typically don't go to our server, they go to Health Connect
    // But we can create DTOs for backup/export purposes if needed
}