package com.seuapp.cycletrack.data.local.realm.dao

import javax.inject.Inject

class NotificationDao @Inject constructor() {
    suspend fun getNotifications(userId: String) = emptyList<Any>()
    suspend fun getPendingNotifications(userId: String, beforeTime: Long) = emptyList<Any>()
    suspend fun upsertNotification(notification: Any) {}
    suspend fun upsertNotifications(notifications: List<Any>) {}
    suspend fun deleteNotification(notificationId: String) {}
    suspend fun hardDeleteNotification(notificationId: String) {}
}
