package com.seuapp.cycletrack.presentation.ui.home

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.seuapp.cycletrack.presentation.state.HomeViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.datetime.LocalDate
import androidx.compose.foundation.background
import androidx.compose.material3.ChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.format.DateTimeFormatter
import androidx.compose.foundation.layout.Spacer
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * Tela Home (Dashboard principal) com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun HomeScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToLogging: () -> Unit,
    onNavigateToInsights: () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val currentCycle by viewModel.currentCycle.collectAsStateWithLifecycle()
    val nextPeriod by viewModel.nextPeriodPrediction.collectAsStateWithLifecycle()
    val nextOvulation by viewModel.nextOvulationPrediction.collectAsStateWithLifecycle()
    val fertileWindow by viewModel.nextFertileWindow.collectAsStateWithLifecycle()
    val todaySymptoms by viewModel.todaySymptoms.collectAsStateWithLifecycle()
    val isInFertileWindow by viewModel.isInFertileWindow.collectAsStateWithLifecycle()
    val isInPms by viewModel.isInPms.collectAsStateWithLifecycle()
    val daysUntilNextPeriod by viewModel.daysUntilNextPeriod.collectAsStateWithLifecycle()
    val activePregnancy by viewModel.activePregnancy.collectAsStateWithLifecycle()
    val partnerConnection by viewModel.partnerConnection.collectAsStateWithLifecycle()
    val cycleDayText by viewModel.cycleDayText.collectAsStateWithLifecycle()
    val currentPhase by viewModel.currentPhase.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Header
        HomeHeader(
            cycleDayText = cycleDayText,
            currentPhase = currentPhase,
            isInFertileWindow = isInFertileWindow,
            isInPms = isInPms,
            activePregnancy = activePregnancy,
            partnerConnection = partnerConnection,
            onNavigateToCalendar = onNavigateToCalendar
        )
        
        // Current Cycle Card
        if (currentCycle != null) {
            CurrentCycleCard(
                cycle = currentCycle,
                nextPeriod = nextPeriod,
                nextOvulation = nextOvulation,
                fertileWindow = fertileWindow,
                daysUntilNextPeriod = daysUntilNextPeriod,
                isInFertileWindow = isInFertileWindow,
                isInPms = isInPms,
                onClick = onNavigateToDetail
            )
        } else {
            EmptyCycleCard(onLogPeriod = { viewModel.logPeriodStart() })
        }
        
        // Quick Actions
        QuickActionsRow(
            actions = viewModel.getQuickActions(
                onLogPeriod = { viewModel.logPeriodStart() },
                onLogSymptoms = onNavigateToLogging,
                onViewCalendar = onNavigateToCalendar,
                onViewInsights = onNavigateToInsights
            )
        )
        
        // Upcoming Predictions
        UpcomingPredictionsCard(
            nextPeriod = nextPeriod,
            nextOvulation = nextOvulation,
            fertileWindow = fertileWindow,
            isInFertileWindow = isInFertileWindow
        )
        
        // Today's Symptoms
        if (todaySymptoms.isNotEmpty()) {
            TodaySymptomsCard(symptoms = todaySymptoms)
        }
        
        // Insights Preview
        InsightsPreviewCard(
            isInFertileWindow = isInFertileWindow,
            isInPms = isInPms,
            daysUntilNextPeriod = daysUntilNextPeriod,
            activePregnancy = activePregnancy
        )
        
        // Partner Status
        if (partnerConnection != null) {
            PartnerStatusCard(partner = partnerConnection)
        }
        
        Spacer(100.dp) // Bottom nav space
    }
    
    // Error handling
    error?.let { msg ->
        androidx.compose.material3.SnackbarHost(
            androidx.compose.material3.SnackbarHostState(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun HomeHeader(
    cycleDayText: String,
    currentPhase: String,
    isInFertileWindow: Boolean,
    isInPms: Boolean,
    activePregnancy: com.seuapp.cycletrack.domain.model.Pregnancy?,
    partnerConnection: com.seuapp.cycletrack.domain.model.PartnerConnection?,
    onNavigateToCalendar: () -> Unit
) {
    val colors = CycleTrackColors()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("CycleTrack", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                    Text(Clock.System.todayIn(TimeZone.currentSystemDefault()).format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", java.util.Locale("pt", "BR"))), fontSize = 14.sp, color = colors.onSurfaceVariant)
                }
                
                // Calendar shortcut
                IconButton(onClick = onNavigateToCalendar) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.CalendarMonth,
                        contentDescription = "Calendário",
                        tint = colors.primary
                    )
                }
            }
            
            // Status chips
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PhaseChip(phase = currentPhase)
                
                if (isInFertileWindow) {
                    StatusChip("🌱 Janela fértil", colors.phaseFollicular)
                }
                
                if (isInPms) {
                    StatusChip("🌙 TPM ativa", colors.phaseLuteal)
                }
                
                if (activePregnancy != null) {
                    StatusChip("🤰 Semana ${activePregnancy.currentWeek}", colors.trimester2)
                }
                
                if (partnerConnection != null) {
                    StatusChip("👫 Conectado", colors.tertiary)
                }
            }
        }
    }
}

