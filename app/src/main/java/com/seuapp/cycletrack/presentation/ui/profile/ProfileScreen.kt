package com.seuapp.cycletrack.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seuapp.cycletrack.presentation.ui.onboarding.GOAL_BODY
import com.seuapp.cycletrack.presentation.ui.onboarding.GOAL_EXPECTING
import com.seuapp.cycletrack.presentation.ui.onboarding.GOAL_PERIOD
import com.seuapp.cycletrack.presentation.ui.onboarding.GOAL_PREGNANT
import com.seuapp.cycletrack.presentation.ui.onboarding.GridOption
import com.seuapp.cycletrack.presentation.ui.onboarding.MultiChoiceGridScreen
import com.seuapp.cycletrack.presentation.ui.onboarding.OnboardingStep
import com.seuapp.cycletrack.presentation.viewmodel.UserProfileViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import com.seuapp.cycletrack.R

private const val DEFAULT_USER_ID = "user_123"

private val goalStep = OnboardingStep.MultiChoiceGrid(
    title = "O que voce quer?",
    subtitle = "Escolha a opcao que mais combina com voce",
    options = listOf(
        GridOption(GOAL_PERIOD, "Monitorar minha menstruação", R.drawable.goal_period),
        GridOption(GOAL_PREGNANT, "Engravidar", R.drawable.goal_body),
        GridOption(GOAL_EXPECTING, "Acompanhar minha gravidez", R.drawable.goal_pregnancy),
        GridOption(GOAL_BODY, "Entender meu corpo", R.drawable.goal_body)
    ),
    resultKey = "goal"
)

private fun goalLabel(goal: String): String = goalStep.options.find { it.key == goal }?.label ?: "Definir objetivo"

/**
 * Tela de perfil com ajustes locais, assinatura e privacidade.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToCycleSettings: () -> Unit = {},
    onNavigateToReminders: () -> Unit = {},
    onNavigateToPartner: () -> Unit = {},
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val colors = CycleTrackColors()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var healthSyncEnabled by remember { mutableStateOf(false) }
    var showGoalSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile(DEFAULT_USER_ID)
    }

    val displayName = profile?.displayName?.ifBlank { "Ana Martins" } ?: "Ana Martins"
    val subscriptionPlan = "Plano Premium"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = CycleTrackDimens.spacingMd,
            vertical = CycleTrackDimens.spacingLg
        ),
        verticalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingMd)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(colors.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(Modifier.width(CycleTrackDimens.spacingMd))
                Column {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subscriptionPlan,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.primary
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusLg),
                colors = CardDefaults.cardColors(containerColor = colors.primaryContainer)
            ) {
                Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
                    Text(
                        text = "CycleTrack Premium",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Previsoes refinadas, exportacao de dados e compartilhamento com o parceiro.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onPrimaryContainer
                    )
                    Spacer(Modifier.height(CycleTrackDimens.spacingSm))
                    Text(
                        text = "Renovar assinatura",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.primary
                    )
                }
            }
        }

        item {
            Text(
                text = "Objetivo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                ActionRow(
                    icon = Icons.Filled.Favorite,
                    title = goalLabel(profile?.goal ?: ""),
                    subtitle = "Toque para alterar seu objetivo no CycleTrack",
                    onClick = { showGoalSheet = true }
                )
            }
        }

        item {
            Text(
                text = "Preferencias",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ActionRow(
                        icon = Icons.Filled.Notifications,
                        title = "Lembretes",
                        subtitle = "Categorias e horarios de periodo, ovulacao e dicas",
                        onClick = onNavigateToReminders
                    )
                    Divider()
                    ToggleRow(
                        icon = Icons.Filled.Favorite,
                        title = "Sincronizar com Health Connect",
                        subtitle = "Peso, temperatura e atividade",
                        checked = healthSyncEnabled,
                        onCheckedChange = { healthSyncEnabled = it }
                    )
                }
            }
        }

        item {
            Text(
                text = "Conta e dados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ActionRow(
                        icon = Icons.Filled.Settings,
                        title = "Ajustes do ciclo",
                        subtitle = "Duracao media e do periodo",
                        onClick = onNavigateToCycleSettings
                    )
                    Divider()
                    ActionRow(
                        icon = Icons.Filled.Share,
                        title = "Compartilhar com parceiro",
                        subtitle = "Convites e permissões",
                        onClick = onNavigateToPartner
                    )
                    Divider()
                    ActionRow(Icons.Filled.Download, "Exportar meus dados", "CSV ou PDF")
                    Divider()
                    ActionRow(Icons.Filled.Lock, "Privacidade e seguranca", "Senha e biometria")
                }
            }
        }

        item {
            Text(
                text = "Todos os dados ficam armazenados no seu aparelho. Pode exportar ou apagar a qualquer momento.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showGoalSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showGoalSheet = false },
            sheetState = sheetState
        ) {
            MultiChoiceGridScreen(
                step = goalStep,
                onSelect = { option ->
                    viewModel.updateGoal(DEFAULT_USER_ID, option.key)
                    showGoalSheet = false
                }
            )
        }
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    )
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(CycleTrackDimens.spacingMd)
    ) {
        Icon(icon, contentDescription = null, tint = CycleTrackColors().primary)
        Spacer(Modifier.width(CycleTrackDimens.spacingSm))
        Column(Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ActionRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(CycleTrackDimens.spacingMd)
    ) {
        Icon(icon, contentDescription = null, tint = CycleTrackColors().primary)
        Spacer(Modifier.width(CycleTrackDimens.spacingSm))
        Column(Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
