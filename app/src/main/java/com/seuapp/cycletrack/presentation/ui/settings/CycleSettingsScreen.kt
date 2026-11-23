package com.seuapp.cycletrack.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import com.seuapp.cycletrack.presentation.viewmodel.HomeViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ptBr = Locale("pt", "BR")
private val dateFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM", ptBr)
private const val MIN_CYCLE_LENGTH = 21
private const val MAX_CYCLE_LENGTH = 40
private const val MIN_PERIOD_LENGTH = 2
private const val MAX_PERIOD_LENGTH = 10

/**
 * Tela de ajustes de ciclo e ovulacao com previa ao vivo, recalculada a
 * partir do startDate do ciclo atual (Fase 4).
 */
@Composable
fun CycleSettingsScreen(onBack: () -> Unit = {}, viewModel: HomeViewModel = hiltViewModel()) {
    val colors = CycleTrackColors()
    val currentCycle by viewModel.currentCycle.collectAsStateWithLifecycle()

    var cycleLength by remember { mutableIntStateOf(28) }
    var periodLength by remember { mutableIntStateOf(5) }
    var initialized by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentCycle) {
        val cycle = currentCycle
        if (cycle != null && initialized == 0) {
            cycleLength = cycle.cycleLength
            periodLength = cycle.periodLength
            initialized = 1
        }
    }

    val preview: Cycle? = currentCycle?.let { cycle ->
        Cycle.createManual(
            userId = cycle.userId,
            startDate = cycle.startDate,
            periodLength = periodLength,
            previousCycleLength = cycleLength
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            horizontal = CycleTrackDimens.spacingMd,
            vertical = CycleTrackDimens.spacingLg
        ),
        verticalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingMd)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Text(
                    text = "Ciclo e ovulação",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            SettingsCard(title = "Duração média do ciclo", subtitle = "Número de dias entre menstruações") {
                Stepper(
                    value = cycleLength,
                    unit = "dias",
                    onDecrement = { cycleLength = (cycleLength - 1).coerceAtLeast(MIN_CYCLE_LENGTH) },
                    onIncrement = { cycleLength = (cycleLength + 1).coerceAtMost(MAX_CYCLE_LENGTH) }
                )
            }
        }

        item {
            SettingsCard(title = "Duração da menstruação", subtitle = "Número médio de dias de sangramento") {
                Stepper(
                    value = periodLength,
                    unit = "dias",
                    onDecrement = { periodLength = (periodLength - 1).coerceAtLeast(MIN_PERIOD_LENGTH) },
                    onIncrement = { periodLength = (periodLength + 1).coerceAtMost(MAX_PERIOD_LENGTH) }
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                colors = CardDefaults.cardColors(containerColor = colors.primaryContainer)
            ) {
                Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
                    Text(
                        text = "Prévia",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onPrimaryContainer
                    )
                    Spacer(Modifier.height(CycleTrackDimens.spacingSm))
                    PreviewRow(
                        "Próxima menstruação",
                        preview?.let { it.startDate.plusDays(it.cycleLength.toLong()).format(dateFormatter) }
                    )
                    PreviewRow("Ovulação estimada", preview?.ovulationDate?.format(dateFormatter))
                    PreviewRow(
                        "Janela fértil",
                        if (preview?.fertileWindowStart != null && preview.fertileWindowEnd != null) {
                            "${preview.fertileWindowStart.format(dateFormatter)} - ${preview.fertileWindowEnd.format(dateFormatter)}"
                        } else {
                            null
                        }
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary, RoundedCornerShape(CycleTrackDimens.radiusLg))
                    .clickable {
                        viewModel.updateCycleSettings(cycleLength, periodLength)
                        onBack()
                    }
                    .padding(vertical = CycleTrackDimens.spacingMd),
                contentAlignment = Alignment.Center
            ) {
                Text("Salvar", color = colors.onPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SettingsCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(CycleTrackDimens.spacingSm))
            content()
        }
    }
}

@Composable
private fun Stepper(value: Int, unit: String, onDecrement: () -> Unit, onIncrement: () -> Unit) {
    val colors = CycleTrackColors()
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingMd)) {
        StepperButton(icon = Icons.Filled.Remove, onClick = onDecrement)
        Text(
            text = "$value $unit",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            color = colors.primary
        )
        StepperButton(icon = Icons.Filled.Add, onClick = onIncrement)
    }
}

@Composable
private fun StepperButton(icon: ImageVector, onClick: () -> Unit) {
    val colors = CycleTrackColors()
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(colors.surfaceVariant, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = colors.primary)
    }
}

@Composable
private fun PreviewRow(label: String, value: String?) {
    val colors = CycleTrackColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = colors.onPrimaryContainer)
        Text(
            text = value ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.onPrimaryContainer
        )
    }
}
