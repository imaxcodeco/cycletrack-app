package com.seuapp.cycletrack.data.remote.interceptors

import android.util.Log
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor para adicionar token de autenticação nas requisições.
 * Clean-room - gerencia access token e refresh automático.
 */
class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {

    private val TAG = "AuthInterceptor"
    private var isRefreshing = false
    private val refreshLock = Any()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Pula autenticação para endpoints públicos
        if (isPublicEndpoint(originalRequest.url().encodedPath())) {
            return chain.proceed(originalRequest)
        }

        val accessToken = preferencesManager.getAuthToken()
            ?: return chain.proceed(originalRequest)

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        var response = chain.proceed(authenticatedRequest)

        // Se token expirado (401), tenta refresh
        if (response.code == 401 && !isRefreshing) {
            response.close()
            response = refreshTokenAndRetry(chain, originalRequest)
        }

        return response
    }

    private fun isPublicEndpoint(path: String): Boolean {
        return path.contains("/auth/login") ||
               path.contains("/auth/register") ||
               path.contains("/auth/refresh") ||
               path.contains("/auth/forgot-password") ||
               path.contains("/auth/reset-password") ||
               path.contains("/auth/verify-email") ||
               path.contains("/auth/resend-verification") ||
               path.contains("/config/") ||
               path.contains("/feature-flags")
    }

    private fun refreshTokenAndRetry(chain: Interceptor.Chain, originalRequest: Request): Response {
        synchronized(refreshLock) {
            if (isRefreshing) {
                // Aguarda refresh de outra thread
                try {
                    refreshLock.wait(5000)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
                return chain.proceed(addAuthHeader(originalRequest))
            }

            isRefreshing = true
        }

        try {
            val refreshToken = preferencesManager.getRefreshToken()
                ?: return chain.proceed(originalRequest)

            // Chama endpoint de refresh
            val refreshRequest = buildRefreshRequest(refreshToken)
            val refreshResponse = okhttp3.OkHttpClient().newCall(refreshRequest).execute()

            if (!refreshResponse.isSuccessful) {
                refreshResponse.close()
                // Refresh falhou - limpa tokens e força logout
                clearAuthTokens()
                return chain.proceed(originalRequest)
            }

            val responseBody = refreshResponse.body?.string()
            refreshResponse.close()

            // Parse novo token
            val authResponse = parseAuthResponse(responseBody)
            if (authResponse != null) {
                saveTokens(authResponse.accessToken, authResponse.refreshToken)
                return chain.proceed(addAuthHeader(originalRequest, authResponse.accessToken))
            }

            return chain.proceed(originalRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Token refresh failed", e)
            return chain.proceed(originalRequest)
        } finally {
            synchronized(refreshLock) {
                isRefreshing = false
                refreshLock.notifyAll()
            }
        }
    }

    private fun addAuthHeader(request: Request, token: String? = null): Request {
        val authToken = token ?: preferencesManager.getAuthToken()
        return if (authToken != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()
        } else {
            request
        }
    }

    private fun buildRefreshRequest(refreshToken: String): Request {
        val json = org.json.JSONObject().put("refreshToken", refreshToken).toString()
        val body = okhttp3.RequestBody.create(json, okhttp3.MediaType.get("application/json; charset=utf-8"))
        
        return okhttp3.Request.Builder()
            .url(preferencesManager.getApiBaseUrl() ?: "https://api.seuapp.com/v1/auth/refresh")
            .post(body)
            .build()
    }

    private fun parseAuthResponse(json: String?): AuthResponse? {
        json?.let {
            try {
                return try {
                    val obj = org.json.JSONObject(it)
                    AuthResponse(
                        accessToken = obj.optString("accessToken"),
                        refreshToken = obj.optString("refreshToken"),
                        tokenType = obj.optString("tokenType", "Bearer"),
                        expiresIn = obj.optLong("expiresIn", 3600L)
                    )
                } catch (e: Exception) {
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse auth response", e)
            }
        }
        return null
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        preferencesManager.setAuthToken(accessToken)
        preferencesManager.setRefreshToken(refreshToken)
    }

    private fun clearAuthTokens() {
        preferencesManager.clearAuthTokens()
    }

    private data class AuthResponse(
        val accessToken: String,
        val refreshToken: String,
        val tokenType: String = "Bearer",
        val expiresIn: Long = 3600
    )
}