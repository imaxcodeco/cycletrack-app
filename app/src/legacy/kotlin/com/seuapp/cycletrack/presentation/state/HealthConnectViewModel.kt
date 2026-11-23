package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.HealthDataPoint
import com.seuapp.cycletrack.domain.usecase.healthconnect.HealthConnectUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

/**
 * ViewModel para integração com Health Connect.
 * Gerencia permissões, sincronização bidirecional e conflitos.
 */
@HiltViewModel
class HealthConnectViewModel @Inject constructor(
    private val healthConnectUseCases: HealthConnectUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    private val _isAvailable = MutableStateFlow(false)
    val isAvailable = _isAvailable.asStateFlow()

    private val _isEnabled = MutableStateFlow(false)
    val isEnabled = _isEnabled.asStateFlow()

    private val _permissionsGranted = MutableStateFlow<Set<String>>(emptySet())
    val permissionsGranted = _permissionsGranted.asStateFlow()

    private val _requiredPermissions = MutableStateFlow<List<PermissionInfo>>(emptyList())
    val requiredPermissions = _requiredPermissions.asStateFlow()

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.IDLE)
    val syncStatus = _syncStatus.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long>(0)
    val lastSyncTime = _lastSyncTime.asStateFlow()

    private val _pendingToSync = MutableStateFlow<List<HealthDataPoint>>(emptyList())
    val pendingToSync = _pendingToSync.asStateFlow()

    private val _conflicts = MutableStateFlow<List<ConflictInfo>>(emptyList())
    val conflicts = _conflicts.asStateFlow()

    private val _syncStats = MutableStateFlow<SyncStats?>(null)
    val syncStats = _syncStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // ============ Data Classes ============

    data class PermissionInfo(
        val dataType: String,
        val displayName: String,
        val description: String,
        val icon: String,
        val isRequired: Boolean,
        val isGranted: Boolean = false
    )

    sealed class SyncStatus {
        object IDLE : SyncStatus()
        data class SYNCING(val progress: Int) : SyncStatus()
        data class SUCCESS(val syncedCount: Int, val timestamp: Long) : SyncStatus()
        data class ERROR(val message: String) : SyncStatus()
        data class CONFLICTS(val count: Int) : SyncStatus()
    }

    data class ConflictInfo(
        val dataPointId: String,
        val dataType: String,
        val localValue: String,
        val remoteValue: String,
        val localTimestamp: Long,
        val remoteTimestamp: Long
    )

    data class SyncStats(
        val totalSynced: Int,
        val lastSyncDurationMs: Long,
        val conflictsResolved: Int,
        val errorsCount: Int
    )

    // ============ Actions ============

    init {
        checkAvailability()
        loadHealthConnectState()
    }

    fun checkAvailability() {
        viewModelScope.launch {
            try {
                val available = healthConnectUseCases.isHealthConnectAvailable()
                _isAvailable.value = available
                
                if (available) {
                    val granted = healthConnectUseCases.getGrantedPermissions()
                    _permissionsGranted.value = granted
                    loadRequiredPermissions()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao verificar Health Connect"
            }
        }
    }

    fun loadHealthConnectState() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                
                _isEnabled.value = preferencesManager.healthConnectEnabled.first()
                
                val stats = healthConnectUseCases.getSyncStats(userId)
                _syncStats.value = stats
                
                val lastSync = preferencesManager.lastSyncTime.first()
                _lastSyncTime.value = lastSync
                
                val pending = healthConnectUseCases.getPendingToSync(userId)
                _pendingToSync.value = pending
                
                val conflictList = healthConnectUseCases.getConflicts(userId)
                _conflicts.value = conflictList
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar estado"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadRequiredPermissions() {
        val permissions = listOf(
            PermissionInfo(
                dataType = "menstruation",
                displayName = "Menstruação",
                description = "Datas de início/fim e fluxo",
                icon = "🩸",
                isRequired = true,
                isGranted = _permissionsGranted.value.contains("menstruation")
            ),
            PermissionInfo(
                dataType = "basal_body_temperature",
                displayName = "Temperatura basal",
                description = "Temperatura corporal em repouso",
                icon = "🌡️",
                isRequired = false,
                isGranted = _permissionsGranted.value.contains("basal_body_temperature")
            ),
            PermissionInfo(
                dataType = "ovulation_test",
                displayName = "Teste de ovulação",
                description = "Resultados de testes de LH",
                icon = "🥚",
                isRequired = false,
                isGranted = _permissionsGranted.value.contains("ovulation_test")
            ),
            PermissionInfo(
                dataType = "weight",
                displayName = "Peso corporal",
                description = "Medições de peso",
                icon = "⚖️",
                isRequired = false,
                isGranted = _permissionsGranted.value.contains("weight")
            ),
            PermissionInfo(
                dataType = "cervical_mucus",
                displayName = "Muco cervical",
                description = "Observações de corrimento",
                icon = "💧",
                isRequired = false,
                isGranted = _permissionsGranted.value.contains("cervical_mucus")
            )
        )
        
        _requiredPermissions.value = permissions
    }

    suspend fun requestPermissions(): Boolean {
        _isLoading.value = true
        return try {
            val requiredTypes = _requiredPermissions.value
                .filter { it.isRequired || it.isGranted }
                .map { it.dataType }
            
            val granted = healthConnectUseCases.requestPermissions(requiredTypes)
            _permissionsGranted.value = granted
            loadRequiredPermissions()
            
            val allRequiredGranted = _requiredPermissions.value
                .filter { it.isRequired }
                .all { it.dataType in granted }
            
            if (allRequiredGranted) {
                enableHealthConnect()
            }
            
            allRequiredGranted
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao solicitar permissões"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun enableHealthConnect(): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            
            healthConnectUseCases.enableSync(userId)
            preferencesManager.setHealthConnectEnabled(true)
            _isEnabled.value = true
            
            // Primeira sincronização
            syncNow()
            
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao ativar Health Connect"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun disableHealthConnect(): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            
            healthConnectUseCases.disableSync(userId)
            preferencesManager.setHealthConnectEnabled(false)
            _isEnabled.value = false
            
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao desativar Health Connect"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun syncNow(): Boolean {
        _syncStatus.value = SyncStatus.SYNCING(0)
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            
            val result = healthConnectUseCases.syncNow(userId)
            
            _syncStatus.value = SyncStatus.SUCCESS(result.syncedCount, System.currentTimeMillis())
            _lastSyncTime.value = System.currentTimeMillis()
            preferencesManager.setLastSyncTime(System.currentTimeMillis())
            
            loadHealthConnectState()
            
            true
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.ERROR(e.message ?: "Erro na sincronização")
            false
        }
    }

    suspend fun resolveConflictLocalWins(conflictId: String): Boolean {
        return try {
            healthConnectUseCases.resolveConflictLocalWins(conflictId)
            loadHealthConnectState()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao resolver conflito"
            false
        }
    }

    suspend fun resolveConflictRemoteWins(conflictId: String): Boolean {
        return try {
            healthConnectUseCases.resolveConflictRemoteWins(conflictId)
            loadHealthConnectState()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao resolver conflito"
            false
        }
    }

    suspend fun resolveAllConflictsLocalWins(): Boolean {
        return try {
            healthConnectUseCases.resolveAllConflictsLocalWins()
            loadHealthConnectState()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao resolver conflitos"
            false
        }
    }

    fun clearError() {
        _error.value = null
    }
}