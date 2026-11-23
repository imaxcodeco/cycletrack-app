package com.seuapp.cycletrack.presentation.ui.settings

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
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
import com.seuapp.cycletrack.presentation.state.SettingsViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider

/**
 * Tela de Configurações - Menu principal de settings com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun SettingsScreen(
    onNavigateToAccount: () -> Unit,
    onNavigateToCycle: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToDisplay: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToData: () -> Unit,
    onNavigateToSync: () -> Unit,
    onNavigateToHealthConnect: () -> Unit,
    onNavigateToPartner: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()
    
    // Account
    val displayName by viewModel.displayName.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    
    // Cycle
    val averageCycleLength by viewModel.averageCycleLength.collectAsStateWithLifecycle()
    val averagePeriodLength by viewModel.averagePeriodLength.collectAsStateWithLifecycle()
    val cycleRegularity by viewModel.cycleRegularity.collectAsStateWithLifecycle()
    val contraceptionMethod by viewModel.contraceptionMethod.collectAsStateWithLifecycle()
    val lastPeriodDate by viewModel.lastPeriodDate.collectAsStateWithLifecycle()
    
    // Notifications
    val periodReminderEnabled by viewModel.periodReminderEnabled.collectAsStateWithLifecycle()
    val periodReminderDaysBefore by viewModel.periodReminderDaysBefore.collectAsStateWithLifecycle()
    val fertileWindowReminderEnabled by viewModel.fertileWindowReminderEnabled.collectAsStateWithLifecycle()
    val ovulationReminderEnabled by viewModel.ovulationReminderEnabled.collectAsStateWithLifecycle()
    val pmsReminderEnabled by viewModel.pmsReminderEnabled.collectAsStateWithLifecycle()
    val pillReminderEnabled by viewModel.pillReminderEnabled.collectAsStateWithLifecycle()
    val pillReminderTime by viewModel.pillReminderTime.collectAsStateWithLifecycle()
    val partnerNotificationsEnabled by viewModel.partnerNotificationsEnabled.collectAsStateWithLifecycle()
    
    // Display
    val temperatureUnit by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val weightUnit by viewModel.weightUnit.collectAsStateWithLifecycle()
    val heightUnit by viewModel.heightUnit.collectAsStateWithLifecycle()
    val firstDayOfWeek by viewModel.firstDayOfWeek.collectAsStateWithLifecycle()
    val calendarViewMode by viewModel.calendarViewMode.collectAsStateWithLifecycle()
    
    // Privacy
    val analyticsOptIn by viewModel.analyticsOptIn.collectAsStateWithLifecycle()
    val crashReportingOptIn by viewModel.crashReportingOptIn.collectAsStateWithLifecycle()
    val marketingOptIn by viewModel.marketingOptIn.collectAsStateWithLifecycle()
    val healthConnectEnabled by viewModel.healthConnectEnabled.collectAsStateWithLifecycle()
    
    // Security
    val biometricEnabled by viewModel.biometricEnabled.collectAsStateWithLifecycle()
    val autoLockEnabled by viewModel.autoLockEnabled.collectAsStateWithLifecycle()
    val autoLockTimeoutMinutes by viewModel.autoLockTimeoutMinutes.collectAsStateWithLifecycle()
    
    // Data & Sync
    val autoSyncEnabled by viewModel.autoSyncEnabled.collectAsStateWithLifecycle()
    val syncFrequencyMinutes by viewModel.syncFrequencyMinutes.collectAsStateWithLifecycle()
    val autoBackupEnabled by viewModel.autoBackupEnabled.collectAsStateWithLifecycle()
    val backupFrequency by viewModel.backupFrequency.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Configurações", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        Text("Personalize seu CycleTrack", fontSize = 16.sp, color = colors.onSurfaceVariant)
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Account Section
            SettingsSection(
                title = "Conta",
                icon = "👤",
                color = colors.primary,
                items = listOf(
                    SettingsItem("Perfil", "Nome, foto, data de nascimento", onNavigateToAccount),
                    SettingsItem("Ciclo", "Duração, contracepção, última menstruação", onNavigateToCycle),
                    SettingsItem("Assinatura", "Gerenciar plano Premium", onNavigateToSubscription)
                )
            )
            
            // Notifications Section
            SettingsSection(
                title = "Notificações",
                icon = "🔔",
                color = colors.phaseMenstrual,
                items = listOf(
                    SettingsItem("Lembretes", "Menstruação, janela fértil, TPM, pílula", onNavigateToNotifications),
                    SettingsItem("Parceiro", "Notificações compartilhadas", onNavigateToPartner)
                )
            )
            
            // Display Section
            SettingsSection(
                title = "Aparência",
                icon = "🎨",
                color = colors.tertiary,
                items = listOf(
                    SettingsItem("Tema", themeModeLabel(themeMode), onNavigateToDisplay),
                    SettingsItem("Idioma", languageLabel(language), onNavigateToDisplay),
                    SettingsItem("Unidades", "Métrico ou Imperial", onNavigateToDisplay)
                )
            )
            
            // Privacy Section
            SettingsSection(
                title = "Privacidade",
                icon = "🔒",
                color = colors.success,
                items = listOf(
                    SettingsItem("Analytics", if (analyticsOptIn) "Ativado" else "Desativado", onNavigateToPrivacy),
                    SettingsItem("Health Connect", if (healthConnectEnabled) "Conectado" else "Desconectado", onNavigateToHealthConnect),
                    SettingsItem("Exportar dados", "LGPD/GDPR - Seus dados", onNavigateToExport),
                    SettingsItem("Excluir conta", "Direito ao esquecimento", onNavigateToPrivacy)
                )
            )
            
            // Security Section
            SettingsSection(
                title = "Segurança",
                icon = "🛡️",
                color = colors.phaseOvulation,
                items = listOf(
                    SettingsItem("Biometria", if (biometricEnabled) "Ativada" else "Desativada", onNavigateToSecurity),
                    SettingsItem("Bloqueio automático", if (autoLockEnabled) "${autoLockTimeoutMinutes} min" else "Desativado", onNavigateToSecurity),
                    SettingsItem("Alterar senha", "Senha da conta", onNavigateToSecurity)
                )
            )
            
            // Data & Sync Section
            SettingsSection(
                title = "Dados e Sincronização",
                icon = "☁️",
                color = colors.phaseFollicular,
                items = listOf(
                    SettingsItem("Backup automático", if (autoBackupEnabled) backupFrequencyLabel(backupFrequency) else "Desativado", onNavigateToData),
                    SettingsItem("Sincronização", if (autoSyncEnabled) "A cada ${syncFrequencyMinutes} min" else "Manual", onNavigateToSync),
                    SettingsItem("Health Connect", if (healthConnectEnabled) "Ativo" else "Inativo", onNavigateToHealthConnect)
                )
            )
            
            // About Section
            SettingsSection(
                title = "Sobre",
                icon = "ℹ️",
                color = colors.onSurfaceVariant,
                items = listOf(
                    SettingsItem("Versão do app", "1.0.0", {}),
                    SettingsItem("Termos de uso", "Leia nossos termos", onNavigateToAbout),
                    SettingsItem("Política de privacidade", "Seus direitos", onNavigateToAbout),
                    SettingsItem("Licenças open source", "Bibliotecas usadas", onNavigateToAbout),
                    SettingsItem("Suporte", "Ajuda e contato", onNavigateToAbout)
                )
            )
        }
        
        // Save button (if there are unsaved changes)
        if (saveSuccess) {
            Text("Configurações salvas com sucesso!", fontSize = 14.sp, color = colors.success, modifier = Modifier.fillMaxWidth().padding(16.dp))
        }
        
        error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error, modifier = Modifier.fillMaxWidth().padding(16.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: String,
    color: Color,
    items: List<SettingsItem>
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 20.sp)
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items.forEachIndexed { index, item ->
                    SettingsItemRow(item = item, isLast = index == items.lastIndex)
                    if (index != items.lastIndex) {
                        Divider(
                            color = colors.outlineVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItemRow(item: SettingsItem, isLast: Boolean) {
    val colors = CycleTrackColors()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = item.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
            Text(text = item.subtitle, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
        if (item.onClick != null) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.ChevronRight,
                contentDescription = "",
                tint = colors.onSurfaceVariant
            )
        }
    }.also { 
        if (item.onClick != null) {
            androidx.compose.foundation.gestures.clickable {
                item.onClick?.invoke()
            }
        }
    }
}

data class SettingsItem(
    val title: String,
    val subtitle: String,
    val onClick: (() -> Unit)? = null
)

fun themeModeLabel(mode: String): String = when (mode) {
    "light" -> "Claro"
    "dark" -> "Escuro"
    else -> "Sistema"
}

fun languageLabel(lang: String): String = when (lang) {
    "pt_BR" -> "Português (Brasil)"
    "en_US" -> "English (US)"
    "es_ES" -> "Español"
    else -> lang
}

fun backupFrequencyLabel(freq: String): String = when (freq) {
    "daily" -> "Diário"
    "weekly" -> "Semanal"
    "monthly" -> "Mensal"
    else -> freq
}