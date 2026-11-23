package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Subscription
import com.seuapp.cycletrack.domain.model.SubscriptionStatus
import com.seuapp.cycletrack.domain.usecase.subscription.SubscriptionUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.combine

/**
 * ViewModel para a tela Premium/Assinatura.
 * Gerencia produtos, assinaturas, compras, restore e gerenciamento.
 */
@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val subscriptionUseCases: SubscriptionUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    private val _products = MutableStateFlow<List<SubscriptionProduct>>(emptyList())
    val products = _products.asStateFlow()

    private val _activeSubscription = MutableStateFlow<Subscription?>(null)
    val activeSubscription = _activeSubscription.asStateFlow()

    private val _allSubscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val allSubscriptions = _allSubscriptions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isPurchasing = MutableStateFlow(false)
    val isPurchasing = _isPurchasing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _purchaseResult = MutableStateFlow<PurchaseResult?>(null)
    val purchaseResult = _purchaseResult.asStateFlow()

    private val _isTrialEligible = MutableStateFlow(true)
    val isTrialEligible = _isTrialEligible.asStateFlow()

    // ============ Computed ============

    val isPremium = _activeSubscription.map { it != null && it.status == SubscriptionStatus.ACTIVE }.distinctUntilChanged()

    val isInTrial = _activeSubscription.map { it != null && it.isTrial }.distinctUntilChanged()

    val subscriptionDisplayStatus = _activeSubscription.map { sub ->
        when {
            sub == null -> "Gratuito"
            sub.isTrial -> "Teste grátis (${sub.trialDaysRemaining} dias)"
            sub.status == SubscriptionStatus.ACTIVE -> "Premium ativo"
            sub.status == SubscriptionStatus.GRACE_PERIOD -> "Período de carência"
            sub.status == SubscriptionStatus.ON_HOLD -> "Pagamento pendente"
            sub.status == SubscriptionStatus.CANCELLED -> "Cancelado (ativo até ${sub.formattedExpiryDate})"
            sub.status == SubscriptionStatus.EXPIRED -> "Expirado"
            sub.status == SubscriptionStatus.PAUSED -> "Pausado"
            else -> sub.status.key
        }
    }.distinctUntilChanged()

    val monthlyProduct = _products.map { it.firstOrNull { it.billingPeriod == "P1M" } }.distinctUntilChanged()

    val yearlyProduct = _products.map { it.firstOrNull { it.billingPeriod == "P1Y" } }.distinctUntilChanged()

    val yearlySavingsPercent = combine(monthlyProduct, yearlyProduct) { monthly, yearly ->
        if (monthly != null && yearly != null) {
            val monthlyTotal = monthly.priceAmountMicros / 1_000_000.0 * 12
            val yearlyTotal = yearly.priceAmountMicros / 1_000_000.0
            val savings = ((monthlyTotal - yearlyTotal) / monthlyTotal * 100).roundToInt()
            savings.coerceIn(0, 99)
        } else 0
    }.distinctUntilChanged()

    // ============ Actions ============

    init {
        loadProducts()
        loadSubscriptionStatus()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val products = subscriptionUseCases.queryProductDetails()
                _products.value = products
                
                // Verifica elegibilidade para teste grátis
                val userId = preferencesManager.getUserId()
                if (userId != null) {
                    _isTrialEligible.value = subscriptionUseCases.isTrialEligible(userId)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar produtos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSubscriptionStatus() {
        viewModelScope.launch {
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                val subscription = subscriptionUseCases.getActiveSubscription(userId)
                _activeSubscription.value = subscription
                
                if (subscription != null) {
                    _allSubscriptions.value = subscriptionUseCases.getSubscriptions(userId)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar assinatura"
            }
        }
    }

    /**
     * Inicia fluxo de compra para um produto.
     */
    suspend fun purchaseProduct(productId: String): PurchaseResult {
        _isPurchasing.value = true
        _error.value = null
        _purchaseResult.value = null
        
        return try {
            val userId = preferencesManager.getUserId() ?: return PurchaseResult.Error(
                errorCode = -1,
                errorMessage = "Usuário não logado",
                debugMessage = "No user ID"
            )
            
            // TODO: Implementar com Activity context
            // val result = subscriptionUseCases.launchBillingFlow(activity, productId)
            // _purchaseResult.value = result
            // if (result is PurchaseResult.Success) loadSubscriptionStatus()
            // return result
            
            PurchaseResult.Error(
                errorCode = -1,
                errorMessage = "Funcionalidade em desenvolvimento",
                debugMessage = "Play Billing integration pending"
            )
        } catch (e: Exception) {
            val result = PurchaseResult.Error(
                errorCode = -1,
                errorMessage = e.message ?: "Erro na compra",
                debugMessage = e.toString()
            )
            _purchaseResult.value = result
            result
        } finally {
            _isPurchasing.value = false
        }
    }

    /**
     * Restaura compras anteriores.
     */
    suspend fun restorePurchases(): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            // TODO: Implementar restore via SubscriptionUseCases
            loadSubscriptionStatus()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao restaurar compras"
            false
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Cancela assinatura ativa.
     */
    suspend fun cancelSubscription(): Boolean {
        _isLoading.value = true
        return try {
            val subscription = _activeSubscription.value ?: return false
            val result = subscriptionUseCases.cancelSubscription(subscription.purchaseToken)
            if (result) {
                loadSubscriptionStatus()
            }
            result
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao cancelar assinatura"
            false
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Gerencia assinatura (abre Play Store).
     */
    fun manageSubscription() {
        // TODO: Abrir Play Store subscriptions page
        // val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions"))
        // context.startActivity(intent)
    }

    /**
     * Verifica e atualiza status da assinatura (para RTDN/webhook).
     */
    suspend fun verifyAndUpdateSubscription(): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val subscriptions = subscriptionUseCases.getSubscriptionsNeedingAcknowledgment(userId)
            
            for (sub in subscriptions) {
                subscriptionUseCases.acknowledgePurchase(sub.purchaseToken)
            }
            
            loadSubscriptionStatus()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao verificar assinatura"
            false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearPurchaseResult() {
        _purchaseResult.value = null
    }

    // ============ Premium Features ============

    data class PremiumFeature(
        val id: String,
        val icon: String,
        val title: String,
        val description: String,
        val category: String,
        val isNew: Boolean = false
    )

    val premiumFeatures = listOf(
        PremiumFeature(
            id = "ai_insights",
            icon = "🤖",
            title = "Insights com IA",
            description = "Previsões avançadas e análise de padrões com machine learning",
            category = "Insights"
        ),
        PremiumFeature(
            id = "advanced_reports",
            icon = "📊",
            title = "Relatórios médicos completos",
            description = "PDFs detalhados para levar ao ginecologista/obstetra",
            category = "Insights"
        ),
        PremiumFeature(
            id = "unlimited_export",
            icon = "📤",
            title = "Exportação ilimitada",
            description = "JSON, CSV, PDF sem restrições de frequência",
            category = "Dados"
        ),
        PremiumFeature(
            id = "custom_symptoms",
            icon = "✏️",
            title = "Sintomas personalizados",
            description = "Crie seus próprios sintomas e categorias",
            category = "Personalização"
        ),
        PremiumFeature(
            id = "custom_reminders",
            icon = "⏰",
            title = "Lembretes personalizados",
            description = "Notificações flexíveis para qualquer evento",
            category = "Personalização"
        ),
        PremiumFeature(
            id = "themes",
            icon = "🎨",
            title = "Temas exclusivos",
            description = "Cores, ícones e estilos premium",
            category = "Personalização"
        ),
        PremiumFeature(
            id = "premium_content",
            icon = "📚",
            title = "Conteúdo exclusivo",
            description = "Artigos e vídeos de especialistas em saúde feminina",
            category = "Conteúdo"
        ),
        PremiumFeature(
            id = "partner_premium",
            icon = "👫",
            title = "Compartilhamento avançado",
            description = "Controle granular do que o parceiro vê",
            category = "Parceiro"
        ),
        PremiumFeature(
            id = "pregnancy_advanced",
            icon = "🤰",
            title = "Gravidez avançada",
            description = "Contração timer, kick counter, checklist hospitalar",
            category = "Gravidez"
        ),
        PremiumFeature(
            id = "no_ads",
            icon = "🚫",
            title = "Sem anúncios",
            description = "Experiência 100% livre de publicidade",
            category = "Experiência"
        ),
        PremiumFeature(
            id = "priority_support",
            icon = "⭐",
            title = "Suporte prioritário",
            description = "Atendimento rápido via chat/email",
            category = "Suporte"
        ),
        PremiumFeature(
            id = "data_backup",
            icon = "☁️",
            title = "Backup automático na nuvem",
            description = "Sincronização contínua e backup diário",
            category = "Dados"
        )
    )

    val premiumCategories = premiumFeatures
        .groupBy { it.category }
        .map { (category, features) -> 
            PremiumCategory(category, features)
        }
        .sortedBy { it.category }

    data class PremiumCategory(
        val name: String,
        val features: List<PremiumFeature>
    )
}

// ============ Data Classes ============

data class SubscriptionProduct(
    val productId: String,
    val title: String,
    val description: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val priceFormatted: String,
    val billingPeriod: String, // P1M, P1Y
    val freeTrialPeriod: String? = null, // P7D, P14D
    val introductoryPriceAmountMicros: Long? = null,
    val introductoryPricePeriod: String? = null
) {
    val priceInCurrency: Double = priceAmountMicros / 1_000_000.0
    val hasFreeTrial: Boolean = freeTrialPeriod != null && freeTrialPeriod.isNotBlank()
    val trialDays: Int = freeTrialPeriod?.removePrefix("P")?.removeSuffix("D")?.toIntOrNull() ?: 0
}

sealed interface PurchaseResult {
    data class Success(
        val purchaseToken: String,
        val orderId: String,
        val productId: String,
        val purchaseTime: Long,
        val isAcknowledged: Boolean
    ) : PurchaseResult

    data class Error(
        val errorCode: Int,
        val errorMessage: String,
        val debugMessage: String
    ) : PurchaseResult

    data class Pending(
        val purchaseToken: String,
        val productId: String
    ) : PurchaseResult

    data class Cancelled(
        val productId: String
    ) : PurchaseResult
}

data class VerifiedPurchase(
    val purchaseToken: String,
    val orderId: String,
    val productId: String,
    val purchaseTime: Long,
    val expiryTime: Long?,
    val isAcknowledged: Boolean,
    val isAutoRenewing: Boolean,
    val status: String
)

data class VerifiedSubscription(
    val productId: String,
    val purchaseToken: String,
    val startTime: Long,
    val expiryTime: Long,
    val autoRenewing: Boolean,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val countryCode: String,
    val developerPayload: String?
)