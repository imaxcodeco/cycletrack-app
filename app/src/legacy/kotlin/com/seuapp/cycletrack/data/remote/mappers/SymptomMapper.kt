package com.seuapp.cycletrack.data.remote.mappers

import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.model.SymptomSeverity
import com.seuapp.cycletrack.data.remote.dto.request.SymptomRequests
import com.seuapp.cycletrack.data.remote.dto.response.SymptomResponses
import com.seuapp.cycletrack.data.local.realm.entities.SymptomRealm
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Instant
import import com.seuapp.cycletrack.util.toEpochMilliseconds

/**
 * Mapeadores para Sintomas entre Domain, Realm, DTOs Request/Response.
 * Clean-room - implementação própria.
 */
object SymptomMapper {

    // Domain <-> Realm
    fun toRealm(domain: Symptom): SymptomRealm = SymptomRealm().apply {
        id = domain.id
        userId = domain.userId
        cycleId = domain.cycleId
        date = domain.date.toEpochMilliseconds()
        category = domain.category
        name = domain.name
        severity = domain.severity.value
        notes = domain.notes
        source = domain.source
        createdAt = domain.createdAt
        updatedAt = domain.updatedAt
        serverId = domain.serverId
        lastSyncedAt = domain.lastSyncedAt
        version = domain.version
        deletedAt = 0L
    }

    fun toDomain(realm: SymptomRealm): Symptom = Symptom(
        id = realm.id,
        userId = realm.userId,
        cycleId = realm.cycleId,
        date = Instant.fromEpochMilliseconds(realm.date).toLocalDate(),
        category = realm.category,
        name = realm.name,
        severity = SymptomSeverity.valueOf(realm.severity),
        notes = realm.notes,
        source = realm.source,
        createdAt = realm.createdAt,
        updatedAt = realm.updatedAt,
        serverId = realm.serverId,
        lastSyncedAt = realm.lastSyncedAt,
        version = realm.version
    )

    // Domain -> Request DTO
    fun toCreateRequest(domain: Symptom): SymptomRequests.CreateSymptomRequest = SymptomRequests.CreateSymptomRequest(
        userId = domain.userId,
        cycleId = domain.cycleId,
        date = domain.date.toEpochMilliseconds(),
        category = domain.category,
        name = domain.name,
        severity = domain.severity.value,
        notes = domain.notes,
        source = domain.source
    )

    fun toUpdateRequest(domain: Symptom): SymptomRequests.UpdateSymptomRequest = SymptomRequests.UpdateSymptomRequest(
        id = domain.id,
        date = domain.date.toEpochMilliseconds(),
        category = domain.category,
        name = domain.name,
        severity = domain.severity.value,
        notes = domain.notes,
        version = domain.version
    )

    // Response DTO -> Domain
    fun fromResponse(response: SymptomResponses.SymptomResponse): Symptom = Symptom(
        id = response.id,
        userId = response.userId,
        cycleId = response.cycleId,
        date = Instant.fromEpochMilliseconds(response.date).toLocalDate(),
        category = response.category,
        name = response.name,
        severity = SymptomSeverity.valueOf(response.severity),
        notes = response.notes,
        source = response.source,
        createdAt = response.createdAt,
        updatedAt = response.updatedAt,
        serverId = response.id,
        lastSyncedAt = System.currentTimeMillis(),
        version = response.version
    )

    fun fromResponseList(responses: List<SymptomResponses.SymptomResponse>): List<Symptom> =
        responses.map { fromResponse(it) }
}