@Composable
fun PhaseChip(phase: String) {
    val colors = CycleTrackColors()
    val (label, color) = when (phase) {
        "menstrual" -> "🩸 Menstrual" to colors.phaseMenstrual
        "follicular" -> "🌱 Folicular" to colors.phaseFollicular
        "ovulation" -> "🥚 Ovulação" to colors.phaseOvulation
        "luteal" -> "🌙 Lútea" to colors.phaseLuteal
        else -> "❓ Desconhecido" to colors.onSurfaceVariant
    }
    
    androidx.compose.material3.Chip(
        onClick = { },
        modifier = Modifier.padding(0.dp),
        colors = androidx.compose.material3.ChipDefaults.colors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatusChip(label: String, color: Color) {
    androidx.compose.material3.Chip(
        onClick = { },
        modifier = Modifier.padding(0.dp),
        colors = androidx.compose.material3.ChipDefaults.colors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CurrentCycleCard(
    cycle: com.seuapp.cycletrack.domain.model.Cycle,
    nextPeriod: com.seuapp.cycletrack.domain.model.Prediction?,
    nextOvulation: com.seuapp.cycletrack.domain.model.Prediction?,
    fertileWindow: Pair<com.seuapp.cycletrack.domain.model.Prediction?, com.seuapp.cycletrack.domain.model.Prediction?>?,
    daysUntilNextPeriod: Int?,
    isInFertileWindow: Boolean,
    isInPms: Boolean,
    onClick: () -> Unit
) {
    val colors = CycleTrackColors()
    val isCurrent = cycle.isCurrent
    
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isCurrent) colors.primaryContainer else colors.surface,
            contentColor = if (isCurrent) colors.onPrimaryContainer else colors.onSurface
        )
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isCurrent) "Ciclo atual" else "Último ciclo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isCurrent) colors.onPrimaryContainer.copy(alpha = 0.8f) else colors.onSurfaceVariant
                    )
                    Text(
                        text = "Iniciado em ${cycle.startDate.format(DateTimeFormatter.ofPattern("d 'de' MMMM", java.util.Locale("pt", "BR")))}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Cycle day indicator
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCurrent) "Dia ${cycle.daysSinceStart + 1}" else "Finalizado",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) colors.onPrimaryContainer else colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Progress bar for current cycle
            if (isCurrent) {
                CycleProgressBar(
                    currentDay = cycle.daysSinceStart + 1,
                    cycleLength = cycle.cycleLength,
                    periodLength = cycle.periodLength,
                    ovulationDay = cycle.ovulationDate?.let { cycle.startDate.daysUntil(it).toInt() + 1 },
                    isInFertileWindow = isInFertileWindow,
                    isInPms = isInPms
                )
            }
            
            // Predictions row
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (nextPeriod != null) {
                    PredictionChip(
                        icon = "🩸",
                        label = "Próxima menstruação",
                        value = nextPeriod.predictedDate.format(DateTimeFormatter.ofPattern("d/MM")),
                        detail = daysUntilNextPeriod?.let { "em $it dias" } ?: "",
                        color = colors.phaseMenstrual
                    )
                }
                
                if (nextOvulation != null) {
                    PredictionChip(
                        icon = "🥚",
                        label = "Próxima ovulação",
                        value = nextOvulation.predictedDate.format(DateTimeFormatter.ofPattern("d/MM")),
                        detail = if (nextOvulation.confidenceScore > 0.7) "Alta confiança" else "Média confiança",
                        color = colors.phaseOvulation
                    )
                }
                
                if (fertileWindow != null && fertileWindow.first != null && fertileWindow.second != null) {
                    PredictionChip(
                        icon = "🌱",
                        label = "Janela fértil",
                        value = "${fertileWindow.first!!.predictedDate.format(DateTimeFormatter.ofPattern("d/MM"))} - ${fertileWindow.second!!.predictedDate.format(DateTimeFormatter.ofPattern("d/MM"))}",
                        detail = if (isInFertileWindow) "ATIVA AGORA" else "",
                        color = colors.phaseFollicular
                    )
                }
            }
        }
    }
}

