package com.seuapp.cycletrack.domain.model

import kotlinx.serialization.Serializable

/**
 * Modelo de domínio para Assinatura.
 * Clean-room para Play Billing Library v5+.
 */
@Serializable
data class Subscription(
    val id: String,
    val userId: String,
    val productId: String,
    val productType: String = "subs",
    val basePlanId: String = "",
    val offerId: String = "",
    val purchaseToken: String,
    val orderId: String,
    val packageName: String = "com.seuapp.cycletrack",
    val status: SubscriptionStatus = SubscriptionStatus.NONE,
    val purchaseState: PurchaseState = PurchaseState.UNKNOWN,
    val purchaseTime: Long = 0L,
    val expiryTime: Long = 0L,
    val autoRenewing: Boolean = true,
    val gracePeriodExpiryTime: Long = 0L,
    val accountHoldExpiryTime: Long = 0L,
    val isTrial: Boolean = false,
    val trialStartTime: Long = 0L,
    val trialEndTime: Long = 0L,
    val priceAmountMicros: Long = 0,
    val priceCurrencyCode: String = "BRL",
    val priceFormatted: String = "",
    val platform: SubscriptionPlatform = SubscriptionPlatform.GOOGLE_PLAY,
    val platformData: String = "",
    val acknowledged: Boolean = false,
    val acknowledgedAt: Long = 0L,
    val lastNotificationType: String = "",
    val lastNotificationTime: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: String = "",
    val lastSyncedAt: Long = 0L,
    val version: Int = 1
) {
    companion object {
        const val PRODUCT_PREMIUM_MONTHLY = "premium_monthly"
        const val PRODUCT_PREMIUM_YEARLY = "premium_yearly"
        const val PRODUCT_PREMIUM_LIFETIME = "premium_lifetime"

        fun createFromPurchase(
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
            platform: SubscriptionPlatform = SubscriptionPlatform.GOOGLE_PLAY,
            platformData: String = ""
        ): Subscription {
            return Subscription(
                id = java.util.UUID.randomUUID().toString(),
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
                platformData = platformData,
                status = if (isTrial) SubscriptionStatus.PENDING else SubscriptionStatus.ACTIVE,
                purchaseState = PurchaseState.PURCHASED,
                autoRenewing = true
            )
        }
    }

    val isActive: Boolean
        get() = status == SubscriptionStatus.ACTIVE && expiryTime > System.currentTimeMillis()

    val isInGracePeriod: Boolean
        get() = gracePeriodExpiryTime > System.currentTimeMillis()

    val isOnAccountHold: Boolean
        get() = accountHoldExpiryTime > System.currentTimeMillis()

    val isTrialActive: Boolean
        get() = isTrial && trialEndTime > System.currentTimeMillis()

    val daysUntilExpiry: Int
        get() = if (expiryTime > 0) {
            ((expiryTime - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
        } else 0

    val isExpiringSoon: Boolean
        get() = daysUntilExpiry > 0 && daysUntilExpiry <= 7

    val needsAcknowledgment: Boolean
        get() = !acknowledged && status in listOf(SubscriptionStatus.ACTIVE, SubscriptionStatus.PENDING)

    val displayStatus: SubscriptionDisplayStatus
        get() = when {
            isTrialActive -> SubscriptionDisplayStatus(
                isPremium = true,
                isTrial = true,
                productId = productId,
                expiresAt = trialEndTime,
                autoRenewing = autoRenewing,
                platform = platform
            )
            isActive -> SubscriptionDisplayStatus(
                isPremium = true,
                isTrial = false,
                productId = productId,
                expiresAt = expiryTime,
                autoRenewing = autoRenewing,
                platform = platform
            )
            else -> SubscriptionDisplayStatus(
                isPremium = false,
                isTrial = false,
                productId = "",
                expiresAt = 0L,
                autoRenewing = false,
                platform = SubscriptionPlatform.NONE
            )
        }
}

enum class SubscriptionStatus(val key: String, val label: String) {
    NONE("none", "Nenhuma"),
    PENDING("pending", "Pendente"),
    ACTIVE("active", "Ativa"),
    PAUSED("paused", "Pausada"),
    CANCELLED("cancelled", "Cancelada"),
    EXPIRED("expired", "Expirada"),
    REVOKED("revoked", "Revogada")
}

enum class PurchaseState(val value: Int, val label: String) {
    UNKNOWN(0, "Desconhecido"),
    PURCHASED(1, "Comprado"),
    PENDING(2, "Pendente")
}

enum class SubscriptionPlatform(val key: String, val label: String) {
    GOOGLE_PLAY("google_play", "Google Play Store"),
    APPLE_APP_STORE("apple_app_store", "Apple App Store"),
    PROMO("promo", "Promocional"),
    TEST("test", "Teste"),
    NONE("none", "Nenhuma")
}

/**
 * Status de exibição da assinatura para UI.
 */
@Serializable
data class SubscriptionDisplayStatus(
    val isPremium: Boolean,
    val isTrial: Boolean,
    val productId: String,
    val expiresAt: Long,
    val autoRenewing: Boolean,
    val platform: SubscriptionPlatform
) {
    val isExpired: Boolean
        get() = expiresAt > 0 && expiresAt < System.currentTimeMillis()

    val daysUntilExpiry: Int
        get() = if (expiresAt > 0) {
            ((expiresAt - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
        } else 0

    val statusLabel: String
        get() = when {
            isTrial -> "Período de Teste (${daysUntilExpiry} dias restantes)"
            isPremium -> if (isExpired) "Expirada" else "Premium (renova em ${daysUntilExpiry} dias)"
            else -> "Gratuito"
        }
}

/**
 * Produto de assinatura disponível.
 */
@Serializable
data class SubscriptionProduct(
    val productId: String,
    val name: String,
    val description: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val priceFormatted: String,
    val subscriptionPeriod: String, // P1M, P1Y
    val freeTrialPeriod: String? = null, // P7D, P14D
    val introductoryPriceAmountMicros: Long = 0,
    val introductoryPricePeriod: String = "",
    val tags: List<String> = emptyList()
) {
    val hasFreeTrial: Boolean
        get() = freeTrialPeriod != null && freeTrialPeriod!.isNotBlank()

    val hasIntroductoryPrice: Boolean
        get() = introductoryPriceAmountMicros > 0
}

/**
 * Resultado de compra.
 */
sealed interface PurchaseResult {
    @Serializable
    data class Success(
        val purchaseToken: String,
        val orderId: String,
        val productId: String,
        val purchaseTime: Long,
        val isAcknowledged: Boolean
    ) : PurchaseResult

    @Serializable
    data class Pending(
        val productId: String
    ) : PurchaseResult

    @Serializable
    data class Error(
        val errorCode: Int,
        val errorMessage: String,
        val debugMessage: String = ""
    ) : PurchaseResult

    @Serializable
    data class UserCancelled(
        val productId: String
    ) : PurchaseResult

    @Serializable
    data class AlreadyOwned(
        val productId: String
    ) : PurchaseResult
}

/**
 * Códigos de erro do Play Billing.
 */
object BillingErrorCode {
    const val OK = 0
    const val USER_CANCELED = 1
    const val SERVICE_UNAVAILABLE = 2
    const val BILLING_UNAVAILABLE = 3
    const val ITEM_UNAVAILABLE = 4
    const val DEVELOPER_ERROR = 5
    const val ERROR = 6
    const val ITEM_ALREADY_OWNED = 7
    const val ITEM_NOT_OWNED = 8
    const val NETWORK_ERROR = 12
    const val FEATURE_NOT_SUPPORTED = 13
    const val SERVICE_DISCONNECTED = -1
}