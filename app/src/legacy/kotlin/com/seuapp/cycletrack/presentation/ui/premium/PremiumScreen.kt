package com.seuapp.cycletrack.presentation.ui.premium

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Chip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seuapp.cycletrack.presentation.state.PremiumViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.coroutines.launch

/**
 * Tela Premium - Assinatura e recursos com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun PremiumScreen(
    onNavigateToFeatures: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToManage: () -> Unit
) {
    val viewModel: PremiumViewModel = hiltViewModel()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val activeSubscription by viewModel.activeSubscription.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val isInTrial by viewModel.isInTrial.collectAsStateWithLifecycle()
    val subscriptionDisplayStatus by viewModel.subscriptionDisplayStatus.collectAsStateWithLifecycle()
    val monthlyProduct by viewModel.monthlyProduct.collectAsStateWithLifecycle()
    val yearlyProduct by viewModel.yearlyProduct.collectAsStateWithLifecycle()
    val yearlySavingsPercent by viewModel.yearlySavingsPercent.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isPurchasing by viewModel.isPurchasing.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val purchaseResult by viewModel.purchaseResult.collectAsStateWithLifecycle()
    val isTrialEligible by viewModel.isTrialEligible.collectAsStateWithLifecycle()
    val premiumCategories = viewModel.premiumCategories
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        PremiumHeaderCard(
            isPremium = isPremium,
            isInTrial = isInTrial,
            subscriptionStatus = subscriptionDisplayStatus,
            onManage = onNavigateToManage
        )
        
        // Current subscription or upgrade options
        if (isPremium || isInTrial) {
            ActiveSubscriptionCard(
                subscription = activeSubscription,
                onManage = onNavigateToManage
            )
        } else {
            UpgradeOptionsCard(
                monthlyProduct = monthlyProduct,
                yearlyProduct = yearlyProduct,
                yearlySavings = yearlySavingsPercent,
                isTrialEligible = isTrialEligible,
                isPurchasing = isPurchasing,
                onMonthlyClick = { monthlyProduct?.let { viewModel.purchaseProduct(it.productId) } },
                onYearlyClick = { yearlyProduct?.let { viewModel.purchaseProduct(it.productId) } },
                onRestore = { viewModel.restorePurchases() }
            )
        }
        
        // Premium features
        Text("Recursos Premium", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        Text("Tudo o que você ganha ao assinar", fontSize = 14.sp, color = colors.onSurfaceVariant)
        
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0)
        ) {
            items(premiumCategories) { category ->
                PremiumCategorySection(category = category)
            }
        }
        
        // Restore purchases
        androidx.compose.material3.TextButton(
            onClick = { viewModel.restorePurchases() },
            enabled = !isLoading
        ) {
            Text("Restaurar compras anteriores", fontSize = 14.sp, color = colors.primary)
        }
        
        error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
        
        purchaseResult?.let { result ->
            when (result) {
                is PremiumViewModel.PurchaseResult.Success -> {
                    Text("Compra realizada com sucesso!", fontSize = 12.sp, color = colors.success)
                }
                is PremiumViewModel.PurchaseResult.Error -> {
                    Text(result.errorMessage, fontSize = 12.sp, color = colors.error)
                }
                is PremiumViewModel.PurchaseResult.Pending -> {
                    Text("Compra pendente...", fontSize = 12.sp, color = colors.warning)
                }
                is PremiumViewModel.PurchaseResult.Cancelled -> {
                    Text("Compra cancelada", fontSize = 12.sp, color = colors.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun PremiumHeaderCard(
    isPremium: Boolean,
    isInTrial: Boolean,
    subscriptionStatus: String,
    onManage: () -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        colors = CardDefaults.cardColors(
            containerColor = if (isPremium) colors.primary else colors.primaryContainer,
            contentColor = if (isPremium) colors.onPrimary else colors.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("✨", fontSize = 48.sp)
            Text(
                text = if (isPremium) "CycleTrack Premium" else "Desbloqueie o Premium",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = subscriptionStatus,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            if (isInTrial) {
                Badge("🎁 Teste grátis ativo", colors.warning, Color.White)
            } else if (isPremium) {
                Badge("✨ Assinatura ativa", colors.success, Color.White)
            }
            if (isPremium) {
                Button(onClick = onManage, shape = RoundedCornerShape(12.dp)) {
                    Text("Gerenciar assinatura", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun ActiveSubscriptionCard(
    subscription: com.seuapp.cycletrack.domain.model.Subscription?,
    onManage: () -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Sua assinatura", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            
            subscription?.let { sub ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(sub.productId, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                        Text("Renova em ${sub.formattedExpiryDate}", fontSize = 14.sp, color = colors.onSurfaceVariant)
                        if (sub.isTrial) {
                            Text("${sub.trialDaysRemaining} dias de teste restantes", fontSize = 12.sp, color = colors.warning)
                        }
                    }
                    Badge(sub.status.displayName, 
                        when (sub.status) {
                            com.seuapp.cycletrack.domain.model.SubscriptionStatus.ACTIVE -> colors.success
                            com.seuapp.cycletrack.domain.model.SubscriptionStatus.GRACE_PERIOD -> colors.warning
                            com.seuapp.cycletrack.domain.model.SubscriptionStatus.ON_HOLD -> colors.error
                            else -> colors.onSurfaceVariant
                        }, 
                        Color.White
                    )
                }
            }
            
            Button(onClick = onManage, shape = RoundedCornerShape(12.dp)) {
                Text("Gerenciar na Play Store", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun UpgradeOptionsCard(
    monthlyProduct: com.seuapp.cycletrack.presentation.state.SubscriptionProduct?,
    yearlyProduct: com.seuapp.cycletrack.presentation.state.SubscriptionProduct?,
    yearlySavings: Int,
    isTrialEligible: Boolean,
    isPurchasing: Boolean,
    onMonthlyClick: () -> Unit,
    onYearlyClick: () -> Unit,
    onRestore: () -> Unit
) {
    val colors = CycleTrackColors()
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Escolha seu plano", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            
            if (isTrialEligible) {
                Badge("🎁 ${monthlyProduct?.freeTrialPeriod?.removePrefix("P")?.removeSuffix("D") ?: 7} dias grátis no primeiro mês!", colors.warning, Color.White)
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Monthly
                ProductCard(
                    product = monthlyProduct,
                    isRecommended = false,
                    savingsPercent = 0,
                    onClick = onMonthlyClick,
                    isLoading = isPurchasing
                )
                
                // Yearly
                ProductCard(
                    product = yearlyProduct,
                    isRecommended = true,
                    savingsPercent = yearlySavings,
                    onClick = onYearlyClick,
                    isLoading = isPurchasing
                )
            }
            
            androidx.compose.material3.TextButton(onClick = onRestore) {
                Text("Restaurar compras", fontSize = 14.sp, color = colors.primary)
            }
        }
    }
}

@Composable
fun ProductCard(
    product: com.seuapp.cycletrack.presentation.state.SubscriptionProduct?,
    isRecommended: Boolean,
    savingsPercent: Int,
    onClick: () -> Unit,
    isLoading: Boolean
) {
    val colors = CycleTrackColors()
    val productColor = if (isRecommended) colors.primary else colors.onSurfaceVariant
    
    Card(
        modifier = Modifier.fillMaxWidth(0.5f)
            .padding(4.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = if (isRecommended) 3.dp else 1.dp,
        colors = CardDefaults.cardColors(
            containerColor = if (isRecommended) colors.primaryContainer else colors.surface,
            contentColor = if (isRecommended) colors.onPrimaryContainer else colors.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isRecommended) {
                Badge("Recomendado • Economize $savingsPercent%", colors.primary, Color.White)
            }
            
            product?.let { p ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(p.billingPeriodLabel, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(p.priceFormatted, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("/${if (p.billingPeriod == "P1M") "mês" else "ano"}", fontSize = 14.sp, color = colors.onSurfaceVariant)
                    
                    if (p.hasFreeTrial) {
                        Text("${p.trialDays} dias grátis", fontSize = 12.sp, color = colors.warning, fontWeight = FontWeight.Medium)
                    }
                    
                    if (p.introductoryPriceAmountMicros != null) {
                        Text("Primeiro período: ${(p.introductoryPriceAmountMicros!! / 1_000_000.0).formatCurrency(p.priceCurrencyCode)}", fontSize = 10.sp, color = colors.onSurfaceVariant)
                    }
                }
                
                Button(
                    onClick = onClick,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Assinar ${p.priceFormatted}/${if (p.billingPeriod == "P1M") "mês" else "ano"}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumCategorySection(category: PremiumViewModel.PremiumCategory) {
    val colors = CycleTrackColors()
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(category.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                Badge("${category.features.size} recursos", colors.primary, Color.White)
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                category.features.forEach { feature ->
                    PremiumFeatureRow(feature = feature)
                }
            }
        }
    }
}

@Composable
fun PremiumFeatureRow(feature: PremiumViewModel.PremiumFeature) {
    val colors = CycleTrackColors()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = feature.icon, fontSize = 24.sp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(feature.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                if (feature.isNew) {
                    Badge("Novo", colors.success, Color.White)
                }
            }
            Text(feature.description, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun Badge(text: String, backgroundColor: Color, textColor: Color) {
    Chip(
        onClick = { },
        modifier = Modifier.padding(0.dp),
        label = { Text(text, fontSize = 10.sp, fontWeight = FontWeight.Medium) },
        colors = ChipDefaults.colors(
            containerColor = backgroundColor,
            contentColor = textColor
        )
    )
}

// Extensions
val com.seuapp.cycletrack.presentation.state.SubscriptionProduct.billingPeriodLabel: String
    get() = when (billingPeriod) {
        "P1M" -> "Mensal"
        "P1Y" -> "Anual"
        else -> billingPeriod
    }

fun Double.formatCurrency(currencyCode: String): String {
    return java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).run {
        currency = java.util.Currency.getInstance(currencyCode)
        format(this@formatCurrency)
    }
}

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Chip
import androidx.compose.material3.ChipDefaults
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seuapp.cycletrack.domain.model.PurchaseResult
import androidx.compose.foundation.layout.Row