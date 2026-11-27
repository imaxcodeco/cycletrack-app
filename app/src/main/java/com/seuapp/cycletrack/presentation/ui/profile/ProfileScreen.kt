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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seuapp.cycletrack.presentation.viewmodel.AuthViewModel
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

private val goalStep = OnboardingStep.MultiChoiceGrid(
    title = "O que você quer?",
    subtitle = "Escolha a opção que mais combina com você",
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
    onSignOut: () -> Unit = {},
    viewModel: UserProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val colors = CycleTrackColors()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var showGoalSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    val displayName = profile?.displayName?.ifBlank { "Ana Martins" } ?: "Ana Martins"

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
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_cycletrack),
                        contentDescription = "CycleTrack",
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(Modifier.width(CycleTrackDimens.spacingMd))
                Column {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
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
                text = "Preferências",
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
                        subtitle = "Categorias e horários de período, ovulação e dicas",
                        onClick = onNavigateToReminders
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
                        subtitle = "Duração média do ciclo e da menstruação",
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
                    ActionRow(Icons.Filled.Lock, "Privacidade e segurança", "Senha e biometria")
                    Divider()
                    ActionRow(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Sair da conta",
                        subtitle = "Encerrar sessão neste aparelho",
                        onClick = {
                            coroutineScope.launch {
                                authViewModel.signOut()
                                onSignOut()
                            }
                        }
                    )
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
                    viewModel.updateGoal(option.key)
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
