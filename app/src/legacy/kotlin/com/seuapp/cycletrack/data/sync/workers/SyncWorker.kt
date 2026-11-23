package com.seuapp.cycletrack.data.sync.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import com.seuapp.cycletrack.data.local.realm.RealmRepository
import com.seuapp.cycletrack.data.local.realm.entities.CycleRealm
import com.seuapp.cycletrack.data.local.realm.entities.SymptomRealm
import com.seuapp.cycletrack.data.local.realm.entities.UserProfileRealm
import com.seuapp.cycletrack.data.remote.api.CycleTrackApi
import com.seuapp.cycletrack.data.remote.mappers.CycleMapper
import com.seuapp.cycletrack.data.remote.mappers.SymptomMapper
import com.seuapp.cycletrack.data.remote.mappers.UserProfileMapper
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.util.AppLogger
import dagger.hilt.android.scopes.Singleton
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import retrofit2.Response

/**
 * Worker para sincronização offline-first com backoff exponencial.
 * Clean-room - implementação própria.
 */
@Singleton
class SyncWorker @Inject constructor(
    @Suppress("UNUSED_PARAMETER") context: Context,
    @Suppress("UNUSED_PARAMETER") params: WorkerParameters,
    private val api: CycleTrackApi,
    private val preferencesManager: PreferencesManager,
    private val realmRepository: RealmRepository
) : CoroutineWorker(context, params) {

    private val logger = AppLogger.getInstance("SyncWorker")
    private val MAX_RETRIES = 3
    private val BASE_BACKOFF_MS = 1000L

    override suspend fun doWork(): Result {
        val userId = preferencesManager.getUserId() ?: return Result.success()
        val authToken = preferencesManager.getAuthToken() ?: return Result.success()
        
        logger.info("Iniciando sincronização para usuário: $userId")
        
        return try {
            // 1. Push local changes to server
            pushLocalChanges(userId, authToken)
            
            // 2. Pull server changes
            pullServerChanges(userId, authToken)
            
            // 3. Update last sync time
            preferencesManager.setLastSyncTime(System.currentTimeMillis())
            
            logger.info("Sincronização concluída com sucesso")
            Result.success()
        } catch (e: Exception) {
            logger.error(e, "Erro na sincronização")
            val retryCount = inputData.getInt("retry_count", 0)
            if (retryCount < MAX_RETRIES) {
                val backoffMs = BASE_BACKOFF_MS * (2.0.pow(retryCount)).toLong()
                Result.retry().toBuilder().setBackoffCriteria(
                    androidx.work.BackoffPolicy.EXPONENTIAL,
                    backoffMs,
                    java.util.concurrent.TimeUnit.MILLISECONDS
                ).build()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun pushLocalChanges(userId: String, authToken: String) {
        val realm = Realm.getDefaultInstance()
        try {
            // Sync cycles
            val cyclesToSync = realm.where(CycleRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("deletedAt", 0L)
                .greaterThan("lastSyncedAt", 0L) // Only unsynced
                .findAll()
            
            for (cycleRealm in cyclesToSync) {
                val domain = CycleMapper.toDomain(cycleRealm)
                val request = if (cycleRealm.serverId.isNullOrBlank()) {
                    CycleMapper.toCreateRequest(domain)
                } else {
                    CycleMapper.toUpdateRequest(domain)
                }
                
                val response = if (cycleRealm.serverId.isNullOrBlank()) {
                    api.createCycle(request, "Bearer $authToken")
                } else {
                    api.updateCycle(cycleRealm.serverId, request, "Bearer $authToken")
                }
                
                if (response.isSuccessful) {
                    val serverCycle = CycleMapper.fromResponse(response.body()!!)
                    realm.executeTransaction { r ->
                        val managed = r.where(CycleRealm::class.java).equalTo("id", cycleRealm.id).findFirst()
                        managed?.apply {
                            serverId = serverCycle.serverId
                            lastSyncedAt = System.currentTimeMillis()
                            version = serverCycle.version
                        }
                    }
                } else {
                    logger.warn("Falha ao sincronizar ciclo ${cycleRealm.id}: ${response.code()}")
                    throw Exception("Sync failed for cycle: ${response.code()}")
                }
            }
            
            // Sync symptoms
            val symptomsToSync = realm.where(SymptomRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("deletedAt", 0L)
                .greaterThan("lastSyncedAt", 0L)
                .findAll()
            
            for (symptomRealm in symptomsToSync) {
                val domain = SymptomMapper.toDomain(symptomRealm)
                val request = if (symptomRealm.serverId.isNullOrBlank()) {
                    SymptomMapper.toCreateRequest(domain)
                } else {
                    SymptomMapper.toUpdateRequest(domain)
                }
                
                val response = if (symptomRealm.serverId.isNullOrBlank()) {
                    api.createSymptom(request, "Bearer $authToken")
                } else {
                    api.updateSymptom(symptomRealm.serverId, request, "Bearer $authToken")
                }
                
                if (response.isSuccessful) {
                    val serverSymptom = SymptomMapper.fromResponse(response.body()!!)
                    realm.executeTransaction { r ->
                        val managed = r.where(SymptomRealm::class.java).equalTo("id", symptomRealm.id).findFirst()
                        managed?.apply {
                            serverId = serverSymptom.serverId
                            lastSyncedAt = System.currentTimeMillis()
                            version = serverSymptom.version
                        }
                    }
                } else {
                    logger.warn("Falha ao sincronizar sintoma ${symptomRealm.id}: ${response.code()}")
                    throw Exception("Sync failed for symptom: ${response.code()}")
                }
            }
            
            // Sync profile
            val profileRealm = realm.where(UserProfileRealm::class.java)
                .equalTo("id", userId)
                .equalTo("deletedAt", 0L)
                .findFirst()
            
            profileRealm?.let { profile ->
                if (profile.lastSyncedAt < profile.updatedAt) {
                    val domain = UserProfileMapper.toDomain(profile)
                    val request = UserProfileMapper.toUpdateRequest(domain)
                    val response = api.updateProfile(userId, request, "Bearer $authToken")
                    
                    if (response.isSuccessful) {
                        val serverProfile = UserProfileMapper.fromResponse(response.body()!!)
                        realm.executeTransaction { r ->
                            val managed = r.where(UserProfileRealm::class.java).equalTo("id", userId).findFirst()
                            managed?.apply {
                                lastSyncedAt = System.currentTimeMillis()
                                version = serverProfile.version
                            }
                        }
                    } else {
                        logger.warn("Falha ao sincronizar perfil: ${response.code()}")
                        throw Exception("Sync failed for profile: ${response.code()}")
                    }
                }
            }
            
        } finally {
            realm.close()
        }
    }

    private suspend fun pullServerChanges(userId: String, authToken: String) {
        val lastSync = preferencesManager.lastSyncTime.firstOrNull() ?: 0L
        
        // Pull cycles
        val cyclesResponse = api.getCyclesSince(userId, lastSync, "Bearer $authToken")
        if (cyclesResponse.isSuccessful) {
            val cycles = CycleMapper.fromResponseList(cyclesResponse.body()!!)
            val realm = Realm.getDefaultInstance()
            try {
                realm.executeTransaction { r ->
                    cycles.forEach { cycle ->
                        val existing = r.where(CycleRealm::class.java).equalTo("id", cycle.id).findFirst()
                        if (existing != null) {
                            // Merge with local changes if needed
                            if (existing.updatedAt < cycle.updatedAt) {
                                val merged = existing.apply {
                                    // Keep local unsynced changes
                                    if (this.lastSyncedAt == 0L) {
                                        // Local has unsynced changes, don't overwrite
                                    } else {
                                        CycleMapper.toRealm(cycle).also { mapped ->
                                            this.serverId = mapped.serverId
                                            this.startDate = mapped.startDate
                                            this.endDate = mapped.endDate
                                            this.cycleLength = mapped.cycleLength
                                            this.periodLength = mapped.periodLength
                                            this.lutealPhaseLength = mapped.lutealPhaseLength
                                            this.ovulationDate = mapped.ovulationDate
                                            this.fertileWindowStart = mapped.fertileWindowStart
                                            this.fertileWindowEnd = mapped.fertileWindowEnd
                                            this.isPredicted = mapped.isPredicted
                                            this.confidenceScore = mapped.confidenceScore
                                            this.source = mapped.source
                                            this.notes = mapped.notes
                                            this.lastSyncedAt = System.currentTimeMillis()
                                            this.version = mapped.version
                                        }
                                    }
                                }
                            }
                        } else {
                            // New from server
                            val mapped = CycleMapper.toRealm(cycle)
                            mapped.lastSyncedAt = System.currentTimeMillis()
                            r.copyToRealm(mapped)
                        }
                    }
                }
            } finally {
                realm.close()
            }
        }
        
        // Pull symptoms
        val symptomsResponse = api.getSymptomsSince(userId, lastSync, "Bearer $authToken")
        if (symptomsResponse.isSuccessful) {
            val symptoms = SymptomMapper.fromResponseList(symptomsResponse.body()!!)
            val realm = Realm.getDefaultInstance()
            try {
                realm.executeTransaction { r ->
                    symptoms.forEach { symptom ->
                        val existing = r.where(SymptomRealm::class.java).equalTo("id", symptom.id).findFirst()
                        if (existing != null) {
                            if (existing.updatedAt < symptom.updatedAt) {
                                if (existing.lastSyncedAt == 0L) {
                                    // Keep local
                                } else {
                                    SymptomMapper.toRealm(symptom).also { mapped ->
                                        existing.serverId = mapped.serverId
                                        existing.date = mapped.date
                                        existing.category = mapped.category
                                        existing.name = mapped.name
                                        existing.severity = mapped.severity
                                        existing.notes = mapped.notes
                                        existing.source = mapped.source
                                        existing.lastSyncedAt = System.currentTimeMillis()
                                        existing.version = mapped.version
                                    }
                                }
                            }
                        } else {
                            val mapped = SymptomMapper.toRealm(symptom)
                            mapped.lastSyncedAt = System.currentTimeMillis()
                            r.copyToRealm(mapped)
                        }
                    }
                }
            } finally {
                realm.close()
            }
        }
        
        // Pull profile
        val profileResponse = api.getProfile(userId, "Bearer $authToken")
        if (profileResponse.isSuccessful) {
            val profile = UserProfileMapper.fromResponse(profileResponse.body()!!)
            val realm = Realm.getDefaultInstance()
            try {
                realm.executeTransaction { r ->
                    val existing = r.where(UserProfileRealm::class.java).equalTo("id", userId).findFirst()
                    if (existing != null && existing.updatedAt < profile.updatedAt) {
                        if (existing.lastSyncedAt > 0) {
                            val mapped = UserProfileMapper.toRealm(profile)
                            existing.lastSyncedAt = System.currentTimeMillis()
                            existing.version = mapped.version
                            // Update fields from server
                            existing.displayName = mapped.displayName
                            existing.email = mapped.email
                            existing.averageCycleLength = mapped.averageCycleLength
                            existing.averagePeriodLength = mapped.averagePeriodLength
                            existing.contraceptionMethod = mapped.contraceptionMethod
                            existing.lastPeriodDate = mapped.lastPeriodDate
                            existing.themeMode = mapped.themeMode
                            existing.language = mapped.language
                            existing.subscriptionStatus = mapped.subscriptionStatus
                            existing.subscriptionProductId = mapped.subscriptionProductId
                            existing.subscriptionExpiresAt = mapped.subscriptionExpiresAt
                            existing.subscriptionAutoRenew = mapped.subscriptionAutoRenew
                            existing.subscriptionPlatform = mapped.subscriptionPlatform
                        }
                    }
                }
            } finally {
                realm.close()
            }
        }
    }
}