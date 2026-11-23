package com.seuapp.cycletrack.data.sync.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.permission.HealthPermission.getReadPermission
import androidx.health.connect.client.permission.HealthPermission.getWritePermission
import androidx.health.connect.client.records.MenstruationRecord
import androidx.health.connect.client.records.BasalBodyTemperatureRecord
import androidx.health.connect.client.records.OvulationTestRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.request.DeleteRecordsRequest
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RecordIdFilter
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import com.seuapp.cycletrack.data.local.realm.RealmRepository
import com.seuapp.cycletrack.data.local.realm.entities.CycleRealm
import com.seuapp.cycletrack.data.local.realm.entities.HealthDataPointRealm
import com.seuapp.cycletrack.data.remote.mappers.HealthDataPointMapper
import com.seuapp.cycletrack.domain.model.HealthDataPoint
import com.seuapp.cycletrack.domain.model.HealthDataType
import com.seuapp.cycletrack.util.AppLogger
import dagger.hilt.android.scopes.Singleton
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import import kotlinx.datetime.Clock

/**
 * Worker para sincronização bidirecional com Health Connect.
 * Clean-room - implementação própria com resolução de conflitos.
 */
@Singleton
class HealthConnectSyncWorker @Inject constructor(
    @Suppress("UNUSED_PARAMETER") context: Context,
    @Suppress("UNUSED_PARAMETER") params: WorkerParameters,
    private val preferencesManager: PreferencesManager,
    private val realmRepository: RealmRepository
) : CoroutineWorker(context, params) {

    private val logger = AppLogger.getInstance("HealthConnectSyncWorker")
    private val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"

    override suspend fun doWork(): Result {
        val userId = preferencesManager.getUserId() ?: return Result.success()
        val isEnabled = preferencesManager.healthConnectEnabled.firstOrNull() ?: false
        
        if (!isEnabled) return Result.success()
        
        logger.info("Iniciando sincronização Health Connect para usuário: $userId")
        
        return try {
            val healthConnectClient = HealthConnectClient.getOrCreate(applicationContext)
            
            // Check permissions
            val permissions = getRequiredPermissions()
            val granted = healthConnectClient.checkPermissions(permissions)
            if (!granted.containsAll(permissions)) {
                logger.warn("Permissões Health Connect não concedidas")
                return Result.success() // Don't retry, wait for user to grant
            }
            
            // 1. Push local changes to Health Connect
            pushToHealthConnect(healthConnectClient, userId)
            
            // 2. Pull changes from Health Connect
            pullFromHealthConnect(healthConnectClient, userId)
            
            // 3. Update last sync time
            preferencesManager.setLastSyncTime(System.currentTimeMillis())
            
            logger.info("Sincronização Health Connect concluída")
            Result.success()
        } catch (e: Exception) {
            logger.error(e, "Erro na sincronização Health Connect")
            Result.retry()
        }
    }

    private fun getRequiredPermissions(): Set<HealthPermission> {
        return setOf(
            getReadPermission(MenstruationRecord::class),
            getWritePermission(MenstruationRecord::class),
            getReadPermission(BasalBodyTemperatureRecord::class),
            getWritePermission(BasalBodyTemperatureRecord::class),
            getReadPermission(OvulationTestRecord::class),
            getWritePermission(OvulationTestRecord::class),
            getReadPermission(WeightRecord::class),
            getWritePermission(WeightRecord::class)
        )
    }

    private suspend fun pushToHealthConnect(client: HealthConnectClient, userId: String) = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        try {
            val pendingRecords = realm.where(HealthDataPointRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("syncStatus", HealthDataPoint.SYNC_STATUS_PENDING)
                .findAll()
            
            for (recordRealm in pendingRecords) {
                val domain = HealthDataPointMapper.toDomain(recordRealm)
                
                try {
                    when (domain.dataType) {
                        HealthDataType.MENSTRUATION -> pushMenstruation(client, domain)
                        HealthDataType.BASAL_BODY_TEMPERATURE -> pushTemperature(client, domain)
                        HealthDataType.OVULATION_TEST -> pushOvulationTest(client, domain)
                        HealthDataType.WEIGHT -> pushWeight(client, domain)
                    }
                    
                    // Mark as synced
                    realm.executeTransaction {
                        val managed = realm.where(HealthDataPointRealm::class.java).equalTo("id", recordRealm.id).findFirst()
                        managed?.apply {
                            syncStatus = HealthDataPoint.SYNC_STATUS_SYNCED
                            healthConnectRecordId = domain.healthConnectRecordId
                            lastSyncedAt = System.currentTimeMillis()
                        }
                    }
                } catch (e: Exception) {
                    logger.error(e, "Erro ao enviar para Health Connect: ${domain.id}")
                    realm.executeTransaction {
                        val managed = realm.where(HealthDataPointRealm::class.java).equalTo("id", recordRealm.id).findFirst()
                        managed?.apply {
                            syncStatus = HealthDataPoint.SYNC_STATUS_FAILED
                            syncError = e.message
                        }
                    }
                }
            }
        } finally {
            realm.close()
        }
    }

    private suspend fun pushMenstruation(client: HealthConnectClient, domain: HealthDataPoint) {
        val record = MenstruationRecord.Builder(
            metadata = domain.toMetadata(),
            startTime = Instant.fromEpochMilliseconds(domain.startTime),
            endTime = Instant.fromEpochMilliseconds(domain.endTime),
            flow = when (domain.flowLevel) {
                1 -> MenstruationRecord.Flow.SPOTTING
                2 -> MenstruationRecord.Flow.LIGHT
                3 -> MenstruationRecord.Flow.MEDIUM
                4 -> MenstruationRecord.Flow.HEAVY
                else -> MenstruationRecord.Flow.UNKNOWN
            }
        ).build()
        
        val result = client.insertRecords(listOf(record))
        if (result.insertedRecords.isNotEmpty()) {
            domain.healthConnectRecordId = result.insertedRecords.first().recordId
        }
    }

    private suspend fun pushTemperature(client: HealthConnectClient, domain: HealthDataPoint) {
        val record = BasalBodyTemperatureRecord.Builder(
            metadata = domain.toMetadata(),
            time = Instant.fromEpochMilliseconds(domain.startTime),
            temperatureCelsius = domain.value
        ).build()
        
        val result = client.insertRecords(listOf(record))
        if (result.insertedRecords.isNotEmpty()) {
            domain.healthConnectRecordId = result.insertedRecords.first().recordId
        }
    }

    private suspend fun pushOvulationTest(client: HealthConnectClient, domain: HealthDataPoint) {
        val record = OvulationTestRecord.Builder(
            metadata = domain.toMetadata(),
            time = Instant.fromEpochMilliseconds(domain.startTime),
            result = when (domain.ovulationTestResult) {
                "positive" -> OvulationTestRecord.Result.POSITIVE
                "negative" -> OvulationTestRecord.Result.NEGATIVE
                else -> OvulationTestRecord.Result.UNKNOWN
            }
        ).build()
        
        val result = client.insertRecords(listOf(record))
        if (result.insertedRecords.isNotEmpty()) {
            domain.healthConnectRecordId = result.insertedRecords.first().recordId
        }
    }

    private suspend fun pushWeight(client: HealthConnectClient, domain: HealthDataPoint) {
        val record = WeightRecord.Builder(
            metadata = domain.toMetadata(),
            time = Instant.fromEpochMilliseconds(domain.startTime),
            weightKilograms = domain.value
        ).build()
        
        val result = client.insertRecords(listOf(record))
        if (result.insertedRecords.isNotEmpty()) {
            domain.healthConnectRecordId = result.insertedRecords.first().recordId
        }
    }

    private suspend fun pullFromHealthConnect(client: HealthConnectClient, userId: String) = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        val lastSync = preferencesManager.lastSyncTime.firstOrNull() ?: 0L
        val since = Instant.fromEpochMilliseconds(lastSync)
        
        // Pull menstruation
        val menstruationResponse = client.readRecords(
            ReadRecordsRequest.Builder(MenstruationRecord::class.java)
                .setTimeRangeFilter(since, Clock.System.now())
                .build()
        )
        
        for (record in menstruationResponse.records) {
            upsertHealthDataPoint(realm, userId, record)
        }
        
        // Pull temperature
        val temperatureResponse = client.readRecords(
            ReadRecordsRequest.Builder(BasalBodyTemperatureRecord::class.java)
                .setTimeRangeFilter(since, Clock.System.now())
                .build()
        )
        
        for (record in temperatureResponse.records) {
            upsertHealthDataPoint(realm, userId, record)
        }
        
        // Pull ovulation tests
        val ovulationResponse = client.readRecords(
            ReadRecordsRequest.Builder(OvulationTestRecord::class.java)
                .setTimeRangeFilter(since, Clock.System.now())
                .build()
        )
        
        for (record in ovulationResponse.records) {
            upsertHealthDataPoint(realm, userId, record)
        }
        
        // Pull weight
        val weightResponse = client.readRecords(
            ReadRecordsRequest.Builder(WeightRecord::class.java)
                .setTimeRangeFilter(since, Clock.System.now())
                .build()
        )
        
        for (record in weightResponse.records) {
            upsertHealthDataPoint(realm, userId, record)
        }
    }

    private fun upsertHealthDataPoint(realm: Realm, userId: String, record: Record) {
        val existing = realm.where(HealthDataPointRealm::class.java)
            .equalTo("healthConnectRecordId", record.metadata.id)
            .findFirst()
        
        val domain = when (record) {
            is MenstruationRecord -> record.toHealthDataPoint(userId)
            is BasalBodyTemperatureRecord -> record.toHealthDataPoint(userId)
            is OvulationTestRecord -> record.toHealthDataPoint(userId)
            is WeightRecord -> record.toHealthDataPoint(userId)
            else -> return
        }
        
        realm.executeTransaction {
            if (existing != null) {
                // Check for conflicts
                if (existing.lastSyncedAt < record.metadata.lastModifiedTime) {
                    // Remote is newer
                    if (existing.syncStatus == HealthDataPoint.SYNC_STATUS_PENDING) {
                        // Local has pending changes - conflict!
                        existing.syncStatus = HealthDataPoint.SYNC_STATUS_CONFLICT
                        existing.conflictResolution = HealthDataPoint.CONFLICT_RESOLUTION_PENDING
                    } else {
                        // Safe to update
                        HealthDataPointMapper.toRealm(domain).also { mapped ->
                            existing.value = mapped.value
                            existing.startTime = mapped.startTime
                            existing.endTime = mapped.endTime
                            existing.flowLevel = mapped.flowLevel
                            existing.ovulationTestResult = mapped.ovulationTestResult
                            existing.lastSyncedAt = System.currentTimeMillis()
                            existing.syncStatus = HealthDataPoint.SYNC_STATUS_SYNCED
                        }
                    }
                }
            } else {
                // New from Health Connect
                val mapped = HealthDataPointMapper.toRealm(domain)
                mapped.syncStatus = HealthDataPoint.SYNC_STATUS_SYNCED
                mapped.lastSyncedAt = System.currentTimeMillis()
                realm.copyToRealm(mapped)
            }
        }
    }
}

