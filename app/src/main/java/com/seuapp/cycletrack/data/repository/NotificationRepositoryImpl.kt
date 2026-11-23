package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.data.local.realm.dao.NotificationDao
import com.seuapp.cycletrack.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override suspend fun getNotifications(userId: String) = emptyList<Any>()
    override suspend fun upsertNotification(notification: Any) {}
    override suspend fun deleteNotification(id: String) {}
}
