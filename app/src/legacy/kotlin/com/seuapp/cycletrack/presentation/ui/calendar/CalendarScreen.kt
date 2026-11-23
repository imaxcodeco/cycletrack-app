package com.seuapp.cycletrack.presentation.ui.calendar

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
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seuapp.cycletrack.presentation.state.CalendarViewModel
import com.seuapp.cycletrack.presentation.state.CalendarDay
import com.seuapp.cycletrack.presentation.state.CalendarIndicator
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.material3.ChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Spacer
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * Tela de Calendário - Visualização mensal/semanal/lista com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun CalendarScreen(
    onDayClick: (LocalDate) -> Unit,
    onViewModeChange: (String) -> Unit
) {
    val viewModel: CalendarViewModel = hiltViewModel()
    val currentMonth by viewModel.currentMonth.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val daysInMonth by viewModel.daysInMonth.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Header
        CalendarHeader(
            currentMonth = currentMonth,
            viewMode = viewMode,
            onViewModeChange = onViewModeChange,
            onPreviousMonth = { viewModel.previousMonth() },
            onNextMonth = { viewModel.nextMonth() },
            onToday = { viewModel.goToToday() }
        )
        
        // Week days
        WeekDaysRow()
        
        // Calendar Grid
        if (viewMode == "month") {
            CalendarGrid(
                days = daysInMonth,
                selectedDate = selectedDate,
                onDayClick = { day ->
                    viewModel.selectDate(day.date)
                    onDayClick(day.date)
                }
            )
        } else if (viewMode == "week") {
            WeekView(
                currentMonth = currentMonth,
                days = daysInMonth,
                selectedDate = selectedDate,
                onDayClick = { day ->
                    viewModel.selectDate(day.date)
                    onDayClick(day.date)
                }
            )
        } else {
            ListView(
                days = daysInMonth,
                selectedDate = selectedDate,
                onDayClick = { day ->
                    viewModel.selectDate(day.date)
                    onDayClick(day.date)
                }
            )
        }
        
        // Legend
        CalendarLegend()
        
        // Selected date detail
        selectedDate?.let { date ->
            val dayData = daysInMonth.firstOrNull { it.date == date }
            dayData?.let { day ->
                DayDetailBottomSheet(
                    day = day,
                    onClose = { viewModel.clearSelection() }
                )
            }
        }
    }
    
    // Loading overlay
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    androidx.compose.material3.CircularProgressIndicator(color = colors.primary)
                    Text("Carregando calendário...", fontSize = 14.sp, color = colors.onSurfaceVariant)
                }
            }
        }
    }
    
    // Error snackbar
    error?.let { msg ->
        // TODO: Show snackbar
    }
}

@Composable
fun CalendarHeader(
    currentMonth: kotlinx.datetime.YearMonth,
    viewMode: String,
    onViewModeChange: (String) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit
) {
    val colors = CycleTrackColors()
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("pt", "BR"))
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.ChevronLeft,
                contentDescription = "Mês anterior"
            )
        }
        
        Text(
            text = currentMonth.format(formatter).uppercaseFirstLetter(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )
        
        // View mode selector
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("month" to "Mês", "week" to "Semana", "list" to "Lista").forEach { (mode, label) ->
                val isSelected = viewMode == mode
                androidx.compose.material3.Chip(
                    onClick = { onViewModeChange(mode) },
                    modifier = Modifier.padding(0.dp),
                    colors = androidx.compose.material3.ChipDefaults.colors(
                        containerColor = if (isSelected) colors.primaryContainer else colors.surfaceVariant,
                        contentColor = if (isSelected) colors.onPrimaryContainer else colors.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(onClick = onToday) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Today,
                    contentDescription = "Hoje",
                    tint = colors.primary
                )
            }
            IconButton(onClick = onNextMonth) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.ChevronRight,
                    contentDescription = "Próximo mês"
                )
            }
        }
    }
}

