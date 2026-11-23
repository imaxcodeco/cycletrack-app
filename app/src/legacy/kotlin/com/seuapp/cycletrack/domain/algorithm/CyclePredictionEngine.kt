package com.seuapp.cycletrack.domain.algorithm

import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.model.PredictionSource
import com.seuapp.cycletrack.domain.model.PredictionType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.math.sqrt
import kotlin.math.min
import kotlin.math.max
import import com.seuapp.cycletrack.util.toEpochMilliseconds
import import kotlinx.datetime.Clock

/**
 * Motor de Previsão de Ciclo - Clean-room Implementation.
 * 
 * Baseado em literatura médica:
 * - OMS: "Menstrual cycle length and variability"
 * - ACOG: "Menstruation in Girls and Adolescents"
 * - NIH: "Menstrual cycle characteristics"
 * 
 * Algoritmo: Média ponderada com janela deslizante
 * - Ciclos mais recentes têm peso maior
 * - Fase lútea fixa em 14 dias (padrão médico)
 * - Janela fértil: 5 dias antes + dia da ovulação + 1 dia depois
 * - Pontuação de confiança baseada em variabilidade (desvio padrão)
 */
object CyclePredictionEngine {

    // Constantes médicas
    private const val DEFAULT_LUTEAL_PHASE = 14
    private const val FERTILE_WINDOW_DAYS_BEFORE = 5
    private const val FERTILE_WINDOW_DAYS_AFTER = 1
    private const val PMS_DAYS_BEFORE = 7
    private const val MIN_CYCLES_FOR_PREDICTION = 3
    private const val MAX_CYCLES_TO_ANALYZE = 12
    private const val MIN_CYCLE_LENGTH = 21
    private const val MAX_CYCLE_LENGTH = 45
    private const val MIN_PERIOD_LENGTH = 2
    private const val MAX_PERIOD_LENGTH = 10

    // Pesos para média ponderada (mais recente = maior peso)
    private val RECENCY_WEIGHTS = doubleArrayOf(
        1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.15, 0.1, 0.05
    )

    /**
     * Gera previsões completas para um usuário.
     * 
     * @param cycles Lista de ciclos históricos (ordenados por data decrescente)
     * @param userId ID do usuário
     * @param lastPeriodStart Início da última menstruação conhecida
     * @param averagePeriodLength Duração média da menstruação
     * @param contraceptionMethod Método contraceptivo (ajusta algoritmo)
     * @return Lista de previsões geradas
     */
    fun generatePredictions(
        cycles: List<Cycle>,
        userId: String,
        lastPeriodStart: LocalDate,
        averagePeriodLength: Int,
        contraceptionMethod: String = "none"
    ): List<Prediction> {
        val validCycles = filterValidCycles(cycles)
        
        if (validCycles.size < MIN_CYCLES_FOR_PREDICTION) {
            return generateFallbackPredictions(
                userId, lastPeriodStart, averagePeriodLength, contraceptionMethod
            )
        }

        // Calcula métricas estatísticas
        val stats = calculateCycleStatistics(validCycles)
        
        // Ajusta para contracepção
        val adjustedStats = adjustForContraception(stats, contraceptionMethod)
        
        // Gera previsões futuras (próximos 6 ciclos)
        val predictions = mutableListOf<Prediction>()
        var currentStart = lastPeriodStart
        
        for (i in 0 until 6) {
            val cyclePredictions = generateCyclePredictions(
                userId = userId,
                cycleStart = currentStart,
                cycleLength = adjustedStats.predictedCycleLength,
                periodLength = adjustedStats.predictedPeriodLength,
                lutealPhase = adjustedStats.predictedLutealPhase,
                confidence = adjustedStats.confidenceScore,
                cycleCount = validCycles.size,
                stdDev = adjustedStats.cycleLengthStdDev,
                source = PredictionSource.LOCAL,
                algorithmVersion = "1.0.0"
            )
            predictions.addAll(cyclePredictions)
            
            // Próximo ciclo
            currentStart = currentStart.plusDays(adjustedStats.predictedCycleLength)
        }

        return predictions
    }

