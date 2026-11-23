package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de parceiro.
 */
@Serializable
data class PartnerConnectionResponse(
    val id: String,
    val userId: String,
    val partnerUserId: String,
    val invitationCode: String,
    val partnerEmail: String,
    val partnerDisplayName: String,
    val partnerAvatarUrl: String,
    val status: String,
    val invitedAt: Long,
    val acceptedAt: Long,
    val revokedAt: Long,
    val shareCycle: Boolean,
    val sharePeriodDates: Boolean,
    val shareOvulation: Boolean,
    val shareFertileWindow: Boolean,
    val sharePms: Boolean,
    val shareSymptoms: Boolean,
    val sharePredictions: Boolean,
    val sharePregnancy: Boolean,
    val shareInsights: Boolean,
    val shareMood: Boolean,
    val shareWeight: Boolean,
    val shareNotes: Boolean,
    val notifyPartnerPeriodStart: Boolean,
    val notifyPartnerPeriodEnd: Boolean,
    val notifyPartnerFertileWindow: Boolean,
    val notifyPartnerOvulation: Boolean,
    val notifyPartnerPms: Boolean,
    val notifyPartnerPregnancy: Boolean,
    val notifyPartnerSymptoms: Boolean,
    val partnerShareCycle: Boolean,
    val partnerSharePeriodDates: Boolean,
    val partnerShareOvulation: Boolean,
    val partnerShareFertileWindow: Boolean,
    val partnerSharePms: Boolean,
    val partnerShareSymptoms: Boolean,
    val partnerSharePredictions: Boolean,
    val partnerSharePregnancy: Boolean,
    val partnerShareInsights: Boolean,
    val partnerShareMood: Boolean,
    val partnerShareWeight: Boolean,
    val partnerShareNotes: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val serverId: String,
    val lastSyncedAt: Long,
    val version: Int
)

@Serializable
data class PartnerSharedDataResponse(
    val partnerId: String,
    val partnerDisplayName: String,
    val partnerAvatarUrl: String,
    val currentCycle: CycleSummaryResponse?,
    val nextPeriodDate: String?,
    val nextOvulationDate: String?,
    val fertileWindowStart: String?,
    val fertileWindowEnd: String?,
    val isInFertileWindow: Boolean,
    val isInPms: Boolean,
    val pregnancy: PregnancySummaryResponse?,
    val insights: List<String>,
    val lastUpdated: Long
)

@Serializable
data class CycleSummaryResponse(
    val startDate: String,
    val endDate: String?,
    val cycleLength: Int,
    val periodLength: Int
)

@Serializable
data class PregnancySummaryResponse(
    val dueDate: String,
    val currentWeek: Int,
    val currentDay: Int,
    val trimester: Int
)