@Composable
fun WeekDaysRow() {
    val colors = CycleTrackColors()
    // 0 = Domingo no Brasil
    val days = listOf("D", "S", "T", "Q", "Q", "S", "S")
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEach { day ->
            Text(
                text = day,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun CalendarGrid(
    days: List<CalendarDay>,
    selectedDate: LocalDate?,
    onDayClick: (CalendarDay) -> Unit
) {
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { day ->
                    CalendarDayCell(
                        day = day,
                        isSelected = selectedDate == day.date,
                        onClick = { onDayClick(day) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = CycleTrackColors()
    val indicator = day.primaryIndicator
    val secondaryIndicators = day.secondaryIndicators
    
    val backgroundColor = when {
        isSelected -> colors.primaryContainer
        day.isToday -> colors.primaryContainer.copy(alpha = 0.3f)
        indicator != null -> indicator.color.parseColor().copy(alpha = 0.15f)
        else -> Color.Transparent
    }
    
    val textColor = when {
        isSelected -> colors.onPrimaryContainer
        day.isToday -> colors.primary
        indicator != null -> indicator.color.parseColor()
        day.isCurrentMonth -> colors.onSurface
        else -> colors.onSurfaceVariant.copy(alpha = 0.4f)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(0.13f)
            .aspectRatio(1f)
            .padding(2.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        elevation = if (day.isToday || isSelected) 2.dp else 0.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = textColor
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.dayNumber.toString(),
                fontSize = 14.sp,
                fontWeight = if (day.isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
            
            // Indicators row
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                indicator?.let { ind ->
                    Box(modifier = Modifier.size(6.dp))
                        .background(ind.color.parseColor(), androidx.compose.foundation.shape.CircleShape)
                }
                secondaryIndicators.take(2).forEach { ind ->
                    Box(modifier = Modifier.size(6.dp))
                        .background(ind.color.parseColor(), androidx.compose.foundation.shape.CircleShape)
                }
            }
        }
    }
}

@Composable
fun WeekView(
    currentMonth: kotlinx.datetime.YearMonth,
    days: List<CalendarDay>,
    selectedDate: LocalDate?,
    onDayClick: (CalendarDay) -> Unit
) {
    val colors = CycleTrackColors()
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    
    // Get current week
    val currentWeekDays = days.filter { it.date >= today.minusDays(today.dayOfWeek.value - 1) && it.date <= today.plusDays(7 - today.dayOfWeek.value) }
    
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        currentWeekDays.forEach { day ->
            WeekDayCell(
                day = day,
                isSelected = selectedDate == day.date,
                onClick = { onDayClick(day) }
            )
        }
    }
}

@Composable
fun WeekDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = CycleTrackColors()
    val indicator = day.primaryIndicator
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = if (isSelected) 2.dp else 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) colors.primaryContainer else if (day.isToday) colors.primaryContainer.copy(alpha = 0.3f) else colors.surface,
            contentColor = if (isSelected) colors.onPrimaryContainer else colors.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Day info
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = day.date.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("pt", "BR"))).uppercaseFirstLetter(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (day.isToday) {
                        Text("Hoje", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                    }
                }
                
                // Indicators
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    indicator?.let { ind ->
                        CycleChip(ind.icon, ind.color)
                    }
                    day.secondaryIndicators.take(2).forEach { ind ->
                        CycleChip(ind.icon, ind.color)
                    }
                    if (day.symptoms.isNotEmpty()) {
                        CycleChip("📝", colors.categoryEmotional)
                    }
                }
            }
            
            // Chevron
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.ChevronRight,
                contentDescription = "",
                tint = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CycleChip(icon: String, color: Color) {
    val colors = CycleTrackColors()
    androidx.compose.material3.Chip(
        onClick = { },
        modifier = Modifier.padding(0.dp),
        colors = androidx.compose.material3.ChipDefaults.colors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 10.sp)
        }
    }
}

@Composable
fun ListView(
    days: List<CalendarDay>,
    selectedDate: LocalDate?,
    onDayClick: (CalendarDay) -> Unit
) {
    val colors = CycleTrackColors()
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.filter { it.isCurrentMonth }.forEach { day ->
            ListDayCell(
                day = day,
                isSelected = selectedDate == day.date,
                onClick = { onDayClick(day) }
            )
        }
    }
}

