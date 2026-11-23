package com.seuapp.cycletrack.data.remote.interceptors

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Interceptor para adicionar headers de estado do app.
 * Clean-room - headers para debug, analytics e feature flags.
 */
class AppStateInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    private val appVersion: String
    private val osVersion: String
    private val deviceModel: String
    private val deviceId: String

    init {
        appVersion = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "unknown"
        }
        osVersion = Build.VERSION.RELEASE
        deviceModel = Build.MODEL
        deviceId = getDeviceId()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } else null

        val connectionType = when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "wifi"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "cellular"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "ethernet"
            else -> "unknown"
        }

        val newRequest = request.newBuilder()
            .header("X-App-Version", appVersion)
            .header("X-OS-Version", osVersion)
            .header("X-Device-Model", deviceModel)
            .header("X-Device-Id", deviceId)
            .header("X-Connection-Type", connectionType)
            .header("X-Platform", "android")
            .build()

        return chain.proceed(newRequest)
    }

    private fun getDeviceId(): String {
        // TODO: Usar Settings.Secure.ANDROID_ID ou gerar UUID persistente
        return java.util.UUID.randomUUID().toString()
    }
}