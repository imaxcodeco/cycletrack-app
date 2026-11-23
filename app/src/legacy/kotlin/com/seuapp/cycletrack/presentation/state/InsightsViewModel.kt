package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Cycle
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
import kotlin.math.sqrt
import javax.inject.Inject
import kotlinx.coroutines.async
import import com.seuapp.cycletrack.util.toEpochMilliseconds
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * ViewModel para a tela de Insights.
 * Gerencia análises estatísticas, tendências e relatórios do ciclo.
 */
@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val cycleUseCases: CycleUseCases,
    private val symptomUseCases: SymptomUseCases,
    private val predictionUseCases: PredictionUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    private val _cycleStatistics = MutableStateFlow<CycleStatistics?>(null)
    val cycleStatistics = _cycleStatistics.asStateFlow()

    private val _periodStatistics = MutableStateFlow<PeriodStatistics?>(null)
    val periodStatistics = _periodStatistics.asStateFlow()

    private val _fertilityInsights = MutableStateFlow<FertilityInsights?>(null)
    val fertilityInsights = _fertilityInsights.asStateFlow()

    private val _symptomPatterns = MutableStateFlow<SymptomPatterns?>(null)
    val symptomPatterns = _symptomPatterns.asStateFlow()

    private val _cycleTrends = MutableStateFlow<CycleTrends?>(null)
    val cycleTrends = _cycleTrends.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // ============ Data Classes ============

    data class CycleStatistics(
        val averageCycleLength: Double,
        val medianCycleLength: Int,
        val minCycleLength: Int,
        val maxCycleLength: Int,
        val standardDeviation: Double,
        val regularityScore: Double, // 0-1
        val regularityLabel: String,
        val totalCycles: Int,
        val cyclesAnalyzed: Int,
        val predictedNextCycleLength: Int,
        val confidenceLevel: String // "Alta", "Média", "Baixa"
    )

    data class PeriodStatistics(
        val averagePeriodLength: Double,
        val medianPeriodLength: Int,
        val minPeriodLength: Int,
        val maxPeriodLength: Int,
        val flowDistribution: Map<String, Int>, // spotting, light, medium, heavy
        val mostCommonFlow: String,
        val totalPeriods: Int
    )

    data class FertilityInsights(
        val averageOvulationDay: Int,
        val averageLutealPhase: Int,
        val fertileWindowDays: Int,
        val nextFertileWindowStart: LocalDate?,
        val nextFertileWindowEnd: LocalDate?,
        val nextOvulationDate: LocalDate?,
        val conceptionProbability: String, // "Alta", "Média", "Baixa"
        val cyclePhaseToday: String // "menstrual", "follicular", "ovulation", "luteal"
    )

    data class SymptomPatterns(
        val mostCommonSymptoms: List<SymptomFrequency>,
        val symptomsByPhase: Map<String, List<SymptomFrequency>>,
        val symptomsByCategory: Map<String, List<SymptomFrequency>>,
        val severityDistribution: Map<String, Int>,
        val totalSymptomsLogged: Int,
        val averageSymptomsPerCycle: Double
    )

    data class SymptomFrequency(
        val name: String,
        val category: String,
        val count: Int,
        val averageSeverity: Double,
        val percentageOfCycles: Double
    )

    data class CycleTrends(
        val last6MonthsCycleLengths: List<CycleLengthPoint>,
        val trendDirection: String, // "improving", "stable", "worsening"
        val trendDescription: String,
        val predictionAccuracy: Double, // 0-1
        val lastPredictionErrorDays: Int?
    )

    data class CycleLengthPoint(
        val month: String, // "Jan 2024"
        val cycleLength: Int,
        val periodLength: Int,
        val isPredicted: Boolean
    )

    // ============ Actions ============

    init {
        loadInsights()
    }

    fun loadInsights() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                
                // Carrega dados em paralelo
                val cyclesDeferred = viewModelScope.async { cycleUseCases.getCycleHistory(userId, 20) }
                val symptomsDeferred = viewModelScope.async { symptomUseCases.getSymptomsInRange(
                    userId, 
                    Clock.System.todayIn(TimeZone.currentSystemDefault()).minusMonths(12).toEpochMilliseconds(), 
                    Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                ) }
                val statsDeferred = viewModelScope.async { cycleUseCases.getCycleStatistics(userId) }
                val predictionsDeferred = viewModelScope.async { predictionUseCases.getUpcomingPredictions(userId) }
                
                val cycles = cyclesDeferred.await()
                val symptoms = symptomsDeferred.await()
                val stats = statsDeferred.await()
                val predictions = predictionsDeferred.await()
                
                _cycleStatistics.value = calculateCycleStatistics(cycles, stats)
                _periodStatistics.value = calculatePeriodStatistics(cycles)
                _fertilityInsights.value = calculateFertilityInsights(cycles, predictions)
                _symptomPatterns.value = calculateSymptomPatterns(symptoms, cycles)
                _cycleTrends.value = calculateCycleTrends(cycles)
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar insights"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateCycleStatistics(cycles: List<Cycle>, stats: Any): CycleStatistics {
        val validCycles = cycles.filter { !it.isPredicted && it.cycleLength in 21..45 }
        val cycleLengths = validCycles.map { it.cycleLength.toDouble() }
        
        val avg = if (cycleLengths.isNotEmpty()) cycleLengths.average() else 28.0
        val median = if (cycleLengths.isNotEmpty()) cycleLengths.sorted()[cycleLengths.size / 2].toInt() else 28
        val min = if (cycleLengths.isNotEmpty()) cycleLengths.minOrNull()!!.toInt() else 28
        val max = if (cycleLengths.isNotEmpty()) cycleLengths.maxOrNull()!!.toInt() else 28
        
        val variance = if (cycleLengths.size > 1) {
            cycleLengths.map { (it - avg).pow(2) }.average()
        } else 0.0
        val stdDev = sqrt(variance)
        
        val regularityScore = when {
            stdDev <= 2.0 -> 1.0
            stdDev <= 4.0 -> 0.8
            stdDev <= 7.0 -> 0.6
            else -> 0.4
        }
        
        val regularityLabel = when {
            stdDev <= 2.0 -> "Muito regular"
            stdDev <= 4.0 -> "Regular"
            stdDev <= 7.0 -> "Moderadamente irregular"
            else -> "Irregular"
        }
        
        val confidenceLevel = when {
            validCycles.size >= 6 && stdDev <= 3.0 -> "Alta"
            validCycles.size >= 3 && stdDev <= 5.0 -> "Média"
            else -> "Baixa"
        }
        
        return CycleStatistics(
            averageCycleLength = avg,
            medianCycleLength = median,
            minCycleLength = min,
            maxCycleLength = max,
            standardDeviation = stdDev,
            regularityScore = regularityScore,
            regularityLabel = regularityLabel,
            totalCycles = cycles.size,
            cyclesAnalyzed = validCycles.size,
            predictedNextCycleLength = avg.roundToInt(),
            confidenceLevel = confidenceLevel
        )
    }

    private fun calculatePeriodStatistics(cycles: List<Cycle>): PeriodStatistics {
        val validCycles = cycles.filter { !it.isPredicted && it.periodLength in 2..10 }
        val periodLengths = validCycles.map { it.periodLength.toDouble() }
        
        val avg = if (periodLengths.isNotEmpty()) periodLengths.average() else 5.0
        val median = if (periodLengths.isNotEmpty()) periodLengths.sorted()[periodLengths.size / 2].toInt() else 5
        val min = if (periodLengths.isNotEmpty()) periodLengths.minOrNull()!!.toInt() else 3
        val max = if (periodLengths.isNotEmpty()) periodLengths.maxOrNull()!!.toInt() else 7
        
        // Flow distribution (simplificado - baseado em periodLength como proxy)
        val flowDist = mutableMapOf<String, Int>()
        validCycles.forEach { cycle ->
            val flow = when {
                cycle.periodLength <= 3 -> "light"
                cycle.periodLength <= 5 -> "medium"
                cycle.periodLength <= 7 -> "heavy"
                else -> "very_heavy"
            }
            flowDist[flow] = flowDist.getOrDefault(flow, 0) + 1
        }
        
        val mostCommonFlow = flowDist.maxByOrNull { it.value }?.key ?: "medium"
        val flowLabels = mapOf(
            "spotting" to "Escape",
            "light" to "Leve",
            "medium" to "Médio",
            "heavy" to "Intenso",
            "very_heavy" to "Muito intenso"
        )
        
        return PeriodStatistics(
            averagePeriodLength = avg,
            medianPeriodLength = median,
            minPeriodLength = min,
            maxPeriodLength = max,
            flowDistribution = flowDist.mapKeys { flowLabels[it.key] ?: it.key },
            mostCommonFlow = flowLabels[mostCommonFlow] ?: "Médio",
            totalPeriods = validCycles.size
        )
    }

    private fun calculateFertilityInsights(cycles: List<Cycle>, predictions: List<com.seuapp.cycletrack.domain.model.Prediction>): FertilityInsights {
        val validCycles = cycles.filter { !it.isPredicted && it.ovulationDate != null }
        
        val avgOvulationDay = if (validCycles.isNotEmpty()) {
            validCycles.map { it.startDate.daysUntil(it.ovulationDate!!).toInt() + 1 }.average().roundToInt()
        } else 14
        
        val avgLutealPhase = if (validCycles.isNotEmpty()) {
            validCycles.map { it.lutealPhaseLength }.average().roundToInt()
        } else 14
        
        val nextFertileStart = predictions.firstOrNull { it.type == com.seuapp.cycletrack.domain.model.PredictionType.FERTILE_WINDOW_START }?.predictedDate
        val nextFertileEnd = predictions.firstOrNull { it.type == com.seuapp.cycletrack.domain.model.PredictionType.FERTILE_WINDOW_END }?.predictedDate
        val nextOvulation = predictions.firstOrNull { it.type == com.seuapp.cycletrack.domain.model.PredictionType.OVULATION }?.predictedDate
        
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val cyclePhaseToday = when {
            predictions.any { it.type == com.seuapp.cycletrack.domain.model.PredictionType.PERIOD_START && it.predictedDate <= today && (it.predictedDate.plusDays(5) >= today) } -> "menstrual"
            nextFertileStart != null && nextFertileEnd != null && today >= nextFertileStart && today <= nextFertileEnd -> "ovulation"
            nextOvulation != null && today.daysUntil(nextOvulation).inDays <= 2 && today.daysUntil(nextOvulation).inDays >= -1 -> "ovulation"
            predictions.any { it.type == com.seuapp.cycletrack.domain.model.PredictionType.PMS_START && it.predictedDate <= today } -> "luteal"
            else -> "follicular"
        }
        
        val conceptionProbability = when {
            cyclePhaseToday == "ovulation" && validCycles.size >= 3 -> "Alta"
            cyclePhaseToday == "ovulation" -> "Média"
            cyclePhaseToday == "follicular" && validCycles.size >= 3 -> "Média"
            else -> "Baixa"
        }
        
        return FertilityInsights(
            averageOvulationDay = avgOvulationDay,
            averageLutealPhase = avgLutealPhase,
            fertileWindowDays = 7, // 5 antes + 1 dia + 1 depois
            nextFertileWindowStart = nextFertileStart,
            nextFertileWindowEnd = nextFertileEnd,
            nextOvulationDate = nextOvulation,
            conceptionProbability = conceptionProbability,
            cyclePhaseToday = cyclePhaseToday
        )
    }

    private fun calculateSymptomPatterns(symptoms: List<Symptom>, cycles: List<Cycle>): SymptomPatterns {
        val totalCycles = cycles.filter { !it.isPredicted }.size
        val totalSymptoms = symptoms.size
        val avgPerCycle = if (totalCycles > 0) totalSymptoms.toDouble() / totalCycles else 0.0
        
        // Frequência por sintoma
        val symptomGroups = symptoms.groupBy { it.name }
        val mostCommon = symptomGroups.entries
            .map { entry ->
                val severities = entry.value.map { it.severity.value.toDouble() }
                SymptomFrequency(
                    name = entry.key,
                    category = entry.value.first().category,
                    count = entry.value.size,
                    averageSeverity = severities.average(),
                    percentageOfCycles = if (totalCycles > 0) (entry.value.size.toDouble() / totalCycles * 100) else 0.0
                )
            }
            .sortedByDescending { it.count }
            .take(10)
        
        // Por fase do ciclo (simplificado)
        val byPhase = mutableMapOf<String, MutableList<Symptom>>()
        symptoms.forEach { symptom ->
            val phase = determinePhase(symptom, cycles)
            byPhase.getOrPut(phase) { mutableListOf() }.add(symptom)
        }
        
        val symptomsByPhase = byPhase.mapValues { (phase, list) ->
            list.groupBy { it.name }.entries
                .map { entry ->
                    val severities = entry.value.map { it.severity.value.toDouble() }
                    SymptomFrequency(
                        name = entry.key,
                        category = entry.value.first().category,
                        count = entry.value.size,
                        averageSeverity = severities.average(),
                        percentageOfCycles = if (totalCycles > 0) (entry.value.size.toDouble() / totalCycles * 100) else 0.0
                    )
                }
                .sortedByDescending { it.count }
        }
        
        // Por categoria
        val symptomsByCategory = symptoms.groupBy { it.category }.mapValues { (cat, list) ->
            list.groupBy { it.name }.entries
                .map { entry ->
                    val severities = entry.value.map { it.severity.value.toDouble() }
                    SymptomFrequency(
                        name = entry.key,
                        category = cat,
                        count = entry.value.size,
                        averageSeverity = severities.average(),
                        percentageOfCycles = if (totalCycles > 0) (entry.value.size.toDouble() / totalCycles * 100) else 0.0
                    )
                }
                .sortedByDescending { it.count }
        }
        
        // Distribuição de severidade
        val severityDist = symptoms.groupBy { it.severity.label }.mapValues { (_, list) -> list.size }
        
        return SymptomPatterns(
            mostCommonSymptoms = mostCommon,
            symptomsByPhase = symptomsByPhase,
            symptomsByCategory = symptomsByCategory,
            severityDistribution = severityDist,
            totalSymptomsLogged = totalSymptoms,
            averageSymptomsPerCycle = avgPerCycle
        )
    }

    private fun determinePhase(symptom: Symptom, cycles: List<Cycle>): String {
        val cycle = cycles.firstOrNull { 
            !it.isPredicted && it.startDate <= symptom.date && (it.endDate == null || it.endDate!! >= symptom.date) 
        }
        if (cycle == null) return "unknown"
        
        val dayOfCycle = cycle.startDate.daysUntil(symptom.date).toInt() + 1
        val ovulationDay = cycle.ovulationDate?.let { cycle.startDate.daysUntil(it).toInt() + 1 } ?: (cycle.cycleLength - 14)
        
        return when {
            dayOfCycle <= cycle.periodLength -> "menstrual"
            dayOfCycle < ovulationDay - 5 -> "follicular"
            dayOfCycle >= ovulationDay - 5 && dayOfCycle <= ovulationDay + 1 -> "ovulation"
            else -> "luteal"
        }
    }

    private fun calculateCycleTrends(cycles: List<Cycle>): CycleTrends {
        val validCycles = cycles.filter { !it.isPredicted }.sortedBy { it.startDate.toEpochDays() }.takeLast(6)
        
        val points = validCycles.map { cycle ->
            CycleLengthPoint(
                month = YearMonth.from(cycle.startDate).toString(),
                cycleLength = cycle.cycleLength,
                periodLength = cycle.periodLength,
                isPredicted = false
            )
        }
        
        // Determina tendência
        val trendDirection = if (points.size >= 3) {
            val firstHalf = points.take(points.size / 2).map { it.cycleLength.toDouble() }.average()
            val secondHalf = points.drop(points.size / 2).map { it.cycleLength.toDouble() }.average()
            val diff = secondHalf - firstHalf
            when {
                diff > 1.0 -> "worsening"
                diff < -1.0 -> "improving"
                else -> "stable"
            }
        } else "stable"
        
        val trendDescription = when (trendDirection) {
            "improving" -> "Ciclos ficando mais regulares"
            "worsening" -> "Ciclos ficando menos regulares"
            else -> "Ciclos estáveis"
        }
        
        return CycleTrends(
            last6MonthsCycleLengths = points,
            trendDirection = trendDirection,
            trendDescription = trendDescription,
            predictionAccuracy = 0.85, // Placeholder
            lastPredictionErrorDays = null
        )
    }

    fun clearError() {
        _error.value = null
    }
}