@Composable
fun ListDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = CycleTrackColors()
    val indicator = day.primaryIndicator
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = if (isSelected) 2.dp else 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) colors.primaryContainer else if (day.isToday) colors.primaryContainer.copy(alpha = 0.3f) else colors.surface,
            contentColor = if (isSelected) colors.onPrimaryContainer else colors.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = day.date.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("pt", "BR"))).uppercaseFirstLetter(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (day.isToday) {
                        Text("Hoje", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                    }
                }
                
                // Symptoms preview
                if (day.symptoms.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        day.symptoms.take(3).forEach { symptom ->
                            SymptomChip(symptom = symptom)
                        }
                        if (day.symptoms.size > 3) {
                            Text("+${day.symptoms.size - 3} mais", fontSize = 10.sp, color = colors.onSurfaceVariant)
                        }
                    }
                }
            }
            
            // Indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                indicator?.let { ind ->
                    Box(modifier = Modifier.size(10.dp))
                        .background(ind.color.parseColor(), androidx.compose.foundation.shape.CircleShape)
                }
                day.secondaryIndicators.take(2).forEach { ind ->
                    Box(modifier = Modifier.size(10.dp))
                        .background(ind.color.parseColor(), androidx.compose.foundation.shape.CircleShape)
                }
            }
            
            // Chevron
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.ChevronRight,
                contentDescription = "",
                tint = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CalendarLegend() {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Legenda", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                LegendItem("🩸", "Menstruação", colors.phaseMenstrual)
                LegendItem("🥚", "Ovulação", colors.phaseOvulation)
                LegendItem("🌱", "Janela fértil", colors.phaseFollicular)
                LegendItem("🔵", "Hoje", colors.primary)
                LegendItem("📝", "Sintomas", colors.categoryEmotional)
                LegendItem("🌙", "TPM", colors.phaseLuteal)
            }
        }
    }
}

@Composable
fun LegendItem(icon: String, label: String, color: Color) {
    val colors = CycleTrackColors()
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = label, fontSize = 12.sp, color = colors.onSurfaceVariant)
    }
}

@Composable
fun DayDetailBottomSheet(
    day: CalendarDay,
    onClose: () -> Unit
) {
    val colors = CycleTrackColors()
    
    androidx.compose.material3.ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
                    .background(colors.surface, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Handle bar
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(4.dp)
                        .background(colors.outlineVariant, RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                )
                
                // Date header
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = day.date.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("pt", "BR"))).uppercaseFirstLetter(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (day.isToday) {
                            Text("Hoje", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                        }
                    }
                    
                    IconButton(onClick = onClose) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Close,
                            contentDescription = "Fechar"
                        )
                    }
                }
                
                // Indicators
                if (day.hasAnyIndicator) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        day.primaryIndicator?.let { ind ->
                            DetailChip(ind.icon, ind.color.toString().replace("#", ""), ind.color)
                        }
                        day.secondaryIndicators.forEach { ind ->
                            DetailChip(ind.icon, ind.color.toString().replace("#", ""), ind.color)
                        }
                    }
                }
                
                // Cycles
                if (day.cycles.isNotEmpty()) {
                    Text("Ciclos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                    day.cycles.forEach { cycle ->
                        CycleDetailRow(cycle = cycle)
                    }
                }
                
                // Predictions
                if (day.predictions.isNotEmpty()) {
                    Text("Previsões", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                    day.predictions.forEach { prediction ->
                        PredictionDetailRow(prediction = prediction)
                    }
                }
                
                // Symptoms
                if (day.symptoms.isNotEmpty()) {
                    Text("Sintomas", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                    day.symptoms.forEach { symptom ->
                        SymptomDetailRow(symptom = symptom)
                    }
                }
                
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    )
}

