package com.seuapp.cycletrack.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * DTOs para requisições de autenticação.
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
    val dateOfBirth: String?, // ISO date
    val gender: String = "female",
    val heightCm: Int = 0,
    val weightKg: Float = 0.0f,
    val averageCycleLength: Int = 28,
    val averagePeriodLength: Int = 5,
    val lastPeriodDate: String?, // ISO date
    val contraceptionMethod: String = "none",
    val pregnancyStatus: String = "not_pregnant",
    val language: String = "pt_BR",
    val isAnonymousMode: Boolean = false,
    val deviceInfo: DeviceInfo
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val deviceInfo: DeviceInfo,
    val fcmToken: String? = null
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)

@Serializable
data class VerifyEmailRequest(
    val token: String
)

@Serializable
data class DeviceInfo(
    val deviceId: String,
    val deviceModel: String,
    val osVersion: String,
    val appVersion: String,
    val platform: String = "android",
    val timezone: String,
    val locale: String
)

@Serializable
data class RegisterDeviceRequest(
    val deviceId: String,
    val fcmToken: String?,
    val deviceInfo: DeviceInfo
)