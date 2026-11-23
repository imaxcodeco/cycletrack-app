package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity representing subscription state and purchase history.
 * Clean-room implementation for Play Billing Library v5+.
 */
@Serializable
open class SubscriptionRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var userId: String = ""

    // Product info
    var productId: String = "" // e.g., "premium_monthly", "premium_yearly"
    var productType: String = "subs" // subs, inapp
    var basePlanId: String = ""
    var offerId: String = ""

    // Purchase details
    var purchaseToken: String = ""
    var orderId: String = ""
    var packageName: String = "com.seuapp.cycletrack"

    // Status
    var status: String = "none" // none, pending, active, paused, cancelled, expired, revoked
    var purchaseState: Int = 0 // 0=unknown, 1=purchased, 2=pending

    // Timing
    var purchaseTime: Long = 0L
    var expiryTime: Long = 0L
    var autoRenewing: Boolean = true
    var gracePeriodExpiryTime: Long = 0L
    var accountHoldExpiryTime: Long = 0L

    // Trial
    var isTrial: Boolean = false
    var trialStartTime: Long = 0L
    var trialEndTime: Long = 0L

    // Price
    var priceAmountMicros: Long = 0
    var priceCurrencyCode: String = "BRL"
    var priceFormatted: String = ""

    // Platform
    var platform: String = "google_play" // google_play, apple_app_store, promo, test
    var platformData: String = "" // JSON with platform-specific fields

    // Acknowledgment
    var acknowledged: Boolean = false
    var acknowledgedAt: Long = 0L

    // RTDN (Real-time Developer Notifications)
    var lastNotificationType: String = "" // SUBSCRIPTION_PURCHASED, SUBSCRIPTION_RENEWED, etc.
    var lastNotificationTime: Long = 0L

    // Metadata
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var deletedAt: Long = 0L
    var serverId: String = ""
    var lastSyncedAt: Long = 0L
    var version: Int = 1

    companion object {
        const val STATUS_NONE = "none"
        const val STATUS_PENDING = "pending"
        const val STATUS_ACTIVE = "active"
        const val STATUS_PAUSED = "paused"
        const val STATUS_CANCELLED = "cancelled"
        const val STATUS_EXPIRED = "expired"
        const val STATUS_REVOKED = "revoked"

        const val PRODUCT_PREMIUM_MONTHLY = "premium_monthly"
        const val PRODUCT_PREMIUM_YEARLY = "premium_yearly"
        const val PRODUCT_PREMIUM_LIFETIME = "premium_lifetime"

        const val PLATFORM_GOOGLE_PLAY = "google_play"
        const val PLATFORM_APPLE_APP_STORE = "apple_app_store"
        const val PLATFORM_PROMO = "promo"
        const val PLATFORM_TEST = "test"

        // RTDN Notification Types
        const val RTDN_SUBSCRIPTION_PURCHASED = "SUBSCRIPTION_PURCHASED"
        const val RTDN_SUBSCRIPTION_RENEWED = "SUBSCRIPTION_RENEWED"
        const val RTDN_SUBSCRIPTION_CANCELLED = "SUBSCRIPTION_CANCELLED"
        const val RTDN_SUBSCRIPTION_PAUSED = "SUBSCRIPTION_PAUSED"
        const val RTDN_SUBSCRIPTION_REVOKED = "SUBSCRIPTION_REVOKED"
        const val RTDN_SUBSCRIPTION_EXPIRED = "SUBSCRIPTION_EXPIRED"
        const val RTDN_SUBSCRIPTION_GRACE_PERIOD_STARTED = "SUBSCRIPTION_GRACE_PERIOD_STARTED"
        const val RTDN_SUBSCRIPTION_ACCOUNT_HOLD = "SUBSCRIPTION_ACCOUNT_HOLD"
        const val RTDN_SUBSCRIPTION_RESTARTED = "SUBSCRIPTION_RESTARTED"
        const val RTDN_SUBSCRIPTION_PRICE_CHANGE_CONFIRMED = "SUBSCRIPTION_PRICE_CHANGE_CONFIRMED"
        const val RTDN_SUBSCRIPTION_DEFERRED = "SUBSCRIPTION_DEFERRED"
    }
}