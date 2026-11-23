package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity for scheduled notifications.
 * Clean-room implementation with WorkManager integration.
 */
@Serializable
open class NotificationRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var userId: String = ""

    @Index
    var type: String = "" // period_start, period_end, fertile_window_start, fertile_window_end, ovulation, pms, pill, appointment, pregnancy, partner, insight, content

    @Index
    var scheduledTime: Long = 0L

    var title: String = ""

    var body: String = ""

    var icon: String = ""

    var color: Int = 0

    var channelId: String = "default"

    var priority: Int = 0 // HIGH, DEFAULT, LOW

    var category: String = "" // alarm, reminder, event, message, call

    // Action buttons
    var action1Label: String = ""
    var action1IntentAction: String = ""
    var action2Label: String = ""
    var action2IntentAction: String = ""

    // Deep linking
    var deepLink: String = ""

    // Recurrence
    var isRecurring: Boolean = false
    var recurrenceRule: String = "" // RRULE format
    var recurrenceEndDate: Long = 0L

    // Targeting
    var targetCycleId: String = ""
    var targetPregnancyId: String = ""
    var targetPartnerId: String = ""

    // State
    var status: String = "scheduled" // scheduled, sent, delivered, opened, dismissed, cancelled, failed
    var sentAt: Long = 0L
    var deliveredAt: Long = 0L
    var openedAt: Long = 0L
    var dismissedAt: Long = 0L
    var failureReason: String = ""

    // Retry
    var retryCount: Int = 0
    var maxRetries: Int = 3
    var nextRetryAt: Long = 0L

    // Metadata
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var deletedAt: Long = 0L
    var serverId: String = ""
    var lastSyncedAt: Long = 0L
    var version: Int = 1

    companion object {
        const val TYPE_PERIOD_START = "period_start"
        const val TYPE_PERIOD_END = "period_end"
        const val TYPE_FERTILE_WINDOW_START = "fertile_window_start"
        const val TYPE_FERTILE_WINDOW_END = "fertile_window_end"
        const val TYPE_OVULATION = "ovulation"
        const val TYPE_PMS_START = "pms_start"
        const val TYPE_PMS_END = "pms_end"
        const val TYPE_PILL = "pill"
        const val TYPE_APPOINTMENT = "appointment"
        const val TYPE_PREGNANCY_MILESTONE = "pregnancy_milestone"
        const val TYPE_PREGNANCY_APPOINTMENT = "pregnancy_appointment"
        const val TYPE_PARTNER_PERIOD_START = "partner_period_start"
        const val TYPE_PARTNER_FERTILE_WINDOW = "partner_fertile_window"
        const val TYPE_PARTNER_OVULATION = "partner_ovulation"
        const val TYPE_INSIGHT = "insight"
        const val TYPE_CONTENT = "content"
        const val TYPE_SYNC = "sync"
        const val TYPE_BACKUP = "backup"
        const val TYPE_EXPORT_READY = "export_ready"

        const val STATUS_SCHEDULED = "scheduled"
        const val STATUS_SENT = "sent"
        const val STATUS_DELIVERED = "delivered"
        const val STATUS_OPENED = "opened"
        const val STATUS_DISMISSED = "dismissed"
        const val STATUS_CANCELLED = "cancelled"
        const val STATUS_FAILED = "failed"

        const val CHANNEL_PERIOD = "period_reminders"
        const val CHANNEL_FERTILITY = "fertility_reminders"
        const val CHANNEL_PILL = "pill_reminders"
        const val CHANNEL_PREGNANCY = "pregnancy_reminders"
        const val CHANNEL_PARTNER = "partner_notifications"
        const val CHANNEL_INSIGHTS = "insights"
        const val CHANNEL_CONTENT = "content"
        const val CHANNEL_SYSTEM = "system"
    }
}