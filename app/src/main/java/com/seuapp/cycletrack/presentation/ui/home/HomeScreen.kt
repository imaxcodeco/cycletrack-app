package com.seuapp.cycletrack.presentation.ui.home

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seuapp.cycletrack.presentation.viewmodel.HomeViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val ptBr = Locale("pt", "BR")

private fun LocalDate.formatMonthDay(): String =
    "${dayOfMonth} ${month.getDisplayName(TextStyle.SHORT, ptBr)}"

private fun LocalDate.formatLong(): String =
    format(DateTimeFormatter.ofPattern("d 'de' MMMM", ptBr))

private fun cyclePhaseLabel(phase: com.seuapp.cycletrack.domain.usecase.CyclePhase): String =
    when (phase) {
        com.seuapp.cycletrack.domain.usecase.CyclePhase.MENSTRUAL -> "Menstrual"
        com.seuapp.cycletrack.domain.usecase.CyclePhase.FOLLICULAR -> "Folicular"
        com.seuapp.cycletrack.domain.usecase.CyclePhase.OVULATION -> "Ovulatória"
        com.seuapp.cycletrack.domain.usecase.CyclePhase.LUTEAL -> "Lútea"
        com.seuapp.cycletrack.domain.usecase.CyclePhase.UNKNOWN -> "—"
    }

/**
 * Confianca da previsao de ovulacao com base no numero real de ciclos registrados,
 * em vez de um percentual fixo que nao reflete nenhum calculo de fato.
 */
private fun ovulationConfidenceLabel(cycleCount: Int): String =
    when {
        cycleCount == 0 -> "Sem dados"
        cycleCount < 3 -> "Estimativa inicial"
        else -> "Estimativa"
    }

/**
 * Tela inicial: mostra o dia do ciclo, a fase atual, a previsão do próximo
 * período e atalhos para os principais dados do ciclo.
 */
@Composable
fun HomeScreen(
    onNavigateToLogging: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val colors = CycleTrackColors()
    val currentCycle by viewModel.currentCycle.collectAsStateWithLifecycle()
    val dayOfCycle by viewModel.dayOfCycle.collectAsStateWithLifecycle()
    val cyclePhase by viewModel.cyclePhase.collectAsStateWithLifecycle()
    val nextPeriodDate by viewModel.nextPeriod.collectAsStateWithLifecycle()
    val ovulationDate by viewModel.ovulationDate.collectAsStateWithLifecycle()
    val fertileWindow by viewModel.fertileWindow.collectAsStateWithLifecycle()
    val cycleCount by viewModel.cycleCount.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // Recarrega o ciclo sempre que a Home volta ao primeiro plano (ex.: depois de
    // registrar uma menstruação na aba Registrar), não só na primeira composição.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshCurrentCycle()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val today = LocalDate.now()
    var selectedDay by remember { mutableStateOf(today) }
    val cycleLength = currentCycle?.cycleLength ?: 28
    val progress = (dayOfCycle.toFloat() / cycleLength).coerceIn(0f, 1f)

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
            Column {
                Text(
                    text = "Olá!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = today.formatLong(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            WeeklyDateStrip(
                today = today,
                cycle = currentCycle,
                selected = selectedDay,
                onSelect = { selectedDay = it }
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CycleTrackDimens.radiusLg))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(colors.primaryContainer, colors.primary.copy(alpha = 0.18f))
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(CycleTrackDimens.spacingMd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val phaseColor = when (cyclePhase) {
                        com.seuapp.cycletrack.domain.usecase.CyclePhase.MENSTRUAL -> colors.phaseMenstrual
                        com.seuapp.cycletrack.domain.usecase.CyclePhase.FOLLICULAR -> colors.primary
                        com.seuapp.cycletrack.domain.usecase.CyclePhase.OVULATION -> colors.phaseOvulation
                        com.seuapp.cycletrack.domain.usecase.CyclePhase.LUTEAL -> colors.phaseLuteal
                        else -> colors.onSurfaceVariant
                    }

                    CycleRing(
                        day = dayOfCycle,
                        progress = progress,
                        phaseColor = phaseColor
                    )
                    Spacer(Modifier.width(CycleTrackDimens.spacingMd))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dia $dayOfCycle do ciclo",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimaryContainer
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = cyclePhaseLabel(cyclePhase),
                            style = MaterialTheme.typography.titleSmall,
                            color = phaseColor
                        )
                        Spacer(Modifier.height(CycleTrackDimens.spacingXs))
                        Text(
                            text = "Próximo período em ${nextPeriodDate?.formatMonthDay() ?: "—"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onPrimaryContainer
                        )
                    }
                }
            }
        }

        item {
            QuickActionsRow(onAction = { onNavigateToLogging() })
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingSm)) {
                InfoTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.EmojiEvents,
                    title = "Ovulação",
                    value = ovulationDate?.formatMonthDay() ?: "—",
                    tint = colors.tertiary
                )
                InfoTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Spa,
                    title = "Janela fértil",
                    value = fertileWindow?.let { fw ->
                        "${fw.start.formatMonthDay()} - ${fw.end.formatMonthDay()}"
                    } ?: "—",
                    tint = colors.success
                )
            }
        }

        item {
            Text(
                text = "Previsões",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        // TODO: Implementar previsões quando PredictionRepository estiver completa
        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(CycleTrackDimens.spacingMd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = colors.primary
                    )
                    Spacer(Modifier.width(CycleTrackDimens.spacingSm))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Ovulação",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = ovulationDate?.formatLong() ?: "—",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = ovulationConfidenceLabel(cycleCount),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.primary
                    )
                }
            }
        }

        item {
            Text(
                text = "Estimativas baseadas no seu histórico. Não é método contraceptivo nem substitui orientação médica.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (cycleCount < 2) {
            item {
                EmptyCycleDataCard(
                    cycleCount = cycleCount,
                    onRegisterPastCycles = onNavigateToLogging
                )
            }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                    colors = CardDefaults.cardColors(containerColor = colors.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(CycleTrackDimens.spacingMd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = null, tint = colors.secondary)
                        Spacer(Modifier.width(CycleTrackDimens.spacingSm))
                        Column {
                            Text(
                                text = "Ciclos registrados: $cycleCount",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSecondaryContainer
                            )
                            Text(
                                text = "Continue registrando para previsões mais precisas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CycleRing(day: Int, progress: Float, phaseColor: Color) {
    Box(
        modifier = Modifier.size(104.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = Color.White.copy(alpha = 0.55f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = phaseColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$day",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "dia",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InfoTile(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    tint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(tint.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(CycleTrackDimens.spacingSm))
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