    /**
     * Gera previsões para um único ciclo.
     */
    private fun generateCyclePredictions(
        userId: String,
        cycleStart: LocalDate,
        cycleLength: Int,
        periodLength: Int,
        lutealPhase: Int,
        confidence: Float,
        cycleCount: Int,
        stdDev: Float,
        source: PredictionSource,
        algorithmVersion: String
    ): List<Prediction> {
        val cycleEnd = cycleStart.plusDays(periodLength - 1)
        val ovulationDate = cycleStart.plusDays(cycleLength - lutealPhase - 1)
        val fertileStart = ovulationDate.minusDays(FERTILE_WINDOW_DAYS_BEFORE)
        val fertileEnd = ovulationDate.plusDays(FERTILE_WINDOW_DAYS_AFTER)
        val pmsStart = cycleStart.minusDays(PMS_DAYS_BEFORE)
        val pmsEnd = cycleStart.minusDays(1)
        val nextPeriodStart = cycleStart.plusDays(cycleLength)

        val baseMetadata = mapOf(
            "cycleLength" to cycleLength,
            "periodLength" to periodLength,
            "lutealPhase" to lutealPhase,
            "confidence" to confidence,
            "cycleCount" to cycleCount,
            "stdDev" to stdDev
        ).toJson()

        return listOf(
            Prediction.createPeriodStart(
                userId, cycleStart, confidence, cycleCount, stdDev, source
            ),
            Prediction(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                type = PredictionType.PERIOD_END,
                predictedDate = cycleEnd,
                confidenceScore = confidence * 0.9f,
                algorithmVersion = algorithmVersion,
                cycleCountUsed = cycleCount,
                standardDeviation = stdDev,
                metadata = baseMetadata,
                source = source,
                expiresAt = cycleEnd.plusDays(3).atStartOfDay().toEpochMilliseconds()
            ),
            Prediction.createOvulation(
                userId, ovulationDate, confidence * 0.85f, cycleCount, stdDev, source
            ),
            Prediction.createFertileWindow(
                userId, fertileStart, fertileEnd, confidence * 0.8f, cycleCount, stdDev, source
            ).first,
            Prediction.createFertileWindow(
                userId, fertileStart, fertileEnd, confidence * 0.8f, cycleCount, stdDev, source
            ).second,
            Prediction(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                type = PredictionType.PMS_START,
                predictedDate = pmsStart,
                confidenceScore = confidence * 0.7f,
                algorithmVersion = algorithmVersion,
                cycleCountUsed = cycleCount,
                standardDeviation = stdDev,
                metadata = baseMetadata,
                source = source,
                expiresAt = pmsEnd.plusDays(2).atStartOfDay().toEpochMilliseconds()
            ),
            Prediction(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                type = PredictionType.PMS_END,
                predictedDate = pmsEnd,
                confidenceScore = confidence * 0.7f,
                algorithmVersion = algorithmVersion,
                cycleCountUsed = cycleCount,
                standardDeviation = stdDev,
                metadata = baseMetadata,
                source = source,
                expiresAt = cycleStart.plusDays(1).atStartOfDay().toEpochMilliseconds()
            ),
            Prediction.createPeriodStart(
                userId, nextPeriodStart, confidence * 0.9f, cycleCount, stdDev, source
            )
        )
    }

    /**
     * Filtra ciclos válidos para análise.
     */
    private fun filterValidCycles(cycles: List<Cycle>): List<Cycle> {
        return cycles
            .filter { !it.isPredicted }
            .filter { it.cycleLength in MIN_CYCLE_LENGTH..MAX_CYCLE_LENGTH }
            .filter { it.periodLength in MIN_PERIOD_LENGTH..MAX_PERIOD_LENGTH }
            .take(MAX_CYCLES_TO_ANALYZE)
    }

