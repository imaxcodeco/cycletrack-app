package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de exportação.
 */
@Serializable
data class ExportResponse(
    val id: String,
    val userId: String,
    val format: String,
    val includeCycles: Boolean,
    val includeSymptoms: Boolean,
    val includePredictions: Boolean,
    val includePregnancy: Boolean,
    val includePartnerData: Boolean,
    val includeHealthConnectData: Boolean,
    val includeSettings: Boolean,
    val includeContent: Boolean,
    val dateRangeStart: Long,
    val dateRangeEnd: Long,
    val anonymize: Boolean,
    val status: String, // pending, processing, completed, failed, expired
    val progress: Int,
    val filePath: String,
    val fileSize: Long,
    val fileHash: String,
    val downloadUrl: String,
    val expiresAt: Long,
    val errorMessage: String,
    val retryCount: Int,
    val requestedAt: Long,
    val startedAt: Long,
    val completedAt: Long,
    val requestedFrom: String,
    val ipAddress: String,
    val userAgent: String,
    val legalBasis: String,
    val retentionDays: Int
)

@Serializable
data class ExportDownloadResponse(
    val downloadUrl: String,
    val expiresAt: Long,
    val fileName: String,
    val fileSize: Long
)