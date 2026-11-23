package com.seuapp.cycletrack.presentation.ui.partner

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Chip
import androidx.compose.material3.OutlinedTextField
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
import com.seuapp.cycletrack.domain.model.PartnerShareLevel
import com.seuapp.cycletrack.presentation.state.PartnerViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormatter
import java.util.Locale

/**
 * Tela de Modo Parceiro - Compartilhamento de ciclo com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun PartnerScreen(
    onNavigateToInvite: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSharedView: () -> Unit
) {
    val viewModel: PartnerViewModel = hiltViewModel()
    val activePartner by viewModel.activePartner.collectAsStateWithLifecycle()
    val allPartners by viewModel.allPartners.collectAsStateWithLifecycle()
    val pendingSent by viewModel.pendingInvitationsSent.collectAsStateWithLifecycle()
    val pendingReceived by viewModel.pendingInvitationsReceived.collectAsStateWithLifecycle()
    val sharedData by viewModel.sharedData.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val inviteEmail by viewModel.inviteEmail.collectAsStateWithLifecycle()
    val selectedShareLevel by viewModel.selectedShareLevel.collectAsStateWithLifecycle()
    val shareLevelOptions = viewModel.shareLevelOptions
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Modo Parceiro", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        Text("Compartilhe seu ciclo com quem você confia", fontSize = 16.sp, color = colors.onSurfaceVariant)
        
        if (isLoading) {
            LoadingCard()
        } else if (isConnected) {
            // Active connection
            ActivePartnerCard(
                partner = activePartner!!,
                sharedData = sharedData,
                onSettings = onNavigateToSettings,
                onSharedView = onNavigateToSharedView
            )
            
            // Sharing preferences
            SharingPreferencesCard(
                partner = activePartner!!,
                shareLevelOptions = shareLevelOptions,
                selectedLevel = selectedShareLevel,
                onLevelChange = { viewModel.setSelectedShareLevel(it) },
                onSave = { 
                    viewModel.updateSharingPreferences(
                        shareCycle = it.shareCycle,
                        sharePeriodDates = it.sharePeriodDates,
                        shareOvulation = it.shareOvulation,
                        shareFertileWindow = it.shareFertileWindow,
                        sharePms = it.sharePms,
                        shareSymptoms = it.shareSymptoms,
                        sharePredictions = it.sharePredictions,
                        sharePregnancy = it.sharePregnancy,
                        shareInsights = it.shareInsights,
                        shareMood = it.shareMood,
                        shareWeight = it.shareWeight,
                        shareNotes = it.shareNotes
                    )
                }
            )
            
            // Notification preferences
            NotificationPreferencesCard(
                partner = activePartner!!,
                onSave = { prefs ->
                    viewModel.updateNotificationPreferences(
                        notifyPeriodStart = prefs.notifyPeriodStart,
                        notifyPeriodEnd = prefs.notifyPeriodEnd,
                        notifyFertileWindow = prefs.notifyFertileWindow,
                        notifyOvulation = prefs.notifyOvulation,
                        notifyPms = prefs.notifyPms,
                        notifyPregnancy = prefs.notifyPregnancy,
                        notifySymptoms = prefs.notifySymptoms
                    )
                }
            )
            
            // Revoke button
            Button(
                onClick = { viewModel.revokePartner(activePartner!!.id) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.errorContainer,
                    contentColor = colors.onErrorContainer
                )
            ) {
                Text("Remover parceiro", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            
        } else if (pendingReceived.isNotEmpty()) {
            // Pending invitations received
            Text("Convites recebidos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            pendingReceived.forEach { invitation ->
                InvitationReceivedCard(
                    invitation = invitation,
                    onAccept = { viewModel.acceptInvitation(it.invitationCode!!) },
                    onDecline = { viewModel.declineInvitation(it.invitationCode!!) }
                )
            }
            
            // Pending sent
            if (pendingSent.isNotEmpty()) {
                Text("Convites enviados", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                pendingSent.forEach { invitation ->
                    InvitationSentCard(
                        invitation = invitation,
                        onResend = { viewModel.resendInvitation(it.invitationCode!!) },
                        onCancel = { viewModel.cancelInvitation(it.invitationCode!!) }
                    )
                }
            }
            
            // Invite new
            InvitePartnerCard(
                email = inviteEmail,
                onEmailChange = { viewModel.setInviteEmail(it) },
                selectedShareLevel = selectedShareLevel,
                shareLevelOptions = shareLevelOptions,
                onLevelChange = { viewModel.setSelectedShareLevel(it) },
                onInvite = { viewModel.invitePartner() }
            )
            
        } else if (pendingSent.isNotEmpty()) {
            // Only pending sent
            Text("Convites enviados", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            pendingSent.forEach { invitation ->
                InvitationSentCard(
                    invitation = invitation,
                    onResend = { viewModel.resendInvitation(it.invitationCode!!) },
                    onCancel = { viewModel.cancelInvitation(it.invitationCode!!) }
                )
            }
            
            InvitePartnerCard(
                email = inviteEmail,
                onEmailChange = { viewModel.setInviteEmail(it) },
                selectedShareLevel = selectedShareLevel,
                shareLevelOptions = shareLevelOptions,
                onLevelChange = { viewModel.setSelectedShareLevel(it) },
                onInvite = { viewModel.invitePartner() }
            )
            
        } else {
            // No connections
            EmptyPartnerCard(
                email = inviteEmail,
                onEmailChange = { viewModel.setInviteEmail(it) },
                selectedShareLevel = selectedShareLevel,
                shareLevelOptions = shareLevelOptions,
                onLevelChange = { viewModel.setSelectedShareLevel(it) },
                onInvite = { viewModel.invitePartner() }
            )
        }
        
        error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
    }
}

@Composable
fun LoadingCard() {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            androidx.compose.material3.CircularProgressIndicator(color = colors.primary)
            Text("Carregando...", fontSize = 14.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun ActivePartnerCard(
    partner: com.seuapp.cycletrack.domain.model.PartnerConnection,
    sharedData: com.seuapp.cycletrack.presentation.state.PartnerViewModel.PartnerSharedData?,
    onSettings: () -> Unit,
    onSharedView: () -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        colors = CardDefaults.cardColors(
            containerColor = colors.tertiary.copy(alpha = 0.1f),
            contentColor = colors.tertiary
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(56.dp)
                            .background(colors.tertiary.copy(alpha = 0.2f), RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👫", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(partner.partnerDisplayName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                        Text("Parceiro conectado", fontSize = 14.sp, color = colors.onSurfaceVariant)
                    }
                }
                
                Chip(
                    onClick = { },
                    modifier = Modifier.padding(0.dp),
                    label = { Text(partner.shareLevel.displayName, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = ChipDefaults.colors(
                        containerColor = colors.tertiary.copy(alpha = 0.2f),
                        contentColor = colors.tertiary
                    )
                )
            }
            
            // Shared data preview
            sharedData?.let { data ->
                Text("O que seu parceiro vê agora:", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurfaceVariant)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (data.currentCycle != null) {
                        DataChip("🩸", "Ciclo atual", colors.phaseMenstrual)
                    }
                    if (data.nextPeriod != null) {
                        DataChip("📅", "Próxima menstruação", colors.phaseMenstrual)
                    }
                    if (data.nextOvulation != null) {
                        DataChip("🥚", "Ovulação", colors.phaseOvulation)
                    }
                    if (data.fertileWindow != null) {
                        DataChip("🌱", "Janela fértil", colors.phaseFollicular)
                    }
                    if (data.isInFertileWindow) {
                        DataChip("🔥", "Fértil agora", colors.success)
                    }
                    if (data.isInPms) {
                        DataChip("🌙", "TPM", colors.phaseLuteal)
                    }
                    if (data.pregnancy != null) {
                        DataChip("🤰", "Gravidez", colors.trimester2)
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                androidx.compose.material3.OutlinedButton(
                    onClick = onSettings,
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Configurar", fontSize = 14.sp)
                }
                Button(
                    onClick = onSharedView,
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Ver compartilhado", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun DataChip(icon: String, label: String, color: Color) {
    val colors = CycleTrackColors()
    Chip(
        onClick = { },
        modifier = Modifier.padding(0.dp),
        label = { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(icon, fontSize = 12.sp)
            Text(label, fontSize = 10.sp)
        }},
        colors = ChipDefaults.colors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        )
    )
}

@Composable
fun SharingPreferencesCard(
    partner: com.seuapp.cycletrack.domain.model.PartnerConnection,
    shareLevelOptions: List<com.seuapp.cycletrack.presentation.state.ShareLevelOption>,
    selectedLevel: PartnerShareLevel,
    onLevelChange: (PartnerShareLevel) -> Unit,
    onSave: (SharingPrefs) -> Unit
) {
    val colors = CycleTrackColors()
    var prefs by remember { mutableStateOf(SharingPrefs.fromPartner(partner)) }
    
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
                Text("O que compartilhar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                // Share level selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    shareLevelOptions.forEach { option ->
                        val isSelected = selectedLevel == option.level
                        Chip(
                            onClick = { onLevelChange(option.level) },
                            modifier = Modifier.padding(0.dp),
                            label = { Text(option.title, fontSize = 10.sp) },
                            colors = ChipDefaults.colors(
                                containerColor = if (isSelected) colors.tertiary else colors.surfaceVariant,
                                contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            
            if (selectedLevel == PartnerShareLevel.CUSTOM) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ToggleRow("Ciclo menstrual", "Datas e duração", prefs.shareCycle) { prefs = prefs.copy(shareCycle = it) }
                    ToggleRow("Datas da menstruação", "Início e fim", prefs.sharePeriodDates) { prefs = prefs.copy(sharePeriodDates = it) }
                    ToggleRow("Ovulação", "Data prevista", prefs.shareOvulation) { prefs = prefs.copy(shareOvulation = it) }
                    ToggleRow("Janela fértil", "Início e fim", prefs.shareFertileWindow) { prefs = prefs.copy(shareFertileWindow = it) }
                    ToggleRow("TPM", "Período prevista", prefs.sharePms) { prefs = prefs.copy(sharePms = it) }
                    ToggleRow("Sintomas", "Todos os sintomas registrados", prefs.shareSymptoms) { prefs = prefs.copy(shareSymptoms = it) }
                    ToggleRow("Previsões", "Próximas menstruações, ovulações", prefs.sharePredictions) { prefs = prefs.copy(sharePredictions = it) }
                    ToggleRow("Gravidez", "Se estiver grávida", prefs.sharePregnancy) { prefs = prefs.copy(sharePregnancy = it) }
                    ToggleRow("Insights", "Relatórios e estatísticas", prefs.shareInsights) { prefs = prefs.copy(shareInsights = it) }
                    ToggleRow("Humor", "Registros de humor", prefs.shareMood) { prefs = prefs.copy(shareMood = it) }
                    ToggleRow("Peso", "Registros de peso", prefs.shareWeight) { prefs = prefs.copy(shareWeight = it) }
                    ToggleRow("Observações", "Notas pessoais", prefs.shareNotes) { prefs = prefs.copy(shareNotes = it) }
                }
            }
            
            Button(
                onClick = { onSave(prefs) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salvar preferências", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

data class SharingPrefs(
    val shareCycle: Boolean = true,
    val sharePeriodDates: Boolean = true,
    val shareOvulation: Boolean = true,
    val shareFertileWindow: Boolean = true,
    val sharePms: Boolean = true,
    val shareSymptoms: Boolean = true,
    val sharePredictions: Boolean = true,
    val sharePregnancy: Boolean = false,
    val shareInsights: Boolean = false,
    val shareMood: Boolean = false,
    val shareWeight: Boolean = false,
    val shareNotes: Boolean = false
) {
    companion object {
        fun fromPartner(partner: com.seuapp.cycletrack.domain.model.PartnerConnection): SharingPrefs {
            return SharingPrefs(
                shareCycle = partner.shareCycle,
                sharePeriodDates = partner.sharePeriodDates,
                shareOvulation = partner.shareOvulation,
                shareFertileWindow = partner.shareFertileWindow,
                sharePms = partner.sharePms,
                shareSymptoms = partner.shareSymptoms,
                sharePredictions = partner.sharePredictions,
                sharePregnancy = partner.sharePregnancy,
                shareInsights = partner.shareInsights,
                shareMood = partner.shareMood,
                shareWeight = partner.shareWeight,
                shareNotes = partner.shareNotes
            )
        }
    }
}

@Composable
fun ToggleRow(title: String, description: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val colors = CycleTrackColors()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
            Text(description, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.tertiary,
                checkedTrackColor = colors.tertiary.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun NotificationPreferencesCard(
    partner: com.seuapp.cycletrack.domain.model.PartnerConnection,
    onSave: (NotificationPrefs) -> Unit
) {
    val colors = CycleTrackColors()
    var prefs by remember { mutableStateOf(NotificationPrefs.fromPartner(partner)) }
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Notificações para parceiro", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ToggleRow("Menstruação iniciou", "Quando a menstruação começar", prefs.notifyPeriodStart) { prefs = prefs.copy(notifyPeriodStart = it) }
                ToggleRow("Menstruação terminou", "Quando a menstruação terminar", prefs.notifyPeriodEnd) { prefs = prefs.copy(notifyPeriodEnd = it) }
                ToggleRow("Janela fértil", "Início da janela fértil", prefs.notifyFertileWindow) { prefs = prefs.copy(notifyFertileWindow = it) }
                ToggleRow("Ovulação", "Dia da ovulação prevista", prefs.notifyOvulation) { prefs = prefs.copy(notifyOvulation = it) }
                ToggleRow("TPM", "Início da TPM", prefs.notifyPms) { prefs = prefs.copy(notifyPms = it) }
                ToggleRow("Gravidez", "Atualizações de gravidez", prefs.notifyPregnancy) { prefs = prefs.copy(notifyPregnancy = it) }
                ToggleRow("Sintomas", "Novos sintomas registrados", prefs.notifySymptoms) { prefs = prefs.copy(notifySymptoms = it) }
            }
            
            Button(
                onClick = { onSave(prefs) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salvar notificações", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

data class NotificationPrefs(
    val notifyPeriodStart: Boolean = true,
    val notifyPeriodEnd: Boolean = true,
    val notifyFertileWindow: Boolean = true,
    val notifyOvulation: Boolean = true,
    val notifyPms: Boolean = true,
    val notifyPregnancy: Boolean = false,
    val notifySymptoms: Boolean = false
) {
    companion object {
        fun fromPartner(partner: com.seuapp.cycletrack.domain.model.PartnerConnection): NotificationPrefs {
            return NotificationPrefs(
                notifyPeriodStart = partner.notifyPeriodStart,
                notifyPeriodEnd = partner.notifyPeriodEnd,
                notifyFertileWindow = partner.notifyFertileWindow,
                notifyOvulation = partner.notifyOvulation,
                notifyPms = partner.notifyPms,
                notifyPregnancy = partner.notifyPregnancy,
                notifySymptoms = partner.notifySymptoms
            )
        }
    }
}

@Composable
fun InvitationReceivedCard(
    invitation: com.seuapp.cycletrack.domain.model.PartnerConnection,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        colors = CardDefaults.cardColors(
            containerColor = colors.successContainer,
            contentColor = colors.onSuccessContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💌", fontSize = 32.sp)
                Spacer(modifier = Modifier.size(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${invitation.partnerDisplayName} quer compartilhar o ciclo com você", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Nível: ${invitation.shareLevel.displayName}", fontSize = 14.sp, color = colors.onSuccessContainer.copy(alpha = 0.8f))
                    Text("Expira em ${invitation.daysUntilExpiry} dias", fontSize = 12.sp, color = colors.onSuccessContainer.copy(alpha = 0.6f))
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                androidx.compose.material3.OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Recusar", fontSize = 14.sp)
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Aceitar", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun InvitationSentCard(
    invitation: com.seuapp.cycletrack.domain.model.PartnerConnection,
    onResend: () -> Unit,
    onCancel: () -> Unit
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📤", fontSize = 28.sp)
                Spacer(modifier = Modifier.size(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Convite enviado para ${invitation.partnerEmail}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text("Nível: ${invitation.shareLevel.displayName}", fontSize = 14.sp, color = colors.onSurfaceVariant)
                    Text("Expira em ${invitation.daysUntilExpiry} dias", fontSize = 12.sp, color = colors.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                androidx.compose.material3.OutlinedButton(
                    onClick = onResend,
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Reenviar", fontSize = 14.sp)
                }
                androidx.compose.material3.OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(0.5f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.error
                    )
                ) {
                    Text("Cancelar", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun InvitePartnerCard(
    email: String,
    onEmailChange: (String) -> Unit,
    selectedShareLevel: PartnerShareLevel,
    shareLevelOptions: List<com.seuapp.cycletrack.presentation.state.ShareLevelOption>,
    onLevelChange: (PartnerShareLevel) -> Unit,
    onInvite: () -> Unit
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
            Text("Convidar novo parceiro", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email do parceiro") },
                placeholder = { Text("parceiro@email.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                shareLevelOptions.forEach { option ->
                    val isSelected = selectedShareLevel == option.level
                    Chip(
                        onClick = { onLevelChange(option.level) },
                        modifier = Modifier.padding(0.dp),
                        label = { Text(option.title, fontSize = 10.sp) },
                        colors = ChipDefaults.colors(
                            containerColor = if (isSelected) colors.primary else colors.surfaceVariant,
                            contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
                        )
                    )
                }
            }
            
            Button(
                onClick = onInvite,
                enabled = email.contains("@"),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enviar convite", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun EmptyPartnerCard(
    email: String,
    onEmailChange: (String) -> Unit,
    selectedShareLevel: PartnerShareLevel,
    shareLevelOptions: List<com.seuapp.cycletrack.presentation.state.ShareLevelOption>,
    onLevelChange: (PartnerShareLevel) -> Unit,
    onInvite: () -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        colors = CardDefaults.cardColors(
            containerColor = colors.tertiary.copy(alpha = 0.1f),
            contentColor = colors.tertiary
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("👫", fontSize = 64.sp)
            Text("Nenhum parceiro conectado", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Convide seu parceiro para ver seu ciclo, janela fértil e mais", fontSize = 14.sp, textAlign = TextAlign.Center, color = colors.onSurfaceVariant)
            
            InvitePartnerCard(
                email = email,
                onEmailChange = onEmailChange,
                selectedShareLevel = selectedShareLevel,
                shareLevelOptions = shareLevelOptions,
                onLevelChange = onLevelChange,
                onInvite = onInvite
            )
        }
    }
}

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Chip
import androidx.compose.material3.ChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer