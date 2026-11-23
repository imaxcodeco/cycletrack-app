package com.seuapp.cycletrack.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val ptBr = Locale("pt", "BR")

enum class HomeDayKind { NONE, PERIOD, FERTILE, OVULATION, PREDICTED }

/**
 * Classifica os 7 dias de uma semana centrada em [today] usando apenas o
 * ciclo atual (sem histórico), suficiente para a faixa de dias da Home.
 */
fun classifyWeek(today: LocalDate, cycle: Cycle?): Map<LocalDate, HomeDayKind> {
    val result = mutableMapOf<LocalDate, HomeDayKind>()
    if (cycle == null) return result

    val periodDays = cycle.periodLength.coerceAtLeast(1)
    for (i in 0 until periodDays) {
        result[cycle.startDate.plusDays(i.toLong())] = HomeDayKind.PERIOD
    }
    val fertileStart: LocalDate? = cycle.fertileWindowStart
    val fertileEnd: LocalDate? = cycle.fertileWindowEnd
    if (fertileStart != null && fertileEnd != null) {
        var date: LocalDate = fertileStart
        while (!date.isAfter(fertileEnd)) {
            if (result[date] == null) result[date] = HomeDayKind.FERTILE
            date = date.plusDays(1)
        }
    }
    if (cycle.ovulationDate != null) {
        result[cycle.ovulationDate] = HomeDayKind.OVULATION
    }
    if (cycle.endDate != null) {
        val nextStart = cycle.endDate.plusDays(1)
        val nextEnd = nextStart.plusDays((cycle.cycleLength - 1).toLong())
        var predicted = nextStart
        while (!predicted.isAfter(nextEnd)) {
            if (result[predicted] == null) result[predicted] = HomeDayKind.PREDICTED
            predicted = predicted.plusDays(1)
        }
    }
    return result
}

@Composable
fun WeeklyDateStrip(today: LocalDate, cycle: Cycle?, selected: LocalDate, onSelect: (LocalDate) -> Unit) {
    val colors = CycleTrackColors()
    val weekDays = classifyWeek(today, cycle)
    val startOfWeek = today.minusDays((today.dayOfWeek.value % 7).toLong())
    val days = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        days.forEach { date ->
            val isToday = date == today
            val kind = weekDays[date] ?: HomeDayKind.NONE
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.NARROW, ptBr).uppercase(),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isToday) {
                    Text(
                        text = "HOJE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
                Spacer(Modifier.height(2.dp))
                val fill = when (kind) {
                    HomeDayKind.PERIOD -> colors.phaseMenstrual
                    HomeDayKind.OVULATION -> colors.phaseOvulation
                    HomeDayKind.PREDICTED -> colors.phaseMenstrual.copy(alpha = 0.3f)
                    else -> Color.Transparent
                }
                val textColor = when {
                    kind == HomeDayKind.FERTILE -> colors.success
                    kind == HomeDayKind.NONE -> MaterialTheme.colorScheme.onSurface
                    else -> Color.White
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clickable { onSelect(date) }
                        .background(fill, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${date.dayOfMonth}",
                        fontSize = 14.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = textColor
                    )
                }
            }
        }
    }
}

private data class QuickAction(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val highlighted: Boolean = false
)

@Composable
fun QuickActionsRow(onAction: (String) -> Unit) {
    val colors = CycleTrackColors()
    val actions = listOf(
        QuickAction("Editar menstruação", Icons.Filled.Edit, highlighted = true),
        QuickAction("Sintomas", Icons.Filled.Add),
        QuickAction("Sexo", Icons.Filled.Favorite),
        QuickAction("Teste", Icons.Filled.Science)
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        actions.forEach { action ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onAction(action.label) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            if (action.highlighted) colors.primary else MaterialTheme.colorScheme.surface,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        action.icon,
                        contentDescription = action.label,
                        tint = if (action.highlighted) Color.White else colors.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.height(CycleTrackDimens.spacingXs))
                Text(
                    text = action.label,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(width = 72.dp, height = 32.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyCycleDataCard(cycleCount: Int, onRegisterPastCycles: () -> Unit) {
    val colors = CycleTrackColors()

    Card(
        shape = RoundedCornerShape(CycleTrackDimens.radiusLg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(CycleTrackDimens.spacingMd),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Ainda não há dados para análise e comparação",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(CycleTrackDimens.spacingSm))

            val needed2 = (2 - cycleCount).coerceAtLeast(0)
            val needed3 = (3 - cycleCount).coerceAtLeast(0)

            if (needed2 > 0) {
                ChecklistLine(
                    text = "Registre pelo menos 2 menstruações para obter informações " +
                        "personalizadas sobre seu último ciclo e a duração da sua menstruação",
                    tint = colors.primary
                )
                Spacer(Modifier.height(CycleTrackDimens.spacingXs))
            }
            if (needed3 > 0) {
                ChecklistLine(
                    text = "Registre pelo menos 3 menstruações para obter informações " +
                        "sobre a regularidade do seu ciclo",
                    tint = colors.primary
                )
            }

            Spacer(Modifier.height(CycleTrackDimens.spacingMd))
            Button(
                onClick = onRegisterPastCycles,
                modifier = Modifier.fillMaxWidth().height(CycleTrackDimens.buttonHeightLg),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Registrar ciclos anteriores", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ChecklistLine(text: String, tint: Color) {
    Row {
        Text("✓ ", color = tint, fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
