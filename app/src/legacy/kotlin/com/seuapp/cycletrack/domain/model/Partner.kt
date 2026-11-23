package com.seuapp.cycletrack.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

/**
 * Modelo de domínio para Conexão com Parceiro.
 * Clean-room com controle granular de compartilhamento.
 */
@Serializable
data class PartnerConnection(
    val id: String,
    val userId: String, // Usuário que possui esta conexão
    val partnerUserId: String, // ID do parceiro
    val invitationCode: String,
    val partnerEmail: String,
    val partnerDisplayName: String = "",
    val partnerAvatarUrl: String = "",
    val status: ConnectionStatus = ConnectionStatus.PENDING,
    val invitedAt: Long = 0L,
    val acceptedAt: Long = 0L,
    val revokedAt: Long = 0L,

    // O que ESTE usuário compartilha COM o parceiro
    val shareCycle: Boolean = true,
    val sharePeriodDates: Boolean = true,
    val shareOvulation: Boolean = true,
    val shareFertileWindow: Boolean = true,
    val sharePms: Boolean = true,
    val shareSymptoms: Boolean = true,
    val sharePredictions: Boolean = true,
    val sharePregnancy: Boolean = false,
    val shareInsights: Boolean = true,
    val shareMood: Boolean = true,
    val shareWeight: Boolean = false,
    val shareNotes: Boolean = false,

    // Notificações para o parceiro
    val notifyPartnerPeriodStart: Boolean = true,
    val notifyPartnerPeriodEnd: Boolean = false,
    val notifyPartnerFertileWindow: Boolean = true,
    val notifyPartnerOvulation: Boolean = true,
    val notifyPartnerPms: Boolean = false,
    val notifyPartnerPregnancy: Boolean = true,
    val notifyPartnerSymptoms: Boolean = false,

    // O que o PARCEIRO compartilha com ESTE usuário
    val partnerShareCycle: Boolean = true,
    val partnerSharePeriodDates: Boolean = true,
    val partnerShareOvulation: Boolean = true,
    val partnerShareFertileWindow: Boolean = true,
    val partnerSharePms: Boolean = true,
    val partnerShareSymptoms: Boolean = true,
    val partnerSharePredictions: Boolean = true,
    val partnerSharePregnancy: Boolean = false,
    val partnerShareInsights: Boolean = true,
    val partnerShareMood: Boolean = true,
    val partnerShareWeight: Boolean = false,
    val partnerShareNotes: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: String = "",
    val lastSyncedAt: Long = 0L,
    val version: Int = 1
) {
    companion object {
        const val INVITATION_CODE_LENGTH = 8
        const val INVITATION_EXPIRY_DAYS = 7

        fun createInvitation(
            userId: String,
            partnerEmail: String,
            partnerDisplayName: String = "",
            shareLevel: PartnerShareLevel = PartnerShareLevel.FULL
        ): PartnerConnection {
            return PartnerConnection(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                partnerUserId = "",
                invitationCode = generateInvitationCode(),
                partnerEmail = partnerEmail,
                partnerDisplayName = partnerDisplayName,
                status = ConnectionStatus.PENDING,
                invitedAt = System.currentTimeMillis(),
                partnerShareLevel = shareLevel
            ).applyShareLevelDefaults(shareLevel)
        }

        private fun generateInvitationCode(): String {
            val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // Exclui chars confusos
            val random = java.security.SecureRandom()
            return (1..INVITATION_CODE_LENGTH)
                .map { chars[random.nextInt(chars.length)] }
                .joinToString("")
        }

        private fun PartnerConnection.applyShareLevelDefaults(level: PartnerShareLevel): PartnerConnection {
            return when (level) {
                PartnerShareLevel.FULL -> this
                PartnerShareLevel.CYCLE_ONLY -> this.copy(
                    shareOvulation = false, shareFertileWindow = false, sharePms = false,
                    shareSymptoms = false, sharePredictions = false, sharePregnancy = false,
                    shareInsights = false, shareMood = false, shareWeight = false, shareNotes = false,
                    notifyPartnerFertileWindow = false, notifyPartnerOvulation = false,
                    notifyPartnerPms = false, notifyPartnerPregnancy = false, notifyPartnerSymptoms = false
                )
                PartnerShareLevel.FERTILITY_ONLY -> this.copy(
                    shareCycle = false, sharePeriodDates = false, sharePms = false,
                    shareSymptoms = false, sharePregnancy = false, shareInsights = false,
                    shareMood = false, shareWeight = false, shareNotes = false,
                    notifyPartnerPeriodStart = false, notifyPartnerPeriodEnd = false,
                    notifyPartnerPms = false, notifyPartnerPregnancy = false, notifyPartnerSymptoms = false
                )
                PartnerShareLevel.INSIGHTS_ONLY -> this.copy(
                    shareCycle = false, sharePeriodDates = false, shareOvulation = false,
                    shareFertileWindow = false, sharePms = false, shareSymptoms = false,
                    sharePredictions = false, sharePregnancy = false, shareMood = false,
                    shareWeight = false, shareNotes = false,
                    notifyPartnerPeriodStart = false, notifyPartnerPeriodEnd = false,
                    notifyPartnerFertileWindow = false, notifyPartnerOvulation = false,
                    notifyPartnerPms = false, notifyPartnerPregnancy = false, notifyPartnerSymptoms = false
                )
            }
        }
    }

    val isActive: Boolean
        get() = status == ConnectionStatus.ACCEPTED

    val isPending: Boolean
        get() = status == ConnectionStatus.PENDING

    val canShareWithPartner: Boolean
        get() = isActive

    val invitationExpired: Boolean
        get() = if (invitedAt > 0) {
            System.currentTimeMillis() - invitedAt > INVITATION_EXPIRY_DAYS * 24 * 60 * 60 * 1000L
        } else false
}

enum class ConnectionStatus(val key: String, val label: String) {
    PENDING("pending", "Pendente"),
    ACCEPTED("accepted", "Aceito"),
    DECLINED("declined", "Recusado"),
    REVOKED("revoked", "Revogado"),
    EXPIRED("expired", "Expirado")
}

enum class PartnerShareLevel(val key: String, val label: String, val description: String) {
    FULL("full", "Completo", "Compartilha tudo: ciclo, fertilidade, sintomas, insights, gravidez"),
    CYCLE_ONLY("cycle_only", "Apenas Ciclo", "Compartilha apenas datas de menstruação e duração do ciclo"),
    FERTILITY_ONLY("fertility_only", "Apenas Fertilidade", "Compartilha janela fértil, ovulação e previsões"),
    INSIGHTS_ONLY("insights_only", "Apenas Insights", "Compartilha apenas insights e resumos sem detalhes")
}

/**
 * Dados compartilhados do parceiro (view para UI).
 */
@Serializable
data class PartnerSharedData(
    val partnerId: String,
    val partnerDisplayName: String,
    val partnerAvatarUrl: String = "",
    val currentCycle: Cycle? = null,
    val nextPeriodDate: LocalDate? = null,
    val nextOvulationDate: LocalDate? = null,
    val fertileWindowStart: LocalDate? = null,
    val fertileWindowEnd: LocalDate? = null,
    val isInFertileWindow: Boolean = false,
    val isInPms: Boolean = false,
    val pregnancy: Pregnancy? = null,
    val insights: List<String> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
)