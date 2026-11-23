package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Subscription
import com.seuapp.cycletrack.domain.model.SubscriptionDisplayStatus
import com.seuapp.cycletrack.domain.model.SubscriptionStatus
import com.seuapp.cycletrack.domain.model.PurchaseResult

/**
 * Interface do repositório de Assinatura.
 * Clean-room - define contrato sem expor implementação.
 */
interface SubscriptionRepository {

    /**
     * Obtém assinatura ativa do usuário.
     */
    suspend fun getActiveSubscription(userId: String): Subscription?

    /**
     * Obtém todas as assinaturas do usuário.
     */
    suspend fun getSubscriptions(userId: String): List<Subscription>

    /**
     * Obtém assinatura por token de compra.
     */
    suspend fun getSubscriptionByPurchaseToken(purchaseToken: String): Subscription?

    /**
     * Obtém assinatura por ID do pedido.
     */
    suspend fun getSubscriptionByOrderId(orderId: String): Subscription?

    /**
     * Obtém assinatura por ID.
     */
    suspend fun getSubscription(subscriptionId: String): Subscription?

    /**
     * Obtém assinaturas que precisam de confirmação (acknowledgment).
     */
    suspend fun getSubscriptionsNeedingAcknowledgment(userId: String): List<Subscription>

    /**
     * Obtém assinaturas expiradas.
     */
    suspend fun getExpiredSubscriptions(userId: String, beforeDate: Long = System.currentTimeMillis()): List<Subscription>

    /**
     * Obtém assinaturas em período de graça.
     */
    suspend fun getSubscriptionsInGracePeriod(userId: String): List<Subscription>

    /**
     * Obtém assinaturas em suspensão de conta.
     */
    suspend fun getSubscriptionsOnAccountHold(userId: String): List<Subscription>

    /**
     * Insere ou atualiza assinatura.
     */
    suspend fun upsertSubscription(subscription: Subscription): Subscription

    /**
     * Cria nova assinatura a partir de compra.
     */
    suspend fun createSubscription(
        userId: String,
        productId: String,
        purchaseToken: String,
        orderId: String,
        purchaseTime: Long,
        expiryTime: Long,
        priceAmountMicros: Long,
        priceCurrencyCode: String,
        priceFormatted: String,
        isTrial: Boolean = false,
        trialStartTime: Long = 0L,
        trialEndTime: Long = 0L,
        platform: String = "google_play",
        platformData: String = ""
    ): Subscription

    /**
     * Atualiza status da assinatura.
     */
    suspend fun updateStatus(subscriptionId: String, status: SubscriptionStatus): Boolean

    /**
     * Atualiza tempo de expiração.
     */
    suspend fun updateExpiryTime(subscriptionId: String, expiryTime: Long): Boolean

    /**
     * Atualiza renovação automática.
     */
    suspend fun updateAutoRenewing(subscriptionId: String, autoRenewing: Boolean): Boolean

    /**
     * Marca assinatura como confirmada (acknowledged).
     */
    suspend fun markAcknowledged(subscriptionId: String): Boolean

    /**
     * Atualiza expiração do período de graça.
     */
    suspend fun updateGracePeriodExpiry(subscriptionId: String, gracePeriodExpiryTime: Long): Boolean

    /**
     * Atualiza expiração da suspensão de conta.
     */
    suspend fun updateAccountHoldExpiry(subscriptionId: String, accountHoldExpiryTime: Long): Boolean

    /**
     * Registra notificação RTDN.
     */
    suspend fun recordNotification(subscriptionId: String, notificationType: String): Boolean

    /**
     * Processa notificação RTDN e atualiza estado da assinatura.
     */
    suspend fun processNotification(
        purchaseToken: String,
        notificationType: String,
        expiryTime: Long? = null,
        autoRenewing: Boolean? = null,
        gracePeriodExpiryTime: Long? = null,
        accountHoldExpiryTime: Long? = null
    ): Boolean

    /**
     * Soft delete.
     */
    suspend fun deleteSubscription(subscriptionId: String): Boolean

    /**
     * Hard delete (LGPD/GDPR).
     */
    suspend fun hardDeleteSubscription(subscriptionId: String): Boolean

    /**
     * Verifica se usuário tem assinatura premium ativa.
     */
    suspend fun hasActivePremium(userId: String): Boolean

    /**
     * Verifica se usuário está em período de teste.
     */
    suspend fun isInTrial(userId: String): Boolean

    /**
     * Obtém status de exibição da assinatura.
     */
    suspend fun getDisplayStatus(userId: String): SubscriptionDisplayStatus

    /**
     * Obtém assinatura para exportação (LGPD/GDPR).
     */
    suspend fun getSubscriptionForExport(userId: String, subscriptionId: String, anonymize: Boolean): Subscription?
}

/**
 * Tipos de notificação RTDN (Real-time Developer Notifications).
 */
object RtdnNotificationType {
    const val SUBSCRIPTION_PURCHASED = "SUBSCRIPTION_PURCHASED"
    const val SUBSCRIPTION_RENEWED = "SUBSCRIPTION_RENEWED"
    const val SUBSCRIPTION_CANCELLED = "SUBSCRIPTION_CANCELLED"
    const val SUBSCRIPTION_PAUSED = "SUBSCRIPTION_PAUSED"
    const val SUBSCRIPTION_REVOKED = "SUBSCRIPTION_REVOKED"
    const val SUBSCRIPTION_EXPIRED = "SUBSCRIPTION_EXPIRED"
    const val SUBSCRIPTION_GRACE_PERIOD_STARTED = "SUBSCRIPTION_GRACE_PERIOD_STARTED"
    const val SUBSCRIPTION_ACCOUNT_HOLD = "SUBSCRIPTION_ACCOUNT_HOLD"
    const val SUBSCRIPTION_RESTARTED = "SUBSCRIPTION_RESTARTED"
    const val SUBSCRIPTION_PRICE_CHANGE_CONFIRMED = "SUBSCRIPTION_PRICE_CHANGE_CONFIRMED"
    const val SUBSCRIPTION_DEFERRED = "SUBSCRIPTION_DEFERRED"
}