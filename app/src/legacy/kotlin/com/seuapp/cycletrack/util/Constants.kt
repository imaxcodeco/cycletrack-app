package com.seuapp.cycletrack.util

/**
 * Constantes globais do app.
 * Clean-room - valores próprios, não copiados do Flo.
 */
object Constants {

    // Package & Build
    const val PACKAGE_NAME = "com.seuapp.cycletrack"
    const val APP_NAME = "CycleTrack"
    const val APP_VERSION = "1.0.0"

    // Database
    const val REALM_DB_NAME = "cycletrack.realm"
    const val REALM_SCHEMA_VERSION = 1
    const val ROOM_DB_NAME = "cycletrack_analytics.db"

    // Preferences
    const val PREFS_NAME = "cycletrack_prefs"
    const val SECURE_PREFS_NAME = "secure_prefs"

    // Notification Channels
    const val CHANNEL_PERIOD = "period_reminders"
    const val CHANNEL_FERTILITY = "fertility_reminders"
    const val CHANNEL_PILL = "pill_reminders"
    const val CHANNEL_PREGNANCY = "pregnancy_reminders"
    const val CHANNEL_PARTNER = "partner_notifications"
    const val CHANNEL_INSIGHTS = "insights"
    const val CHANNEL_CONTENT = "content"
    const val CHANNEL_SYSTEM = "system"
    const val CHANNEL_SYNC = "sync_notifications"

    // Notification Priorities
    const val PRIORITY_HIGH = 1
    const val PRIORITY_DEFAULT = 0
    const val PRIORITY_LOW = -1

    // WorkManager
    const val SYNC_WORK_NAME = "sync_work"
    const val BACKUP_WORK_NAME = "backup_work"
    const val EXPORT_WORK_NAME = "export_work"
    const val NOTIFICATION_WORK_NAME = "notification_work"
    const val HEALTH_SYNC_WORK_NAME = "health_sync_work"
    const val ANALYTICS_WORK_NAME = "analytics_work"

    // Sync intervals (minutes)
    const val DEFAULT_SYNC_INTERVAL = 15
    const val MIN_SYNC_INTERVAL = 1
    const val MAX_SYNC_INTERVAL = 1440 // 24h

    // Backup
    const val DEFAULT_BACKUP_INTERVAL_DAYS = 7
    const val MAX_BACKUP_INTERVAL_DAYS = 30

    // Export
    const val EXPORT_RETENTION_DAYS = 7
    const val MAX_EXPORT_RETENTION_DAYS = 30
    const val EXPORT_MAX_FILE_SIZE_MB = 100

    // Partner
    const val INVITATION_CODE_LENGTH = 8
    const val INVITATION_EXPIRY_DAYS = 7
    const val MAX_PARTNER_CONNECTIONS = 1

    // Prediction
    const val MIN_CYCLES_FOR_PREDICTION = 3
    const val MAX_CYCLES_TO_ANALYZE = 12
    const val DEFAULT_LUTEAL_PHASE = 14
    const val FERTILE_WINDOW_DAYS_BEFORE = 5
    const val FERTILE_WINDOW_DAYS_AFTER = 1
    const val PMS_DAYS_BEFORE = 7

    // Cycle validation
    const val MIN_CYCLE_LENGTH = 21
    const val MAX_CYCLE_LENGTH = 45
    const val MIN_PERIOD_LENGTH = 2
    const val MAX_PERIOD_LENGTH = 10

    // Health Connect
    const val HEALTH_CONNECT_SYNC_INTERVAL_MINUTES = 30
    const val HEALTH_CONNECT_MAX_RETRIES = 3

    // Biometric
    const val BIOMETRIC_TIMEOUT_SECONDS = 30

    // Auto-lock
    const val DEFAULT_AUTO_LOCK_MINUTES = 5
    const val MIN_AUTO_LOCK_MINUTES = 1
    const val MAX_AUTO_LOCK_MINUTES = 60

    // Analytics
    const val ANALYTICS_BATCH_SIZE = 50
    const val ANALYTICS_FLUSH_INTERVAL_MS = 30000
    const val MAX_ANALYTICS_EVENTS_LOCAL = 1000

    // Session
    const val SESSION_TIMEOUT_MS = 30 * 60 * 1000 // 30 min

    // Network
    const val CONNECT_TIMEOUT_SECONDS = 30
    const val READ_TIMEOUT_SECONDS = 30
    const val WRITE_TIMEOUT_SECONDS = 30
    const val CACHE_SIZE_MB = 50

    // Security
    const val CERT_PIN_EXPIRY_DAYS = 365
    const val TOKEN_REFRESH_THRESHOLD_MINUTES = 5

    // Content
    const val CONTENT_CACHE_DAYS = 30
    const val CONTENT_MAX_OFFLINE_ITEMS = 100

    // Pregnancy
    const val PREGNANCY_WEEKS = 40
    const val TRIMESTER_1_END = 13
    const val TRIMESTER_2_END = 27

    // Perimenopause
    const val PERIMENOPAUSE_MIN_AGE = 40
    const val MENOPAUSE_AVG_AGE = 51

    // Deep Links
    const val DEEP_LINK_SCHEME = "cycletrack"
    const val DEEP_LINK_HOST = "app"

    // Intents
    const val ACTION_OPEN_CYCLE = "com.seuapp.cycletrack.OPEN_CYCLE"
    const val ACTION_LOG_SYMPTOM = "com.seuapp.cycletrack.LOG_SYMPTOM"
    const val ACTION_VIEW_PREDICTION = "com.seuapp.cycletrack.VIEW_PREDICTION"
    const val ACTION_PARTNER_INVITE = "com.seuapp.cycletrack.PARTNER_INVITE"

    // Permissions
    const val PERMISSION_POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
    const val PERMISSION_BODY_SENSORS = "android.permission.BODY_SENSORS"
    const val PERMISSION_ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION"

    // Feature Flags (controlados por Remote Config)
    const val FEATURE_HEALTH_CONNECT = "health_connect_enabled"
    const val FEATURE_PARTNER_MODE = "partner_mode_enabled"
    const val FEATURE_PREGNANCY_MODE = "pregnancy_mode_enabled"
    const val FEATURE_PERIMENOPAUSE_MODE = "perimenopause_mode_enabled"
    const val FEATURE_ANONYMOUS_MODE = "anonymous_mode_enabled"
    const val FEATURE_EXPORT = "export_enabled"
    const val FEATURE_BIOMETRIC = "biometric_enabled"
    const val FEATURE_CONTENT = "content_enabled"
    const val FEATURE_PREMIUM = "premium_enabled"
    const val FEATURE_ANALYTICS = "analytics_enabled"

    // Error Codes
    const val ERROR_NETWORK = "NETWORK_ERROR"
    const val ERROR_AUTH = "AUTH_ERROR"
    const val ERROR_VALIDATION = "VALIDATION_ERROR"
    const val ERROR_SERVER = "SERVER_ERROR"
    const val ERROR_NOT_FOUND = "NOT_FOUND"
    const val ERROR_CONFLICT = "CONFLICT"
    const val ERROR_PERMISSION = "PERMISSION_DENIED"
    const val ERROR_QUOTA = "QUOTA_EXCEEDED"
    const val ERROR_MAINTENANCE = "MAINTENANCE_MODE"
    const val ERROR_UNKNOWN = "UNKNOWN_ERROR"
}