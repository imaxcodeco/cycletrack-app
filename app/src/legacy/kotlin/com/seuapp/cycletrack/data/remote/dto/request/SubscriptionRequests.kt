package com.seuapp.cycletrack.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * DTOs para requisições de assinatura.
 */
@Serializable
data class RecordPurchaseRequest(
    val productId: String,
    val purchaseToken: String,
    val orderId: String,
    val purchaseTime: Long,
    val expiryTime: Long,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val priceFormatted: String,
    val isTrial: Boolean = false,
    val trialStartTime: Long = 0,
    val trialEndTime: Long = 0,
    val platform: String = "google_play",
    val platformData: String = ""
)

@Serializable
data class AcknowledgePurchaseRequest(
    val purchaseToken: String
)