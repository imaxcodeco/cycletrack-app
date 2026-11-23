package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de conteúdo.
 */
@Serializable
data class ContentResponse(
    val id: String,
    val category: String,
    val tags: String,
    val title: String,
    val subtitle: String,
    val body: String,
    val summary: String,
    val author: String,
    val sourceUrl: String,
    val imageUrl: String,
    val videoUrl: String,
    val durationMinutes: Int,
    val targetCyclePhase: String,
    val targetPregnancyWeekMin: Int,
    val targetPregnancyWeekMax: Int,
    val targetConditions: String,
    val language: String,
    val region: String,
    val isPremium: Boolean,
    val minAppVersion: String,
    val maxAppVersion: String,
    val viewCount: Long,
    val likeCount: Long,
    val bookmarkCount: Long,
    val shareCount: Long,
    val isBookmarked: Boolean,
    val isRead: Boolean,
    val readProgress: Float,
    val lastReadAt: Long,
    val publishedAt: Long,
    val expiresAt: Long,
    val version: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val serverId: String,
    val lastSyncedAt: Long
)

@Serializable
data class ContentListResponse(
    val content: List<ContentResponse>,
    val total: Int,
    val limit: Int,
    val offset: Int
)