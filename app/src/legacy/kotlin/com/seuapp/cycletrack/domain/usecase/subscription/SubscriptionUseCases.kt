package com.seuapp.cycletrack.domain.usecase.subscription

import com.seuapp.cycletrack.domain.model.Subscription
import com.seuapp.cycletrack.domain.model.SubscriptionDisplayStatus
import com.seuapp.cycletrack.domain.model.SubscriptionStatus
import com.seuapp.cycletrack.domain.model.PurchaseResult
import com.seuapp.cycletrack.domain.model.SubscriptionProduct
import com.seuapp.cycletrack.domain.repository.SubscriptionRepository
import com.seuapp.cycletrack.domain.repository.UserProfileRepository

/**
 * Casos de uso para Assinaturas (Play Billing v5+).
 * Clean-room - gerencia compras, validação e status premium.
 */
class SubscriptionUseCases(
    private val subscriptionRepository: SubscriptionRepository,
    private val userProfileRepository: UserProfileRepository,
    private val billingClient: BillingClientWrapper
) {

    /**
     * Obtém produtos disponíveis para compra.
     */
    suspend fun getAvailableProducts(): List<SubscriptionProduct> {
        return billingClient.queryProductDetails()
    }

    /**
     * Inicia fluxo de compra.
     */
    suspend fun launchPurchaseFlow(
        activity: android.app.Activity,
        productId: String
    ): PurchaseResult {
        return billingClient.launchBillingFlow(activity, productId)
    }

    /**
     * Processa compra bem-sucedida.
     */
    suspend fun handlePurchase(
        userId: String,
        purchaseToken: String,
        orderId: String,
        productId: String,
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
    ): Subscription {
        val subscription = subscriptionRepository.createSubscription(
            userId = userId,
            productId = productId,
            purchaseToken = purchaseToken,
            orderId = orderId,
            purchaseTime = purchaseTime,
            expiryTime = expiryTime,
            priceAmountMicros = priceAmountMicros,
            priceCurrencyCode = priceCurrencyCode,
            priceFormatted = priceFormatted,
            isTrial = isTrial,
            trialStartTime = trialStartTime,
            trialEndTime = trialEndTime,
            platform = platform,
            platformData = platformData
        )

        // Atualiza perfil do usuário
        userProfileRepository.updateSubscriptionStatus(
            userId = userId,
            status = if (isTrial) SubscriptionStatus.TRIAL.key else SubscriptionStatus.PREMIUM.key,
            productId = productId,
            expiresAt = if (isTrial) trialEndTime else expiryTime,
            autoRenew = true,
            platform = platform
        )

        return subscription
    }

    /**
     * Confirma compra (acknowledgment).
     */
    suspend fun acknowledgePurchase(purchaseToken: String): Boolean {
        val subscription = subscriptionRepository.getSubscriptionByPurchaseToken(purchaseToken)
            ?: return false
        
        val acknowledged = billingClient.acknowledgePurchase(purchaseToken)
        if (acknowledged) {
            subscriptionRepository.markAcknowledged(subscription.id)
        }
        return acknowledged
    }

    /**
     * Obtém status de exibição da assinatura.
     */
    suspend fun getSubscriptionStatus(userId: String): SubscriptionDisplayStatus {
        return subscriptionRepository.getDisplayStatus(userId)
    }

    /**
     * Verifica se usuário tem premium ativo.
     */
    suspend fun hasActivePremium(userId: String): Boolean {
        return subscriptionRepository.hasActivePremium(userId)
    }

    /**
     * Verifica se usuário está em trial.
     */
    suspend fun isInTrial(userId: String): Boolean {
        return subscriptionRepository.isInTrial(userId)
    }

    /**
     * Obtém assinatura ativa.
     */
    suspend fun getActiveSubscription(userId: String): Subscription? {
        return subscriptionRepository.getActiveSubscription(userId)
    }

    /**
     * Restaura compras (para novo dispositivo/reinstalação).
     */
    suspend fun restorePurchases(userId: String): List<Subscription> {
        val purchases = billingClient.queryPurchases()
        val subscriptions = mutableListOf<Subscription>()
        
        for (purchase in purchases) {
            val existing = subscriptionRepository.getSubscriptionByPurchaseToken(purchase.purchaseToken)
            if (existing == null) {
                val subscription = subscriptionRepository.createSubscription(
                    userId = userId,
                    productId = purchase.productId,
                    purchaseToken = purchase.purchaseToken,
                    orderId = purchase.orderId,
                    purchaseTime = purchase.purchaseTime,
                    expiryTime = purchase.expiryTime,
                    priceAmountMicros = purchase.priceAmountMicros,
                    priceCurrencyCode = purchase.priceCurrencyCode,
                    priceFormatted = purchase.priceFormatted,
                    isTrial = purchase.isTrial,
                    trialStartTime = purchase.trialStartTime,
                    trialEndTime = purchase.trialEndTime,
                    platform = purchase.platform
                )
                subscriptions.add(subscription)
            }
        }

        // Atualiza status no perfil
        val activeSub = subscriptions.firstOrNull { it.isActive }
        if (activeSub != null) {
            userProfileRepository.updateSubscriptionStatus(
                userId = userId,
                status = if (activeSub.isTrial) SubscriptionStatus.TRIAL.key else SubscriptionStatus.PREMIUM.key,
                productId = activeSub.productId,
                expiresAt = if (activeSub.isTrial) activeSub.trialEndTime else activeSub.expiryTime,
                autoRenew = activeSub.autoRenewing,
                platform = activeSub.platform
            )
        }

        return subscriptions
    }

    /**
     * Cancela assinatura (usuario cancela renovação).
     */
    suspend fun cancelSubscription(userId: String): Boolean {
        val subscription = subscriptionRepository.getActiveSubscription(userId)
            ?: return false
        
        val cancelled = billingClient.cancelSubscription(subscription.purchaseToken)
        if (cancelled) {
            subscriptionRepository.updateStatus(subscription.id, SubscriptionStatus.CANCELLED)
            subscriptionRepository.updateAutoRenewing(subscription.id, false)
            userProfileRepository.updateSubscriptionStatus(
                userId = userId,
                status = SubscriptionStatus.CANCELLED.key,
                productId = subscription.productId
            )
        }
        return cancelled
    }

    /**
     * Processa notificação RTDN do servidor.
     */
    suspend fun processRtdnNotification(
        purchaseToken: String,
        notificationType: String,
        expiryTime: Long? = null,
        autoRenewing: Boolean? = null,
        gracePeriodExpiryTime: Long? = null,
        accountHoldExpiryTime: Long? = null
    ): Boolean {
        return subscriptionRepository.processNotification(
            purchaseToken = purchaseToken,
            notificationType = notificationType,
            expiryTime = expiryTime,
            autoRenewing = autoRenewing,
            gracePeriodExpiryTime = gracePeriodExpiryTime,
            accountHoldExpiryTime = accountHoldExpiryTime
        )
    }

    /**
     * Sincroniza assinaturas com servidor (verifica status real).
     */
    suspend fun syncSubscriptions(userId: String): List<Subscription> {
        val localSubs = subscriptionRepository.getSubscriptions(userId)
        val synced = mutableListOf<Subscription>()
        
        for (sub in localSubs) {
            val verified = billingClient.verifySubscription(sub.purchaseToken)
            if (verified != null) {
                val updated = sub.copy(
                    expiryTime = verified.expiryTime,
                    autoRenewing = verified.autoRenewing,
                    status = verified.status,
                    gracePeriodExpiryTime = verified.gracePeriodExpiryTime,
                    accountHoldExpiryTime = verified.accountHoldExpiryTime
                )
                subscriptionRepository.upsertSubscription(updated)
                synced.add(updated)
            }
        }
        
        return synced
    }
}

/**
 * Wrapper para BillingClient (implementação na camada de dados).
 */
interface BillingClientWrapper {
    suspend fun queryProductDetails(): List<SubscriptionProduct>
    suspend fun launchBillingFlow(activity: android.app.Activity, productId: String): PurchaseResult
    suspend fun acknowledgePurchase(purchaseToken: String): Boolean
    suspend fun queryPurchases(): List<VerifiedPurchase>
    suspend fun cancelSubscription(purchaseToken: String): Boolean
    suspend fun verifySubscription(purchaseToken: String): VerifiedSubscription?
}

/**
 * Compra verificada do Play Billing.
 */
data class VerifiedPurchase(
    val productId: String,
    val purchaseToken: String,
    val orderId: String,
    val purchaseTime: Long,
    val expiryTime: Long,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val priceFormatted: String,
    val isTrial: Boolean,
    val trialStartTime: Long,
    val trialEndTime: Long,
    val platform: String
)

/**
 * Assinatura verificada do servidor.
 */
data class VerifiedSubscription(
    val expiryTime: Long,
    val autoRenewing: Boolean,
    val status: SubscriptionStatus,
    val gracePeriodExpiryTime: Long = 0L,
    val accountHoldExpiryTime: Long = 0L
)