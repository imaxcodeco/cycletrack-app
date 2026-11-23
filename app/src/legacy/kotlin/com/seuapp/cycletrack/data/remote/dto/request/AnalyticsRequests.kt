package com.seuapp.cycletrack.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * DTOs para requisições de analytics.
 */
@Serializable
data class AnalyticsEventsRequest(
    val events: List<AnalyticsEventDto>
)

@Serializable
data class AnalyticsEventDto(
    val eventName: String,
    val eventCategory: String,
    val eventAction: String,
    val eventLabel: String = "",
    val eventValue: Long = 0,
    val screenName: String = "",
    val previousScreenName: String = "",
    val properties: String = "{}",
    val timestamp: Long
)

@Serializable
data class AnalyticsSessionsRequest(
    val sessions: List<AnalyticsSessionDto>
)

@Serializable
data class AnalyticsSessionDto(
    val sessionId: String,
    val startTime: Long,
    val endTime: Long,
    val durationMs: Long,
    val screenViews: Int,
    val interactions: Int,
    val crashes: Int,
    val anrs: Int,
    val wasBackgrounded: Boolean,
    val backgroundCount: Int,
    val totalBackgroundTimeMs: Long,
    val entryScreen: String = "",
    val entrySource: String = "",
    val entryCampaign: String = ""
)

@Serializable
data class RegisterTokenRequest(
    val fcmToken: String,
    val deviceId: String
)

@Serializable
data class SyncHealthDataRequest(
    val dataPoints: List<HealthDataPointDto>
)

@Serializable
data class HealthDataPointDto(
    val dataType: String,
    val startTime: Long,
    val endTime: Long = 0,
    val timeZoneOffset: Int = 0,
    val value: String = "",
    val numericValue: Double = 0.0,
    val unit: String = "",
    val source: String = "app",
    val sourcePackageName: String = "",
    val sourceRecordId: String = "",
    val flowLevel: String = "",
    val testResult: String = "",
    val temperatureCelsius: Double = 0.0,
    val mucusConsistency: String = "",
    val pregnancyTestResult: String = ""
)