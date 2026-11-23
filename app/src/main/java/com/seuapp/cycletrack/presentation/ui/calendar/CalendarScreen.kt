@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.seuapp.cycletrack.presentation.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.presentation.viewmodel.HomeViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val ptBr = Locale("pt", "BR")
private const val MONTHS_BACK = 12
private const val MONTHS_FORWARD = 12

private enum class DayKind { NONE, PERIOD, FERTILE, OVULATION, PREDICTED, TODAY }

/**
 * Calendario com scroll continuo entre meses (estilo Flo) e bottom sheet
 * de detalhe ao tocar num dia.
 */
@Composable
fun CalendarScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val colors = CycleTrackColors()
    val currentCycle by viewModel.currentCycle.collectAsStateWithLifecycle()

    val today = LocalDate.now()
    val cycles = listOfNotNull(currentCycle)
    val cycleDays = remember(currentCycle) { buildCycleDays(cycles, currentCycle, today) }

    val months = remember { (-MONTHS_BACK..MONTHS_FORWARD).map { YearMonth.from(today).plusMonths(it.toLong()) } }
    val listState = rememberLazyListState()
    var sheetDate by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshCurrentCycle()
    }

    LaunchedEffect(Unit) {
        listState.scrollToItem(MONTHS_BACK)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            horizontal = CycleTrackDimens.spacingMd,
            vertical = CycleTrackDimens.spacingLg
        ),
        verticalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingLg)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = com.seuapp.cycletrack.R.drawable.icon_calendar_heart),
                            contentDescription = null,
                            modifier = Modifier.size(CycleTrackDimens.iconMd)
                        )
                        Spacer(Modifier.width(CycleTrackDimens.spacingXs))
                        Text("Legenda", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.height(CycleTrackDimens.spacingSm))
                    LegendRow(colors.phaseMenstrual, "Período")
                    LegendRow(colors.success, "Janela fértil")
                    LegendRow(colors.phaseOvulation, "Ovulação")
                    LegendRow(colors.phaseMenstrual.copy(alpha = 0.35f), "Período previsto")
                }
            }
        }

        items(months) { month ->
            MonthSection(
                month = month,
                today = today,
                cycleDays = cycleDays,
                onDayClick = { date -> sheetDate = date }
            )
        }
    }

    val sheetState = rememberModalBottomSheetState()
    sheetDate?.let { date ->
        ModalBottomSheet(
            onDismissRequest = { sheetDate = null },
            sheetState = sheetState
        ) {
            DayDetailContent(date = date, currentCycle = currentCycle, cycleDays = cycleDays)
        }
    }
}

@Composable
private fun MonthSection(
    month: YearMonth,
    today: LocalDate,
    cycleDays: Map<LocalDate, DayKind>,
    onDayClick: (LocalDate) -> Unit
) {
    Column {
        Text(
            text = month.month.getDisplayName(TextStyle.FULL, ptBr).lowercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(CycleTrackDimens.spacingSm))

        Card(
            shape = RoundedCornerShape(CycleTrackDimens.radiusLg),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(CycleTrackDimens.spacingSm)) {
                Row(Modifier.fillMaxWidth()) {
                    listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { label ->
                        Text(
                            text = label,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(CycleTrackDimens.spacingXs))

                val first = month.atDay(1)
                val offset = (first.dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7
                val daysInMonth = month.lengthOfMonth()
                val cells = offset + daysInMonth
                val rows = (cells + 6) / 7

                for (row in 0 until rows) {
                    Row(Modifier.fillMaxWidth()) {
                        for (column in 0 until 7) {
                            val cellIndex = row * 7 + column
                            val dayNumber = cellIndex - offset + 1
                            if (dayNumber < 1 || dayNumber > daysInMonth) {
                                Box(Modifier.weight(1f).aspectRatio(1f))
                            } else {
                                val date = month.atDay(dayNumber)
                                val kind = cycleDays[date] ?: DayKind.NONE
                                DayCell(
                                    date = date,
                                    kind = kind,
                                    today = date == today,
                                    onClick = { onDayClick(date) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayDetailContent(date: LocalDate, currentCycle: Cycle?, cycleDays: Map<LocalDate, DayKind>) {
    val cycleDay = currentCycle?.let { cycle ->
        (date.toEpochDay() - cycle.startDate.toEpochDay()).toInt() + 1
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CycleTrackDimens.spacingMd, vertical = CycleTrackDimens.spacingSm)
    ) {
        Text(
            text = date.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", ptBr)),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (cycleDay != null && cycleDay > 0) {
            Spacer(Modifier.height(CycleTrackDimens.spacingXs))
            Text(
                text = "${cycleDay}º dia do ciclo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(CycleTrackDimens.spacingSm))
        Text(
            text = describeDay(date, cycleDays),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(CycleTrackDimens.spacingLg))
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    kind: DayKind,
    today: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = CycleTrackColors()
    val fill = when (kind) {
        DayKind.PERIOD -> colors.phaseMenstrual
        DayKind.FERTILE -> colors.success
        DayKind.OVULATION -> colors.phaseOvulation
        DayKind.PREDICTED -> colors.phaseMenstrual.copy(alpha = 0.35f)
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(fill, CircleShape)
                .then(
                    if (today) {
                        Modifier.border(2.dp, colors.primary, CircleShape)
                    } else {
                        Modifier
                    }
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (today) FontWeight.Bold else FontWeight.Normal,
                color = if (kind == DayKind.NONE) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    Color.White
                }
            )
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.size(CycleTrackDimens.spacingSm))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

private fun describeDay(date: LocalDate, cycleDays: Map<LocalDate, DayKind>): String = when (cycleDays[date]) {
    DayKind.PERIOD -> "Dia de período"
    DayKind.FERTILE -> "Dentro da janela fértil"
    DayKind.OVULATION -> "Ovulação estimada"
    DayKind.PREDICTED -> "Período previsto"
    else -> "Sem registro especial"
}

private fun buildCycleDays(
    cycles: List<Cycle>,
    currentCycle: Cycle?,
    today: LocalDate
): Map<LocalDate, DayKind> {
    val result = mutableMapOf<LocalDate, DayKind>()
    cycles.forEach { cycle ->
        val periodDays = cycle.periodLength.coerceAtLeast(1)
        for (i in 0 until periodDays) {
            result[cycle.startDate.plusDays(i.toLong())] = DayKind.PERIOD
        }
    }
    currentCycle?.let { cycle ->
        val fertileStart = cycle.fertileWindowStart
        val fertileEnd = cycle.fertileWindowEnd
        if (fertileStart != null && fertileEnd != null) {
            var date: LocalDate = fertileStart
            while (!date.isAfter(fertileEnd)) {
                if (result[date] == null) result[date] = DayKind.FERTILE
                date = date.plusDays(1L)
            }
        }
        if (cycle.ovulationDate != null) {
            result[cycle.ovulationDate] = DayKind.OVULATION
        }

        if (cycle.endDate != null) {
            val nextStart = cycle.endDate.plusDays(1)
            val nextEnd = nextStart.plusDays((cycle.cycleLength - 1).toLong())
            var predicted = nextStart
            while (!predicted.isAfter(nextEnd)) {
                if (result[predicted] == null) result[predicted] = DayKind.PREDICTED
                predicted = predicted.plusDays(1)
            }
        }
    }
    return result
}