// Extensions for Health Connect Records
private fun HealthDataPoint.toMetadata() = androidx.health.connect.client.records.Metadata.Builder()
    .setId(java.util.UUID.randomUUID().toString())
    .setLastModifiedTime(Clock.System.now())
    .build()

private fun MenstruationRecord.toHealthDataPoint(userId: String): HealthDataPoint {
    val flowLevel = when (this.flow) {
        MenstruationRecord.Flow.SPOTTING -> 1
        MenstruationRecord.Flow.LIGHT -> 2
        MenstruationRecord.Flow.MEDIUM -> 3
        MenstruationRecord.Flow.HEAVY -> 4
        else -> 0
    }
    return HealthDataPoint(
        id = java.util.UUID.randomUUID().toString(),
        userId = userId,
        dataType = HealthDataType.MENSTRUATION,
        startTime = this.startTime.toEpochMilli(),
        endTime = this.endTime.toEpochMilli(),
        value = flowLevel.toDouble(),
        flowLevel = flowLevel,
        source = HealthDataPoint.SOURCE_HEALTH_CONNECT,
        healthConnectRecordId = this.metadata.id,
        syncStatus = HealthDataPoint.SYNC_STATUS_SYNCED
    )
}

private fun BasalBodyTemperatureRecord.toHealthDataPoint(userId: String): HealthDataPoint {
    return HealthDataPoint(
        id = java.util.UUID.randomUUID().toString(),
        userId = userId,
        dataType = HealthDataType.BASAL_BODY_TEMPERATURE,
        startTime = this.time.toEpochMilli(),
        endTime = this.time.toEpochMilli(),
        value = this.temperatureCelsius,
        source = HealthDataPoint.SOURCE_HEALTH_CONNECT,
        healthConnectRecordId = this.metadata.id,
        syncStatus = HealthDataPoint.SYNC_STATUS_SYNCED
    )
}

