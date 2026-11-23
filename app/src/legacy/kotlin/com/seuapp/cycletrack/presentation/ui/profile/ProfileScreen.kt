package com.seuapp.cycletrack.presentation.ui.profile

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import com.seuapp.cycletrack.presentation.state.ProfileViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormatter
import java.util.Locale

/**
 * Tela de Perfil - Menu principal do usuário (aba Profile) com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToPregnancy: () -> Unit,
    onNavigateToPartner: () -> Unit,
    onNavigateToPremium: () -> Unit
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val cycleCount by viewModel.cycleCount.collectAsStateWithLifecycle()
    val symptomCount by viewModel.symptomCount.collectAsStateWithLifecycle()
    val ovulationCount by viewModel.ovulationCount.collectAsStateWithLifecycle()
    val daysSinceFirstCycle by viewModel.daysSinceFirstCycle.collectAsStateWithLifecycle()
    val subscriptionStatus by viewModel.subscriptionStatus.collectAsStateWithLifecycle()
    val activePregnancy by viewModel.activePregnancy.collectAsStateWithLifecycle()
    val partnerConnection by viewModel.partnerConnection.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Header
        UserHeaderCard(
            profile = userProfile,
            subscriptionStatus = subscriptionStatus
        )
        
        // Stats Summary
        StatsSummaryCard(
            stats = viewModel.getStats()
        )
        
        // Main Features Grid
        FeaturesGrid(
            features = viewModel.getFeatureItems(
                onPregnancy = onNavigateToPregnancy,
                onPartner = onNavigateToPartner,
                onPremium = onNavigateToPremium,
                onSettings = onNavigateToSettings
            )
        )
        
        // Quick Links
        QuickLinksCard(
            links = viewModel.getQuickLinks(
                onMedicalReport = { /* navigate to report */ },
                onExportData = { /* navigate to export */ },
                onSyncNow = { /* trigger sync */ },
                onHelp = { /* navigate to help */ },
                onLegal = { /* navigate to legal */ }
            )
        )
        
        // App version
        Text("CycleTrack v1.0.0", fontSize = 12.sp, color = colors.onSurfaceVariant.copy(alpha = 0.6f), textAlign = TextAlign.Center)
        
        Spacer(100.dp)
    }
    
    error?.let { msg ->
        // TODO: Show error
    }
}

@Composable
fun UserHeaderCard(
    profile: com.seuapp.cycletrack.domain.model.UserProfile?,
    subscriptionStatus: String
) {
    val colors = CycleTrackColors()
    val isPremium = subscriptionStatus != "free"
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        colors = CardDefaults.cardColors(
            containerColor = colors.primaryContainer,
            contentColor = colors.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier.size(72.dp)
                        .background(if (isPremium) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(36.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = profile?.displayName?.firstOrNull()?.uppercaseChar() ?: "👤", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = if (isPremium) Color.White else colors.onSurface)
                }
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = profile?.displayName ?: "Usuária", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(text = profile?.email ?: "email@exemplo.com", fontSize = 14.sp, color = colors.onPrimaryContainer.copy(alpha = 0.8f))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isPremium) {
                            Badge("✨ Premium", colors.primary, Color.White)
                        }
                        Badge("${profile?.let { calculateYearsSince(it.createdAt) ?: 0 }} anos", colors.onPrimaryContainer.copy(alpha = 0.7f), colors.primaryContainer)
                    }
                }
            }
            
            // Subscription status
            SubscriptionStatusRow(status = subscriptionStatus)
        }
    }
}

@Composable
fun Badge(text: String, textColor: Color, backgroundColor: Color) {
    androidx.compose.material3.Chip(
        onClick = { },
        modifier = Modifier.padding(0.dp),
        colors = ChipDefaults.colors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SubscriptionStatusRow(status: String) {
    val colors = CycleTrackColors()
    val (icon, label, color) = when {
        status.contains("Premium") -> "✨" to "Premium ativo" to colors.success
        status.contains("Teste") -> "🎁" to status to colors.warning
        status.contains("Carência") -> "⏳" to status to colors.warning
        status.contains("Pendente") -> "💳" to status to colors.error
        status.contains("Cancelado") -> "🚫" to status to colors.error
        status.contains("Expirado") -> "⌛" to status to colors.onSurfaceVariant
        else -> "🆓" to "Plano gratuito" to colors.onSurfaceVariant
    }
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 16.sp)
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = color)
    }
}

@Composable
fun StatsSummaryCard(stats: List<ProfileViewModel.StatItem>) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            stats.forEachIndexed { index, stat ->
                if (index > 0) Divider()
                StatItem(
                    icon = stat.icon,
                    value = stat.value,
                    label = stat.label
                )
            }
        }
    }
}

@Composable
fun StatItem(icon: String, value: String, label: String) {
    val colors = CycleTrackColors()
    Box(
        modifier = Modifier.fillMaxWidth(0.25f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 20.sp)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text(text = label, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun FeaturesGrid(features: List<ProfileViewModel.FeatureItem>) {
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
            Text("Recursos principais", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                features.take(2).forEach { feature ->
                    FeatureButton(
                        feature = feature,
                        modifier = Modifier.fillMaxWidth(0.48f)
                            .size(140.dp)
                    )
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                features.drop(2).take(2).forEach { feature ->
                    FeatureButton(
                        feature = feature,
                        modifier = Modifier.fillMaxWidth(0.48f)
                            .size(140.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureButton(
    feature: ProfileViewModel.FeatureItem,
    modifier: Modifier = Modifier
) {
    val colors = CycleTrackColors()
    Card(
        modifier = modifier
            .padding(4.dp),
        onClick = feature.action,
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp,
        colors = CardDefaults.cardColors(
            containerColor = Color(feature.color).copy(alpha = 0.1f),
            contentColor = Color(feature.color)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = feature.icon, fontSize = 28.sp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = feature.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                feature.badge?.let { badge ->
                    Badge(badge, Color.White, Color(feature.color))
                }
            }
            Text(text = feature.subtitle, fontSize = 10.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun QuickLinksCard(links: List<ProfileViewModel.QuickLinkItem>) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Links rápidos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            links.forEach { link ->
                QuickLinkRow(
                    icon = link.icon,
                    title = link.title,
                    subtitle = link.subtitle,
                    onClick = link.action
                )
            }
        }
    }
}

@Composable
fun QuickLinkRow(icon: String, title: String, subtitle: String, onClick: () -> Unit) {
    val colors = CycleTrackColors()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
            Text(text = subtitle, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.fillMaxWidth())
        androidx.compose.material3.Icon(
            imageVector = androidx.compose.material.icons.Icons.Filled.ChevronRight,
            contentDescription = "",
            tint = colors.onSurfaceVariant
        )
    }.also {
        androidx.compose.foundation.gestures.clickable {
            onClick()
        }
    }
}

private fun calculateYearsSince(createdAt: Long): Int {
    val diff = System.currentTimeMillis() - createdAt
    return (diff / (1000L * 60 * 60 * 24 * 365)).toInt()
}

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Chip
import androidx.compose.material3.ChipDefaults
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer