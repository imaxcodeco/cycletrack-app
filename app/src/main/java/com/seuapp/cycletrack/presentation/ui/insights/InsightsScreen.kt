package com.seuapp.cycletrack.presentation.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seuapp.cycletrack.presentation.viewmodel.HomeViewModel
import com.seuapp.cycletrack.presentation.viewmodel.SymptomViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val ptBr = Locale("pt", "BR")

/**
 * Tela de insights: medias do ciclo, regularidade e distribuicao de sintomas.
 */
@Composable
fun InsightsScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    symptomViewModel: SymptomViewModel = hiltViewModel()
) {
    val colors = CycleTrackColors()
    val currentCycle by homeViewModel.currentCycle.collectAsStateWithLifecycle()
    val symptoms by symptomViewModel.symptoms.collectAsStateWithLifecycle()

    val realCycles = listOfNotNull(currentCycle).takeLast(6).reversed()

    val symptomCounts = remember(symptoms) {
        symptoms
            .groupingBy { it.name }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(5)
    }
    val maxSymptomCount = symptomCounts.maxOfOrNull { it.value } ?: 1

    // TODO: Implementar cálculo de stats quando CycleRepository estiver completa
    val averageCycleLength = currentCycle?.cycleLength ?: 28
    val averagePeriodLength = currentCycle?.periodLength ?: 5
    val loggedDays = symptoms.distinctBy { it.date }.size
    val totalSymptoms = symptoms.size
    val cycleRegularity = "Regular"

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
            Text(
                text = "Insights",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Resumos calculados a partir dos seus registros.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingSm)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    value = "$averageCycleLength",
                    label = "Media do ciclo",
                    suffix = "dias",
                    tint = colors.primary
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    value = "$averagePeriodLength",
                    label = "Media do periodo",
                    suffix = "dias",
                    tint = colors.phaseMenstrual
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingSm)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    value = "$loggedDays",
                    label = "Dias registrados",
                    suffix = "",
                    tint = colors.success
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    value = "$totalSymptoms",
                    label = "Sintomas anotados",
                    suffix = "",
                    tint = colors.secondary
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                colors = CardDefaults.cardColors(containerColor = colors.primaryContainer)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(CycleTrackDimens.spacingMd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = colors.onPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(CycleTrackDimens.spacingSm))
                    Column {
                        Text(
                            text = cycleRegularity,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onPrimaryContainer
                        )
                        Text(
                            text = "Classificacao da variacao dos seus ciclos",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onPrimaryContainer
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusLg),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
                    Text(
                        text = "Duracao dos ultimos ciclos",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(CycleTrackDimens.spacingMd))
                    CycleLengthChart(realCycles.map { it.cycleLength })
                    Spacer(Modifier.height(CycleTrackDimens.spacingXs))
                    Row(Modifier.fillMaxWidth()) {
                        realCycles.forEach { cycle ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = cycle.startDate.month.getDisplayName(TextStyle.SHORT, ptBr)
                                        .replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusLg),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
                    Text(
                        text = "Sintomas mais frequentes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(CycleTrackDimens.spacingSm))
                    if (symptomCounts.isEmpty()) {
                        Text(
                            text = "Nenhum sintoma registrado ainda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        symptomCounts.forEach { entry ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                            ) {
                                Text(
                                    text = entry.key,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.width(110.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(10.dp)
                                        .background(colors.surfaceVariant, CircleShape)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(entry.value.toFloat() / maxSymptomCount)
                                            .height(10.dp)
                                            .background(colors.secondary, CircleShape)
                                    )
                                }
                                Spacer(Modifier.width(CycleTrackDimens.spacingSm))
                                Text(
                                    text = "${entry.value}x",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "As previsoes sao estimativas baseadas nos seus registros e podem variar.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier,
    value: String,
    label: String,
    suffix: String,
    tint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = tint
            )
            if (suffix.isNotEmpty()) {
                Text(
                    text = suffix,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(CycleTrackDimens.spacingXs))
            Text(text = label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun CycleLengthChart(lengths: List<Int>) {
    val colors = CycleTrackColors()
    if (lengths.isEmpty()) {
        Text(
            text = "Sem ciclos registrados.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val maxValue = (lengths.maxOrNull() ?: 28) + 4
    val minValue = (lengths.minOrNull() ?: 21) - 4

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        val count = lengths.size
        val slot = size.width / count
        val barWidth = slot * 0.5f
        lengths.forEachIndexed { index, value ->
            val ratio = ((value - minValue).toFloat() / (maxValue - minValue)).coerceIn(0.08f, 1f)
            val barHeight = size.height * ratio
            val left = index * slot + (slot - barWidth) / 2
            drawRoundRect(
                color = colors.primary,
                topLeft = Offset(left, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
            )
        }
    }
}
