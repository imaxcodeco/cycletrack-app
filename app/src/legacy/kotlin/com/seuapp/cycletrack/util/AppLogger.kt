package com.seuapp.cycletrack.util

import android.content.Context
import android.util.Log
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import timber.log.Timber

/**
 * Logger centralizado do app.
 * Clean-room - wrapper sobre Timber com controle de consentimento.
 */
class AppLogger private constructor() {

    private var crashlyticsEnabled = false

    companion object {
        @Volatile
        private var INSTANCE: AppLogger? = null

        fun init(context: Context): AppLogger {
            return INSTANCE ?: synchronized(this) {
                val instance = AppLogger()
                instance.initialize(context)
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(): AppLogger = INSTANCE ?: AppLogger()
    }

    private fun initialize(context: Context) {
        // Verifica consentimento para crash reporting
        val prefs = PreferencesManager.getInstance(context)
        crashlyticsEnabled = prefs.crashReportingOptIn
        
        // Planta árvores do Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            if (crashlyticsEnabled) {
                Timber.plant(CrashlyticsTree())
            }
            Timber.plant(ReleaseTree())
        }
    }

    fun setCrashReportingEnabled(enabled: Boolean) {
        crashlyticsEnabled = enabled
    }

    // Métodos de conveniência
    fun v(tag: String, message: String) = Timber.tag(tag).v(message)
    fun d(tag: String, message: String) = Timber.tag(tag).d(message)
    fun i(tag: String, message: String) = Timber.tag(tag).i(message)
    fun w(tag: String, message: String) = Timber.tag(tag).w(message)
    fun e(tag: String, message: String, throwable: Throwable? = null) = 
        if (throwable != null) Timber.tag(tag).e(throwable, message) else Timber.tag(tag).e(message)

    fun v(message: String) = Timber.v(message)
    fun d(message: String) = Timber.d(message)
    fun i(message: String) = Timber.i(message)
    fun w(message: String) = Timber.w(message)
    fun e(message: String, throwable: Throwable? = null) = 
        if (throwable != null) Timber.e(throwable, message) else Timber.e(message)

    // Logs estruturados para analytics
    fun logEvent(eventName: String, properties: Map<String, Any> = emptyMap()) {
        i("ANALYTICS", "Event: $eventName, Props: $properties")
    }

    fun logScreenView(screenName: String, previousScreen: String? = null) {
        i("SCREEN", "View: $screenName, Previous: $previousScreen")
    }

    fun logError(error: String, throwable: Throwable? = null, context: Map<String, Any> = emptyMap()) {
        e("ERROR", "Error: $error, Context: $context", throwable)
    }

    fun logPerformance(operation: String, durationMs: Long, success: Boolean) {
        d("PERF", "Op: $operation, Duration: ${durationMs}ms, Success: $success")
    }
}

/**
 * Árvore do Timber para debug.
 */
private class DebugTree : Timber.DebugTree() {
    override fun createStackElementTag(stack: StackTraceElement): String {
        return "CyTrack ${stack.className.substringAfterLast(".")}.${stack.methodName}:${stack.lineNumber}"
    }
}

/**
 * Árvore para release (sem crashlytics).
 */
private class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority >= Log.INFO) {
            // Envia para backend de logs se necessário
        }
    }
}

/**
 * Árvore para Crashlytics.
 */
private class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN) {
            com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(
                t ?: Exception(message)
            )
        }
    }
}