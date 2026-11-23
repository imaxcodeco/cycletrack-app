package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.NotificationRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.seuapp.cycletrack.data.local.realm.detached
import kotlin.math.pow

/**
 * DAO for NotificationRealm operations.
 * Clean-room implementation for notification scheduling and tracking.
 */
class NotificationDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get all notifications for a user.
     */
    suspend fun getNotifications(userId: String): List<NotificationRealm> = withContext(Dispatchers.IO) {
        realm.where(NotificationRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .sort("scheduledTime", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get pending notifications (scheduled but not sent).
     */
    suspend fun getPendingNotifications(userId: String, beforeTime: Long = System.currentTimeMillis()): List<NotificationRealm> = withContext(Dispatchers.IO) {
        realm.where(NotificationRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("status", NotificationRealm.STATUS_SCHEDULED)
            .lessThanOrEqualTo("scheduledTime", beforeTime)
            .sort("scheduledTime", io.realm.Sort.ASCENDING)
            .findAll()
    }

    /**
     * Get notifications by type.
     */
    suspend fun getNotificationsByType(userId: String, type: String): List<NotificationRealm> = withContext(Dispatchers.IO) {
        realm.where(NotificationRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("type", type)
            .sort("scheduledTime", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get notifications by status.
     */
    suspend fun getNotificationsByStatus(userId: String, status: String): List<NotificationRealm> = withContext(Dispatchers.IO) {
        realm.where(NotificationRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("status", status)
            .sort("scheduledTime", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get recurring notifications.
     */
    suspend fun getRecurringNotifications(userId: String): List<NotificationRealm> = withContext(Dispatchers.IO) {
        realm.where(NotificationRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("isRecurring", true)
            .sort("scheduledTime", io.realm.Sort.ASCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get notifications needing retry.
     */
    suspend fun getNotificationsNeedingRetry(beforeTime: Long = System.currentTimeMillis()): List<NotificationRealm> = withContext(Dispatchers.IO) {
        realm.where(NotificationRealm::class.java)
            .equalTo("deletedAt", 0L)
            .equalTo("status", NotificationRealm.STATUS_FAILED)
            .lessThanOrEqualTo("nextRetryAt", beforeTime)
            .sort("nextRetryAt", io.realm.Sort.ASCENDING)
            .findAll()
            .filter { it.retryCount < it.maxRetries }
            .detached()
    }

    /**
     * Get notification by ID.
     */
    suspend fun getNotification(notificationId: String): NotificationRealm? = withContext(Dispatchers.IO) {
        realm.where(NotificationRealm::class.java)
            .equalTo("id", notificationId)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Insert or update a notification.
     */
    suspend fun upsertNotification(notification: NotificationRealm): NotificationRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            notification.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(notification)
        }
    }

    /**
     * Insert multiple notifications.
     */
    suspend fun upsertNotifications(notifications: List<NotificationRealm>): List<NotificationRealm> = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            notifications.forEach { it.updatedAt = System.currentTimeMillis() }
            realm.copyToRealmOrUpdate(notifications)
        }
    }

    /**
     * Mark notification as sent.
     */
    suspend fun markAsSent(notificationId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notification = realm.where(NotificationRealm::class.java)
                .equalTo("id", notificationId)
                .findFirst()
            notification?.let {
                it.status = NotificationRealm.STATUS_SENT
                it.sentAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Mark notification as delivered.
     */
    suspend fun markAsDelivered(notificationId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notification = realm.where(NotificationRealm::class.java)
                .equalTo("id", notificationId)
                .findFirst()
            notification?.let {
                it.status = NotificationRealm.STATUS_DELIVERED
                it.deliveredAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Mark notification as opened.
     */
    suspend fun markAsOpened(notificationId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notification = realm.where(NotificationRealm::class.java)
                .equalTo("id", notificationId)
                .findFirst()
            notification?.let {
                it.status = NotificationRealm.STATUS_OPENED
                it.openedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Mark notification as dismissed.
     */
    suspend fun markAsDismissed(notificationId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notification = realm.where(NotificationRealm::class.java)
                .equalTo("id", notificationId)
                .findFirst()
            notification?.let {
                it.status = NotificationRealm.STATUS_DISMISSED
                it.dismissedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Mark notification as failed and schedule retry.
     */
    suspend fun markAsFailed(notificationId: String, reason: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notification = realm.where(NotificationRealm::class.java)
                .equalTo("id", notificationId)
                .findFirst()
            notification?.let {
                it.retryCount++
                it.failureReason = reason
                if (it.retryCount < it.maxRetries) {
                    it.status = NotificationRealm.STATUS_SCHEDULED
                    // Exponential backoff: 5min, 15min, 45min, etc.
                    val delayMinutes = 5 * (3.0.pow(it.retryCount - 1)).toLong()
                    it.nextRetryAt = System.currentTimeMillis() + (delayMinutes * 60 * 1000)
                } else {
                    it.status = NotificationRealm.STATUS_FAILED
                }
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Cancel a notification.
     */
    suspend fun cancelNotification(notificationId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notification = realm.where(NotificationRealm::class.java)
                .equalTo("id", notificationId)
                .findFirst()
            notification?.let {
                it.status = NotificationRealm.STATUS_CANCELLED
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Cancel all notifications of a type for a user.
     */
    suspend fun cancelNotificationsByType(userId: String, type: String): Int = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notifications = realm.where(NotificationRealm::class.java)
                .equalTo("userId", userId)
                .equalTo("deletedAt", 0L)
                .equalTo("type", type)
                .in("status", arrayOf(NotificationRealm.STATUS_SCHEDULED, NotificationRealm.STATUS_FAILED))
                .findAll()
            val count = notifications.size
            notifications.forEach { it.status = NotificationRealm.STATUS_CANCELLED }
            count
        }
    }

    /**
     * Delete old sent/delivered notifications (cleanup).
     */
    suspend fun cleanupOldNotifications(olderThan: Long = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000): Int = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notifications = realm.where(NotificationRealm::class.java)
                .lessThan("scheduledTime", olderThan)
                .in("status", arrayOf(
                    NotificationRealm.STATUS_SENT,
                    NotificationRealm.STATUS_DELIVERED,
                    NotificationRealm.STATUS_OPENED,
                    NotificationRealm.STATUS_DISMISSED,
                    NotificationRealm.STATUS_CANCELLED
                ))
                .findAll()
            val count = notifications.size
            notifications.deleteAllFromRealm()
            count
        }
    }

    /**
     * Delete failed notifications that exceeded max retries.
     */
    suspend fun deleteFailedNotifications(): Int = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notifications = realm.where(NotificationRealm::class.java)
                .equalTo("status", NotificationRealm.STATUS_FAILED)
                .findAll()
                .filter { it.retryCount >= it.maxRetries }
            val count = notifications.size
            notifications.forEach { it.deleteFromRealm() }
            count
        }
    }

    /**
     * Soft delete a notification.
     */
    suspend fun deleteNotification(notificationId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notification = realm.where(NotificationRealm::class.java)
                .equalTo("id", notificationId)
                .findFirst()
            notification?.let {
                it.deletedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Hard delete a notification (GDPR/LGPD).
     */
    suspend fun hardDeleteNotification(notificationId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val notification = realm.where(NotificationRealm::class.java)
                .equalTo("id", notificationId)
                .findFirst()
            notification?.deleteFromRealm() ?: false
        }
    }

    /**
     * Get notification statistics.
     */
    suspend fun getNotificationStats(userId: String): NotificationStats = withContext(Dispatchers.IO) {
        val all = realm.where(NotificationRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .findAll()

        val scheduled = all.count { it.status == NotificationRealm.STATUS_SCHEDULED }
        val sent = all.count { it.status == NotificationRealm.STATUS_SENT }
        val delivered = all.count { it.status == NotificationRealm.STATUS_DELIVERED }
        val opened = all.count { it.status == NotificationRealm.STATUS_OPENED }
        val dismissed = all.count { it.status == NotificationRealm.STATUS_DISMISSED }
        val failed = all.count { it.status == NotificationRealm.STATUS_FAILED }
        val cancelled = all.count { it.status == NotificationRealm.STATUS_CANCELLED }

        val byType = all
            .groupBy { it.type }
            .mapValues { (_, v) -> v.size }

        NotificationStats(
            total = all.size,
            scheduled = scheduled,
            sent = sent,
            delivered = delivered,
            opened = opened,
            dismissed = dismissed,
            failed = failed,
            cancelled = cancelled,
            byType = byType
        )
    }
}

/**
 * Data class for notification statistics.
 */
data class NotificationStats(
    val total: Int,
    val scheduled: Int,
    val sent: Int,
    val delivered: Int,
    val opened: Int,
    val dismissed: Int,
    val failed: Int,
    val cancelled: Int,
    val byType: Map<String, Int>
) {
    val deliveryRate: Double
        get() = if (sent > 0) (delivered * 100.0 / sent) else 0.0

    val openRate: Double
        get() = if (delivered > 0) (opened * 100.0 / delivered) else 0.0

    val failureRate: Double
        get() = if (total > 0) (failed * 100.0 / total) else 0.0

    /**
     * Executa uma transacao de escrita devolvendo o valor produzido pelo bloco.
     * A API Java do Realm nao retorna valor de `executeTransaction`.
     */
    private suspend fun <T> writeTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
        var result: T? = null
        realm.executeTransaction { transactionRealm ->
            result = block(transactionRealm)
        }
        @Suppress("UNCHECKED_CAST")
        result as T
    }

}