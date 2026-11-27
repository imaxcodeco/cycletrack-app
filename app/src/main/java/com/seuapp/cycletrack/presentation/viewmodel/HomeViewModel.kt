package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.data.session.UserSession
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.usecase.CyclePhase
import com.seuapp.cycletrack.domain.usecase.FertileWindow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cycleRepository: CycleRepository,
    private val userSession: UserSession
) : ViewModel() {

    private val _currentCycle = MutableStateFlow<Cycle?>(null)
    val currentCycle = _currentCycle.asStateFlow()

    private val _dayOfCycle = MutableStateFlow(1)
    val dayOfCycle = _dayOfCycle.asStateFlow()

    private val _cyclePhase = MutableStateFlow(CyclePhase.UNKNOWN)
    val cyclePhase = _cyclePhase.asStateFlow()

    private val _nextPeriod = MutableStateFlow<LocalDate?>(null)
    val nextPeriod = _nextPeriod.asStateFlow()

    private val _ovulationDate = MutableStateFlow<LocalDate?>(null)
    val ovulationDate = _ovulationDate.asStateFlow()

    private val _fertileWindow = MutableStateFlow<FertileWindow?>(null)
    val fertileWindow = _fertileWindow.asStateFlow()

    private val _cycleCount = MutableStateFlow(0)
    val cycleCount = _cycleCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    companion object {
        /** Quantos ciclos recentes entram na média usada para prever o próximo período/ovulação. */
        private const val CYCLES_FOR_AVERAGE = 6
    }

    init {
        loadCurrentCycle()
    }

    private fun loadCurrentCycle() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val userId = userSession.currentUserId()
                val recentCycles = try {
                    cycleRepository.getRecentCycles(userId, CYCLES_FOR_AVERAGE)
                } catch (e: Exception) {
                    Timber.w(e, "Failed to load recent cycles")
                    emptyList()
                }

                if (recentCycles.isEmpty()) {
                    // Sem dados reais: não inventamos um ciclo. A UI mostra o empty state.
                    clearCycleState()
                } else {
                    applyCycle(recentCycles)
                }

                _cycleCount.value = try {
                    cycleRepository.getCycleCount(userId)
                } catch (e: Exception) {
                    0
                }

                Timber.d("Cycles loaded: ${recentCycles.size}")
            } catch (e: Exception) {
                Timber.e(e, "Error in loadCurrentCycle")
                _error.value = e.message
                clearCycleState()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun clearCycleState() {
        _currentCycle.value = null
        _dayOfCycle.value = 1
        _cyclePhase.value = CyclePhase.UNKNOWN
        _nextPeriod.value = null
        _ovulationDate.value = null
        _fertileWindow.value = null
    }

    fun refreshCurrentCycle() {
        loadCurrentCycle()
    }

    fun addCycle(cycle: Cycle) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                cycleRepository.upsertCycle(cycle)
                Timber.d("Cycle added: ${cycle.id}")
                loadCurrentCycle()
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error adding cycle")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Recalcula ovulacao/janela fertil a partir do cycleLength/periodLength informados,
     * preservando o startDate do ciclo atual, e persiste o resultado.
     */
    fun updateCycleSettings(cycleLength: Int, periodLength: Int) {
        val cycle = _currentCycle.value ?: return
        val recalculated = Cycle.createManual(
            userId = cycle.userId,
            startDate = cycle.startDate,
            periodLength = periodLength,
            previousCycleLength = cycleLength
        ).copy(id = cycle.id)
        addCycle(recalculated)
    }

    /**
     * Usa a média dos últimos ciclos registrados (não só o mais recente) para prever o
     * próximo período, ovulação e janela fértil. Isso evita que um único ciclo atípico
     * distorça a previsão e faz a precisão melhorar conforme mais ciclos são registrados
     * (Flo é criticado por previsões que "não atualizam" com novos dados).
     */
    private fun applyCycle(cycles: List<Cycle>) {
        val latest = cycles.first()
        val avgCycleLength = cycles.map { it.cycleLength }.average().toInt().coerceIn(15, 90)
        val avgPeriodLength = cycles.map { it.periodLength }.average().toInt().coerceIn(1, 15)
        val lutealPhase = latest.lutealPhaseLength.takeIf { it in 7..21 } ?: 14

        val startDate = latest.startDate
        val today = LocalDate.now()
        val dayOfCycle = ((today.toEpochDay() - startDate.toEpochDay()).toInt() + 1).coerceAtLeast(1)

        // Dia do ciclo em que ocorre a ovulação (dia 1 = início da menstruação), padrão
        // clínico ACOG/ASRM: cycleLength - lutealPhase. startDate já É o dia 1, daí o -1.
        val ovulationCycleDay = (avgCycleLength - lutealPhase).coerceAtLeast(1)
        val ovulationDate = startDate.plusDays((ovulationCycleDay - 1).toLong())

        _currentCycle.value = latest.copy(cycleLength = avgCycleLength, periodLength = avgPeriodLength)
        _dayOfCycle.value = dayOfCycle
        _cyclePhase.value = when {
            dayOfCycle <= avgPeriodLength -> CyclePhase.MENSTRUAL
            dayOfCycle < ovulationCycleDay - 1 -> CyclePhase.FOLLICULAR
            dayOfCycle <= ovulationCycleDay -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }
        _nextPeriod.value = startDate.plusDays(avgCycleLength.toLong())
        _ovulationDate.value = ovulationDate
        // Janela fértil = 6 dias terminando no dia da ovulação (ASRM, "Optimizing natural
        // fertility: a committee opinion"): 5 dias antes + o próprio dia da ovulação.
        _fertileWindow.value = FertileWindow(
            ovulationDate.minusDays(5),
            ovulationDate
        )
    }
}
