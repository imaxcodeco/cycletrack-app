package com.seuapp.cycletrack.data.local.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesKeys
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.preferences.rxjava3.rxPreferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import kotlinx.coroutines.withContext

/**
 * Manager for app preferences using DataStore and EncryptedSharedPreferences.
 * Clean-room implementation with secure storage for sensitive data.
 */
class PreferencesManager private constructor(
    private val dataStore: androidx.datastore.preferences.rxjava3.RxDataStore<Preferences>,
    private val encryptedPrefs: SharedPreferences
) {

    // Preferences Keys (DataStore - non-sensitive)
    private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    private val KEY_ONBOARDING_STEP = intPreferencesKey("onboarding_step")
    private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    private val KEY_LANGUAGE = stringPreferencesKey("language")
    private val KEY_FIRST_DAY_OF_WEEK = intPreferencesKey("first_day_of_week")
    private val KEY_CALENDAR_VIEW_MODE = stringPreferencesKey("calendar_view_mode")
    private val KEY_TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
    private val KEY_WEIGHT_UNIT = stringPreferencesKey("weight_unit")
    private val KEY_HEIGHT_UNIT = stringPreferencesKey("height_unit")
    private val KEY_ANALYTICS_OPT_IN = booleanPreferencesKey("analytics_opt_in")
    private val KEY_CRASH_REPORTING_OPT_IN = booleanPreferencesKey("crash_reporting_opt_in")
    private val KEY_MARKETING_OPT_IN = booleanPreferencesKey("marketing_opt_in")
    private val KEY_HEALTH_CONNECT_ENABLED = booleanPreferencesKey("health_connect_enabled")
    private val KEY_NOTIFICATION_CHANNELS = stringSetPreferencesKey("notification_channels")
    private val KEY_APP_OPEN_COUNT = longPreferencesKey("app_open_count")
    private val KEY_LAST_APP_VERSION = stringPreferencesKey("last_app_version")
    private val KEY_FIRST_INSTALL_TIME = longPreferencesKey("first_install_time")
    private val KEY_LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
    private val KEY_SYNC_FREQUENCY_MINUTES = intPreferencesKey("sync_frequency_minutes")
    private val KEY_AUTO_SYNC_ENABLED = booleanPreferencesKey("auto_sync_enabled")
    private val KEY_BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    private val KEY_AUTO_LOCK_ENABLED = booleanPreferencesKey("auto_lock_enabled")
    private val KEY_AUTO_LOCK_TIMEOUT = intPreferencesKey("auto_lock_timeout_minutes")

    // Encrypted Preferences Keys (sensitive data)
    private const val ENC_KEY_AUTH_TOKEN = "auth_token"
    private const val ENC_KEY_REFRESH_TOKEN = "refresh_token"
    private const val ENC_KEY_USER_ID = "user_id"
    private const val ENC_KEY_USER_EMAIL = "user_email"
    private const val ENC_KEY_FCM_TOKEN = "fcm_token"
    private const val ENC_KEY_APPSFLYER_ID = "appsflyer_id"
    private const val ENC_KEY_AMPLITUDE_DEVICE_ID = "amplitude_device_id"
    private const val ENC_KEY_SENTRY_DSN = "sentry_dsn"
    private const val ENC_KEY_API_BASE_URL = "api_base_url"
    private const val ENC_KEY_CERTIFICATE_PINS = "certificate_pins"
    private const val ENC_KEY_LAST_KNOWN_CYCLE = "last_known_cycle"
    private const val ENC_KEY_LAST_KNOWN_PREDICTIONS = "last_known_predictions"

    companion object {
        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun initialize(encryptedPrefs: SharedPreferences) {
            // DataStore will be created lazily
        }

        fun getInstance(context: Context): PreferencesManager = INSTANCE ?: synchronized(this) {
            val dataStore = RxPreferenceDataStoreBuilder(context, "cycletrack_prefs").build()
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val encryptedPrefs = EncryptedSharedPreferences.create(
                "secure_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            val instance = PreferencesManager(dataStore, encryptedPrefs)
            INSTANCE = instance
            instance
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    // ============ DataStore Preferences (Non-sensitive) ============

    // Onboarding
    val onboardingCompleted: Flowable<Boolean> = dataStore.data
        .map { it.getBoolean(KEY_ONBOARDING_COMPLETED, false) }
        .distinctUntilChanged()

    suspend fun setOnboardingCompleted(completed: Boolean) = dataStore.updateDataAsync { it.putBoolean(KEY_ONBOARDING_COMPLETED, completed) }.await()

    val onboardingStep: Flowable<Int> = dataStore.data
        .map { it.getInt(KEY_ONBOARDING_STEP, 0) }
        .distinctUntilChanged()

    suspend fun setOnboardingStep(step: Int) = dataStore.updateDataAsync { it.putInt(KEY_ONBOARDING_STEP, step) }.await()

    // Theme
    val themeMode: Flowable<String> = dataStore.data
        .map { it.getString(KEY_THEME_MODE, "system") }
        .distinctUntilChanged()

    suspend fun setThemeMode(mode: String) = dataStore.updateDataAsync { it.putString(KEY_THEME_MODE, mode) }.await()

    // Language
    val language: Flowable<String> = dataStore.data
        .map { it.getString(KEY_LANGUAGE, "pt_BR") }
        .distinctUntilChanged()

    suspend fun setLanguage(lang: String) = dataStore.updateDataAsync { it.putString(KEY_LANGUAGE, lang) }.await()

    // Calendar
    val firstDayOfWeek: Flowable<Int> = dataStore.data
        .map { it.getInt(KEY_FIRST_DAY_OF_WEEK, 1) }
        .distinctUntilChanged()

    suspend fun setFirstDayOfWeek(day: Int) = dataStore.updateDataAsync { it.putInt(KEY_FIRST_DAY_OF_WEEK, day.coerceIn(1, 7)) }.await()

    val calendarViewMode: Flowable<String> = dataStore.data
        .map { it.getString(KEY_CALENDAR_VIEW_MODE, "month") }
        .distinctUntilChanged()

    suspend fun setCalendarViewMode(mode: String) = dataStore.updateDataAsync { it.putString(KEY_CALENDAR_VIEW_MODE, mode) }.await()

    // Units
    val temperatureUnit: Flowable<String> = dataStore.data
        .map { it.getString(KEY_TEMPERATURE_UNIT, "celsius") }
        .distinctUntilChanged()

    suspend fun setTemperatureUnit(unit: String) = dataStore.updateDataAsync { it.putString(KEY_TEMPERATURE_UNIT, unit) }.await()

    val weightUnit: Flowable<String> = dataStore.data
        .map { it.getString(KEY_WEIGHT_UNIT, "kg") }
        .distinctUntilChanged()

    suspend fun setWeightUnit(unit: String) = dataStore.updateDataAsync { it.putString(KEY_WEIGHT_UNIT, unit) }.await()

    val heightUnit: Flowable<String> = dataStore.data
        .map { it.getString(KEY_HEIGHT_UNIT, "cm") }
        .distinctUntilChanged()

    suspend fun setHeightUnit(unit: String) = dataStore.updateDataAsync { it.putString(KEY_HEIGHT_UNIT, unit) }.await()

    // Consent
    val analyticsOptIn: Flowable<Boolean> = dataStore.data
        .map { it.getBoolean(KEY_ANALYTICS_OPT_IN, true) }
        .distinctUntilChanged()

    suspend fun setAnalyticsOptIn(optIn: Boolean) = dataStore.updateDataAsync { it.putBoolean(KEY_ANALYTICS_OPT_IN, optIn) }.await()

    val crashReportingOptIn: Flowable<Boolean> = dataStore.data
        .map { it.getBoolean(KEY_CRASH_REPORTING_OPT_IN, true) }
        .distinctUntilChanged()

    suspend fun setCrashReportingOptIn(optIn: Boolean) = dataStore.updateDataAsync { it.putBoolean(KEY_CRASH_REPORTING_OPT_IN, optIn) }.await()

    val marketingOptIn: Flowable<Boolean> = dataStore.data
        .map { it.getBoolean(KEY_MARKETING_OPT_IN, false) }
        .distinctUntilChanged()

    suspend fun setMarketingOptIn(optIn: Boolean) = dataStore.updateDataAsync { it.putBoolean(KEY_MARKETING_OPT_IN, optIn) }.await()

    // Health Connect
    val healthConnectEnabled: Flowable<Boolean> = dataStore.data
        .map { it.getBoolean(KEY_HEALTH_CONNECT_ENABLED, false) }
        .distinctUntilChanged()

    suspend fun setHealthConnectEnabled(enabled: Boolean) = dataStore.updateDataAsync { it.putBoolean(KEY_HEALTH_CONNECT_ENABLED, enabled) }.await()

    // Notifications
    val notificationChannels: Flowable<Set<String>> = dataStore.data
        .map { it.getStringSet(KEY_NOTIFICATION_CHANNELS) ?: emptySet() }
        .distinctUntilChanged()

    suspend fun setNotificationChannels(channels: Set<String>) = dataStore.updateDataAsync { it.putStringSet(KEY_NOTIFICATION_CHANNELS, channels) }.await()

    suspend fun addNotificationChannel(channel: String) = dataStore.updateDataAsync {
        val current = it.getStringSet(KEY_NOTIFICATION_CHANNELS) ?: emptySet()
        it.putStringSet(KEY_NOTIFICATION_CHANNELS, current + channel)
    }.await()

    suspend fun removeNotificationChannel(channel: String) = dataStore.updateDataAsync {
        val current = it.getStringSet(KEY_NOTIFICATION_CHANNELS) ?: emptySet()
        it.putStringSet(KEY_NOTIFICATION_CHANNELS, current - channel)
    }.await()

    // App Stats
    val appOpenCount: Flowable<Long> = dataStore.data
        .map { it.getLong(KEY_APP_OPEN_COUNT, 0) }
        .distinctUntilChanged()

    suspend fun incrementAppOpenCount(): Long = dataStore.updateDataAsync {
        val count = it.getLong(KEY_APP_OPEN_COUNT, 0) + 1
        it.putLong(KEY_APP_OPEN_COUNT, count)
        count
    }.await()

    val lastAppVersion: Flowable<String> = dataStore.data
        .map { it.getString(KEY_LAST_APP_VERSION, "") }
        .distinctUntilChanged()

    suspend fun setLastAppVersion(version: String) = dataStore.updateDataAsync { it.putString(KEY_LAST_APP_VERSION, version) }.await()

    val firstInstallTime: Flowable<Long> = dataStore.data
        .map { it.getLong(KEY_FIRST_INSTALL_TIME, System.currentTimeMillis()) }
        .distinctUntilChanged()

    suspend fun setFirstInstallTime(time: Long) = dataStore.updateDataAsync { it.putLong(KEY_FIRST_INSTALL_TIME, time) }.await()

    // Sync
    val lastSyncTime: Flowable<Long> = dataStore.data
        .map { it.getLong(KEY_LAST_SYNC_TIME, 0) }
        .distinctUntilChanged()

    suspend fun setLastSyncTime(time: Long) = dataStore.updateDataAsync { it.putLong(KEY_LAST_SYNC_TIME, time) }.await()

    val syncFrequencyMinutes: Flowable<Int> = dataStore.data
        .map { it.getInt(KEY_SYNC_FREQUENCY_MINUTES, 15) }
        .distinctUntilChanged()

    suspend fun setSyncFrequencyMinutes(minutes: Int) = dataStore.updateDataAsync { it.putInt(KEY_SYNC_FREQUENCY_MINUTES, minutes.coerceIn(1, 1440)) }.await()

    val autoSyncEnabled: Flowable<Boolean> = dataStore.data
        .map { it.getBoolean(KEY_AUTO_SYNC_ENABLED, true) }
        .distinctUntilChanged()

    suspend fun setAutoSyncEnabled(enabled: Boolean) = dataStore.updateDataAsync { it.putBoolean(KEY_AUTO_SYNC_ENABLED, enabled) }.await()

    // Security
    val biometricEnabled: Flowable<Boolean> = dataStore.data
        .map { it.getBoolean(KEY_BIOMETRIC_ENABLED, false) }
        .distinctUntilChanged()

    suspend fun setBiometricEnabled(enabled: Boolean) = dataStore.updateDataAsync { it.putBoolean(KEY_BIOMETRIC_ENABLED, enabled) }.await()

    val autoLockEnabled: Flowable<Boolean> = dataStore.data
        .map { it.getBoolean(KEY_AUTO_LOCK_ENABLED, true) }
        .distinctUntilChanged()

    suspend fun setAutoLockEnabled(enabled: Boolean) = dataStore.updateDataAsync { it.putBoolean(KEY_AUTO_LOCK_ENABLED, enabled) }.await()

    val autoLockTimeoutMinutes: Flowable<Int> = dataStore.data
        .map { it.getInt(KEY_AUTO_LOCK_TIMEOUT, 5) }
        .distinctUntilChanged()

    suspend fun setAutoLockTimeoutMinutes(minutes: Int) = dataStore.updateDataAsync { it.putInt(KEY_AUTO_LOCK_TIMEOUT, minutes.coerceIn(1, 60)) }.await()

    // ============ EncryptedSharedPreferences (Sensitive) ============

    // Auth tokens
    suspend fun setAuthToken(token: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? = encryptedPrefs.getString(ENC_KEY_AUTH_TOKEN, null)

    suspend fun setRefreshToken(token: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_REFRESH_TOKEN, token).apply()
    }

    fun getRefreshToken(): String? = encryptedPrefs.getString(ENC_KEY_REFRESH_TOKEN, null)

    suspend fun clearAuthTokens(): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit()
            .remove(ENC_KEY_AUTH_TOKEN)
            .remove(ENC_KEY_REFRESH_TOKEN)
            .apply()
    }

    // User identity
    suspend fun setUserId(userId: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? = encryptedPrefs.getString(ENC_KEY_USER_ID, null)

    suspend fun setUserEmail(email: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? = encryptedPrefs.getString(ENC_KEY_USER_EMAIL, null)

    // FCM Token
    suspend fun setFcmToken(token: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_FCM_TOKEN, token).apply()
    }

    fun getFcmToken(): String? = encryptedPrefs.getString(ENC_KEY_FCM_TOKEN, null)

    // Third-party IDs
    suspend fun setAppsFlyerId(id: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_APPSFLYER_ID, id).apply()
    }

    fun getAppsFlyerId(): String? = encryptedPrefs.getString(ENC_KEY_APPSFLYER_ID, null)

    suspend fun setAmplitudeDeviceId(id: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_AMPLITUDE_DEVICE_ID, id).apply()
    }

    fun getAmplitudeDeviceId(): String? = encryptedPrefs.getString(ENC_KEY_AMPLITUDE_DEVICE_ID, null)

    // Configuration
    suspend fun setSentryDsn(dsn: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_SENTRY_DSN, dsn).apply()
    }

    fun getSentryDsn(): String? = encryptedPrefs.getString(ENC_KEY_SENTRY_DSN, null)

    suspend fun setApiBaseUrl(url: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_API_BASE_URL, url).apply()
    }

    fun getApiBaseUrl(): String? = encryptedPrefs.getString(ENC_KEY_API_BASE_URL, null)

    suspend fun setCertificatePins(pins: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_CERTIFICATE_PINS, pins).apply()
    }

    fun getCertificatePins(): String? = encryptedPrefs.getString(ENC_KEY_CERTIFICATE_PINS, null)

    // Cached data for offline
    suspend fun setLastKnownCycle(cycleJson: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_LAST_KNOWN_CYCLE, cycleJson).apply()
    }

    fun getLastKnownCycle(): String? = encryptedPrefs.getString(ENC_KEY_LAST_KNOWN_CYCLE, null)

    suspend fun setLastKnownPredictions(predictionsJson: String): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(ENC_KEY_LAST_KNOWN_PREDICTIONS, predictionsJson).apply()
    }

    fun getLastKnownPredictions(): String? = encryptedPrefs.getString(ENC_KEY_LAST_KNOWN_PREDICTIONS, null)

    // Clear all sensitive data
    suspend fun clearAllSensitiveData(): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().clear().apply()
    }

    // Check if user is logged in
    val isLoggedIn: Boolean
        get() = getAuthToken() != null && getUserId() != null
}