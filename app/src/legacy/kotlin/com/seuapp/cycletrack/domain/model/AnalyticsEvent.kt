package com.seuapp.cycletrack.domain.model

import kotlinx.serialization.Serializable

/**
 * Modelo de domínio para Evento de Analytics.
 * Clean-room - privacidade por design.
 */
@Serializable
data class AnalyticsEvent(
    val id: Long = 0,
    val userId: String,
    val sessionId: String,
    val eventName: String,
    val eventCategory: String,
    val eventAction: String,
    val eventLabel: String = "",
    val eventValue: Long = 0,
    val screenName: String = "",
    val previousScreenName: String = "",
    val appVersion: String = "",
    val osVersion: String = "",
    val deviceModel: String = "",
    val locale: String = "",
    val properties: String = "{}",
    val timestamp: Long = System.currentTimeMillis(),
    val serverTimestamp: Long = 0,
    val sentAt: Long = 0,
    val syncStatus: String = AnalyticsEvent.SYNC_PENDING,
    val retryCount: Int = 0,
    val analyticsConsent: Boolean = true,
    val crashReportingConsent: Boolean = true
) {
    companion object {
        const val SYNC_PENDING = "pending"
        const val SYNC_SENT = "sent"
        const val SYNC_FAILED = "failed"

        // Categorias de eventos
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

        // Ações comuns
        const val ACTION_VIEW = "view"
        const val ACTION_CLICK = "click"
        const val ACTION_TAP = "tap"
        const val ACTION_SWIPE = "swipe"
        const val ACTION_SCROLL = "scroll"
        const val ACTION_INPUT = "input"
        const val ACTION_SELECT = "select"
        const val ACTION_SUBMIT = "submit"
        const val ACTION_CANCEL = "cancel"
        const val ACTION_COMPLETE = "complete"
        const val ACTION_START = "start"
        const val ACTION_FINISH = "finish"
        const val ACTION_ERROR = "error"
        const val ACTION_RETRY = "retry"
        const val ACTION_SHARE = "share"
        const val ACTION_EXPORT = "export"
        const val ACTION_SYNC = "sync"
        const val ACTION_BACKUP = "backup"
        const val ACTION_RESTORE = "restore"
        const val ACTION_LOGIN = "login"
        const val ACTION_LOGOUT = "logout"
        const val ACTION_REGISTER = "register"
        const val ACTION_PURCHASE = "purchase"
        const val ACTION_SUBSCRIBE = "subscribe"
        const val ACTION_CANCEL_SUBSCRIPTION = "cancel_subscription"
    }
}

/**
 * Modelo de domínio para Sessão do Usuário.
 */
@Serializable
data class UserSession(
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
    val entryScreen: String = "",
    val entrySource: String = "",
    val entryCampaign: String = "",
    val screenViews: Int = 0,
    val interactions: Int = 0,
    val crashes: Int = 0,
    val anrs: Int = 0,
    val wasBackgrounded: Boolean = false,
    val backgroundCount: Int = 0,
    val totalBackgroundTimeMs: Long = 0,
    val analyticsConsent: Boolean = true,
    val syncStatus: String = UserSession.SYNC_PENDING,
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