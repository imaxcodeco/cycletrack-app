package com.seuapp.cycletrack.presentation.ui.insights

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.seuapp.cycletrack.presentation.state.InsightsViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormatter
import java.util.Locale

/**
 * Tela de Insights - Análises e relatórios do ciclo com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun InsightsScreen(onNavigateToReport: () -> Unit) {
    val viewModel: InsightsViewModel = hiltViewModel()
    val cycleStats by viewModel.cycleStatistics.collectAsStateWithLifecycle()
    val periodStats by viewModel.periodStatistics.collectAsStateWithLifecycle()
    val fertilityInsights by viewModel.fertilityInsights.collectAsStateWithLifecycle()
    val symptomPatterns by viewModel.symptomPatterns.collectAsStateWithLifecycle()
    val cycleTrends by viewModel.cycleTrends.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Insights", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        Text("Análise inteligente do seu ciclo", fontSize = 16.sp, color = colors.onSurfaceVariant)
        
        if (isLoading) {
            LoadingCard()
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cycle Regularity
                cycleStats?.let { stats ->
                    InsightCard(
                        icon = "📊",
                        title = "Regularidade do ciclo",
                        value = stats.regularityLabel,
                        detail = "Média: ${String.format("%.1f", stats.averageCycleLength)} dias • Desvio: ${String.format("%.1f", stats.standardDeviation)} dias",
                        trend = "Confiança: ${stats.confidenceLevel}",
                        trendColor = when (stats.confidenceLevel) {
                            "Alta" -> colors.success
                            "Média" -> colors.warning
                            else -> colors.error
                        },
                        color = colors.success,
                        progress = stats.regularityScore.toFloat(),
                        onClick = { /* navigate to cycles insight */ }
                    )
                }
                
                // Period Stats
                periodStats?.let { stats ->
                    InsightCard(
                        icon = "🩸",
                        title = "Menstruação",
                        value = "${String.format("%.1f", stats.averagePeriodLength)} dias (média)",
                        detail = "Fluxo mais comum: ${stats.mostCommonFlow}",
                        trend = "${stats.totalPeriods} ciclos analisados",
                        color = colors.phaseMenstrual,
                        progress = (stats.averagePeriodLength / 7f).coerceIn(0f, 1f),
                        onClick = { /* navigate to period insight */ }
                    )
                }
                
                // Fertility
                fertilityInsights?.let { insights ->
                    InsightCard(
                        icon = "🌱",
                        title = "Janela fértil",
                        value = "Dia ${insights.averageOvulationDay} do ciclo",
                        detail = "Fase lútea: ${insights.averageLutealPhase} dias • ${insights.cyclePhaseToday}",
                        trend = insights.nextFertileWindowStart?.let { "Próxima: ${it.format(DateTimeFormatter.ofPattern("d/MM"))}" } ?: "Calculando...",
                        trendColor = when (insights.cyclePhaseToday) {
                            "ovulation" -> colors.success
                            "follicular" -> colors.phaseFollicular
                            "luteal" -> colors.phaseLuteal
                            else -> colors.onSurfaceVariant
                        },
                        color = colors.phaseFollicular,
                        progress = (insights.fertileWindowDays / 10f).coerceIn(0f, 1f),
                        onClick = { /* navigate to fertility insight */ }
                    )
                }
                
                // Symptoms
                symptomPatterns?.let { patterns ->
                    val topSymptom = patterns.mostCommonSymptoms.firstOrNull()
                    InsightCard(
                        icon = "📝",
                        title = "Sintomas frequentes",
                        value = topSymptom?.let { "${it.name.replace("_", " ").uppercaseFirstLetter()} (${it.count}x)" } ?: "Nenhum sintoma registrado",
                        detail = "Média de ${String.format("%.1f", patterns.averageSymptomsPerCycle)} sintomas por ciclo",
                        trend = "Total: ${patterns.totalSymptomsLogged} registros",
                        color = colors.primary,
                        progress = (patterns.averageSymptomsPerCycle / 10f).coerceIn(0f, 1f),
                        onClick = { /* navigate to symptoms insight */ }
                    )
                }
                
                // Cycle Trends
                cycleTrends?.let { trends ->
                    InsightCard(
                        icon = "📈",
                        title = "Tendência do ciclo",
                        value = "${trends.last6MonthsCycleLengths.lastOrNull()?.cycleLength ?: 0} dias (último ciclo)",
                        detail = "Média 6 meses: ${trends.last6MonthsCycleLengths.map { it.cycleLength }.average().roundToInt()} dias",
                        trend = trends.trendDescription,
                        trendColor = when (trends.trendDirection) {
                            "improving" -> colors.success
                            "stable" -> colors.phaseFollicular
                            else -> colors.warning
                        },
                        color = colors.phaseLuteal,
                        progress = trends.predictionAccuracy.toFloat(),
                        onClick = { /* navigate to trends */ }
                    )
                }
                
                // Cycle Length Chart
                cycleTrends?.let { trends ->
                    CycleLengthChartCard(trends = trends.last6MonthsCycleLengths)
                }
                
                // Top Symptoms
                symptomPatterns?.let { patterns ->
                    TopSymptomsCard(patterns = patterns.mostCommonSymptoms)
                }
                
                // Cycle Phase Distribution
                symptomPatterns?.let { patterns ->
                    PhaseDistributionCard(patterns = patterns.symptomsByPhase)
                }
                
                // Report Button
                ReportCard(onClick = onNavigateToReport)
            }
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
            Text("Carregando insights...", fontSize = 14.sp, color = colors.onSurfaceVariant)
        }
    )
}