@Composable
fun CycleProgressBar(
    currentDay: Int,
    cycleLength: Int,
    periodLength: Int,
    ovulationDay: Int?,
    isInFertileWindow: Boolean,
    isInPms: Boolean
) {
    val colors = CycleTrackColors()
    val progress = (currentDay.toFloat() / cycleLength).coerceIn(0f, 1f)
    val periodProgress = (periodLength.toFloat() / cycleLength).coerceIn(0f, 1f)
    val ovulationProgress = ovulationDay?.let { (it.toFloat() / cycleLength).coerceIn(0f, 1f) }
    val fertileStartProgress = ovulationDay?.let { ((it - 5).toFloat() / cycleLength).coerceIn(0f, 1f) }
    val fertileEndProgress = ovulationDay?.let { ((it + 1).toFloat() / cycleLength).coerceIn(0f, 1f) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main progress
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth()
                .height(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Background track
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxWidth()
                    .height(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                androidx.compose.foundation.shape.RoundedCornerShape(6.dp).let { shape ->
                    Modifier.background(shape, colors.outlineVariant)
                }
            }
            
            // Period segment
            if (periodProgress > 0) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxWidth(periodProgress)
                        .height(12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    androidx.compose.foundation.shape.RoundedCornerShape(6.dp).let { shape ->
                        Modifier.background(shape, colors.phaseMenstrual)
                    }
                }
            }
            
            // Fertile window segment
            if (fertileStartProgress != null && fertileEndProgress != null && fertileEndProgress > fertileStartProgress) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxWidth(fertileEndProgress - fertileStartProgress)
                        .height(12.dp)
                        .offset(x = (fertileStartProgress * 100).dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    androidx.compose.foundation.shape.RoundedCornerShape(6.dp).let { shape ->
                        Modifier.background(shape, colors.phaseFollicular.copy(alpha = 0.5f))
                    }
                }
            }
            
            // Ovulation marker
            ovulationProgress?.let { ovProgress ->
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.size(16.dp)
                        .offset(x = (ovProgress * 100).dp - 8.dp, y = -2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🥚", fontSize = 12.sp)
                }
            }
            
            // Current position
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.size(20.dp)
                    .offset(x = (progress * 100).dp - 10.dp, y = -4.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.size(12.dp)
                        .background(colors.primary, androidx.compose.foundation.shape.CircleShape)
                )
            }
        }
        
        // Labels
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Início", fontSize = 10.sp, color = colors.onSurfaceVariant)
            Text("Dia $currentDay", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = colors.primary)
            Text("Fim ($cycleLength)", fontSize = 10.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun PredictionChip(
    icon: String,
    label: String,
    value: String,
    detail: String,
    color: Color
) {
    val colors = CycleTrackColors()
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(0.3f)
            .size(100.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 20.sp)
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            if (detail.isNotBlank()) {
                Text(text = detail, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = color, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun EmptyCycleCard(onLogPeriod: () -> Unit) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = colors.surfaceVariant.copy(alpha = 0.5f),
            contentColor = colors.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🩸", fontSize = 48.sp)
            Text("Nenhum ciclo registrado", fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Toque abaixo para registrar sua primeira menstruação", fontSize = 14.sp, textAlign = TextAlign.Center)
            Button(onClick = onLogPeriod, shape = RoundedCornerShape(12.dp)) {
                Text("Registrar menstruação", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun QuickActionsRow(actions: List<HomeViewModel.QuickAction>) {
    val colors = CycleTrackColors()
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ações rápidas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            actions.forEach { action ->
                QuickActionButton(
                    icon = action.icon,
                    title = action.title,
                    subtitle = action.subtitle,
                    color = Color(action.color),
                    onClick = action.action
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: String,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth(0.48f)
            .size(120.dp)
            .padding(4.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = icon, fontSize = 28.sp)
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(text = subtitle, fontSize = 10.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun UpcomingPredictionsCard(
    nextPeriod: com.seuapp.cycletrack.domain.model.Prediction?,
    nextOvulation: com.seuapp.cycletrack.domain.model.Prediction?,
    fertileWindow: Pair<com.seuapp.cycletrack.domain.model.Prediction?, com.seuapp.cycletrack.domain.model.Prediction?>?,
    isInFertileWindow: Boolean
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
            Text("Próximas previsões", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (nextPeriod != null) {
                    PredictionRow(
                        icon = "🩸",
                        title = "Próxima menstruação",
                        date = nextPeriod.predictedDate,
                        type = "period",
                        confidence = nextPeriod.confidenceScore
                    )
                }
                
                if (nextOvulation != null) {
                    PredictionRow(
                        icon = "🥚",
                        title = "Próxima ovulação",
                        date = nextOvulation.predictedDate,
                        type = "ovulation",
                        confidence = nextOvulation.confidenceScore
                    )
                }
                
                if (fertileWindow != null && fertileWindow.first != null && fertileWindow.second != null) {
                    PredictionRow(
                        icon = "🌱",
                        title = "Janela fértil",
                        date = fertileWindow.first!!.predictedDate,
                        endDate = fertileWindow.second!!.predictedDate,
                        type = "fertile",
                        confidence = fertileWindow.first!!.confidenceScore,
                        isActive = isInFertileWindow
                    )
                }
            }
        }
    }
}

@Composable
fun PredictionRow(
    icon: String,
    title: String,
    date: kotlinx.datetime.LocalDate,
    endDate: kotlinx.datetime.LocalDate? = null,
    type: String,
    confidence: Float,
    isActive: Boolean = false
) {
    val colors = CycleTrackColors()
    val confidenceLabel = when {
        confidence >= 0.8 -> "Alta"
        confidence >= 0.5 -> "Média"
        else -> "Baixa"
    }
    val confidenceColor = when {
        confidence >= 0.8 -> colors.success
        confidence >= 0.5 -> colors.warning
        else -> colors.error
    }
    
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(if (isActive) colors.phaseFollicular.copy(alpha = 0.1f) else Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                if (isActive) {
                    Text(text = "ATIVA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.phaseFollicular)
                }
            }
            Text(
                text = if (endDate != null) 
                    "${date.format(DateTimeFormatter.ofPattern("d/MM"))} - ${endDate.format(DateTimeFormatter.ofPattern("d/MM"))}"
                else 
                    date.format(DateTimeFormatter.ofPattern("d/MM")),
                fontSize = 14.sp,
                color = colors.onSurface
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth())
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = "Confiança: $confidenceLabel", fontSize = 10.sp, color = confidenceColor)
            androidx.compose.material3.LinearProgressIndicator(
                progress = confidence,
                modifier = Modifier.size(40.dp),
                color = confidenceColor,
                trackColor = confidenceColor.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun TodaySymptomsCard(symptoms: List<com.seuapp.cycletrack.domain.model.Symptom>) {
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
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sintomas de hoje", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                Text("${symptoms.size} registrado(s)", fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                symptoms.take(5).forEach { symptom ->
                    SymptomChip(symptom = symptom)
                }
                if (symptoms.size > 5) {
                    SymptomChip(extraCount = symptoms.size - 5)
                }
            }
        }
    }
}

@Composable
fun SymptomChip(symptom: com.seuapp.cycletrack.domain.model.Symptom? = null, extraCount: Int? = null) {
    val colors = CycleTrackColors()
    val (label, color, icon) = if (symptom != null) {
        val catColor = when (symptom.category) {
            "physical" -> colors.categoryPhysical
            "emotional" -> colors.categoryEmotional
            "discharge" -> colors.categoryDischarge
            "pain" -> colors.categoryPain
            "lifestyle" -> colors.categoryLifestyle
            else -> colors.onSurfaceVariant
        }
        val symptomIcon = when (symptom.name) {
            "cramps" -> "🤕"
            "headache" -> "🤯"
            "mood_swings" -> "🎭"
            "bloating" -> "🎈"
            "fatigue" -> "😴"
            "spotting" -> "🩸"
            else -> "📝"
        }
        symptom.name.replace("_", " ").uppercaseFirstLetter() to catColor to symptomIcon
    } else {
        "+$extraCount" to colors.onSurfaceVariant to "..."
    }
    
    Card(
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 20.sp)
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, maxLines = 2, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun InsightsPreviewCard(
    isInFertileWindow: Boolean,
    isInPms: Boolean,
    daysUntilNextPeriod: Int?,
    activePregnancy: com.seuapp.cycletrack.domain.model.Pregnancy?
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
            Text("Insights rápidos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isInFertileWindow) {
                    InsightRow("🌱", "Janela fértil ativa", "Melhor momento para concepção", colors.phaseFollicular)
                }
                if (isInPms) {
                    InsightRow("🌙", "TPM provável", "Cuide-se: hidratação, sono e exercício leve", colors.phaseLuteal)
                }
                daysUntilNextPeriod?.let { days ->
                    if (days <= 3 && days > 0) {
                        InsightRow("🩸", "Menstruação em $days dia(s)", "Prepare-se com absorventes/coletor", colors.phaseMenstrual)
                    } else if (days == 0) {
                        InsightRow("🩸", "Menstruação prevista para hoje", "Registre quando iniciar", colors.phaseMenstrual)
                    }
                }
                activePregnancy?.let { pregnancy ->
                    InsightRow("🤰", "Semana ${pregnancy.currentWeek} de gravidez", "${pregnancy.daysUntilDue} dias até o parto", colors.trimester2)
                }
            }
        }
    }
}

@Composable
fun InsightRow(icon: String, title: String, detail: String, color: Color) {
    val colors = CycleTrackColors()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
            Text(text = detail, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun PartnerStatusCard(partner: com.seuapp.cycletrack.domain.model.PartnerConnection) {
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
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp)
                            .background(colors.tertiary.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👫", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = partner.partnerDisplayName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                        Text(text = "Parceiro conectado", fontSize = 12.sp, color = colors.onSurfaceVariant)
                    }
                }
                Text(text = partner.shareLevel.displayName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = colors.tertiary)
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PartnerQuickAction("🩸", "Ciclo")
                PartnerQuickAction("🌱", "Fértil")
                PartnerQuickAction("🥚", "Ovulação")
                PartnerQuickAction("🌙", "TPM")
            }
        }
    }
}

@Composable
fun PartnerQuickAction(icon: String, label: String) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth(0.23f)
            .size(70.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = colors.tertiary.copy(alpha = 0.1f),
            contentColor = colors.tertiary
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 18.sp)
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

private fun String.uppercaseFirstLetter(): String {
    return if (isEmpty()) this else this[0].uppercaseChar() + this.substring(1)
}