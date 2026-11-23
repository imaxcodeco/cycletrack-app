package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de configuração.
 */
@Serializable
data class AppConfigResponse(
    val apiVersion: String,
    val minAppVersion: String,
    val forceUpdate: Boolean,
    val maintenanceMode: Boolean,
    val maintenanceMessage: String,
    val termsOfServiceUrl: String,
    val privacyPolicyUrl: String,
    val supportEmail: String,
    val defaultLanguage: String,
    val supportedLanguages: List<String>,
    val defaultRegion: String,
    val supportedRegions: List<String>
)

@Serializable
data class FeatureFlagsResponse(
    val healthConnectEnabled: Boolean,
    val partnerModeEnabled: Boolean,
    val pregnancyModeEnabled: Boolean,
    val perimenopauseModeEnabled: Boolean,
    val anonymousModeEnabled: Boolean,
    val exportEnabled: Boolean,
    val biometricAuthEnabled: Boolean,
    val contentEnabled: Boolean,
    val premiumEnabled: Boolean,
    val analyticsEnabled: Boolean
)