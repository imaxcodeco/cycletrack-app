package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de assinatura.
 */
@Serializable
data class SubscriptionStatusResponse(
    val isPremium: Boolean,
    val isTrial: Boolean,
    val productId: String,
    val expiresAt: Long,
    val autoRenewing: Boolean,
    val platform: String,
    val status: String // active, cancelled, expired, trial, grace_period, account_hold
)

@Serializable
data class SubscriptionProductsResponse(
    val products: List<SubscriptionProductResponse>
)

@Serializable
data class SubscriptionProductResponse(
    val productId: String,
    val name: String,
    val description: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val priceFormatted: String,
    val subscriptionPeriod: String, // P1M, P1Y
    val freeTrialPeriod: String?, // P7D, P14D
    val introductoryPriceAmountMicros: Long = 0,
    val introductoryPricePeriod: String = "",
    val tags: List<String>
)

@Serializable
data class SubscriptionResponse(
    val id: String,
    val userId: String,
    val productId: String,
    val productType: String,
    val basePlanId: String,
    val offerId: String,
    val purchaseToken: String,
    val orderId: String,
    val packageName: String,
    val status: String,
    val purchaseState: Int,
    val purchaseTime: Long,
    val expiryTime: Long,
    val autoRenewing: Boolean,
    val gracePeriodExpiryTime: Long,
    val accountHoldExpiryTime: Long,
    val isTrial: Boolean,
    val trialStartTime: Long,
    val trialEndTime: Long,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val priceFormatted: String,
    val platform: String,
    val platformData: String,
    val acknowledged: Boolean,
    val acknowledgedAt: Long,
    val lastNotificationType: String,
    val lastNotificationTime: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val serverId: String,
    val lastSyncedAt: Long,
    val version: Int
)

@Serializable
data class SubscriptionListResponse(
    val subscriptions: List<SubscriptionResponse>,
    val total: Int
)