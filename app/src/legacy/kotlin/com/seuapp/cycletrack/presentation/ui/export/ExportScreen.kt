package com.seuapp.cycletrack.presentation.ui.export

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
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
import com.seuapp.cycletrack.presentation.state.ExportViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormatter
import java.util.Locale

/**
 * Tela de Exportação - LGPD/GDPR Art. 20 com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun ExportScreen(
    onNavigateToProgress: () -> Unit,
    onNavigateToDownload: () -> Unit
) {
    val viewModel: ExportViewModel = hiltViewModel()
    val exports by viewModel.exports.collectAsStateWithLifecycle()
    val pendingExports by viewModel.pendingExports.collectAsStateWithLifecycle()
    val currentExport by viewModel.currentExport.collectAsStateWithLifecycle()
    val exportProgress by viewModel.exportProgress.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isExporting by viewModel.isExporting.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val canExport by viewModel.canExport.collectAsStateWithLifecycle()
    val formatOptions = viewModel.formatOptions
    val dateRangeOptions = viewModel.dateRangeOptions
    
    // Form state
    val selectedFormat by viewModel.selectedFormat.collectAsStateWithLifecycle()
    val includeCycles by viewModel.includeCycles.collectAsStateWithLifecycle()
    val includeSymptoms by viewModel.includeSymptoms.collectAsStateWithLifecycle()
    val includePredictions by viewModel.includePredictions.collectAsStateWithLifecycle()
    val includePregnancy by viewModel.includePregnancy.collectAsStateWithLifecycle()
    val includePartnerData by viewModel.includePartnerData.collectAsStateWithLifecycle()
    val includeHealthConnectData by viewModel.includeHealthConnectData.collectAsStateWithLifecycle()
    val includeSettings by viewModel.includeSettings.collectAsStateWithLifecycle()
    val includeContent by viewModel.includeContent.collectAsStateWithLifecycle()
    val dateRangeStart by viewModel.dateRangeStart.collectAsStateWithLifecycle()
    val dateRangeEnd by viewModel.dateRangeEnd.collectAsStateWithLifecycle()
    val anonymize by viewModel.anonymize.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Exportar dados", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        Text("Baixe uma cópia completa dos seus dados (LGPD/GDPR Art. 20)", fontSize = 16.sp, color = colors.onSurfaceVariant)
        
        // Export form
        if (currentExport == null) {
            ExportFormCard(
                formatOptions = formatOptions,
                selectedFormat = selectedFormat,
                onFormatChange = { viewModel.setFormat(it) },
                includeCycles = includeCycles,
                onCyclesChange = { viewModel.setIncludeCycles(it) },
                includeSymptoms = includeSymptoms,
                onSymptomsChange = { viewModel.setIncludeSymptoms(it) },
                includePredictions = includePredictions,
                onPredictionsChange = { viewModel.setIncludePredictions(it) },
                includePregnancy = includePregnancy,
                onPregnancyChange = { viewModel.setIncludePregnancy(it) },
                includePartnerData = includePartnerData,
                onPartnerChange = { viewModel.setIncludePartnerData(it) },
                includeHealthConnectData = includeHealthConnectData,
                onHealthChange = { viewModel.setIncludeHealthConnectData(it) },
                includeSettings = includeSettings,
                onSettingsChange = { viewModel.setIncludeSettings(it) },
                includeContent = includeContent,
                onContentChange = { viewModel.setIncludeContent(it) },
                dateRangeOptions = dateRangeOptions,
                dateRangeStart = dateRangeStart,
                dateRangeEnd = dateRangeEnd,
                onDateRangeChange = { start, end ->
                    viewModel.setCustomDateRange(start, end)
                },
                anonymize = anonymize,
                onAnonymizeChange = { viewModel.setAnonymize(it) },
                canExport = canExport,
                isExporting = isExporting,
                onExport = { viewModel.requestExport() }
            )
        } else {
            // Show progress
            ExportProgressCard(
                export = currentExport,
                progress = exportProgress,
                onCancel = { /* cancel export */ }
            )
        }
        
        // Previous exports
        if (exports.isNotEmpty()) {
            Text("Exportações anteriores", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0)
            ) {
                items(exports) { export ->
                    ExportHistoryCard(
                        export = export,
                        onDownload = { viewModel.downloadExport(it.id) },
                        onDelete = { viewModel.deleteExport(it.id) },
                        onRetry = { viewModel.retryExport(it.id) }
                    )
                }
            }
        }
        
        error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
    }
}