@Composable
fun DetailChip(icon: String, label: String, color: Color) {
    val colors = CycleTrackColors()
    CycleChip(
        modifier = Modifier.padding(0.dp),
        onClick = { },
        label = { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = icon, fontSize = 12.sp)
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }},
        colors = ChipDefaults.colors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        )
    )
}

@Composable
fun CycleDetailRow(cycle: com.seuapp.cycletrack.domain.model.Cycle) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Ciclo ${cycle.cycleLength} dias", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                Text("Iniciado ${cycle.startDate.format(DateTimeFormatter.ofPattern("d/MM"))}", fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
            if (cycle.isCurrent) {
                Text("Atual", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primary)
            }
        }
    }
}

@Composable
fun PredictionDetailRow(prediction: com.seuapp.cycletrack.domain.model.Prediction) {
    val colors = CycleTrackColors()
    val (icon, color) = when (prediction.type) {
        com.seuapp.cycletrack.domain.model.PredictionType.PERIOD_START -> "🩸" to colors.phaseMenstrual
        com.seuapp.cycletrack.domain.model.PredictionType.PERIOD_END -> "🩸" to colors.phaseMenstrual
        com.seuapp.cycletrack.domain.model.PredictionType.OVULATION -> "🥚" to colors.phaseOvulation
        com.seuapp.cycletrack.domain.model.PredictionType.FERTILE_WINDOW_START -> "🌱" to colors.phaseFollicular
        com.seuapp.cycletrack.domain.model.PredictionType.FERTILE_WINDOW_END -> "🌱" to colors.phaseFollicular
        com.seuapp.cycletrack.domain.model.PredictionType.PMS_START -> "🌙" to colors.phaseLuteal
        com.seuapp.cycletrack.domain.model.PredictionType.PMS_END -> "🌙" to colors.phaseLuteal
        else -> "📅" to colors.onSurfaceVariant
    }
    
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
            Text(text = icon, fontSize = 20.sp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(prediction.type.displayName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                Text("Confiança: ${(prediction.confidenceScore * 100).roundToInt()}%", fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.fillMaxWidth())
            Column(horizontalAlignment = Alignment.End) {
                Text(prediction.predictedDate.format(DateTimeFormatter.ofPattern("d/MM")), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (prediction.actualDate != null) {
                    Text("Real: ${prediction.actualDate!!.format(DateTimeFormatter.ofPattern("d/MM"))}", fontSize = 10.sp, color = colors.success)
                }
            }
        }
    }
}

@Composable
fun SymptomDetailRow(symptom: com.seuapp.cycletrack.domain.model.Symptom) {
    val colors = CycleTrackColors()
    val categoryColor = when (symptom.category) {
        "physical" -> colors.categoryPhysical
        "emotional" -> colors.categoryEmotional
        "discharge" -> colors.categoryDischarge
        "pain" -> colors.categoryPain
        "lifestyle" -> colors.categoryLifestyle
        else -> colors.onSurfaceVariant
    }
    val severityIcon = when (symptom.severity) {
        com.seuapp.cycletrack.domain.model.SymptomSeverity.MILD -> "🟢"
        com.seuapp.cycletrack.domain.model.SymptomSeverity.MODERATE -> "🟡"
        com.seuapp.cycletrack.domain.model.SymptomSeverity.SEVERE -> "🔴"
    }
    
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
            Text(text = severityIcon, fontSize = 16.sp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(symptom.name.replace("_", " ").uppercaseFirstLetter(), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                Text(symptom.category.uppercaseFirstLetter(), fontSize = 12.sp, color = categoryColor)
            }
            Spacer(modifier = Modifier.fillMaxWidth())
            if (symptom.notes.isNotBlank()) {
                Text(symptom.notes, fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
        }
    }
}

// Helpers
private fun String.uppercaseFirstLetter(): String {
    return if (isEmpty()) this else this[0].uppercaseChar() + this.substring(1)
}

private fun String.parseColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        Color.Gray
    }
}

private fun CalendarIndicator.Companion.invoke(color: String): CalendarIndicator {
    // Helper para compatibilidade
    return values().firstOrNull { it.color == color } ?: CalendarIndicator.SYMPTOM
}