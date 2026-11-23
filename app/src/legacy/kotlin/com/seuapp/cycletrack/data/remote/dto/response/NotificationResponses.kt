package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de notificações.
 */
@Serializable
data class NotificationResponse(
    val id: String,
    val userId: String,
    val type: String,
    val scheduledTime: Long,
    val title: String,
    val body: String,
    val icon: String,
    val color: Int,
    val channelId: String,
    val priority: Int,
    val category: String,
    val action1Label: String,
    val action1IntentAction: String,
    val action2Label: String,
    val action2IntentAction: String,
    val deepLink: String,
    val isRecurring: Boolean,
    val recurrenceRule: String,
    val recurrenceEndDate: Long,
    val targetCycleId: String,
    val targetPregnancyId: String,
    val targetPartnerId: String,
    val status: String,
    val sentAt: Long,
    val deliveredAt: Long,
    val openedAt: Long,
    val dismissedAt: Long,
    val failureReason: String,
    val retryCount: Int,
    val maxRetries: Int,
    val nextRetryAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val serverId: String,
    val lastSyncedAt: Long,
    val version: Int
)

@Serializable
data class NotificationListResponse(
    val notifications: List<NotificationResponse>,
    val total: Int,
    val limit: Int,
    val offset: Int
)