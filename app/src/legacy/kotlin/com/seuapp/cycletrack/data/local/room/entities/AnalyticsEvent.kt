package com.seuapp.cycletrack.data.local.room.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Room entity for analytics events.
 * Clean-room implementation for privacy-compliant analytics.
 */
@Serializable
@Entity(
    tableName = "analytics_events",
    indices = [
        Index(value = ["userId", "timestamp"]),
        Index(value = ["eventName", "timestamp"]),
        Index(value = ["sessionId"]),
    ]
)
data class AnalyticsEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: String,
    val sessionId: String,
    val eventName: String,
    val eventCategory: String, // screen_view, button_click, feature_use, error, etc.
    val eventAction: String,
    val eventLabel: String = "",
    val eventValue: Long = 0,

    // Context
    val screenName: String = "",
    val previousScreenName: String = "",
    val appVersion: String = "",
    val osVersion: String = "",
    val deviceModel: String = "",
    val locale: String = "",

    // Custom properties (JSON)
    val properties: String = "{}",

    // Timing
    val timestamp: Long = System.currentTimeMillis(),
    val serverTimestamp: Long = 0,
    val sentAt: Long = 0,

    // Sync
    val syncStatus: String = "pending", // pending, sent, failed
    val retryCount: Int = 0,

    // Consent
    val analyticsConsent: Boolean = true,
    val crashReportingConsent: Boolean = true
) {
    companion object {
        const val SYNC_PENDING = "pending"
        const val SYNC_SENT = "sent"
        const val SYNC_FAILED = "failed"

        // Event Categories
        const val CATEGORY_SCREEN_VIEW = "screen_view"
        const val CATEGORY_BUTTON_CLICK = "button_click"
        const val CATEGORY_FEATURE_USE = "feature_use"
        const val CATEGORY_ONBOARDING = "onboarding"
        const val CATEGORY_CYCLE_TRACKING = "cycle_tracking"
        const val CATEGORY_SYMPTOM_LOGGING = "symptom_logging"
        const val CATEGORY_PREDICTION = "prediction"
        const val CATEGORY_PREGNANCY = "pregnancy"
        const val CATEGORY_PARTNER = "partner"
        const val CATEGORY_PREMIUM = "premium"
        const val CATEGORY_SETTINGS = "settings"
        const val CATEGORY_EXPORT = "export"
        const val CATEGORY_HEALTH_CONNECT = "health_connect"
        const val CATEGORY_NOTIFICATION = "notification"
        const val CATEGORY_ERROR = "error"
        const val CATEGORY_PERFORMANCE = "performance"
    }
}