@Composable
fun ExportFormCard(
    formatOptions: List<ExportViewModel.FormatOption>,
    selectedFormat: String,
    onFormatChange: (String) -> Unit,
    includeCycles: Boolean,
    onCyclesChange: (Boolean) -> Unit,
    includeSymptoms: Boolean,
    onSymptomsChange: (Boolean) -> Unit,
    includePredictions: Boolean,
    onPredictionsChange: (Boolean) -> Unit,
    includePregnancy: Boolean,
    onPregnancyChange: (Boolean) -> Unit,
    includePartnerData: Boolean,
    onPartnerChange: (Boolean) -> Unit,
    includeHealthConnectData: Boolean,
    onHealthChange: (Boolean) -> Unit,
    includeSettings: Boolean,
    onSettingsChange: (Boolean) -> Unit,
    includeContent: Boolean,
    onContentChange: (Boolean) -> Unit,
    dateRangeOptions: List<ExportViewModel.DateRangeOption>,
    dateRangeStart: LocalDate?,
    dateRangeEnd: LocalDate?,
    onDateRangeChange: (LocalDate?, LocalDate?) -> Unit,
    anonymize: Boolean,
    onAnonymizeChange: (Boolean) -> Unit,
    canExport: Boolean,
    isExporting: Boolean,
    onExport: () -> Unit
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
            // Format selection
            Text("Formato", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                formatOptions.forEach { option ->
                    val isSelected = selectedFormat == option.id
                    Chip(
                        onClick = { onFormatChange(option.id) },
                        modifier = Modifier.padding(0.dp),
                        label = { 
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(option.icon, fontSize = 14.sp)
                                Text(option.title, fontSize = 12.sp)
                            }
                        },
                        colors = ChipDefaults.colors(
                            containerColor = if (isSelected) colors.primary else colors.surfaceVariant,
                            contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
                        )
                    )
                }
            }
            
            // Data to include
            Text("Dados a incluir", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CheckboxRow("Ciclos menstruais", includeCycles, onCyclesChange)
                CheckboxRow("Sintomas", includeSymptoms, onSymptomsChange)
                CheckboxRow("Previsões", includePredictions, onPredictionsChange)
                CheckboxRow("Gravidez", includePregnancy, onPregnancyChange)
                CheckboxRow("Dados do parceiro", includePartnerData, onPartnerChange)
                CheckboxRow("Health Connect", includeHealthConnectData, onHealthChange)
                CheckboxRow("Configurações", includeSettings, onSettingsChange)
                CheckboxRow("Conteúdo lido", includeContent, onContentChange)
            }
            
            // Date range
            Text("Período", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                dateRangeOptions.forEach { option ->
                    val isCustom = option.id == "custom"
                    val isSelected = when {
                        isCustom -> dateRangeStart != null && dateRangeEnd != null
                        else -> dateRangeStart == option.startDate && dateRangeEnd == option.endDate
                    }
                    
                    Chip(
                        onClick = { 
                            if (!isCustom) viewModel.setDateRange(option.id)
                        },
                        modifier = Modifier.padding(0.dp),
                        label = { Text(option.title, fontSize = 12.sp) },
                        colors = ChipDefaults.colors(
                            containerColor = if (isSelected) colors.primary else colors.surfaceVariant,
                            contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
                        )
                    )
                }
                
                if (dateRangeStart != null && dateRangeEnd != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = dateRangeStart!!.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            onValueChange = { },
                            label = { Text("Início") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )
                        OutlinedTextField(
                            value = dateRangeEnd!!.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            onValueChange = { },
                            label = { Text("Fim") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )
                    }
                }
            }
            
            // Anonymize
            CheckboxRow(
                "Anonimizar dados (remover identificadores pessoais)",
                anonymize,
                onAnonymizeChange
            )
            
            // Export button
            Button(
                onClick = onExport,
                enabled = canExport && !isExporting,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isExporting) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Gerando exportação...", fontSize = 14.sp)
                    }
                } else {
                    Text("Solicitar exportação", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun CheckboxRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val colors = CycleTrackColors()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = colors.onSurface)
        Checkbox(
            checked = checked,
            onCheckedChange = onChange,
            colors = CheckboxDefaults.colors(
                checkedColor = colors.primary,
                uncheckedColor = colors.onSurfaceVariant
            )
        )
    }
}

