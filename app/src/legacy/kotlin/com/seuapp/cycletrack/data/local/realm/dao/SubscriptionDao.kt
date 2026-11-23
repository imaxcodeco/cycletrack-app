package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.SubscriptionRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.seuapp.cycletrack.data.local.realm.detached

/**
 * DAO for SubscriptionRealm operations.
 * Clean-room implementation for Play Billing Library v5+ subscription management.
 */
class SubscriptionDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get active subscription for a user.
     */
    suspend fun getActiveSubscription(userId: String): SubscriptionRealm? = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("status", SubscriptionRealm.STATUS_ACTIVE)
            .findFirst()
            .detached()
    }

    /**
     * Get all subscriptions for a user.
     */
    suspend fun getSubscriptions(userId: String): List<SubscriptionRealm> = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .sort("purchaseTime", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get subscription by purchase token.
     */
    suspend fun getSubscriptionByPurchaseToken(purchaseToken: String): SubscriptionRealm? = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("purchaseToken", purchaseToken)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Get subscription by order ID.
     */
    suspend fun getSubscriptionByOrderId(orderId: String): SubscriptionRealm? = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("orderId", orderId)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Get subscription by ID.
     */
    suspend fun getSubscription(subscriptionId: String): SubscriptionRealm? = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("id", subscriptionId)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Get subscriptions needing acknowledgment.
     */
    suspend fun getSubscriptionsNeedingAcknowledgment(userId: String): List<SubscriptionRealm> = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("acknowledged", false)
            .in("status", arrayOf(SubscriptionRealm.STATUS_ACTIVE, SubscriptionRealm.STATUS_PENDING))
            .findAll()
            .detached()
    }

    /**
     * Get expired subscriptions.
     */
    suspend fun getExpiredSubscriptions(userId: String, beforeDate: Long = System.currentTimeMillis()): List<SubscriptionRealm> = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .lessThan("expiryTime", beforeDate)
            .in("status", arrayOf(SubscriptionRealm.STATUS_ACTIVE, SubscriptionRealm.STATUS_PENDING, SubscriptionRealm.STATUS_PAUSED))
            .findAll()
    }

    /**
     * Get subscriptions in grace period.
     */
    suspend fun getSubscriptionsInGracePeriod(userId: String): List<SubscriptionRealm> = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .greaterThan("gracePeriodExpiryTime", System.currentTimeMillis())
            .findAll()
            .detached()
    }

    /**
     * Get subscriptions on account hold.
     */
    suspend fun getSubscriptionsOnAccountHold(userId: String): List<SubscriptionRealm> = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .greaterThan("accountHoldExpiryTime", System.currentTimeMillis())
            .findAll()
            .detached()
    }

    /**
     * Insert or update a subscription.
     */
    suspend fun upsertSubscription(subscription: SubscriptionRealm): SubscriptionRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            subscription.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(subscription)
        }
    }

    /**
     * Create a new subscription from purchase.
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
        platform: String = SubscriptionRealm.PLATFORM_GOOGLE_PLAY,
        platformData: String = ""
    ): SubscriptionRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = SubscriptionRealm().apply {
                id = java.util.UUID.randomUUID().toString()
                this.userId = userId
                this.productId = productId
                this.productType = "subs"
                this.purchaseToken = purchaseToken
                this.orderId = orderId
                this.purchaseTime = purchaseTime
                this.expiryTime = expiryTime
                this.autoRenewing = true
                this.isTrial = isTrial
                this.trialStartTime = trialStartTime
                this.trialEndTime = trialEndTime
                this.priceAmountMicros = priceAmountMicros
                this.priceCurrencyCode = priceCurrencyCode
                this.priceFormatted = priceFormatted
                this.platform = platform
                this.platformData = platformData
                this.status = if (isTrial) SubscriptionRealm.STATUS_PENDING else SubscriptionRealm.STATUS_ACTIVE
                this.purchaseState = 1 // Purchased
                this.createdAt = System.currentTimeMillis()
                this.updatedAt = System.currentTimeMillis()
                this.version = 1
            }
            realm.copyToRealm(subscription)
        }
    }

    /**
     * Update subscription status.
     */
    suspend fun updateStatus(subscriptionId: String, status: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = realm.where(SubscriptionRealm::class.java)
                .equalTo("id", subscriptionId)
                .findFirst()
            subscription?.let {
                it.status = status
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Update subscription expiry time.
     */
    suspend fun updateExpiryTime(subscriptionId: String, expiryTime: Long): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = realm.where(SubscriptionRealm::class.java)
                .equalTo("id", subscriptionId)
                .findFirst()
            subscription?.let {
                it.expiryTime = expiryTime
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Update auto-renewing status.
     */
    suspend fun updateAutoRenewing(subscriptionId: String, autoRenewing: Boolean): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = realm.where(SubscriptionRealm::class.java)
                .equalTo("id", subscriptionId)
                .findFirst()
            subscription?.let {
                it.autoRenewing = autoRenewing
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Mark subscription as acknowledged.
     */
    suspend fun markAcknowledged(subscriptionId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = realm.where(SubscriptionRealm::class.java)
                .equalTo("id", subscriptionId)
                .findFirst()
            subscription?.let {
                it.acknowledged = true
                it.acknowledgedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Update grace period expiry.
     */
    suspend fun updateGracePeriodExpiry(subscriptionId: String, gracePeriodExpiryTime: Long): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = realm.where(SubscriptionRealm::class.java)
                .equalTo("id", subscriptionId)
                .findFirst()
            subscription?.let {
                it.gracePeriodExpiryTime = gracePeriodExpiryTime
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Update account hold expiry.
     */
    suspend fun updateAccountHoldExpiry(subscriptionId: String, accountHoldExpiryTime: Long): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = realm.where(SubscriptionRealm::class.java)
                .equalTo("id", subscriptionId)
                .findFirst()
            subscription?.let {
                it.accountHoldExpiryTime = accountHoldExpiryTime
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Record RTDN notification.
     */
    suspend fun recordNotification(subscriptionId: String, notificationType: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = realm.where(SubscriptionRealm::class.java)
                .equalTo("id", subscriptionId)
                .findFirst()
            subscription?.let {
                it.lastNotificationType = notificationType
                it.lastNotificationTime = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Process RTDN notification and update subscription state.
     */
    suspend fun processNotification(
        purchaseToken: String,
        notificationType: String,
        expiryTime: Long? = null,
        autoRenewing: Boolean? = null,
        gracePeriodExpiryTime: Long? = null,
        accountHoldExpiryTime: Long? = null
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = realm.where(SubscriptionRealm::class.java)
                .equalTo("purchaseToken", purchaseToken)
                .findFirst()
            subscription?.let {
                it.lastNotificationType = notificationType
                it.lastNotificationTime = System.currentTimeMillis()

                when (notificationType) {
                    SubscriptionRealm.RTDN_SUBSCRIPTION_PURCHASED -> {
                        it.status = SubscriptionRealm.STATUS_ACTIVE
                        it.purchaseState = 1
                    }
                    SubscriptionRealm.RTDN_SUBSCRIPTION_RENEWED -> {
                        it.status = SubscriptionRealm.STATUS_ACTIVE
                        expiryTime?.let { it.expiryTime = it }
                    }
                    SubscriptionRealm.RTDN_SUBSCRIPTION_CANCELLED -> {
                        it.autoRenewing = false
                        it.status = SubscriptionRealm.STATUS_CANCELLED
                    }
                    SubscriptionRealm.RTDN_SUBSCRIPTION_PAUSED -> {
                        it.status = SubscriptionRealm.STATUS_PAUSED
                    }
                    SubscriptionRealm.RTDN_SUBSCRIPTION_REVOKED -> {
                        it.status = SubscriptionRealm.STATUS_REVOKED
                    }
                    SubscriptionRealm.RTDN_SUBSCRIPTION_EXPIRED -> {
                        it.status = SubscriptionRealm.STATUS_EXPIRED
                    }
                    SubscriptionRealm.RTDN_SUBSCRIPTION_GRACE_PERIOD_STARTED -> {
                        gracePeriodExpiryTime?.let { it.gracePeriodExpiryTime = it }
                    }
                    SubscriptionRealm.RTDN_SUBSCRIPTION_ACCOUNT_HOLD -> {
                        accountHoldExpiryTime?.let { it.accountHoldExpiryTime = it }
                    }
                    SubscriptionRealm.RTDN_SUBSCRIPTION_RESTARTED -> {
                        it.status = SubscriptionRealm.STATUS_ACTIVE
                        it.autoRenewing = true
                    }
                }

                expiryTime?.let { it.expiryTime = it }
                autoRenewing?.let { it.autoRenewing = it }
                gracePeriodExpiryTime?.let { it.gracePeriodExpiryTime = it }
                accountHoldExpiryTime?.let { it.accountHoldExpiryTime = it }

                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Soft delete a subscription.
     */
    suspend fun deleteSubscription(subscriptionId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = realm.where(SubscriptionRealm::class.java)
                .equalTo("id", subscriptionId)
                .findFirst()
            subscription?.let {
                it.deletedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Hard delete a subscription (GDPR/LGPD).
     */
    suspend fun hardDeleteSubscription(subscriptionId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val subscription = realm.where(SubscriptionRealm::class.java)
                .equalTo("id", subscriptionId)
                .findFirst()
            subscription?.deleteFromRealm() ?: false
        }
    }

    /**
     * Check if user has active premium subscription.
     */
    suspend fun hasActivePremium(userId: String): Boolean = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .in("status", arrayOf(SubscriptionRealm.STATUS_ACTIVE, SubscriptionRealm.STATUS_PENDING, SubscriptionRealm.STATUS_PAUSED))
            .greaterThan("expiryTime", System.currentTimeMillis())
            .count() > 0
    }

    /**
     * Check if user is in trial.
     */
    suspend fun isInTrial(userId: String): Boolean = withContext(Dispatchers.IO) {
        realm.where(SubscriptionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("isTrial", true)
            .in("status", arrayOf(SubscriptionRealm.STATUS_ACTIVE, SubscriptionRealm.STATUS_PENDING))
            .greaterThan("trialEndTime", System.currentTimeMillis())
            .count() > 0
    }

    /**
     * Get subscription status for display.
     */
    suspend fun getDisplayStatus(userId: String): SubscriptionDisplayStatus = withContext(Dispatchers.IO) {
        val active = getActiveSubscription(userId)
        val trial = realm.where(SubscriptionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("isTrial", true)
            .in("status", arrayOf(SubscriptionRealm.STATUS_ACTIVE, SubscriptionRealm.STATUS_PENDING))
            .greaterThan("trialEndTime", System.currentTimeMillis())
            .findFirst()

        return when {
            trial != null -> SubscriptionDisplayStatus(
                isPremium = true,
                isTrial = true,
                productId = trial.productId,
                expiresAt = trial.trialEndTime,
                autoRenewing = trial.autoRenewing,
                platform = trial.platform
            )
            active != null -> SubscriptionDisplayStatus(
                isPremium = true,
                isTrial = false,
                productId = active.productId,
                expiresAt = active.expiryTime,
                autoRenewing = active.autoRenewing,
                platform = active.platform
            )
            else -> SubscriptionDisplayStatus(
                isPremium = false,
                isTrial = false,
                productId = "",
                expiresAt = 0L,
                autoRenewing = false,
                platform = ""
            )
        }
    }
}

/**
 * Data class for subscription display status.
 */
data class SubscriptionDisplayStatus(
    val isPremium: Boolean,
    val isTrial: Boolean,
    val productId: String,
    val expiresAt: Long,
    val autoRenewing: Boolean,
    val platform: String
) {
    val isExpired: Boolean
        get() = expiresAt > 0 && expiresAt < System.currentTimeMillis()

    val daysUntilExpiry: Int
        get() = if (expiresAt > 0) {
            ((expiresAt - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
        } else 0

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