    /**
     * Calcula estatísticas dos ciclos com média ponderada.
     */
    private fun calculateCycleStatistics(cycles: List<Cycle>): CycleStatistics {
        val sortedCycles = cycles.sortedBy { it.startDate.toEpochDays() }
        val count = sortedCycles.size
        
        // Média ponderada por recência (ciclos mais recentes = maior peso)
        var weightedCycleLengthSum = 0.0
        var weightedPeriodLengthSum = 0.0
        var weightSum = 0.0
        
        for (i in sortedCycles.indices) {
            val weight = if (i < RECENCY_WEIGHTS.size) RECENCY_WEIGHTS[i] else 0.05
            weightedCycleLengthSum += sortedCycles[i].cycleLength * weight
            weightedPeriodLengthSum += sortedCycles[i].periodLength * weight
            weightSum += weight
        }
        
        val predictedCycleLength = (weightedCycleLengthSum / weightSum).roundToInt()
        val predictedPeriodLength = (weightedPeriodLengthSum / weightSum).roundToInt()
        
        // Desvio padrão do comprimento do ciclo
        val cycleLengths = sortedCycles.map { it.cycleLength.toDouble() }
        val meanCycleLength = cycleLengths.average()
        val variance = cycleLengths.map { (it - meanCycleLength).pow(2) }.average()
        val cycleLengthStdDev = sqrt(variance).toFloat()
        
        // Fase lútea (mediana dos ciclos com ovulação conhecida)
        val lutealPhases = sortedCycles
            .filter { it.ovulationDate != null && it.startDate != null }
            .map { it.startDate!.daysUntil(it.ovulationDate!) + 1 }
            .filter { it > 0 && it < 20 }
        
        val predictedLutealPhase = if (lutealPhases.isNotEmpty()) {
            lutealPhases.sorted()[lutealPhases.size / 2]
        } else DEFAULT_LUTEAL_PHASE
        
        // Pontuação de confiança (0-1)
        // Baseada em: número de ciclos, variabilidade, consistência
        val cycleCountFactor = min(1.0, count / 6.0) // Máx confiança com 6+ ciclos
        val variabilityFactor = max(0.0, 1.0 - (cycleLengthStdDev / 10.0)) // Penaliza variabilidade > 10 dias
        val consistencyFactor = calculateConsistencyFactor(sortedCycles)
        
        val confidenceScore = (cycleCountFactor * 0.4 + variabilityFactor * 0.4 + consistencyFactor * 0.2).toFloat()

        return CycleStatistics(
            predictedCycleLength = predictedCycleLength.coerceIn(MIN_CYCLE_LENGTH, MAX_CYCLE_LENGTH),
            predictedPeriodLength = predictedPeriodLength.coerceIn(MIN_PERIOD_LENGTH, MAX_PERIOD_LENGTH),
            predictedLutealPhase = predictedLutealPhase.coerceIn(10, 18),
            cycleLengthStdDev = cycleLengthStdDev,
            confidenceScore = confidenceScore.coerceIn(0.1f, 0.95f),
            cycleCount = count
        )
    }

    /**
     * Calcula fator de consistência baseado em padrões.
     */
    private fun calculateConsistencyFactor(cycles: List<Cycle>): Double {
        if (cycles.size < 3) return 0.5
        
        // Verifica se ciclos recentes são consistentes
        val recentCycles = cycles.take(3)
        val lengths = recentCycles.map { it.cycleLength }
        val maxDiff = lengths.maxOrNull()!! - lengths.minOrNull()!!
        
        return when {
            maxDiff <= 2 -> 1.0
            maxDiff <= 4 -> 0.8
            maxDiff <= 7 -> 0.6
            else -> 0.4
        }
    }

    /**
     * Ajusta estatísticas para método contraceptivo.
     */
    private fun adjustForContraception(stats: CycleStatistics, method: String): CycleStatistics {
        return when (method.lowercase()) {
            "pill", "patch", "ring", "injection", "implant", "iud_hormonal" -> {
                // Contraceptivos hormonais: ciclos mais regulares, fase lútea pode variar
                stats.copy(
                    predictedCycleLength = 28, // Tipicamente 28 dias com pílula
                    predictedLutealPhase = 14,
                    cycleLengthStdDev = 0.5f,
                    confidenceScore = min(0.9f, stats.confidenceScore + 0.1f)
                )
            }
            "iud_copper" -> {
                // DIU cobre: pode aumentar duração e fluxo
                stats.copy(
                    predictedPeriodLength = min(MAX_PERIOD_LENGTH, stats.predictedPeriodLength + 1),
                    cycleLengthStdDev = stats.cycleLengthStdDev * 1.2f
                )
            }
            "fertility_awareness" -> {
                // Métodos de percepção: depende da regularidade natural
                stats
            }
            else -> stats // none, condom, withdrawal, sterilization, other
        }
    }