@Composable
fun ExportProgressCard(
    export: com.seuapp.cycletrack.domain.model.Export,
    progress: Int,
    onCancel: () -> Unit
) {
    val colors = CycleTrackColors()
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
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📤", fontSize = 48.sp)
            Text("Gerando exportação", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("${export.format.uppercase()} • ${export.dateRangeStart} a ${export.dateRangeEnd}", fontSize = 14.sp, color = colors.onPrimaryContainer.copy(alpha = 0.8f))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    trackThickness = 8.dp,
                    color = colors.primary,
                    trackColor = colors.onPrimaryContainer.copy(alpha = 0.3f)
                )
                Text("$progress%", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            
            androidx.compose.material3.TextButton(onClick = onCancel) {
                Text("Cancelar", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ExportHistoryCard(
    export: com.seuapp.cycletrack.domain.model.Export,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onRetry: () -> Unit
) {
    val colors = CycleTrackColors()
    val (statusIcon, statusColor) = when (export.status) {
        com.seuapp.cycletrack.domain.model.Export.STATUS_COMPLETED -> "✅" to colors.success
        com.seuapp.cycletrack.domain.model.Export.STATUS_PROCESSING -> "⏳" to colors.warning
        com.seuapp.cycletrack.domain.model.Export.STATUS_FAILED -> "❌" to colors.error
        com.seuapp.cycletrack.domain.model.Export.STATUS_PENDING -> "⏳" to colors.warning
        com.seuapp.cycletrack.domain.model.Export.STATUS_EXPIRED -> "⌛" to colors.onSurfaceVariant
        else -> "❓" to colors.onSurfaceVariant
    }
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(12.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(statusIcon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.size(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("${export.format.uppercase()} • ${export.requestedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("${export.fileSize?.let { formatFileSize(it) } ?: "—"}", fontSize = 12.sp, color = colors.onSurfaceVariant)
                    }
                }
                
                Chip(
                    onClick = { },
                    modifier = Modifier.padding(0.dp),
                    label = { Text(export.statusDisplayName, fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                    colors = ChipDefaults.colors(
                        containerColor = statusColor.copy(alpha = 0.15f),
                        contentColor = statusColor
                    )
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (export.status) {
                    com.seuapp.cycletrack.domain.model.Export.STATUS_COMPLETED -> {
                        Button(onClick = onDownload, modifier = Modifier.fillMaxWidth(0.5f), shape = RoundedCornerShape(12.dp)) {
                            Text("Baixar", fontSize = 14.sp)
                        }
                    }
                    com.seuapp.cycletrack.domain.model.Export.STATUS_FAILED -> {
                        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth(0.5f), shape = RoundedCornerShape(12.dp)) {
                            Text("Tentar novamente", fontSize = 14.sp)
                        }
                    }
                }
                androidx.compose.material3.OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(0.5f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.error)
                ) {
                    Text("Excluir", fontSize = 14.sp)
                }
            }
        }
    }
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${(bytes / 1024.0).roundToInt()} KB"
        bytes < 1024 * 1024 * 1024 -> "${String.format("%.1f", bytes / (1024.0 * 1024))} MB"
        else -> "${String.format("%.1f", bytes / (1024.0 * 1024 * 1024))} GB"
    }
}

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Chip
import androidx.compose.material3.ChipDefaults
import kotlinx.datetime.format.DateTimeFormatter
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seuapp.cycletrack.presentation.state.DateRangeOption
import com.seuapp.cycletrack.presentation.state.FormatOption
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer