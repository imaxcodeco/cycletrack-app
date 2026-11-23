package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.model.PredictionType
import com.seuapp.cycletrack.domain.usecase.cycle.CycleUseCases
import com.seuapp.cycletrack.domain.usecase.symptom.SymptomUseCases
import com.seuapp.cycletrack.domain.usecase.prediction.PredictionUseCases
import com.seuapp.cycletrack.domain.usecase.pregnancy.PregnancyUseCases
import com.seuapp.cycletrack.domain.usecase.partner.PartnerUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.async
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * ViewModel para a tela Home (Dashboard principal).
 * Gerencia ciclo atual, previsões, sintomas de hoje e status do parceiro.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cycleUseCases: CycleUseCases,
    private val symptomUseCases: SymptomUseCases,
    private val predictionUseCases: PredictionUseCases,
    private val pregnancyUseCases: PregnancyUseCases,
    private val partnerUseCases: PartnerUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============
    
    private val _currentCycle = MutableStateFlow<Cycle?>(null)
    val currentCycle = _currentCycle.asStateFlow()

    private val _nextPeriodPrediction = MutableStateFlow<Prediction?>(null)
    val nextPeriodPrediction = _nextPeriodPrediction.asStateFlow()

    private val _nextOvulationPrediction = MutableStateFlow<Prediction?>(null)
    val nextOvulationPrediction = _nextOvulationPrediction.asStateFlow()

    private val _nextFertileWindow = MutableStateFlow<Pair<Prediction?, Prediction?>?>(null)
    val nextFertileWindow = _nextFertileWindow.asStateFlow()

    private val _pmsPrediction = MutableStateFlow<Prediction?>(null)
    val pmsPrediction = _pmsPrediction.asStateFlow()

    private val _todaySymptoms = MutableStateFlow<List<com.seuapp.cycletrack.domain.model.Symptom>>(emptyList())
    val todaySymptoms = _todaySymptoms.asStateFlow()

    private val _isInFertileWindow = MutableStateFlow(false)
    val isInFertileWindow = _isInFertileWindow.asStateFlow()

    private val _isInPms = MutableStateFlow(false)
    val isInPms = _isInPms.asStateFlow()

    private val _daysUntilNextPeriod = MutableStateFlow<Int?>(null)
    val daysUntilNextPeriod = _daysUntilNextPeriod.asStateFlow()

    private val _activePregnancy = MutableStateFlow<com.seuapp.cycletrack.domain.model.Pregnancy?>(null)
    val activePregnancy = _activePregnancy.asStateFlow()

    private val _partnerConnection = MutableStateFlow<com.seuapp.cycletrack.domain.model.PartnerConnection?>(null)
    val partnerConnection = _partnerConnection.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // ============ Computed State ============

    val cycleDayText = combine(currentCycle, daysUntilNextPeriod) { cycle, days ->
        if (cycle != null) {
            if (cycle.isCurrent) {
                "Dia ${cycle.daysSinceStart + 1} do ciclo"
            } else if (days != null && days > 0) {
                "$days dias até a próxima menstruação"
            } else if (days == 0) {
                "Menstruação prevista para hoje"
            } else if (days == 1) {
                "Menstruação prevista para amanhã"
            } else {
                "Ciclo finalizado"
            }
        } else {
            "Nenhum ciclo registrado"
        }
    }.distinctUntilChanged()

    val currentPhase = combine(currentCycle, _nextPeriodPrediction, _nextOvulationPrediction, _nextFertileWindow) { cycle, period, ovulation, fertile ->
        if (cycle != null && cycle.isCurrent) {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            if (cycle.endDate != null && today <= cycle.endDate!!) {
                "menstrual"
            } else if (fertile != null && fertile.first != null && fertile.second != null &&
                       today >= fertile.first!!.predictedDate && today <= fertile.second!!.predictedDate) {
                "ovulation"
            } else if (ovulation != null && today.daysUntil(ovulation.predictedDate).inDays <= 2 && today.daysUntil(ovulation.predictedDate).inDays >= -1) {
                "ovulation"
            } else if (period != null && today.daysUntil(period.predictedDate).inDays <= 7 && today.daysUntil(period.predictedDate).inDays >= 0) {
                "luteal"
            } else {
                "follicular"
            }
        } else {
            "unknown"
        }
    }.distinctUntilChanged()

    // ============ Initialization ============

    init {
        loadHomeData()
    }

    // ============ Actions ============

    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                
                // Carrega dados em paralelo
                val currentCycleDeferred = viewModelScope.async { cycleUseCases.getCurrentCycle(userId) }
                val nextPeriodDeferred = viewModelScope.async { cycleUseCases.getNextPeriodPrediction(userId) }
                val nextOvulationDeferred = viewModelScope.async { cycleUseCases.getNextOvulationPrediction(userId) }
                val nextFertileDeferred = viewModelScope.async { cycleUseCases.getNextFertileWindowPrediction(userId) }
                val todaySymptomsDeferred = viewModelScope.async { symptomUseCases.getSymptomsForDate(userId, Clock.System.todayIn(TimeZone.currentSystemDefault())) }
                val fertileWindowDeferred = viewModelScope.async { cycleUseCases.isInFertileWindowToday(userId) }
                val pmsDeferred = viewModelScope.async { cycleUseCases.isInPmsToday(userId) }
                val daysUntilDeferred = viewModelScope.async { cycleUseCases.getDaysUntilNextPeriod(userId) }
                val pregnancyDeferred = viewModelScope.async { pregnancyUseCases.getActivePregnancy(userId) }
                val partnerDeferred = viewModelScope.async { partnerUseCases.getActivePartner(userId) }
                
                _currentCycle.value = currentCycleDeferred.await()
                _nextPeriodPrediction.value = nextPeriodDeferred.await()
                _nextOvulationPrediction.value = nextOvulationDeferred.await()
                _nextFertileWindow.value = nextFertileDeferred.await()
                _todaySymptoms.value = todaySymptomsDeferred.await()
                _isInFertileWindow.value = fertileWindowDeferred.await()
                _isInPms.value = pmsDeferred.await()
                _daysUntilNextPeriod.value = daysUntilDeferred.await()
                _activePregnancy.value = pregnancyDeferred.await()
                _partnerConnection.value = partnerDeferred.await()
                
                // Busca previsão de TPM
                val predictions = predictionUseCases.getUpcomingPredictions(userId)
                _pmsPrediction.value = predictions.firstOrNull { it.type == PredictionType.PMS_START }
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar dados da home"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Registra início de menstruação na Home.
     */
    suspend fun logPeriodStart(): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val cycle = cycleUseCases.logPeriodStart(
                userId = userId,
                startDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
            )
            _currentCycle.value = cycle
            loadHomeData() // Recarrega previsões
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao registrar menstruação"
            false
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Registra fim de menstruação.
     */
    suspend fun logPeriodEnd(cycleId: String): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val cycle = cycleUseCases.logPeriodEnd(
                userId = userId,
                cycleId = cycleId,
                endDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
            )
            if (cycle != null) {
                _currentCycle.value = cycle
                loadHomeData()
            }
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao finalizar menstruação"
            false
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Força regeneração de previsões (após sync, etc).
     */
    suspend fun forceRefreshPredictions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                cycleUseCases.forceRegeneratePredictions(userId)
                loadHomeData()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao atualizar previsões"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // ============ Quick Actions ============

    data class QuickAction(
        val id: String,
        val icon: String,
        val title: String,
        val subtitle: String,
        val color: String, // Hex color
        val action: () -> Unit
    )

    fun getQuickActions(
        onLogPeriod: () -> Unit,
        onLogSymptoms: () -> Unit,
        onViewCalendar: () -> Unit,
        onViewInsights: () -> Unit
    ): List<QuickAction> {
        val colors = com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors()
        return listOf(
            QuickAction(
                id = "log_period",
                icon = "🩸",
                title = "Registrar menstruação",
                subtitle = currentCycle.value?.isCurrent == true ? "Finalizar menstruação" : "Iniciar nova menstruação",
                color = "#${colors.phaseMenstrual.toArgb().toString(16).substring(2).uppercase()}",
                action = onLogPeriod
            ),
            QuickAction(
                id = "log_symptoms",
                icon = "📝",
                title = "Registrar sintomas",
                subtitle = "${todaySymptoms.value.size} sintomas hoje",
                color = "#${colors.primary.toArgb().toString(16).substring(2).uppercase()}",
                action = onLogSymptoms
            ),
            QuickAction(
                id = "view_calendar",
                icon = "📅",
                title = "Ver calendário",
                subtitle = "Visualização mensal completa",
                color = "#${colors.tertiary.toArgb().toString(16).substring(2).uppercase()}",
                action = onViewCalendar
            ),
            QuickAction(
                id = "view_insights",
                icon = "📊",
                title = "Ver insights",
                subtitle = "Análise e relatórios",
                color = "#${colors.phaseFollicular.toArgb().toString(16).substring(2).uppercase()}",
                action = onViewInsights
            )
        )
    }
}