package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val cycleRepository: CycleRepository
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

    init {
        loadCurrentCycle()
    }

    private fun loadCurrentCycle() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val userId = "user_123"
                val latestCycle = try {
                    cycleRepository.getLatestCycle(userId)
                } catch (e: Exception) {
                    Timber.w(e, "Failed to load cycle, using dummy")
                    null
                }

                val cycle = latestCycle ?: createDummyCycle()
                applyCycle(cycle)

                _cycleCount.value = try {
                    cycleRepository.getCycleCount(userId)
                } catch (e: Exception) {
                    0
                }

                Timber.d("Cycle loaded: day $dayOfCycle of ${cycle.cycleLength}")
            } catch (e: Exception) {
                Timber.e(e, "Error in loadCurrentCycle")
                _error.value = e.message
                // Provide fallback dummy data
                _currentCycle.value = createDummyCycle()
                _dayOfCycle.value = 15
                _cyclePhase.value = CyclePhase.OVULATION
                _nextPeriod.value = LocalDate.now().plusDays(15)
                _ovulationDate.value = LocalDate.now().plusDays(2)
                _fertileWindow.value = FertileWindow(LocalDate.now().minusDays(2), LocalDate.now().plusDays(2))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun createDummyCycle(): Cycle {
        val startDate = LocalDate.now().minusDays(14)
        val endDate = startDate.plusDays(4)
        val cycleLength = 28
        val lutealPhase = 14
        val ovulationDate = startDate.plusDays((cycleLength - lutealPhase - 1).toLong())
        val fertileStart = ovulationDate.minusDays(5)
        val fertileEnd = ovulationDate.plusDays(1)

        return Cycle(
            id = "dummy_cycle_1",
            userId = "user_123",
            startDate = startDate,
            endDate = endDate,
            cycleLength = cycleLength,
            periodLength = 5,
            lutealPhaseLength = lutealPhase,
            ovulationDate = ovulationDate,
            fertileWindowStart = fertileStart,
            fertileWindowEnd = fertileEnd,
            notes = "Ciclo simulado para testes"
        )
    }

    fun refreshCurrentCycle() {
        loadCurrentCycle()
    }

    fun addCycle(cycle: Cycle) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                cycleRepository.upsertCycle(cycle)
                applyCycle(cycle)
                Timber.d("Cycle added: ${cycle.id}")
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

    private fun applyCycle(cycle: Cycle) {
        _currentCycle.value = cycle

        val startDate = cycle.startDate
        val today = LocalDate.now()
        val dayOfCycle = ((today.toEpochDay() - startDate.toEpochDay()).toInt() + 1).coerceAtLeast(1)

        _dayOfCycle.value = dayOfCycle
        _cyclePhase.value = when {
            dayOfCycle <= cycle.periodLength -> CyclePhase.MENSTRUAL
            dayOfCycle <= 11 -> CyclePhase.FOLLICULAR
            dayOfCycle <= 14 -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }

        _nextPeriod.value = startDate.plusDays(cycle.cycleLength.toLong())
        _ovulationDate.value = cycle.ovulationDate ?: startDate.plusDays(14)
        _fertileWindow.value = FertileWindow(
            cycle.fertileWindowStart ?: startDate.plusDays(9),
            cycle.fertileWindowEnd ?: startDate.plusDays(14)
        )
    }
}
