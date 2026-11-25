package com.seuapp.cycletrack.data.local.prefs

import android.content.Context

object OnboardingPrefs {
    private const val PREFS_NAME = "cycletrack_onboarding"
    private const val KEY_COMPLETE = "onboarding_complete"

    fun isComplete(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_COMPLETE, false)

    fun setComplete(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_COMPLETE, true)
            .apply()
    }
}
