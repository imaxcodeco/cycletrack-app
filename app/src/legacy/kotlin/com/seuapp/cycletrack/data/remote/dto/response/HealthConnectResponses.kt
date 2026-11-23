package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de Health Connect.
 */
@Serializable
data class SyncHealthDataResponse(
    val synced: Int,
    val conflicts: Int,
    val errors: Int,
    val conflictDetails: List<ConflictDetail>
)

@Serializable
data class ConflictDetail(
    val localRecordId: String,
    val remoteRecordId: String,
    val dataType: String,
    val resolution: String // local_wins, remote_wins, merge
)

@Serializable
data class HealthConnectPermissionsResponse(
    val grantedPermissions: List<String>,
    val availablePermissions: List<String>
)