@Composable
fun InsightCard(
    icon: String,
    title: String,
    value: String,
    detail: String,
    trend: String,
    trendColor: Color = androidx.compose.ui.graphics.Color.Unspecified,
    color: Color,
    progress: Float = 0f,
    onClick: () -> Unit
) {
    val colors = CycleTrackColors()
    val effectiveTrendColor = if (trendColor != Color.Unspecified) trendColor else color
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = icon, fontSize = 28.sp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                    Text(text = trend, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = effectiveTrendColor)
                }
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                Text(text = detail, fontSize = 12.sp, color = colors.onSurfaceVariant)
                
                if (progress > 0) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth(),
                        trackThickness = 4.dp,
                        color = color,
                        trackColor = color.copy(alpha = 0.2f)
                    )
                }
            }
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.ChevronRight,
                contentDescription = "",
                tint = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CycleLengthChartCard(trends: List<InsightsViewModel.CycleLengthPoint>) {
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
            Text("Duração dos ciclos (últimos 6 meses)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            
            // Simple bar chart
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                trends.forEach { point ->
                    ChartBar(
                        label = point.month,
                        value = point.cycleLength,
                        maxValue = trends.map { it.cycleLength }.maxOrNull() ?: 35,
                        color = if (point.isPredicted) colors.phaseOvulation else colors.primary,
                        secondaryValue = point.periodLength,
                        secondaryColor = colors.phaseMenstrual
                    )
                }
            }
        }
    }
}

@Composable
fun ChartBar(
    label: String,
    value: Int,
    maxValue: Int,
    color: Color,
    secondaryValue: Int,
    secondaryColor: Color
) {
    val colors = CycleTrackColors()
    val progress = (value.toFloat() / maxValue).coerceIn(0f, 1f)
    val secondaryProgress = (secondaryValue.toFloat() / maxValue).coerceIn(0f, 1f)
    
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = colors.onSurface, modifier = Modifier.size(60.dp))
        
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Background
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Modifier.background(RoundedCornerShape(4.dp), colors.outlineVariant)
            }
            
            // Cycle length bar
            if (progress > 0) {
                Box(
                    modifier = Modifier.fillMaxWidth(progress)
                        .height(24.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Modifier.background(RoundedCornerShape(4.dp), color)
                }
            }
            
            // Period length overlay
            if (secondaryProgress > 0 && secondaryProgress < progress) {
                Box(
                    modifier = Modifier.fillMaxWidth(secondaryProgress)
                        .height(24.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Modifier.background(RoundedCornerShape(4.dp), secondaryColor)
                }
            }
            
            // Value label
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text("${value}d / ${secondaryValue}d", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun TopSymptomsCard(patterns: List<InsightsViewModel.SymptomFrequency>) {
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
            Text("Sintomas mais frequentes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                patterns.take(5).forEach { symptom ->
                    SymptomFrequencyRow(symptom = symptom)
                }
            }
        }
    }
}

