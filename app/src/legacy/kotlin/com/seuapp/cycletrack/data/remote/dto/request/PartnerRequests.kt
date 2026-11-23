package com.seuapp.cycletrack.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * DTOs para requisições de parceiro.
 */
@Serializable
data class InvitePartnerRequest(
    val partnerEmail: String,
    val partnerDisplayName: String = "",
    val shareLevel: String = "full" // full, cycle_only, fertility_only, insights_only
)

@Serializable
data class AcceptInvitationRequest(
    val invitationCode: String,
    val partnerDisplayName: String,
    val partnerAvatarUrl: String = ""
)

@Serializable
data class DeclineInvitationRequest(
    val invitationCode: String
)

@Serializable
data class UpdateSharingPreferencesRequest(
    val shareCycle: Boolean? = null,
    val sharePeriodDates: Boolean? = null,
    val shareOvulation: Boolean? = null,
    val shareFertileWindow: Boolean? = null,
    val sharePms: Boolean? = null,
    val shareSymptoms: Boolean? = null,
    val sharePredictions: Boolean? = null,
    val sharePregnancy: Boolean? = null,
    val shareInsights: Boolean? = null,
    val shareMood: Boolean? = null,
    val shareWeight: Boolean? = null,
    val shareNotes: Boolean? = null
)

@Serializable
data class UpdatePartnerNotificationsRequest(
    val notifyPeriodStart: Boolean? = null,
    val notifyPeriodEnd: Boolean? = null,
    val notifyFertileWindow: Boolean? = null,
    val notifyOvulation: Boolean? = null,
    val notifyPms: Boolean? = null,
    val notifyPregnancy: Boolean? = null,
    val notifySymptoms: Boolean? = null
)