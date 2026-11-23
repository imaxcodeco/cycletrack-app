package com.seuapp.cycletrack.data.remote.interceptors

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Interceptor para controle de cache offline-first.
 * Clean-room - estratégia de cache inteligente.
 */
class CacheInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val cacheControl = request.cacheControl()

        // Se não tem cache control, aplica padrão
        val newRequest = if (cacheControl.isEmpty()) {
            request.newBuilder()
                .cacheControl(okhttp3.CacheControl.Builder()
                    .maxAge(5, java.util.concurrent.TimeUnit.MINUTES)
                    .maxStale(7, java.util.concurrent.TimeUnit.DAYS)
                    .build())
                .build()
        } else {
            request
        }

        var response = chain.proceed(newRequest)

        // Se offline, força uso de cache stale
        if (!isNetworkAvailable()) {
            response = response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=604800")
                .build()
        }

        return response
    }

    private fun isNetworkAvailable(): Boolean {
        // TODO: Implementar verificação real de conectividade
        // Pode usar ConnectivityManager ou WorkManager network constraints
        return true
    }
}