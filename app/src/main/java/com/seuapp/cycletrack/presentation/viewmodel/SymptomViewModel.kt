package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.data.session.UserSession
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.repository.SymptomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class SymptomViewModel @Inject constructor(
    private val symptomRepository: SymptomRepository,
    private val cycleRepository: CycleRepository,
    private val userSession: UserSession
) : ViewModel() {

    private val _symptoms = MutableStateFlow<List<Symptom>>(emptyList())
    val symptoms = _symptoms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    /** Mensagem de feedback após registrar menstruação, consumida pela UI (snackbar). */
    private val _periodLogMessage = MutableStateFlow<String?>(null)
    val periodLogMessage = _periodLogMessage.asStateFlow()

    fun consumePeriodLogMessage() {
        _periodLogMessage.value = null
    }

    fun loadSymptoms(date: LocalDate) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _symptoms.value = symptomRepository.getSymptomsForDate(userSession.currentUserId(), date.toEpochDay())
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error loading symptoms")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Alterna um item (chip) de uma categoria para a data informada: se já estiver
     * marcado, remove; senão, cria. O id é determinístico (userId+data+categoria+nome)
     * para que marcar/desmarcar seja idempotente.
     */
    fun toggleSymptom(date: LocalDate, category: String, name: String) {
        viewModelScope.launch {
            try {
                val userId = userSession.currentUserId()
                val id = "${userId}_${date.toEpochDay()}_${category}_$name"
                val existing = _symptoms.value.find { it.id == id }
                if (existing != null) {
                    symptomRepository.deleteSymptom(id)
                    _symptoms.value = _symptoms.value.filterNot { it.id == id }
                } else {
                    val symptom = Symptom(
                        id = id,
                        userId = userId,
                        date = date,
                        name = name,
                        category = category
                    )
                    symptomRepository.upsertSymptom(symptom)
                    _symptoms.value = _symptoms.value + symptom
                }
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error toggling symptom")
            }
        }
    }

    /**
     * Salva uma medida numerica (peso ou temperatura) para a data informada.
     * Usa um id deterministico por categoria+data para que editar sobrescreva
     * o valor anterior em vez de duplicar. Se [value] for nulo, remove o
     * registro (campo limpo pela usuaria).
     */
    fun saveMeasurement(date: LocalDate, category: String, value: Double?) {
        viewModelScope.launch {
            try {
                val userId = userSession.currentUserId()
                val id = "${userId}_${date.toEpochDay()}_measurement_$category"
                if (value == null) {
                    symptomRepository.deleteSymptom(id)
                    _symptoms.value = _symptoms.value.filterNot { it.id == id }
                    return@launch
                }
                val symptom = Symptom(
                    id = id,
                    userId = userId,
                    date = date,
                    name = category,
                    category = category,
                    value = value
                )
                symptomRepository.upsertSymptom(symptom)
                _symptoms.value = _symptoms.value.filterNot { it.id == id } + symptom
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error saving measurement")
            }
        }
    }

    fun addSymptom(symptom: Symptom) {
        viewModelScope.launch {
            try {
                symptomRepository.upsertSymptom(symptom)
                _symptoms.value = _symptoms.value + symptom
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error adding symptom")
            }
        }
    }

    /**
     * Registra o início de uma menstruação na data informada, criando ou avançando o ciclo.
     *
     * Regras:
     * - Sem nenhum ciclo ainda: cria o primeiro ciclo começando em [date].
     * - [date] à frente do ciclo atual (>= [MIN_NEW_CYCLE_GAP] dias): é uma nova menstruação.
     *   Fecha o ciclo anterior gravando sua duração REAL observada (dias entre os dois inícios)
     *   e cria um novo ciclo em [date], estimando o comprimento pela média dos ciclos recentes.
     *   É isso que faz a previsão melhorar a cada ciclo registrado.
     * - [date] dentro de [MIN_NEW_CYCLE_GAP] dias do início atual: considerado o mesmo período
     *   (evita criar ciclos duplicados por marcar o fluxo em dias seguidos). Não faz nada.
     * - [date] antes do ciclo atual: registra um ciclo passado (backfill de histórico), com a
     *   duração igual ao intervalo até o próximo início conhecido.
     */
    fun logPeriodStart(date: LocalDate) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = userSession.currentUserId()
                val recent = cycleRepository.getRecentCycles(userId, CYCLES_FOR_AVERAGE)
                val latest = recent.firstOrNull()
                val avgPeriod = recent.map { it.periodLength }
                    .ifEmpty { listOf(DEFAULT_PERIOD_LENGTH) }
                    .average().toInt().coerceIn(1, 15)

                when {
                    latest == null -> {
                        cycleRepository.upsertCycle(
                            Cycle.createManual(userId, date, avgPeriod)
                        )
                        _periodLogMessage.value = "Menstruação registrada."
                    }

                    date == latest.startDate -> {
                        _periodLogMessage.value = "Esta menstruação já estava registrada neste dia."
                    }

                    date.isAfter(latest.startDate) -> {
                        val gap = ChronoUnit.DAYS.between(latest.startDate, date).toInt()
                        if (gap < MIN_NEW_CYCLE_GAP) {
                            _periodLogMessage.value =
                                "Este dia faz parte da menstruação atual."
                        } else {
                            // Fecha o ciclo anterior com a duração real observada.
                            cycleRepository.upsertCycle(latest.copy(cycleLength = gap))
                            // Estima o novo ciclo pela média dos ciclos recentes já observados.
                            val estimatedLength = recent.map { it.cycleLength }
                                .plus(gap)
                                .average().toInt().coerceIn(15, 90)
                            cycleRepository.upsertCycle(
                                Cycle.createManual(
                                    userId = userId,
                                    startDate = date,
                                    periodLength = avgPeriod,
                                    previousCycleLength = estimatedLength
                                )
                            )
                            _periodLogMessage.value = "Nova menstruação registrada."
                        }
                    }

                    else -> {
                        // date antes do ciclo atual: ciclo passado (backfill).
                        val lengthToNext =
                            ChronoUnit.DAYS.between(date, latest.startDate).toInt().coerceIn(15, 90)
                        cycleRepository.upsertCycle(
                            Cycle.createManual(
                                userId = userId,
                                startDate = date,
                                periodLength = avgPeriod,
                                previousCycleLength = lengthToNext
                            )
                        )
                        _periodLogMessage.value = "Ciclo anterior registrado."
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error logging period start")
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        /** Ciclos recentes usados para estimar a duração do próximo. */
        private const val CYCLES_FOR_AVERAGE = 6

        /** Duração de menstruação padrão quando não há histórico. */
        private const val DEFAULT_PERIOD_LENGTH = 5

        /**
         * Intervalo mínimo (dias) entre dois inícios para contar como ciclos distintos.
         * Abaixo disso, trata-se do mesmo período — evita ciclos duplicados por marcar o
         * fluxo em dias consecutivos. Ciclos clínicos normais têm >= 21 dias; 10 é um guarda
         * conservador que ainda permite ciclos curtos reais.
         */
        private const val MIN_NEW_CYCLE_GAP = 10
    }
}
