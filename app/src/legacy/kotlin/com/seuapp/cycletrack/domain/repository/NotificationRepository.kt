package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Notification

/**
 * Interface do repositório de Notificações.
 * Clean-room - define contrato sem expor implementação.
 */
interface NotificationRepository {

    /**
     * Obtém todas as notificações do usuário.
     */
    suspend fun getNotifications(userId: String): List<Notification>

    /**
     * Obtém notificações pendentes (agendadas mas não enviadas).
     */
    suspend fun getPendingNotifications(userId: String, beforeTime: Long = System.currentTimeMillis()): List<Notification>

    /**
     * Obtém notificações por tipo.
     */
    suspend fun getNotificationsByType(userId: String, type: String): List<Notification>

    /**
     * Obtém notificações por status.
     */
    suspend fun getNotificationsByStatus(userId: String, status: String): List<Notification>

    /**
     * Obtém notificações recorrentes.
     */
    suspend fun getRecurringNotifications(userId: String): List<Notification>

    /**
     * Obtém notificações precisando de retry.
     */
    suspend fun getNotificationsNeedingRetry(beforeTime: Long = System.currentTimeMillis()): List<Notification>

    /**
     * Obtém notificação por ID.
     */
    suspend fun getNotification(notificationId: String): Notification?

    /**
     * Insere ou atualiza notificação.
     */
    suspend fun upsertNotification(notification: Notification): Notification

    /**
     * Insere múltiplas notificações.
     */
    suspend fun upsertNotifications(notifications: List<Notification>): List<Notification>

    /**
     * Marca como enviada.
     */
    suspend fun markAsSent(notificationId: String): Boolean

    /**
     * Marca como entregue.
     */
    suspend fun markAsDelivered(notificationId: String): Boolean

    /**
     * Marca como aberta.
     */
    suspend fun markAsOpened(notificationId: String): Boolean

    /**
     * Marca como dispensada.
     */
    suspend fun markAsDismissed(notificationId: String): Boolean

    /**
     * Marca como falhada e agenda retry.
     */
    suspend fun markAsFailed(notificationId: String, reason: String): Boolean

    /**
     * Cancela notificação.
     */
    suspend fun cancelNotification(notificationId: String): Boolean

    /**
     * Cancela todas notificações de um tipo para usuário.
     */
    suspend fun cancelNotificationsByType(userId: String, type: String): Int

    /**
     * Limpa notificações antigas enviadas/entregues.
     */
    suspend fun cleanupOldNotifications(olderThan: Long = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000): Int

    /**
     * Deleta notificações falhadas que excederam max retries.
     */
    suspend fun deleteFailedNotifications(): Int

    /**
     * Soft delete.
     */
    suspend fun deleteNotification(notificationId: String): Boolean

    /**
     * Hard delete (LGPD/GDPR).
     */
    suspend fun hardDeleteNotification(notificationId: String): Boolean

    /**
     * Obtém estatísticas de notificações.
     */
    suspend fun getNotificationStats(userId: String): NotificationStats
}

/**
 * Modelo de domínio para Notificação.
 */
@Serializable
data class Notification(
    val id: String,
    val userId: String,
    val type: String,
    val scheduledTime: Long,
    val title: String,
    val body: String,
    val icon: String = "",
    val color: Int = 0,
    val channelId: String = "default",
    val priority: Int = 0,
    val category: String = "",
    val action1Label: String = "",
    val action1IntentAction: String = "",
    val action2Label: String = "",
    val action2IntentAction: String = "",
    val deepLink: String = "",
    val isRecurring: Boolean = false,
    val recurrenceRule: String = "",
    val recurrenceEndDate: Long = 0L,
    val targetCycleId: String = "",
    val targetPregnancyId: String = "",
    val targetPartnerId: String = "",
    val status: String = Notification.STATUS_SCHEDULED,
    val sentAt: Long = 0L,
    val deliveredAt: Long = 0L,
    val openedAt: Long = 0L,
    val dismissedAt: Long = 0L,
    val failureReason: String = "",
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val nextRetryAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: String = "",
    val lastSyncedAt: Long = 0L,
    val version: Int = 1
) {
    companion object {
        const val TYPE_PERIOD_START = "period_start"
        const val TYPE_PERIOD_END = "period_end"
        const val TYPE_FERTILE_WINDOW_START = "fertile_window_start"
        const val TYPE_FERTILE_WINDOW_END = "fertile_window_end"
        const val TYPE_OVULATION = "ovulation"
        const val TYPE_PMS_START = "pms_start"
        const val TYPE_PMS_END = "pms_end"
        const val TYPE_PILL = "pill"
        const val TYPE_APPOINTMENT = "appointment"
        const val TYPE_PREGNANCY_MILESTONE = "pregnancy_milestone"
        const val TYPE_PREGNANCY_APPOINTMENT = "pregnancy_appointment"
        const val TYPE_PARTNER_PERIOD_START = "partner_period_start"
        const val TYPE_PARTNER_FERTILE_WINDOW = "partner_fertile_window"
        const val TYPE_PARTNER_OVULATION = "partner_ovulation"
        const val TYPE_INSIGHT = "insight"
        const val TYPE_CONTENT = "content"
        const val TYPE_SYNC = "sync"
        const val TYPE_BACKUP = "backup"
        const val TYPE_EXPORT_READY = "export_ready"

        const val STATUS_SCHEDULED = "scheduled"
        const val STATUS_SENT = "sent"
        const val STATUS_DELIVERED = "delivered"
        const val STATUS_OPENED = "opened"
        const val STATUS_DISMISSED = "dismissed"
        const val STATUS_CANCELLED = "cancelled"
        const val STATUS_FAILED = "failed"

        const val CHANNEL_PERIOD = "period_reminders"
        const val CHANNEL_FERTILITY = "fertility_reminders"
        const val CHANNEL_PILL = "pill_reminders"
        const val CHANNEL_PREGNANCY = "pregnancy_reminders"
        const val CHANNEL_PARTNER = "partner_notifications"
        const val CHANNEL_INSIGHTS = "insights"
        const val CHANNEL_CONTENT = "content"
        const val CHANNEL_SYSTEM = "system"
    }
}

/**
 * Estatísticas de notificações.
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
}