@Composable
fun SymptomFrequencyRow(symptom: InsightsViewModel.SymptomFrequency) {
    val colors = CycleTrackColors()
    val categoryColor = when (symptom.category) {
        "physical" -> colors.categoryPhysical
        "emotional" -> colors.categoryEmotional
        "discharge" -> colors.categoryDischarge
        "pain" -> colors.categoryPain
        "lifestyle" -> colors.categoryLifestyle
        else -> colors.onSurfaceVariant
    }
    
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(symptom.name.replace("_", " ").uppercaseFirstLetter(), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
            Text(symptom.category.uppercaseFirstLetter(), fontSize = 12.sp, color = categoryColor)
        }
        Spacer(modifier = Modifier.fillMaxWidth())
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("${symptom.count}x", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text("Severidade: ${String.format("%.1f", symptom.averageSeverity)}/3", fontSize = 10.sp, color = colors.onSurfaceVariant)
            Text("${String.format("%.1f", symptom.percentageOfCycles)}% dos ciclos", fontSize = 10.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun PhaseDistributionCard(patterns: Map<String, List<InsightsViewModel.SymptomFrequency>>) {
    val colors = CycleTrackColors()
    val phaseOrder = listOf("menstrual", "follicular", "ovulation", "luteal")
    val phaseLabels = mapOf(
        "menstrual" to "🩸 Menstrual",
        "follicular" to "🌱 Folicular",
        "ovulation" to "🥚 Ovulação",
        "luteal" to "🌙 Lútea"
    )
    val phaseColors = mapOf(
        "menstrual" to colors.phaseMenstrual,
        "follicular" to colors.phaseFollicular,
        "ovulation" to colors.phaseOvulation,
        "luteal" to colors.phaseLuteal
    )
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Sintomas por fase do ciclo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                phaseOrder.forEach { phase ->
                    val symptoms = patterns[phase] ?: emptyList()
                    val totalCount = symptoms.sumOf { it.count }
                    
                    PhaseRow(
                        phaseLabel = phaseLabels[phase] ?: phase,
                        color = phaseColors[phase] ?: colors.onSurfaceVariant,
                        totalCount = totalCount,
                        topSymptoms = symptoms.take(3)
                    )
                }
            }
        }
    }
}

@Composable
fun PhaseRow(
    phaseLabel: String,
    color: Color,
    totalCount: Int,
    topSymptoms: List<InsightsViewModel.SymptomFrequency>
) {
    val colors = CycleTrackColors()
    
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Phase indicator
        Box(
            modifier = Modifier.size(40.dp)
                .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = phaseLabel.split(" ").last(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(phaseLabel, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                Text("$totalCount sintomas", fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
            
            if (topSymptoms.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    topSymptoms.forEach { symptom ->
                        Text(
                            text = "${symptom.name.replace("_", " ").uppercaseFirstLetter()} (${symptom.count})",
                            fontSize = 10.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(onClick: () -> Unit) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp,
        colors = CardDefaults.cardColors(
            containerColor = colors.primaryContainer,
            contentColor = colors.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Relatório completo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("PDF/CSV para levar ao médico", fontSize = 14.sp)
            }
            Text("📄", fontSize = 28.sp)
        }
    }
}

private fun String.uppercaseFirstLetter(): String {
    return if (isEmpty()) this else this[0].uppercaseChar() + this.substring(1)
}

import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer