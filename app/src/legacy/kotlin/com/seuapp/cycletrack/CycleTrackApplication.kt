package com.seuapp.cycletrack

import android.app.Application
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import com.seuapp.cycletrack.data.local.realm.RealmManager
import com.seuapp.cycletrack.util.AppLogger
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltAndroidApp
class CycleTrackApplication : Application(), Configuration.Provider {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Configuracao do WorkManager (o initializer automatico foi removido do manifest). */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        initializeTimber()
        initializeRealm()
        initializeWorkManager()
        initializeEncryptedPrefs()
        AppLogger.init(this)
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }

    private fun initializeRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("cycletrack.realm")
            .schemaVersion(1)
            .migration(RealmManager.migration)
            .compactOnLaunch()
            .build()
        Realm.setDefaultConfiguration(config)
    }

    private fun initializeWorkManager() {
        try {
            WorkManager.initialize(this, workManagerConfiguration)
        } catch (e: IllegalStateException) {
            Timber.w("WorkManager ja inicializado: ${e.message}")
        }
    }

    private fun initializeEncryptedPrefs() {
        scope.launch {
            try {
                val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                val encryptedPrefs = EncryptedSharedPreferences.create(
                    "secure_prefs",
                    masterKeyAlias,
                    this@CycleTrackApplication,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
                PreferencesManager.initialize(encryptedPrefs)
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize EncryptedSharedPreferences")
            }
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // Clear image caches, etc.
        }
    }
}

// Crashlytics Tree for Timber
class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN) {
            FirebaseCrashlytics.getInstance().recordException(
                t ?: Exception(message)
            )
        }
    }
}
