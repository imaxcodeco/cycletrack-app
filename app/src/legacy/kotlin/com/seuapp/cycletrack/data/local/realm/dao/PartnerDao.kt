package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.PartnerRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.seuapp.cycletrack.data.local.realm.detached

/**
 * DAO for PartnerRealm operations.
 * Clean-room implementation for partner sharing mode.
 */
class PartnerDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get active partner connection for a user.
     */
    suspend fun getActivePartner(userId: String): PartnerRealm? = withContext(Dispatchers.IO) {
        realm.where(PartnerRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("status", PartnerRealm.STATUS_ACCEPTED)
            .findFirst()
            .detached()
    }

    /**
     * Get all partner connections for a user.
     */
    suspend fun getPartners(userId: String): List<PartnerRealm> = withContext(Dispatchers.IO) {
        realm.where(PartnerRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .sort("createdAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get partner connection by ID.
     */
    suspend fun getPartner(partnerId: String): PartnerRealm? = withContext(Dispatchers.IO) {
        realm.where(PartnerRealm::class.java)
            .equalTo("id", partnerId)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Get partner connection by invitation code.
     */
    suspend fun getPartnerByInvitationCode(code: String): PartnerRealm? = withContext(Dispatchers.IO) {
        realm.where(PartnerRealm::class.java)
            .equalTo("invitationCode", code)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Get pending invitations sent by user.
     */
    suspend fun getPendingInvitationsSent(userId: String): List<PartnerRealm> = withContext(Dispatchers.IO) {
        realm.where(PartnerRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("status", PartnerRealm.STATUS_PENDING)
            .sort("invitedAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get pending invitations received by user (by email).
     */
    suspend fun getPendingInvitationsReceived(email: String): List<PartnerRealm> = withContext(Dispatchers.IO) {
        realm.where(PartnerRealm::class.java)
            .equalTo("partnerEmail", email)
            .equalTo("deletedAt", 0L)
            .equalTo("status", PartnerRealm.STATUS_PENDING)
            .sort("invitedAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Insert or update a partner connection.
     */
    suspend fun upsertPartner(partner: PartnerRealm): PartnerRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            partner.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(partner)
        }
    }

    /**
     * Create a new partner invitation.
     */
    suspend fun createInvitation(
        userId: String,
        partnerEmail: String,
        partnerDisplayName: String = "",
        shareLevel: String = PartnerRealm.SHARE_FULL
    ): PartnerRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val invitation = PartnerRealm().apply {
                id = java.util.UUID.randomUUID().toString()
                this.userId = userId
                this.partnerEmail = partnerEmail
                this.partnerDisplayName = partnerDisplayName
                this.invitationCode = generateInvitationCode()
                this.status = PartnerRealm.STATUS_PENDING
                this.invitedAt = System.currentTimeMillis()
                this.partnerShareLevel = shareLevel
                // Default sharing settings
                this.shareCycle = true
                this.sharePeriodDates = true
                this.shareOvulation = true
                this.shareFertileWindow = true
                this.sharePms = true
                this.shareSymptoms = true
                this.sharePredictions = true
                this.shareInsights = true
                this.shareMood = true
                this.notifyPartnerPeriodStart = true
                this.notifyPartnerFertileWindow = true
                this.notifyPartnerOvulation = true
                this.notifyPartnerPregnancy = true
                this.createdAt = System.currentTimeMillis()
                this.updatedAt = System.currentTimeMillis()
                this.version = 1
            }
            realm.copyToRealm(invitation)
        }
    }

    /**
     * Accept a partner invitation.
     */
    suspend fun acceptInvitation(invitationCode: String, partnerUserId: String, partnerDisplayName: String, partnerAvatarUrl: String = ""): PartnerRealm? = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val invitation = realm.where(PartnerRealm::class.java)
                .equalTo("invitationCode", invitationCode)
                .equalTo("status", PartnerRealm.STATUS_PENDING)
                .findFirst()
            .detached()
            invitation?.let {
                it.status = PartnerRealm.STATUS_ACCEPTED
                it.partnerUserId = partnerUserId
                it.partnerDisplayName = partnerDisplayName
                it.partnerAvatarUrl = partnerAvatarUrl
                it.acceptedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                it.version++
                realm.copyToRealmOrUpdate(it)
            }
        }
    }

    /**
     * Decline a partner invitation.
     */
    suspend fun declineInvitation(invitationCode: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val invitation = realm.where(PartnerRealm::class.java)
                .equalTo("invitationCode", invitationCode)
                .equalTo("status", PartnerRealm.STATUS_PENDING)
                .findFirst()
            invitation?.let {
                it.status = PartnerRealm.STATUS_DECLINED
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Revoke partner connection.
     */
    suspend fun revokePartner(userId: String, partnerId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val partner = realm.where(PartnerRealm::class.java)
                .equalTo("id", partnerId)
                .equalTo("userId", userId)
                .findFirst()
            partner?.let {
                it.status = PartnerRealm.STATUS_REVOKED
                it.revokedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Update sharing preferences.
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
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val partner = realm.where(PartnerRealm::class.java)
                .equalTo("id", partnerId)
                .findFirst()
            partner?.let {
                shareCycle?.let { it.shareCycle = it }
                sharePeriodDates?.let { it.sharePeriodDates = it }
                shareOvulation?.let { it.shareOvulation = it }
                shareFertileWindow?.let { it.shareFertileWindow = it }
                sharePms?.let { it.sharePms = it }
                shareSymptoms?.let { it.shareSymptoms = it }
                sharePredictions?.let { it.sharePredictions = it }
                sharePregnancy?.let { it.sharePregnancy = it }
                shareInsights?.let { it.shareInsights = it }
                shareMood?.let { it.shareMood = it }
                shareWeight?.let { it.shareWeight = it }
                shareNotes?.let { it.shareNotes = it }
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Update notification preferences.
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
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val partner = realm.where(PartnerRealm::class.java)
                .equalTo("id", partnerId)
                .findFirst()
            partner?.let {
                notifyPeriodStart?.let { it.notifyPartnerPeriodStart = it }
                notifyPeriodEnd?.let { it.notifyPartnerPeriodEnd = it }
                notifyFertileWindow?.let { it.notifyPartnerFertileWindow = it }
                notifyOvulation?.let { it.notifyPartnerOvulation = it }
                notifyPms?.let { it.notifyPartnerPms = it }
                notifyPregnancy?.let { it.notifyPartnerPregnancy = it }
                notifySymptoms?.let { it.notifyPartnerSymptoms = it }
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Update partner's sharing preferences (what partner shares with user).
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
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val partner = realm.where(PartnerRealm::class.java)
                .equalTo("id", partnerId)
                .findFirst()
            partner?.let {
                shareCycle?.let { it.partnerShareCycle = it }
                sharePeriodDates?.let { it.partnerSharePeriodDates = it }
                shareOvulation?.let { it.partnerShareOvulation = it }
                shareFertileWindow?.let { it.partnerShareFertileWindow = it }
                sharePms?.let { it.partnerSharePms = it }
                shareSymptoms?.let { it.partnerShareSymptoms = it }
                sharePredictions?.let { it.partnerSharePredictions = it }
                sharePregnancy?.let { it.partnerSharePregnancy = it }
                shareInsights?.let { it.partnerShareInsights = it }
                shareMood?.let { it.partnerShareMood = it }
                shareWeight?.let { it.partnerShareWeight = it }
                shareNotes?.let { it.partnerShareNotes = it }
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Update partner display info.
     */
    suspend fun updatePartnerDisplayInfo(
        partnerId: String,
        displayName: String? = null,
        avatarUrl: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val partner = realm.where(PartnerRealm::class.java)
                .equalTo("id", partnerId)
                .findFirst()
            partner?.let {
                displayName?.let { it.partnerDisplayName = it }
                avatarUrl?.let { it.partnerAvatarUrl = it }
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Soft delete partner connection.
     */
    suspend fun deletePartner(partnerId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val partner = realm.where(PartnerRealm::class.java)
                .equalTo("id", partnerId)
                .findFirst()
            partner?.let {
                it.deletedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Hard delete partner connection (GDPR/LGPD).
     */
    suspend fun hardDeletePartner(partnerId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val partner = realm.where(PartnerRealm::class.java)
                .equalTo("id", partnerId)
                .findFirst()
            partner?.deleteFromRealm() ?: false
        }
    }

    /**
     * Generate a unique invitation code.
     */
    private fun generateInvitationCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // Exclude confusing chars
        val random = java.security.SecureRandom()
        return (1..PartnerRealm.INVITATION_CODE_LENGTH)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }

    /**
     * Executa uma transacao de escrita devolvendo o valor produzido pelo bloco.
     * A API Java do Realm nao retorna valor de `executeTransaction`.
     */
    private suspend fun <T> writeTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
        var result: T? = null
        realm.executeTransaction { transactionRealm ->
            result = block(transactionRealm)
        }
        @Suppress("UNCHECKED_CAST")
        result as T
    }

}