private fun OvulationTestRecord.toHealthDataPoint(userId: String): HealthDataPoint {
    val result = when (this.result) {
        OvulationTestRecord.Result.POSITIVE -> "positive"
        OvulationTestRecord.Result.NEGATIVE -> "negative"
        else -> "unknown"
    }
    return HealthDataPoint(
        id = java.util.UUID.randomUUID().toString(),
        userId = userId,
        dataType = HealthDataType.OVULATION_TEST,
        startTime = this.time.toEpochMilli(),
        endTime = this.time.toEpochMilli(),
        value = 0.0,
        ovulationTestResult = result,
        source = HealthDataPoint.SOURCE_HEALTH_CONNECT,
        healthConnectRecordId = this.metadata.id,
        syncStatus = HealthDataPoint.SYNC_STATUS_SYNCED
    )
}

private fun WeightRecord.toHealthDataPoint(userId: String): HealthDataPoint {
    return HealthDataPoint(
        id = java.util.UUID.randomUUID().toString(),
        userId = userId,
        dataType = HealthDataType.WEIGHT,
        startTime = this.time.toEpochMilli(),
        endTime = this.time.toEpochMilli(),
        value = this.weightKilograms,
        source = HealthDataPoint.SOURCE_HEALTH_CONNECT,
        healthConnectRecordId = this.metadata.id,
        syncStatus = HealthDataPoint.SYNC_STATUS_SYNCED
    )
}