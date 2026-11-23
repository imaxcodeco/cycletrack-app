package com.seuapp.cycletrack.presentation.ui.healthconnect

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
import androidx.compose.material3.LinearProgressIndicator
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
import com.seuapp.cycletrack.presentation.state.HealthConnectViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens

/**
 * Tela de Health Connect - Integração com Google Health Connect.
 * Clean-room - implementação própria completa.
 */

@Composable
fun HealthConnectScreen(
    onNavigateToSettings: () -> Unit
) {
    val viewModel: HealthConnectViewModel = hiltViewModel()
    val isAvailable by viewModel.isAvailable.collectAsStateWithLifecycle()
    val isEnabled by viewModel.isEnabled.collectAsStateWithLifecycle()
    val permissionsGranted by viewModel.permissionsGranted.collectAsStateWithLifecycle()
    val requiredPermissions by viewModel.requiredPermissions.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastSyncTime.collectAsStateWithLifecycle()
    val pendingToSync by viewModel.pendingToSync.collectAsStateWithLifecycle()
    val conflicts by viewModel.conflicts.collectAsStateWithLifecycle()
    val syncStats by viewModel.syncStats.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Health Connect", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        Text("Sincronize com apps de saúde do Google", fontSize = 16.sp, color = colors.onSurfaceVariant)
        
        if (!isAvailable) {
            NotAvailableCard()
        } else if (!isEnabled) {
            SetupCard(
                requiredPermissions = requiredPermissions,
                onRequestPermissions = { viewModel.requestPermissions() },
                onEnable = { viewModel.enableHealthConnect() }
            )
        } else {
            // Enabled state
            StatusCard(
                lastSyncTime = lastSyncTime,
                syncStatus = syncStatus,
                onSyncNow = { viewModel.syncNow() }
            )
            
            PermissionsCard(
                requiredPermissions = requiredPermissions
            )
            
            // Pending to sync
            if (pendingToSync.isNotEmpty()) {
                PendingSyncCard(
                    count = pendingToSync.size,
                    onSyncNow = { viewModel.syncNow() }
                )
            }
            
            // Conflicts
            if (conflicts.isNotEmpty()) {
                ConflictsCard(
                    conflicts = conflicts,
                    onResolveAllLocal = { viewModel.resolveAllConflictsLocalWins() },
                    onResolveLocal = { id -> viewModel.resolveConflictLocalWins(id) },
                    onResolveRemote = { id -> viewModel.resolveConflictRemoteWins(id) }
                )
            }
            
            // Stats
            syncStats?.let { stats ->
                SyncStatsCard(stats = stats)
            }
            
            // Disable button
            Button(
                onClick = { viewModel.disableHealthConnect() },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.errorContainer,
                    contentColor = colors.onErrorContainer
                )
            ) {
                Text("Desativar Health Connect", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        
        error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
    }
}

@Composable
fun NotAvailableCard() {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🏥", fontSize = 48.sp)
            Text("Health Connect não disponível", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("O Health Connect não está instalado ou não é compatível com este dispositivo.", fontSize = 14.sp, textAlign = TextAlign.Center, color = colors.onSurfaceVariant)
            Text("Você pode baixá-lo na Play Store para sincronizar dados de saúde.", fontSize = 12.sp, textAlign = TextAlign.Center, color = colors.onSurfaceVariant)
            Button(onClick = { /* open Play Store */ }, shape = RoundedCornerShape(12.dp)) {
                Text("Abrir Play Store", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SetupCard(
    requiredPermissions: List<HealthConnectViewModel.PermissionInfo>,
    onRequestPermissions: () -> Unit,
    onEnable: () -> Unit
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
            Text("Configurar Health Connect", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text("O CycleTrack pode ler e gravar dados de saúde no Health Connect.", fontSize = 14.sp, color = colors.onSurfaceVariant)
            
            Text("Permissões necessárias:", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                requiredPermissions.forEach { perm ->
                    PermissionRow(
                        permission = perm,
                        onToggle = { /* handled by requestPermissions */ }
                    )
                }
            }
            
            Button(
                onClick = onRequestPermissions,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Solicitar permissões", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            
            Text("Após conceder as permissões, o Health Connect será ativado automaticamente.", fontSize = 12.sp, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun PermissionRow(
    permission: HealthConnectViewModel.PermissionInfo,
    onToggle: (Boolean) -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(permission.icon, fontSize = 24.sp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(permission.displayName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                    if (permission.isRequired) {
                        Text("Obrigatório", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = colors.error)
                    }
                }
                Text(permission.description, fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.fillMaxWidth())
            androidx.compose.material3.Checkbox(
                checked = permission.isGranted,
                onCheckedChange = { /* handled by request */ },
                colors = CheckboxDefaults.colors(
                    checkedColor = colors.success,
                    uncheckedColor = colors.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun StatusCard(
    lastSyncTime: Long,
    syncStatus: HealthConnectViewModel.SyncStatus,
    onSyncNow: () -> Unit
) {
    val colors = CycleTrackColors()
    val (statusText, statusColor, isLoading) = when (syncStatus) {
        is HealthConnectViewModel.SyncStatus.IDLE -> "Pronto para sincronizar" to colors.success to false
        is HealthConnectViewModel.SyncStatus.SYNCING -> "Sincronizando... ${it.progress}%" to colors.warning to true
        is HealthConnectViewModel.SyncStatus.SUCCESS -> "Sincronizado com sucesso (${it.syncedCount} itens)" to colors.success to false
        is HealthConnectViewModel.SyncStatus.ERROR -> "Erro: ${it.message}" to colors.error to false
        is HealthConnectViewModel.SyncStatus.CONFLICTS -> "${it.count} conflitos detectados" to colors.warning to false
    }
    
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Status da sincronização", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                    Text(statusText, fontSize = 14.sp, color = statusColor)
                }
                
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(color = statusColor, modifier = Modifier.size(24.dp))
                }
            }
            
            if (lastSyncTime > 0) {
                Text("Última sincronização: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("pt", "BR")).format(java.util.Date(lastSyncTime))}", fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
            
            Button(
                onClick = onSyncNow,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Sincronizando...", fontSize = 14.sp)
                    }
                } else {
                    Text("Sincronizar agora", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun PermissionsCard(requiredPermissions: List<HealthConnectViewModel.PermissionInfo>) {
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
            Text("Permissões concedidas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                requiredPermissions.filter { it.isGranted }.forEach { perm ->
                    PermissionRow(
                        permission = perm,
                        onToggle = { }
                    )
                }
            }
        }
    }
}

@Composable
fun PendingSyncCard(count: Int, onSyncNow: () -> Unit) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp,
        colors = CardDefaults.cardColors(
            containerColor = colors.warningContainer,
            contentColor = colors.onWarningContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⏳", fontSize = 24.sp)
                Spacer(modifier = Modifier.size(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("$count itens pendentes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Dados locais aguardando sincronização com Health Connect", fontSize = 12.sp, color = colors.onWarningContainer.copy(alpha = 0.8f))
                }
            }
            Button(onClick = onSyncNow, shape = RoundedCornerShape(12.dp)) {
                Text("Sincronizar agora", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ConflictsCard(
    conflicts: List<HealthConnectViewModel.ConflictInfo>,
    onResolveAllLocal: () -> Unit,
    onResolveLocal: (String) -> Unit,
    onResolveRemote: (String) -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp,
        colors = CardDefaults.cardColors(
            containerColor = colors.errorContainer,
            contentColor = colors.onErrorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⚠️ ${conflicts.size} conflitos detectados", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Button(onClick = onResolveAllLocal, shape = RoundedCornerShape(12.dp)) {
                    Text("Resolver todos (local)", fontSize = 12.sp)
                }
            }
            
            Text("O mesmo dado foi alterado no app e no Health Connect. Escolha qual versão manter.", fontSize = 12.sp, color = colors.onErrorContainer.copy(alpha = 0.8f))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                conflicts.forEach { conflict ->
                    ConflictRow(
                        conflict = conflict,
                        onLocal = { onResolveLocal(conflict.dataPointId) },
                        onRemote = { onResolveRemote(conflict.dataPointId) }
                    )
                }
            }
        }
    }
}

@Composable
fun ConflictRow(
    conflict: HealthConnectViewModel.ConflictInfo,
    onLocal: () -> Unit,
    onRemote: () -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("${conflict.dataType} • ${java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale("pt", "BR")).format(java.util.Date(conflict.localTimestamp))}", fontSize = 12.sp, color = colors.onErrorContainer.copy(alpha = 0.7f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Local", fontSize = 10.sp, color = colors.onErrorContainer.copy(alpha = 0.6f))
                    Text(conflict.localValue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Column(
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Health Connect", fontSize = 10.sp, color = colors.onErrorContainer.copy(alpha = 0.6f))
                    Text(conflict.remoteValue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onLocal, modifier = Modifier.fillMaxWidth(0.5f), shape = RoundedCornerShape(8.dp)) {
                    Text("Manter local", fontSize = 12.sp)
                }
                Button(onClick = onRemote, modifier = Modifier.fillMaxWidth(0.5f), shape = RoundedCornerShape(8.dp)) {
                    Text("Manter remoto", fontSize = 12.sp)
                }
            }
        }
    )
}

@Composable
fun SyncStatsCard(stats: HealthConnectViewModel.SyncStats) {
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
            Text("Estatísticas de sincronização", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem("🔄", "${stats.totalSynced}", "Total sincronizado")
                androidx.compose.material3.VerticalDivider()
                StatItem("⏱️", "${stats.lastSyncDurationMs / 1000}s", "Última duração")
                androidx.compose.material3.VerticalDivider()
                StatItem("⚠️", "${stats.conflictsResolved}", "Conflitos resolvidos")
                androidx.compose.material3.VerticalDivider()
                StatItem("❌", "${stats.errorsCount}", "Erros")
            }
        }
    )
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
            Text(icon, fontSize = 20.sp)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text(label, fontSize = 10.sp, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.VerticalDivider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer