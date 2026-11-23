package com.seuapp.cycletrack.domain.usecase.partner

import com.seuapp.cycletrack.domain.model.PartnerConnection
import com.seuapp.cycletrack.domain.model.PartnerSharedData
import com.seuapp.cycletrack.domain.model.PartnerShareLevel
import com.seuapp.cycletrack.domain.model.ConnectionStatus
import com.seuapp.cycletrack.domain.repository.PartnerRepository

/**
 * Casos de uso para Modo Parceiro.
 * Clean-room - gerencia convites, conexões e compartilhamento.
 */
class PartnerUseCases(
    private val partnerRepository: PartnerRepository
) {

    /**
     * Cria convite para parceiro.
     */
    suspend fun invitePartner(
        userId: String,
        partnerEmail: String,
        partnerDisplayName: String = "",
        shareLevel: PartnerShareLevel = PartnerShareLevel.FULL
    ): PartnerConnection {
        // Valida email
        if (!partnerEmail.contains("@")) {
            throw IllegalArgumentException("Email inválido")
        }
        
        // Verifica se já existe convite pendente para este email
        val existing = partnerRepository.getPendingInvitationsReceived(partnerEmail)
            .firstOrNull { it.status == ConnectionStatus.PENDING }
        if (existing != null) {
            throw IllegalStateException("Já existe convite pendente para este email")
        }

        return partnerRepository.createInvitation(
            userId = userId,
            partnerEmail = partnerEmail.lowercase(),
            partnerDisplayName = partnerDisplayName,
            shareLevel = shareLevel
        )
    }

    /**
     * Aceita convite de parceiro.
     */
    suspend fun acceptInvitation(
        invitationCode: String,
        partnerUserId: String,
        partnerDisplayName: String,
        partnerAvatarUrl: String = ""
    ): PartnerConnection? {
        return partnerRepository.acceptInvitation(
            invitationCode = invitationCode,
            partnerUserId = partnerUserId,
            partnerDisplayName = partnerDisplayName,
            partnerAvatarUrl = partnerAvatarUrl
        )
    }

    /**
     * Recusa convite de parceiro.
     */
    suspend fun declineInvitation(invitationCode: String): Boolean {
        return partnerRepository.declineInvitation(invitationCode)
    }

    /**
     * Obtém conexão ativa com parceiro.
     */
    suspend fun getActivePartner(userId: String): PartnerConnection? {
        return partnerRepository.getActivePartner(userId)
    }

    /**
     * Obtém dados compartilhados do parceiro para exibição.
     */
    suspend fun getPartnerSharedData(userId: String): PartnerSharedData? {
        val connection = partnerRepository.getActivePartner(userId)
        return connection?.let { partnerRepository.getPartnerSharedData(it.id) }
    }

    /**
     * Obtém todas as conexões do usuário.
     */
    suspend fun getPartners(userId: String): List<PartnerConnection> {
        return partnerRepository.getPartners(userId)
    }

    /**
     * Obtém convites pendentes enviados.
     */
    suspend fun getPendingInvitationsSent(userId: String): List<PartnerConnection> {
        return partnerRepository.getPendingInvitationsSent(userId)
    }

    /**
     * Obtém convites pendentes recebidos.
     */
    suspend fun getPendingInvitationsReceived(email: String): List<PartnerConnection> {
        return partnerRepository.getPendingInvitationsReceived(email.lowercase())
    }

    /**
     * Revoga conexão com parceiro.
     */
    suspend fun revokePartner(userId: String, partnerId: String): Boolean {
        return partnerRepository.revokePartner(userId, partnerId)
    }

    /**
     * Atualiza o que este usuário compartilha com o parceiro.
     */
    suspend fun updateMySharingPreferences(
        partnerId: String,
        shareCycle: Boolean? = null,
        sharePeriodDates: Boolean? = null,
        shareOvulation: Boolean? = null,
        shareFertileWindow: Boolean? = null,
        sharePms: Boolean? = null,
        shareSymptoms: Boolean? = null,
        sharePredictions: Boolean? = null,
        sharePregnancy: Boolean? = null,
        shareInsights: Boolean? = null,
        shareMood: Boolean? = null,
        shareWeight: Boolean? = null,
        shareNotes: Boolean? = null
    ): Boolean {
        return partnerRepository.updateSharingPreferences(
            partnerId = partnerId,
            shareCycle = shareCycle,
            sharePeriodDates = sharePeriodDates,
            shareOvulation = shareOvulation,
            shareFertileWindow = shareFertileWindow,
            sharePms = sharePms,
            shareSymptoms = shareSymptoms,
            sharePredictions = sharePredictions,
            sharePregnancy = sharePregnancy,
            shareInsights = shareInsights,
            shareMood = shareMood,
            shareWeight = shareWeight,
            shareNotes = shareNotes
        )
    }

    /**
     * Atualiza notificações para o parceiro.
     */
    suspend fun updatePartnerNotifications(
        partnerId: String,
        notifyPeriodStart: Boolean? = null,
        notifyPeriodEnd: Boolean? = null,
        notifyFertileWindow: Boolean? = null,
        notifyOvulation: Boolean? = null,
        notifyPms: Boolean? = null,
        notifyPregnancy: Boolean? = null,
        notifySymptoms: Boolean? = null
    ): Boolean {
        return partnerRepository.updateNotificationPreferences(
            partnerId = partnerId,
            notifyPeriodStart = notifyPeriodStart,
            notifyPeriodEnd = notifyPeriodEnd,
            notifyFertileWindow = notifyFertileWindow,
            notifyOvulation = notifyOvulation,
            notifyPms = notifyPms,
            notifyPregnancy = notifyPregnancy,
            notifySymptoms = notifySymptoms
        )
    }

    /**
     * Atualiza o que o parceiro compartilha (preferências recebidas).
     */
    suspend fun updatePartnerSharingPreferences(
        partnerId: String,
        shareCycle: Boolean? = null,
        sharePeriodDates: Boolean? = null,
        shareOvulation: Boolean? = null,
        shareFertileWindow: Boolean? = null,
        sharePms: Boolean? = null,
        shareSymptoms: Boolean? = null,
        sharePredictions: Boolean? = null,
        sharePregnancy: Boolean? = null,
        shareInsights: Boolean? = null,
        shareMood: Boolean? = null,
        shareWeight: Boolean? = null,
        shareNotes: Boolean? = null
    ): Boolean {
        return partnerRepository.updatePartnerSharingPreferences(
            partnerId = partnerId,
            shareCycle = shareCycle,
            sharePeriodDates = sharePeriodDates,
            shareOvulation = shareOvulation,
            shareFertileWindow = shareFertileWindow,
            sharePms = sharePms,
            shareSymptoms = shareSymptoms,
            sharePredictions = sharePredictions,
            sharePregnancy = sharePregnancy,
            shareInsights = shareInsights,
            shareMood = shareMood,
            shareWeight = shareWeight,
            shareNotes = shareNotes
        )
    }

    /**
     * Atualiza informações de exibição do parceiro.
     */
    suspend fun updatePartnerDisplayInfo(
        partnerId: String,
        displayName: String? = null,
        avatarUrl: String? = null
    ): Boolean {
        return partnerRepository.updatePartnerDisplayInfo(
            partnerId = partnerId,
            displayName = displayName,
            avatarUrl = avatarUrl
        )
    }

    /**
     * Define nível de compartilhamento pré-definido.
     */
    suspend fun setShareLevel(partnerId: String, level: PartnerShareLevel): Boolean {
        return when (level) {
            PartnerShareLevel.FULL -> updateMySharingPreferences(partnerId)
            PartnerShareLevel.CYCLE_ONLY -> updateMySharingPreferences(
                partnerId,
                shareOvulation = false, shareFertileWindow = false, sharePms = false,
                shareSymptoms = false, sharePredictions = false, sharePregnancy = false,
                shareInsights = false, shareMood = false, shareWeight = false, shareNotes = false
            )
            PartnerShareLevel.FERTILITY_ONLY -> updateMySharingPreferences(
                partnerId,
                shareCycle = false, sharePeriodDates = false, sharePms = false,
                shareSymptoms = false, sharePregnancy = false, shareInsights = false,
                shareMood = false, shareWeight = false, shareNotes = false
            )
            PartnerShareLevel.INSIGHTS_ONLY -> updateMySharingPreferences(
                partnerId,
                shareCycle = false, sharePeriodDates = false, shareOvulation = false,
                shareFertileWindow = false, sharePms = false, shareSymptoms = false,
                sharePredictions = false, sharePregnancy = false, shareMood = false,
                shareWeight = false, shareNotes = false
            )
        }
    }

    /**
     * Deleta conexão (soft delete).
     */
    suspend fun deletePartner(partnerId: String): Boolean {
        return partnerRepository.deletePartner(partnerId)
    }

    /**
     * Hard delete conexão (LGPD/GDPR).
     */
    suspend fun hardDeletePartner(partnerId: String): Boolean {
        return partnerRepository.hardDeletePartner(partnerId)
    }

    /**
     * Verifica se convite expirou.
     */
    suspend fun isInvitationExpired(invitationCode: String): Boolean {
        return partnerRepository.isInvitationExpired(invitationCode)
    }

    /**
     * Obtém conexão para exportação (LGPD/GDPR).
     */
    suspend fun getPartnerForExport(userId: String, partnerId: String, anonymize: Boolean): PartnerConnection? {
        return partnerRepository.getPartnerForExport(userId, partnerId, anonymize)
    }
}