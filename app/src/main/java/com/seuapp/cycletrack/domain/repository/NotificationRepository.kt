package com.seuapp.cycletrack.domain.repository

interface NotificationRepository {
    suspend fun getNotifications(userId: String): List<Any>
    suspend fun upsertNotification(notification: Any)
    suspend fun deleteNotification(id: String)
}
