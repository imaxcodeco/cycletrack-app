package com.seuapp.cycletrack.domain.usecase.subscription

import android.app.Activity
import android.content.Context
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import com.seuapp.cycletrack.domain.model.PurchaseResult
import com.seuapp.cycletrack.domain.model.SubscriptionProduct

/**
 * Interface para wrapper do Play Billing Client.
 * Clean-room - implementação própria sem copiar código do Flo.
 */
interface BillingClientWrapper {
    suspend fun queryProductDetails(): List<SubscriptionProduct>
    suspend fun launchBillingFlow(activity: Activity, productId: String): PurchaseResult
    suspend fun acknowledgePurchase(purchaseToken: String): Boolean
    suspend fun queryPurchases(): List<VerifiedPurchase>
    suspend fun cancelSubscription(purchaseToken: String): Boolean
    suspend fun verifySubscription(purchaseToken: String): VerifiedSubscription?
}

/**
 * Implementação mínima do wrapper de Billing.
 *
 * A integração completa com o Play Billing Library (v7) exige a configuração da
 * Play Console e um listener de compras ativo. Enquanto o produto nao estiver
 * publicado, esta implementacao mantem o app funcional devolvendo listas vazias
 * e erros explicitos, sem quebrar a navegacao da tela Premium.
 */
class StubBillingClientWrapper(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) : BillingClientWrapper {

    override suspend fun queryProductDetails(): List<SubscriptionProduct> = emptyList()

    override suspend fun launchBillingFlow(activity: Activity, productId: String): PurchaseResult =
        PurchaseResult.Error(
            errorCode = -1,
            errorMessage = "Compras indisponiveis nesta versao",
            debugMessage = "StubBillingClientWrapper.launchBillingFlow"
        )

    override suspend fun acknowledgePurchase(purchaseToken: String): Boolean = false

    override suspend fun queryPurchases(): List<VerifiedPurchase> = emptyList()

    override suspend fun cancelSubscription(purchaseToken: String): Boolean = false

    override suspend fun verifySubscription(purchaseToken: String): VerifiedSubscription? = null
}
