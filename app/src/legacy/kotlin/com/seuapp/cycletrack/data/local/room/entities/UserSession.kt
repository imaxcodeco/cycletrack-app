package com.seuapp.cycletrack.data.local.room.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Room entity for user session tracking.
 * Clean-room implementation for session analytics.
 */
@Serializable
@Entity(
    tableName = "user_sessions",
    indices = [
        Index(value = ["userId", "startTime"]),
        Index(value = ["sessionId"]),
    ]
)
data class UserSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val sessionId: String,
    val userId: String,

    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = 0,
    val durationMs: Long = 0,

    val appVersion: String = "",
    val osVersion: String = "",
    val deviceModel: String = "",
    val locale: String = "",
    val timezone: String = "",

    // Entry point
    val entryScreen: String = "",
    val entrySource: String = "", // notification, deep_link, widget, direct, etc.
    val entryCampaign: String = "",

    // Engagement
    val screenViews: Int = 0,
    val interactions: Int = 0,
    val crashes: Int = 0,
    val anrs: Int = 0,

    // Lifecycle
    val wasBackgrounded: Boolean = false,
    val backgroundCount: Int = 0,
    val totalBackgroundTimeMs: Long = 0,

    // Consent
    val analyticsConsent: Boolean = true,

    // Sync
    val syncStatus: String = "pending",
    val sentAt: Long = 0
) {
    companion object {
        const val SYNC_PENDING = "pending"
        const val SYNC_SENT = "sent"
        const val SYNC_FAILED = "failed"

        const val SOURCE_DIRECT = "direct"
        const val SOURCE_NOTIFICATION = "notification"
        const val SOURCE_DEEP_LINK = "deep_link"
        const val SOURCE_WIDGET = "widget"
        const val SOURCE_SHORTCUT = "shortcut"
        const val SOURCE_PARTNER = "partner"
    }
}