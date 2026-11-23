package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity representing partner connection and sharing preferences.
 * Clean-room implementation with granular sharing controls.
 */
@Serializable
open class PartnerRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var userId: String = "" // The user who owns this connection

    @Index
    var partnerUserId: String = "" // The partner's user ID

    @Index
    var invitationCode: String = ""

    var partnerEmail: String = ""

    var partnerDisplayName: String = ""

    var partnerAvatarUrl: String = ""

    // Connection state
    var status: String = "pending" // pending, accepted, declined, revoked, expired
    var invitedAt: Long = 0L
    var acceptedAt: Long = 0L
    var revokedAt: Long = 0L

    // Sharing permissions (what THIS user shares WITH partner)
    var shareCycle: Boolean = true
    var sharePeriodDates: Boolean = true
    var shareOvulation: Boolean = true
    var shareFertileWindow: Boolean = true
    var sharePms: Boolean = true
    var shareSymptoms: Boolean = true
    var sharePredictions: Boolean = true
    var sharePregnancy: Boolean = false
    var shareInsights: Boolean = true
    var shareMood: Boolean = true
    var shareWeight: Boolean = false
    var shareNotes: Boolean = false

    // Notification preferences
    var notifyPartnerPeriodStart: Boolean = true
    var notifyPartnerPeriodEnd: Boolean = false
    var notifyPartnerFertileWindow: Boolean = true
    var notifyPartnerOvulation: Boolean = true
    var notifyPartnerPms: Boolean = false
    var notifyPartnerPregnancy: Boolean = true
    var notifyPartnerSymptoms: Boolean = false

    // Partner's sharing permissions (what partner shares with THIS user)
    var partnerShareCycle: Boolean = true
    var partnerSharePeriodDates: Boolean = true
    var partnerShareOvulation: Boolean = true
    var partnerShareFertileWindow: Boolean = true
    var partnerSharePms: Boolean = true
    var partnerShareSymptoms: Boolean = true
    var partnerSharePredictions: Boolean = true
    var partnerSharePregnancy: Boolean = false
    var partnerShareInsights: Boolean = true
    var partnerShareMood: Boolean = true
    var partnerShareWeight: Boolean = false
    var partnerShareNotes: Boolean = false

    // Metadata
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var deletedAt: Long = 0L
    var serverId: String = ""
    var lastSyncedAt: Long = 0L
    var version: Int = 1

    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_ACCEPTED = "accepted"
        const val STATUS_DECLINED = "declined"
        const val STATUS_REVOKED = "revoked"
        const val STATUS_EXPIRED = "expired"

        const val INVITATION_CODE_LENGTH = 8
        const val INVITATION_EXPIRY_DAYS = 7
    }
}