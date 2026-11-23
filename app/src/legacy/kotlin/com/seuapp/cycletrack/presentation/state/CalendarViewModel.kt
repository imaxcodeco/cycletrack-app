package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.usecase.cycle.CycleUseCases
import com.seuapp.cycletrack.domain.usecase.symptom.SymptomUseCases
import com.seuapp.cycletrack.domain.usecase.prediction.PredictionUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.async
import import com.seuapp.cycletrack.util.toEpochMilliseconds
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * ViewModel para a tela de Calendário.
 * Gerencia visualização mensal/semanal/lista com ciclos, previsões e sintomas.
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val cycleUseCases: CycleUseCases,
    private val symptomUseCases: SymptomUseCases,
    private val predictionUseCases: PredictionUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth = _currentMonth.asStateFlow()

    private val _viewMode = MutableStateFlow("month") // month, week, list
    val viewMode = _viewMode.asStateFlow()

    private val _cycles = MutableStateFlow<List<Cycle>>(emptyList())
    val cycles = _cycles.asStateFlow()

    private val _predictions = MutableStateFlow<List<Prediction>>(emptyList())
    val predictions = _predictions.asStateFlow()

    private val _symptomsByDate = MutableStateFlow<Map<LocalDate, List<Symptom>>>(emptyMap())
    val symptomsByDate = _symptomsByDate.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate = _selectedDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // ============ Computed ============

    val daysInMonth = combine(currentMonth, cycles, predictions, symptomsByDate) { month, cycles, predictions, symptoms ->
        val firstDay = month.atDay(1)
        val lastDay = month.atEndOfMonth()
        val firstDayOfWeek = firstDay.dayOfWeek.value // 1=Mon, 7=Sun
        val daysInMonth = month.lengthOfMonth()
        
        // Ajusta para firstDayOfWeek (1=Domingo no Brasil)
        val offset = (firstDayOfWeek % 7) // 0=Dom, 1=Seg, ...
        
        val days = mutableListOf<CalendarDay>()
        
        // Dias do mês anterior (padding)
        val prevMonth = month.minusMonths(1)
        val prevMonthDays = prevMonth.lengthOfMonth()
        for (i in 1..offset) {
            val day = prevMonth.atDay(prevMonthDays - offset + i)
            days.add(buildCalendarDay(day, false, cycles, predictions, symptoms))
        }
        
        // Dias do mês atual
        for (dayOfMonth in 1..daysInMonth) {
            val day = month.atDay(dayOfMonth)
            days.add(buildCalendarDay(day, true, cycles, predictions, symptoms))
        }
        
        // Dias do próximo mês (padding para completar 6 semanas = 42 dias)
        val nextMonth = month.plusMonths(1)
        val remaining = 42 - days.size
        for (i in 1..remaining) {
            val day = nextMonth.atDay(i)
            days.add(buildCalendarDay(day, false, cycles, predictions, symptoms))
        }
        
        days
    }.distinctUntilChanged()

    private fun buildCalendarDay(
        date: LocalDate,
        isCurrentMonth: Boolean,
        cycles: List<Cycle>,
        predictions: List<Prediction>,
        symptoms: Map<LocalDate, List<Symptom>>
    ): CalendarDay {
        val dayCycles = cycles.filter { it.startDate <= date && (it.endDate == null || it.endDate!! >= date) }
        val dayPredictions = predictions.filter { it.predictedDate == date }
        val daySymptoms = symptoms[date] ?: emptyList()
        
        val hasPeriod = dayCycles.any { it.isCurrent } || dayCycles.any { it.startDate == date }
        val isPeriodStart = dayCycles.any { it.startDate == date }
        val isPeriodEnd = dayCycles.any { it.endDate == date }
        
        val ovulationPrediction = dayPredictions.firstOrNull { it.type == com.seuapp.cycletrack.domain.model.PredictionType.OVULATION }
        val fertileStartPrediction = dayPredictions.firstOrNull { it.type == com.seuapp.cycletrack.domain.model.PredictionType.FERTILE_WINDOW_START }
        val fertileEndPrediction = dayPredictions.firstOrNull { it.type == com.seuapp.cycletrack.domain.model.PredictionType.FERTILE_WINDOW_END }
        val periodStartPrediction = dayPredictions.firstOrNull { it.type == com.seuapp.cycletrack.domain.model.PredictionType.PERIOD_START }
        val pmsStartPrediction = dayPredictions.firstOrNull { it.type == com.seuapp.cycletrack.domain.model.PredictionType.PMS_START }
        val pmsEndPrediction = dayPredictions.firstOrNull { it.type == com.seuapp.cycletrack.domain.model.PredictionType.PMS_END }
        
        val isFertile = (fertileStartPrediction != null || fertileEndPrediction != null) ||
            (fertileStartPrediction != null && fertileEndPrediction != null && date >= fertileStartPrediction.predictedDate && date <= fertileEndPrediction.predictedDate)
        
        val isOvulation = ovulationPrediction != null
        val isPredictedPeriodStart = periodStartPrediction != null
        val isPms = pmsStartPrediction != null || (pmsStartPrediction != null && pmsEndPrediction != null && date >= pmsStartPrediction.predictedDate && date <= pmsEndPrediction.predictedDate)
        val isToday = date == Clock.System.todayIn(TimeZone.currentSystemDefault())
        
        return CalendarDay(
            date = date,
            dayNumber = date.dayOfMonth,
            isCurrentMonth = isCurrentMonth,
            isToday = isToday,
            hasPeriod = hasPeriod,
            isPeriodStart = isPeriodStart,
            isPeriodEnd = isPeriodEnd,
            isFertile = isFertile,
            isOvulation = isOvulation,
            isPredictedPeriodStart = isPredictedPeriodStart,
            isPms = isPms,
            symptoms = daySymptoms,
            cycles = dayCycles,
            predictions = dayPredictions
        )
    }

    // ============ Actions ============

    init {
        loadCalendarData()
    }

    fun loadCalendarData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                val month = _currentMonth.value
                val startDate = month.atDay(1).minusDays(7) // Inclui semana anterior
                val endDate = month.atEndOfMonth().plusDays(7) // Inclui semana posterior
                
                val cyclesDeferred = viewModelScope.async { cycleUseCases.getCyclesInRange(userId, startDate.toEpochMilliseconds(), endDate.toEpochMilliseconds()) }
                val predictionsDeferred = viewModelScope.async { predictionUseCases.getUpcomingPredictions(userId).filter { it.predictedDate >= startDate && it.predictedDate <= endDate } }
                val symptomsDeferred = viewModelScope.async { symptomUseCases.getSymptomsInRange(userId, startDate.toEpochMilliseconds(), endDate.toEpochMilliseconds()) }
                
                _cycles.value = cyclesDeferred.await()
                _predictions.value = predictionsDeferred.await()
                
                val symptomsList = symptomsDeferred.await()
                val symptomsMap = symptomsList.groupBy { it.date }
                _symptomsByDate.value = symptomsMap
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar calendário"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
        loadCalendarData()
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
        loadCalendarData()
    }

    fun goToToday() {
        _currentMonth.value = YearMonth.now()
        loadCalendarData()
    }

    fun setViewMode(mode: String) {
        _viewMode.value = mode
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun clearSelection() {
        _selectedDate.value = null
    }

    fun getSelectedDateData(): CalendarDay? {
        _selectedDate.value?.let { date ->
            daysInMonth.value.firstOrNull { it.date == date }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

/**
 * Representa um dia no calendário com todos os dados necessários para renderização.
 */
data class CalendarDay(
    val date: LocalDate,
    val dayNumber: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val hasPeriod: Boolean,
    val isPeriodStart: Boolean,
    val isPeriodEnd: Boolean,
    val isFertile: Boolean,
    val isOvulation: Boolean,
    val isPredictedPeriodStart: Boolean,
    val isPms: Boolean,
    val symptoms: List<com.seuapp.cycletrack.domain.model.Symptom>,
    val cycles: List<com.seuapp.cycletrack.domain.model.Cycle>,
    val predictions: List<com.seuapp.cycletrack.domain.model.Prediction>
) {
    val primaryIndicator: CalendarIndicator?
        get() {
            return when {
                isToday -> CalendarIndicator.TODAY
                isPeriodStart -> CalendarIndicator.PERIOD_START
                isPeriodEnd -> CalendarIndicator.PERIOD_END
                hasPeriod -> CalendarIndicator.PERIOD
                isOvulation -> CalendarIndicator.OVULATION
                isFertile -> CalendarIndicator.FERTILE
                isPredictedPeriodStart -> CalendarIndicator.PREDICTED_PERIOD
                isPms -> CalendarIndicator.PMS
                else -> null
            }
        }

    val secondaryIndicators: List<CalendarIndicator>
        get() {
            val list = mutableListOf<CalendarIndicator>()
            if (hasPeriod && !isPeriodStart && !isPeriodEnd) list.add(CalendarIndicator.PERIOD)
            if (isFertile && !isOvulation) list.add(CalendarIndicator.FERTILE)
            if (isPms) list.add(CalendarIndicator.PMS)
            if (symptoms.isNotEmpty()) list.add(CalendarIndicator.SYMPTOM)
            return list
        }

    val hasAnyIndicator: Boolean
        get() = primaryIndicator != null || secondaryIndicators.isNotEmpty()
}

enum class CalendarIndicator(
    val color: String,
    val icon: String
) {
    PERIOD("#E84D6E", "🩸"),
    PERIOD_START("#E84D6E", "▶️"),
    PERIOD_END("#E84D6E", "⏹️"),
    PREDICTED_PERIOD("#E84D6E", "🔮"),
    OVULATION("#F59E0B", "🥚"),
    FERTILE("#22C55E", "🌱"),
    PMS("#3B82F6", "🌙"),
    SYMPTOM("#6366F1", "📝"),
    TODAY("#E84D6E", "🔵")
}