    /**
     * Previsões de fallback quando não há dados suficientes.
     */
    private fun generateFallbackPredictions(
        userId: String,
        lastPeriodStart: LocalDate,
        averagePeriodLength: Int,
        contraceptionMethod: String
    ): List<Prediction> {
        val cycleLength = when (contraceptionMethod.lowercase()) {
            "pill", "patch", "ring", "injection", "implant", "iud_hormonal" -> 28
            else -> 28 // Default médico
        }
        
        val lutealPhase = DEFAULT_LUTEAL_PHASE
        val confidence = 0.3f // Baixa confiança sem histórico
        
        return generateCyclePredictions(
            userId = userId,
            cycleStart = lastPeriodStart,
            cycleLength = cycleLength,
            periodLength = averagePeriodLength.coerceIn(MIN_PERIOD_LENGTH, MAX_PERIOD_LENGTH),
            lutealPhase = lutealPhase,
            confidence = confidence,
            cycleCount = 0,
            stdDev = 5.0f, // Alto desvio padrão = baixa confiança
            source = PredictionSource.LOCAL,
            algorithmVersion = "1.0.0"
        )
    }

    /**
     * Atualiza previsões com dados reais (para aprendizado).
     */
    fun updateWithActualData(
        predictions: List<Prediction>,
        actualPeriodStart: LocalDate?,
        actualOvulationDate: LocalDate?
    ): List<Prediction> {
        return predictions.map { prediction ->
            when (prediction.type) {
                PredictionType.PERIOD_START -> {
                    actualPeriodStart?.let { actual ->
                        val accuracyDays = actual.toEpochDays() - prediction.predictedDate.toEpochDays()
                        prediction.copy(
                            actualDate = actual,
                            accuracyDays = accuracyDays.toInt()
                        )
                    } ?: prediction
                }
                PredictionType.OVULATION -> {
                    actualOvulationDate?.let { actual ->
                        val accuracyDays = actual.toEpochDays() - prediction.predictedDate.toEpochDays()
                        prediction.copy(
                            actualDate = actual,
                            accuracyDays = accuracyDays.toInt()
                        )
                    } ?: prediction
                }
                else -> prediction
            }
        }
    }

    /**
     * Verifica se está na janela fértil hoje.
     */
    fun isInFertileWindow(predictions: List<Prediction>): Boolean {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return predictions.any { p ->
            p.type in listOf(PredictionType.FERTILE_WINDOW_START, PredictionType.FERTILE_WINDOW_END) &&
            p.predictedDate <= today &&
            (p.type == PredictionType.FERTILE_WINDOW_END || 
             predictions.any { it.type == PredictionType.FERTILE_WINDOW_END && it.predictedDate >= today })
        }
    }

    /**
     * Verifica se está na TPM hoje.
     */
    fun isInPms(predictions: List<Prediction>): Boolean {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return predictions.any { p ->
            p.type in listOf(PredictionType.PMS_START, PredictionType.PMS_END) &&
            p.predictedDate <= today &&
            (p.type == PredictionType.PMS_END || 
             predictions.any { it.type == PredictionType.PMS_END && it.predictedDate >= today })
        }
    }

    /**
     * Obtém dias até próxima menstruação.
     */
    fun daysUntilNextPeriod(predictions: List<Prediction>): Int? {
        return predictions
            .filter { it.type == PredictionType.PERIOD_START && it.predictedDate >= Clock.System.todayIn(TimeZone.currentSystemDefault()) }
            .minByOrNull { it.predictedDate }
            ?.let { Clock.System.todayIn(TimeZone.currentSystemDefault()).daysUntil(it.predictedDate).toInt() }
    }
}

/**
 * Estatísticas calculadas dos ciclos.
 */
private data class CycleStatistics(
    val predictedCycleLength: Int,
    val predictedPeriodLength: Int,
    val predictedLutealPhase: Int,
    val cycleLengthStdDev: Float,
    val confidenceScore: Float,
    val cycleCount: Int
)

/**
 * Extensão para conversão para JSON simples.
 */
private fun Map<String, Any>.toJson(): String {
    return buildString {
        append("{")
        forEachIndexed { index, (key, value) ->
            if (index > 0) append(",")
            append("\"$key\":")
            when (value) {
                is String -> append("\"$value\"")
                is Number, is Boolean -> append(value.toString())
                else -> append("\"$value\"")
            }
        }
        append("}")
    }
}