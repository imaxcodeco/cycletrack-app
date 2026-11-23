package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.PartnerConnection
import com.seuapp.cycletrack.domain.model.PartnerShareLevel
import com.seuapp.cycletrack.domain.model.PartnerSharedData
import com.seuapp.cycletrack.domain.model.ConnectionStatus

/**
 * Interface do repositório de Conexão com Parceiro.
 * Clean-room - define contrato sem expor implementação.
 */
interface PartnerRepository {

    /**
     * Obtém conexão ativa com parceiro.
     */
    suspend fun getActivePartner(userId: String): PartnerConnection?

    /**
     * Obtém todas as conexões de parceiro do usuário.
     */
    suspend fun getPartners(userId: String): List<PartnerConnection>

    /**
     * Obtém conexão por ID.
     */
    suspend fun getPartner(partnerId: String): PartnerConnection?

    /**
     * Obtém conexão por código de convite.
     */
    suspend fun getPartnerByInvitationCode(code: String): PartnerConnection?

    /**
     * Obtém convites pendentes enviados pelo usuário.
     */
    suspend fun getPendingInvitationsSent(userId: String): List<PartnerConnection>

    /**
     * Obtém convites pendentes recebidos pelo email.
     */
    suspend fun getPendingInvitationsReceived(email: String): List<PartnerConnection>

    /**
     * Insere ou atualiza conexão.
     */
    suspend fun upsertPartner(partner: PartnerConnection): PartnerConnection

    /**
     * Cria novo convite de parceiro.
     */
    suspend fun createInvitation(
        userId: String,
        partnerEmail: String,
        partnerDisplayName: String = "",
        shareLevel: PartnerShareLevel = PartnerShareLevel.FULL
    ): PartnerConnection

    /**
     * Aceita convite de parceiro.
     */
    suspend fun acceptInvitation(
        invitationCode: String,
        partnerUserId: String,
        partnerDisplayName: String,
        partnerAvatarUrl: String = ""
    ): PartnerConnection?

    /**
     * Recusa convite de parceiro.
     */
    suspend fun declineInvitation(invitationCode: String): Boolean

    /**
     * Revoga conexão com parceiro.
     */
    suspend fun revokePartner(userId: String, partnerId: String): Boolean

    /**
     * Atualiza preferências de compartilhamento (o que este usuário compartilha).
     */
    suspend fun updateSharingPreferences(
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
    ): Boolean

    /**
     * Atualiza preferências de notificação para o parceiro.
     */
    suspend fun updateNotificationPreferences(
        partnerId: String,
        notifyPeriodStart: Boolean? = null,
        notifyPeriodEnd: Boolean? = null,
        notifyFertileWindow: Boolean? = null,
        notifyOvulation: Boolean? = null,
        notifyPms: Boolean? = null,
        notifyPregnancy: Boolean? = null,
        notifySymptoms: Boolean? = null
    ): Boolean

    /**
     * Atualiza preferências de compartilhamento do parceiro (o que parceiro compartilha).
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
    ): Boolean

    /**
     * Atualiza informações de exibição do parceiro.
     */
    suspend fun updatePartnerDisplayInfo(
        partnerId: String,
        displayName: String? = null,
        avatarUrl: String? = null
    ): Boolean

    /**
     * Soft delete.
     */
    suspend fun deletePartner(partnerId: String): Boolean

    /**
     * Hard delete (LGPD/GDPR).
     */
    suspend fun hardDeletePartner(partnerId: String): Boolean

    /**
     * Obtém dados compartilhados do parceiro para exibição na UI.
     */
    suspend fun getPartnerSharedData(partnerId: String): PartnerSharedData?

    /**
     * Verifica se convite expirou.
     */
    suspend fun isInvitationExpired(invitationCode: String): Boolean

    /**
     * Obtém conexão para exportação (LGPD/GDPR).
     */
    suspend fun getPartnerForExport(userId: String, partnerId: String, anonymize: Boolean): PartnerConnection?
}