package com.seuapp.cycletrack.data.remote.mappers

import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.data.remote.dto.request.CycleRequests
import com.seuapp.cycletrack.data.remote.dto.response.CycleResponses
import com.seuapp.cycletrack.data.local.realm.entities.CycleRealm
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Instant
import import com.seuapp.cycletrack.util.toEpochMilliseconds

/**
 * Mapeadores para Ciclos entre Domain, Realm, DTOs Request/Response.
 * Clean-room - implementação própria.
 */
object CycleMapper {

    // Domain <-> Realm
    fun toRealm(domain: Cycle): CycleRealm = CycleRealm().apply {
        id = domain.id
        userId = domain.userId
        startDate = domain.startDate.toEpochMilliseconds()
        endDate = domain.endDate?.toEpochMilliseconds()
        cycleLength = domain.cycleLength
        periodLength = domain.periodLength
        lutealPhaseLength = domain.lutealPhaseLength
        ovulationDate = domain.ovulationDate?.toEpochMilliseconds()
        fertileWindowStart = domain.fertileWindowStart?.toEpochMilliseconds()
        fertileWindowEnd = domain.fertileWindowEnd?.toEpochMilliseconds()
        isPredicted = domain.isPredicted
        confidenceScore = domain.confidenceScore
        source = domain.source
        notes = domain.notes
        createdAt = domain.createdAt
        updatedAt = domain.updatedAt
        serverId = domain.serverId
        lastSyncedAt = domain.lastSyncedAt
        version = domain.version
        deletedAt = 0L
    }

    fun toDomain(realm: CycleRealm): Cycle = Cycle(
        id = realm.id,
        userId = realm.userId,
        startDate = Instant.fromEpochMilliseconds(realm.startDate).toLocalDate(),
        endDate = realm.endDate?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        cycleLength = realm.cycleLength,
        periodLength = realm.periodLength,
        lutealPhaseLength = realm.lutealPhaseLength,
        ovulationDate = realm.ovulationDate?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        fertileWindowStart = realm.fertileWindowStart?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        fertileWindowEnd = realm.fertileWindowEnd?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        isPredicted = realm.isPredicted,
        confidenceScore = realm.confidenceScore,
        source = realm.source,
        notes = realm.notes,
        createdAt = realm.createdAt,
        updatedAt = realm.updatedAt,
        serverId = realm.serverId,
        lastSyncedAt = realm.lastSyncedAt,
        version = realm.version
    )

    // Domain -> Request DTO
    fun toCreateRequest(domain: Cycle): CycleRequests.CreateCycleRequest = CycleRequests.CreateCycleRequest(
        userId = domain.userId,
        startDate = domain.startDate.toEpochMilliseconds(),
        endDate = domain.endDate?.toEpochMilliseconds(),
        cycleLength = domain.cycleLength,
        periodLength = domain.periodLength,
        lutealPhaseLength = domain.lutealPhaseLength,
        ovulationDate = domain.ovulationDate?.toEpochMilliseconds(),
        fertileWindowStart = domain.fertileWindowStart?.toEpochMilliseconds(),
        fertileWindowEnd = domain.fertileWindowEnd?.toEpochMilliseconds(),
        isPredicted = domain.isPredicted,
        confidenceScore = domain.confidenceScore,
        source = domain.source,
        notes = domain.notes
    )

    fun toUpdateRequest(domain: Cycle): CycleRequests.UpdateCycleRequest = CycleRequests.UpdateCycleRequest(
        id = domain.id,
        startDate = domain.startDate.toEpochMilliseconds(),
        endDate = domain.endDate?.toEpochMilliseconds(),
        cycleLength = domain.cycleLength,
        periodLength = domain.periodLength,
        lutealPhaseLength = domain.lutealPhaseLength,
        ovulationDate = domain.ovulationDate?.toEpochMilliseconds(),
        fertileWindowStart = domain.fertileWindowStart?.toEpochMilliseconds(),
        fertileWindowEnd = domain.fertileWindowEnd?.toEpochMilliseconds(),
        isPredicted = domain.isPredicted,
        confidenceScore = domain.confidenceScore,
        notes = domain.notes,
        version = domain.version
    )

    // Response DTO -> Domain
    fun fromResponse(response: CycleResponses.CycleResponse): Cycle = Cycle(
        id = response.id,
        userId = response.userId,
        startDate = Instant.fromEpochMilliseconds(response.startDate).toLocalDate(),
        endDate = response.endDate?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        cycleLength = response.cycleLength,
        periodLength = response.periodLength,
        lutealPhaseLength = response.lutealPhaseLength,
        ovulationDate = response.ovulationDate?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        fertileWindowStart = response.fertileWindowStart?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        fertileWindowEnd = response.fertileWindowEnd?.let { Instant.fromEpochMilliseconds(it).toLocalDate() },
        isPredicted = response.isPredicted,
        confidenceScore = response.confidenceScore,
        source = response.source,
        notes = response.notes,
        createdAt = response.createdAt,
        updatedAt = response.updatedAt,
        serverId = response.id,
        lastSyncedAt = System.currentTimeMillis(),
        version = response.version
    )

    fun fromResponseList(responses: List<CycleResponses.CycleResponse>): List<Cycle> =
        responses.map { fromResponse(it) }
}