package com.seuapp.cycletrack.presentation.ui.logging

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seuapp.cycletrack.presentation.state.LoggingViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.datetime.LocalDate

/**
 * Tela de Registro (Logging) - Menstruação, Sintomas, Peso, Temperatura, Humor com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun LoggingScreen(
    onLogPeriod: () -> Unit,
    onLogSymptoms: () -> Unit,
    onLogWeight: () -> Unit,
    onLogTemperature: () -> Unit,
    onLogMood: () -> Unit
) {
    val viewModel: LoggingViewModel = hiltViewModel()
    val currentTab by remember { mutableStateOf("period") } // period, symptoms, weight, temperature, mood
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Header
        LoggingHeader()
        
        // Tab bar
        LoggingTabBar(currentTab = currentTab, onTabChange = { currentTab = it })
        
        // Content
        when (currentTab) {
            "period" -> PeriodLoggingScreen(viewModel = viewModel)
            "symptoms" -> SymptomLoggingScreen(viewModel = viewModel)
            "weight" -> WeightLoggingScreen(viewModel = viewModel)
            "temperature" -> TemperatureLoggingScreen(viewModel = viewModel)
            "mood" -> MoodLoggingScreen(viewModel = viewModel)
            else -> PeriodLoggingScreen(viewModel = viewModel)
        }
        
        Spacer(100.dp)
    }
}

@Composable
fun LoggingHeader() {
    val colors = CycleTrackColors()
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("Registrar", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        Text("O que você quer registrar hoje?", fontSize = 16.sp, color = colors.onSurfaceVariant)
    }
}

@Composable
fun LoggingTabBar(currentTab: String, onTabChange: (String) -> Unit) {
    val colors = CycleTrackColors()
    val tabs = listOf(
        "period" to "Menstruação" to "🩸",
        "symptoms" to "Sintomas" to "📝",
        "weight" to "Peso" to "⚖️",
        "temperature" to "Temp. Basal" to "🌡️",
        "mood" to "Humor" to "😊"
    )
    
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
            .background(colors.surfaceVariant, RoundedCornerShape(12.dp)),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEach { (tabId, label, icon) ->
            val isSelected = currentTab == tabId
            androidx.compose.material3.Chip(
                onClick = { onTabChange(tabId) },
                modifier = Modifier.fillMaxWidth(0.2f)
                    .padding(4.dp),
                colors = androidx.compose.material3.ChipDefaults.colors(
                    containerColor = if (isSelected) colors.primary else colors.surfaceVariant,
                    contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = icon, fontSize = 14.sp)
                    Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                }
            }
        }
    }
}

// ============ Period Logging ============

@Composable
fun PeriodLoggingScreen(viewModel: LoggingViewModel) {
    val state by viewModel.periodLoggingState.collectAsStateWithLifecycle()
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toggle start/end
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabButton("Início", state.isLoggingStart) { viewModel.setPeriodLoggingStart(true) }
            TabButton("Fim", !state.isLoggingStart) { viewModel.setPeriodLoggingStart(false) }
        }
        
        if (state.isLoggingStart) {
            // Start period form
            DateField(
                label = "Data de início",
                value = state.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                onClick = { /* Date picker */ }
            )
            
            FlowLevelSelector(
                selectedLevel = state.flowLevel,
                onLevelChange = { viewModel.setPeriodFlowLevel(it) }
            )
            
            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.setPeriodNotes(it) },
                label = { Text("Observações (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3
            )
            
            Button(
                onClick = { viewModel.savePeriodLog() },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Registrar início da menstruação", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            // End period form
            DateField(
                label = "Data de fim",
                value = state.endDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Selecione a data",
                onClick = { /* Date picker */ }
            )
            
            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.setPeriodNotes(it) },
                label = { Text("Observações (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3
            )
            
            Button(
                onClick = { viewModel.savePeriodLog() },
                enabled = !state.isLoading && state.endDate != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Registrar fim da menstruação", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        
        state.error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
        
        state.success?.let {
            Text("Registrado com sucesso!", fontSize = 12.sp, color = colors.success)
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = CycleTrackColors()
    androidx.compose.material3.Chip(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(0.5f).padding(0.dp),
        colors = androidx.compose.material3.ChipDefaults.colors(
            containerColor = if (isSelected) colors.primary else colors.surfaceVariant,
            contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DateField(label: String, value: String, onClick: () -> Unit) {
    val colors = CycleTrackColors()
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth()
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        onClick = onClick
    )
}

@Composable
fun FlowLevelSelector(selectedLevel: LoggingViewModel.PeriodLoggingState.FlowLevel, onLevelChange: (LoggingViewModel.PeriodLoggingState.FlowLevel) -> Unit) {
    val colors = CycleTrackColors()
    val levels = LoggingViewModel.PeriodLoggingState.FlowLevel.values()
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Intensidade do fluxo", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            levels.forEach { level ->
                FlowLevelChip(
                    level = level,
                    isSelected = selectedLevel == level,
                    onClick = { onLevelChange(level) }
                )
            }
        }
    }
}

@Composable
fun FlowLevelChip(
    level: LoggingViewModel.PeriodLoggingState.FlowLevel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = CycleTrackColors()
    val levelColor = when (level) {
        LoggingViewModel.PeriodLoggingState.FlowLevel.SPOTTING -> colors.phaseMenstrual.copy(alpha = 0.3f)
        LoggingViewModel.PeriodLoggingState.FlowLevel.LIGHT -> colors.phaseMenstrual.copy(alpha = 0.5f)
        LoggingViewModel.PeriodLoggingState.FlowLevel.MEDIUM -> colors.phaseMenstrual
        LoggingViewModel.PeriodLoggingState.FlowLevel.HEAVY -> colors.phaseMenstrual.copy(alpha = 0.8f)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(0.23f)
            .padding(4.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = if (isSelected) 2.dp else 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) levelColor else colors.surface,
            contentColor = if (isSelected) Color.White else levelColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Visual indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(level.value) { i ->
                    Box(modifier = Modifier.size(8.dp))
                        .background(if (isSelected) Color.White else levelColor, RoundedCornerShape(4.dp))
                }
            }
            Text(text = level.label, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

// ============ Symptom Logging ============

@Composable
fun SymptomLoggingScreen(viewModel: LoggingViewModel) {
    val state by viewModel.symptomLoggingState.collectAsStateWithLifecycle()
    val catalog = viewModel.symptomCatalog
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date selector
        DateField(
            label = "Data",
            value = state.selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onClick = { /* Date picker */ }
        )
        
        // Categories
        catalog.entries.forEach { (category, symptoms) ->
            SymptomCategorySection(
                category = category,
                symptoms = symptoms,
                selectedSymptoms = state.selectedSymptoms,
                onToggle = { name, cat ->
                    viewModel.toggleSymptom(name, cat)
                },
                onSeverityChange = { name, severity ->
                    viewModel.updateSymptomSeverity(name, severity)
                },
                onNotesChange = { name, notes ->
                    viewModel.updateSymptomNotes(name, notes)
                }
            )
        }
        
        if (state.selectedSymptoms.isNotEmpty()) {
            Spacer(16.dp)
            Button(
                onClick = { viewModel.saveSymptoms() },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Salvar ${state.selectedSymptoms.size} sintoma(s)", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        
        state.error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
    }
}

@Composable
fun SymptomCategorySection(
    category: String,
    symptoms: List<LoggingViewModel.SymptomInfo>,
    selectedSymptoms: Map<String, LoggingViewModel.SymptomInput>,
    onToggle: (String, String) -> Unit,
    onSeverityChange: (String, com.seuapp.cycletrack.domain.model.SymptomSeverity) -> Unit,
    onNotesChange: (String, String) -> Unit
) {
    val colors = CycleTrackColors()
    val categoryLabel = LoggingViewModel.SymptomCatalog.getCategoryLabel(category)
    val categoryColor = when (category) {
        "physical" -> colors.categoryPhysical
        "emotional" -> colors.categoryEmotional
        "discharge" -> colors.categoryDischarge
        "pain" -> colors.categoryPain
        "lifestyle" -> colors.categoryLifestyle
        else -> colors.onSurfaceVariant
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(categoryLabel, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = categoryColor)
        
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0)
        ) {
            items(symptoms) { symptom ->
                SymptomSelectorCard(
                    symptom = symptom,
                    isSelected = symptom.key in selectedSymptoms,
                    selectedInput = selectedSymptoms[symptom.key],
                    onToggle = { onToggle(symptom.key, category) },
                    onSeverityChange = { onSeverityChange(symptom.key, it) },
                    onNotesChange = { onNotesChange(symptom.key, it) }
                )
            }
        }
    }
}

@Composable
fun SymptomSelectorCard(
    symptom: LoggingViewModel.SymptomInfo,
    isSelected: Boolean,
    selectedInput: LoggingViewModel.SymptomInput?,
    onToggle: () -> Unit,
    onSeverityChange: (com.seuapp.cycletrack.domain.model.SymptomSeverity) -> Unit,
    onNotesChange: (String) -> Unit
) {
    val colors = CycleTrackColors()
    val input = selectedInput ?: LoggingViewModel.SymptomInput(symptom.key, "", com.seuapp.cycletrack.domain.model.SymptomSeverity.MILD, "")
    
    Card(
        modifier = Modifier.size(140.dp)
            .padding(4.dp),
        onClick = onToggle,
        shape = RoundedCornerShape(12.dp),
        elevation = if (isSelected) 2.dp else 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) colors.primaryContainer else colors.surface,
            contentColor = if (isSelected) colors.onPrimaryContainer else colors.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = symptom.emoji, fontSize = 28.sp)
            Text(text = symptom.label, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, maxLines = 2)
            
            if (isSelected) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Severity selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        com.seuapp.cycletrack.domain.model.SymptomSeverity.values().forEach { severity ->
                            SeverityChip(
                                severity = severity,
                                isSelected = input.severity == severity,
                                onClick = { onSeverityChange(severity) }
                            )
                        }
                    }
                    
                    // Notes
                    OutlinedTextField(
                        value = input.notes,
                        onValueChange = onNotesChange,
                        label = { Text("Obs.") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Composable
fun SeverityChip(
    severity: com.seuapp.cycletrack.domain.model.SymptomSeverity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = CycleTrackColors()
    val color = when (severity) {
        com.seuapp.cycletrack.domain.model.SymptomSeverity.MILD -> colors.success
        com.seuapp.cycletrack.domain.model.SymptomSeverity.MODERATE -> colors.warning
        com.seuapp.cycletrack.domain.model.SymptomSeverity.SEVERE -> colors.error
    }
    
    androidx.compose.material3.Chip(
        onClick = onClick,
        modifier = Modifier.padding(0.dp),
        colors = androidx.compose.material3.ChipDefaults.colors(
            containerColor = if (isSelected) color else color.copy(alpha = 0.15f),
            contentColor = if (isSelected) Color.White else color
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text = severity.label.substring(0, 1), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// ============ Weight Logging ============

@Composable
fun WeightLoggingScreen(viewModel: LoggingViewModel) {
    val state by viewModel.weightLoggingState.collectAsStateWithLifecycle()
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Weight input with slider
        WeightInputField(
            weight = state.weight,
            unit = state.unit,
            onWeightChange = { viewModel.setWeight(it) },
            onUnitChange = { viewModel.setWeightUnit(it) }
        )
        
        DateField(
            label = "Data",
            value = state.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onClick = { /* Date picker */ }
        )
        
        OutlinedTextField(
            value = state.notes,
            onValueChange = { viewModel.setWeightNotes(it) },
            label = { Text("Observações (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = 2
        )
        
        Button(
            onClick = { viewModel.saveWeight() },
            enabled = !state.isLoading && state.weight > 0,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (state.isLoading) {
                androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Salvar peso", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        
        state.error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
        
        state.success?.let {
            Text("Peso salvo com sucesso!", fontSize = 12.sp, color = colors.success)
        }
    }
}

@Composable
fun WeightInputField(
    weight: Double,
    unit: String,
    onWeightChange: (Double) -> Unit,
    onUnitChange: (String) -> Unit
) {
    val colors = CycleTrackColors()
    val displayWeight = if (weight == 0.0) "" else weight.toString()
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unit selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("kg" to "Kg", "lbs" to "Lbs").forEach { (u, label) ->
                    val isSelected = unit == u
                    androidx.compose.material3.Chip(
                        onClick = { onUnitChange(u) },
                        colors = ChipDefaults.colors(
                            containerColor = if (isSelected) colors.primary else colors.surfaceVariant,
                            contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.fillMaxWidth())
            
            // Weight display
            Text(text = "${displayWeight} $unit", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        }
        
        // Slider
        val maxWeight = if (unit == "kg") 200.0 else 440.0
        androidx.compose.material3.Slider(
            value = weight.coerceIn(0.0, maxWeight).toFloat(),
            onValueChange = { onWeightChange(it.toDouble()) },
            valueRange = 0f..maxWeight.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = colors.primary,
                trackColor = colors.primaryContainer,
                activeTrackColor = colors.primary
            )
        )
        
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0", fontSize = 12.sp, color = colors.onSurfaceVariant)
            Text(maxWeight.toInt().toString(), fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}

// ============ Temperature Logging ============

@Composable
fun TemperatureLoggingScreen(viewModel: LoggingViewModel) {
    val state by viewModel.temperatureLoggingState.collectAsStateWithLifecycle()
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Temperature input
        TemperatureInputField(
            temperature = state.temperature,
            unit = state.unit,
            onTemperatureChange = { viewModel.setTemperature(it) },
            onUnitChange = { viewModel.setTemperatureUnit(it) }
        )
        
        DateField(
            label = "Data",
            value = state.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onClick = { /* Date picker */ }
        )
        
        TimeField(
            label = "Horário",
            value = state.time,
            onClick = { /* Time picker */ }
        )
        
        OutlinedTextField(
            value = state.notes,
            onValueChange = { viewModel.setTemperatureNotes(it) },
            label = { Text("Observações (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = 2
        )
        
        // BBT info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = 1.dp,
            colors = CardDefaults.cardColors(
                containerColor = colors.infoContainer,
                contentColor = colors.onInfoContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("💡", fontSize = 20.sp)
                    Text("Dica: Meça a temperatura basal ao acordar, antes de levantar da cama, sempre no mesmo horário.", fontSize = 12.sp)
                }
            }
        }
        
        Button(
            onClick = { viewModel.saveTemperature() },
            enabled = !state.isLoading && state.temperature > 0,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (state.isLoading) {
                androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Salvar temperatura", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        
        state.error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
        
        state.success?.let {
            Text("Temperatura salva com sucesso!", fontSize = 12.sp, color = colors.success)
        }
    }
}

@Composable
fun TemperatureInputField(
    temperature: Double,
    unit: String,
    onTemperatureChange: (Double) -> Unit,
    onUnitChange: (String) -> Unit
) {
    val colors = CycleTrackColors()
    val displayTemp = if (temperature == 0.0) "" : String = temperature.toString()
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unit selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("celsius" to "°C", "fahrenheit" to "°F").forEach { (u, label) ->
                    val isSelected = unit == u
                    androidx.compose.material3.Chip(
                        onClick = { onUnitChange(u) },
                        colors = ChipDefaults.colors(
                            containerColor = if (isSelected) colors.primary else colors.surfaceVariant,
                            contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.fillMaxWidth())
            
            // Temperature display
            Text(text = "${displayTemp}°${if (unit == "celsius") "C" else "F"}", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        }
        
        // Slider
        val (minTemp, maxTemp) = if (unit == "celsius") 35.0..42.0 else 95.0..107.6
        androidx.compose.material3.Slider(
            value = temperature.coerceIn(minTemp, maxTemp).toFloat(),
            onValueChange = { onTemperatureChange(it.toDouble()) },
            valueRange = minTemp.toFloat()..maxTemp.toFloat(),
            steps = ((maxTemp - minTemp) * 10).toInt(),
            colors = SliderDefaults.colors(
                thumbColor = colors.primary,
                trackColor = colors.primaryContainer,
                activeTrackColor = colors.primary
            )
        )
        
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(minTemp.toString(), fontSize = 12.sp, color = colors.onSurfaceVariant)
            Text(maxTemp.toString(), fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun TimeField(label: String, value: String, onClick: () -> Unit) {
    val colors = CycleTrackColors()
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth()
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        onClick = onClick
    )
}

// ============ Mood Logging ============

@Composable
fun MoodLoggingScreen(viewModel: LoggingViewModel) {
    val state by viewModel.moodLoggingState.collectAsStateWithLifecycle()
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Como você está se sentindo hoje?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        
        DateField(
            label = "Data",
            value = state.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onClick = { /* Date picker */ }
        )
        
        // Mood grid
        androidx.compose.foundation.lazy.LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0)
        ) {
            items(LoggingViewModel.MoodType.values()) { mood ->
                MoodCard(
                    mood = mood,
                    isSelected = state.mood == mood,
                    onClick = { viewModel.setMood(mood) }
                )
            }
        }
        
        OutlinedTextField(
            value = state.notes,
            onValueChange = { viewModel.setMoodNotes(it) },
            label = { Text("Observações (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = 2
        )
        
        Button(
            onClick = { viewModel.saveMood() },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (state.isLoading) {
                androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Salvar humor", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        
        state.error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
        
        state.success?.let {
            Text("Humor salvo com sucesso!", fontSize = 12.sp, color = colors.success)
        }
    }
}

@Composable
fun MoodCard(
    mood: LoggingViewModel.MoodType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = CycleTrackColors()
    val moodColor = when (mood) {
        LoggingViewModel.MoodType.VERY_HAPPY, LoggingViewModel.MoodType.HAPPY -> colors.success
        LoggingViewModel.MoodType.NEUTRAL -> colors.warning
        LoggingViewModel.MoodType.SAD, LoggingViewModel.MoodType.VERY_SAD -> colors.error
        LoggingViewModel.MoodType.ANXIOUS, LoggingViewModel.MoodType.IRRITATED -> colors.phaseOvulation
        LoggingViewModel.MoodType.TIRED -> colors.phaseFollicular
    }
    
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(4.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = if (isSelected) 3.dp else 1.dp,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) moodColor else moodColor.copy(alpha = 0.1f),
            contentColor = if (isSelected) Color.White else moodColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = mood.emoji, fontSize = 32.sp)
            Text(text = mood.label, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

import kotlinx.datetime.format.DateTimeFormatter
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import androidx.compose.material3.Chip
import androidx.compose.material3.ChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seuapp.cycletrack.presentation.state.SymptomCatalog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer