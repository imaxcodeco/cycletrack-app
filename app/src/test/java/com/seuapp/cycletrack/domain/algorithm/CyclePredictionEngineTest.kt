package com.seuapp.cycletrack.domain.algorithm

import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.model.PredictionSource
import com.seuapp.cycletrack.domain.model.PredictionType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Testes unitários para o CyclePredictionEngine.
 * Clean-room - valida algoritmo próprio de previsão.
 */
class CyclePredictionEngineTest {

    private fun createCycle(
        startDate: LocalDate,
        cycleLength: Int,
        periodLength: Int = 5,
        ovulationDate: LocalDate? = null
    ): Cycle {
        val lutealPhase = 14
        val ovulation = ovulationDate ?: startDate.plusDays(cycleLength - lutealPhase - 1)
        val fertileStart = ovulation.minusDays(5)
        val fertileEnd = ovulation.plusDays(1)
        
        return Cycle(
            id = java.util.UUID.randomUUID().toString(),
            userId = "test-user",
            startDate = startDate,
            endDate = startDate.plusDays(periodLength - 1),
            cycleLength = cycleLength,
            periodLength = periodLength,
            lutealPhaseLength = lutealPhase,
            ovulationDate = ovulation,
            fertileWindowStart = fertileStart,
            fertileWindowEnd = fertileEnd,
            isPredicted = false,
            confidenceScore = 1.0f,
            source = Cycle.SOURCE_MANUAL,
            notes = ""
        )
    }

    @Test
    fun `generatePredictions with sufficient cycles returns predictions`() {
        // Given: 6 ciclos regulares de 28 dias
        val cycles = mutableListOf<Cycle>()
        var start = LocalDate.of(2024, 1, 1)
        repeat(6) {
            cycles.add(createCycle(start, 28))
            start = start.plusDays(28)
        }
        
        // When
        val predictions = CyclePredictionEngine.generatePredictions(
            cycles = cycles,
            userId = "test-user",
            lastPeriodStart = start.minusDays(28),
            averagePeriodLength = 5,
            contraceptionMethod = "none"
        )
        
        // Then
        assertFalse(predictions.isEmpty())
        assertEquals(42, predictions.size) // 6 ciclos * 7 previsões cada
        
        // Verifica tipos de previsão
        val types = predictions.map { it.type }.toSet()
        assertTrue(types.contains(PredictionType.PERIOD_START))
        assertTrue(types.contains(PredictionType.PERIOD_END))
        assertTrue(types.contains(PredictionType.OVULATION))
        assertTrue(types.contains(PredictionType.FERTILE_WINDOW_START))
        assertTrue(types.contains(PredictionType.FERTILE_WINDOW_END))
        assertTrue(types.contains(PredictionType.PMS_START))
        assertTrue(types.contains(PredictionType.PMS_END))
    }

    @Test
    fun `generatePredictions with insufficient cycles uses fallback`() {
        // Given: apenas 2 ciclos (menos que mínimo de 3)
        val cycles = mutableListOf<Cycle>()
        var start = LocalDate.of(2024, 1, 1)
        repeat(2) {
            cycles.add(createCycle(start, 28))
            start = start.plusDays(28)
        }
        
        // When
        val predictions = CyclePredictionEngine.generatePredictions(
            cycles = cycles,
            userId = "test-user",
            lastPeriodStart = start.minusDays(28),
            averagePeriodLength = 5,
            contraceptionMethod = "none"
        )
        
        // Then: deve usar fallback com ciclo de 28 dias
        assertFalse(predictions.isEmpty())
        val periodStarts = predictions.filter { it.type == PredictionType.PERIOD_START }
        assertTrue(periodStarts.all { it.confidenceScore < 0.5f }) // Baixa confiança
    }

    @Test
    fun `generatePredictions with pill contraception uses 28-day cycle`() {
        // Given: ciclos irregulares mas usa pílula
        val cycles = mutableListOf<Cycle>()
        val lengths = listOf(25, 31, 27, 29, 30, 28)
        var start = LocalDate.of(2024, 1, 1)
        lengths.forEach { length ->
            cycles.add(createCycle(start, length))
            start = start.plusDays(length)
        }
        
        // When
        val predictions = CyclePredictionEngine.generatePredictions(
            cycles = cycles,
            userId = "test-user",
            lastPeriodStart = start.minusDays(lengths.last()),
            averagePeriodLength = 5,
            contraceptionMethod = "pill"
        )
        
        // Then: deve prever ciclos de 28 dias (padrão da pílula)
        val periodStarts = predictions.filter { it.type == PredictionType.PERIOD_START }
        val predictedLengths = periodStarts.zipWithNext { a, b ->
            a.predictedDate.daysUntil(b.predictedDate).toInt()
        }
        assertTrue(predictedLengths.all { it == 28 })
    }

    @Test
    fun `generatePredictions with copper IUD increases period length`() {
        // Given: ciclos regulares com DIU de cobre
        val cycles = mutableListOf<Cycle>()
        var start = LocalDate.of(2024, 1, 1)
        repeat(6) {
            cycles.add(createCycle(start, 28, 5))
            start = start.plusDays(28)
        }
        
        // When
        val predictions = CyclePredictionEngine.generatePredictions(
            cycles = cycles,
            userId = "test-user",
            lastPeriodStart = start.minusDays(28),
            averagePeriodLength = 5,
            contraceptionMethod = "iud_copper"
        )
        
        // Then: deve aumentar duração da menstruação em 1 dia
        val periodEnds = predictions.filter { it.type == PredictionType.PERIOD_END }
        val periodStarts = predictions.filter { it.type == PredictionType.PERIOD_START }
        
        periodEnds.zip(periodStarts).forEach { (end, start) ->
            val periodLength = start.predictedDate.daysUntil(end.predictedDate).toInt() + 1
            assertEquals(6, periodLength) // 5 + 1 = 6
        }
    }

    @Test
    fun `confidenceScore increases with more cycles`() {
        // Given: diferentes quantidades de ciclos
        val cycles3 = mutableListOf<Cycle>()
        val cycles6 = mutableListOf<Cycle>()
        val cycles12 = mutableListOf<Cycle>()
        
        var start = LocalDate.of(2024, 1, 1)
        repeat(12) { i ->
            val cycle = createCycle(start, 28)
            if (i < 3) cycles3.add(cycle)
            if (i < 6) cycles6.add(cycle)
            cycles12.add(cycle)
            start = start.plusDays(28)
        }
        
        // When
        val preds3 = CyclePredictionEngine.generatePredictions(cycles3, "u", start.minusDays(28), 5, "none")
        val preds6 = CyclePredictionEngine.generatePredictions(cycles6, "u", start.minusDays(28), 5, "none")
        val preds12 = CyclePredictionEngine.generatePredictions(cycles12, "u", start.minusDays(28), 5, "none")
        
        // Then: mais ciclos = maior confiança
        val conf3 = preds3.first { it.type == PredictionType.PERIOD_START }.confidenceScore
        val conf6 = preds6.first { it.type == PredictionType.PERIOD_START }.confidenceScore
        val conf12 = preds12.first { it.type == PredictionType.PERIOD_START }.confidenceScore
        
        assertTrue(conf6 > conf3)
        assertTrue(conf12 >= conf6)
    }

    @Test
    fun `confidenceScore decreases with high variability`() {
        // Given: ciclos com alta variabilidade
        val cyclesVariable = mutableListOf<Cycle>()
        val lengths = listOf(21, 35, 24, 40, 26, 38) // Desvio padrão alto
        var start = LocalDate.of(2024, 1, 1)
        lengths.forEach { length ->
            cyclesVariable.add(createCycle(start, length))
            start = start.plusDays(length)
        }
        
        val cyclesRegular = mutableListOf<Cycle>()
        start = LocalDate.of(2024, 1, 1)
        repeat(6) {
            cyclesRegular.add(createCycle(start, 28))
            start = start.plusDays(28)
        }
        
        // When
        val predsVariable = CyclePredictionEngine.generatePredictions(cyclesVariable, "u", start.minusDays(lengths.last()), 5, "none")
        val predsRegular = CyclePredictionEngine.generatePredictions(cyclesRegular, "u", start.minusDays(28), 5, "none")
        
        // Then: ciclos regulares têm maior confiança
        val confVariable = predsVariable.first { it.type == PredictionType.PERIOD_START }.confidenceScore
        val confRegular = predsRegular.first { it.type == PredictionType.PERIOD_START }.confidenceScore
        
        assertTrue(confRegular > confVariable)
    }

    @Test
    fun `fertileWindow is 5 days before + ovulation day + 1 day after`() {
        // Given: ciclo de 28 dias
        val cycles = mutableListOf<Cycle>()
        var start = LocalDate.of(2024, 1, 1)
        repeat(6) {
            cycles.add(createCycle(start, 28))
            start = start.plusDays(28)
        }
        
        // When
        val predictions = CyclePredictionEngine.generatePredictions(cycles, "u", start.minusDays(28), 5, "none")
        
        // Then
        val fertileStart = predictions.first { it.type == PredictionType.FERTILE_WINDOW_START }
        val fertileEnd = predictions.first { it.type == PredictionType.FERTILE_WINDOW_END }
        val ovulation = predictions.first { it.type == PredictionType.OVULATION }
        
        // Ovulação no dia 14 (28 - 14 = 14)
        assertEquals(14, ovulation.predictedDate.dayOfMonth)
        // Janela fértil: dia 9 a 15 (5 antes + 1 dia + 1 depois)
        assertEquals(9, fertileStart.predictedDate.dayOfMonth)
        assertEquals(15, fertileEnd.predictedDate.dayOfMonth)
    }

    @Test
    fun `pmsWindow is 7 days before period`() {
        // Given: ciclo de 28 dias
        val cycles = mutableListOf<Cycle>()
        var start = LocalDate.of(2024, 1, 1)
        repeat(6) {
            cycles.add(createCycle(start, 28))
            start = start.plusDays(28)
        }
        
        // When
        val predictions = CyclePredictionEngine.generatePredictions(cycles, "u", start.minusDays(28), 5, "none")
        
        // Then
        val pmsStart = predictions.first { it.type == PredictionType.PMS_START }
        val pmsEnd = predictions.first { it.type == PredictionType.PMS_END }
        val periodStart = predictions.first { it.type == PredictionType.PERIOD_START }
        
        // TPM: 7 dias antes da menstruação
        assertEquals(7, pmsStart.predictedDate.daysUntil(periodStart.predictedDate).toInt())
        assertEquals(1, periodStart.predictedDate.daysUntil(pmsEnd.predictedDate).toInt())
    }

    @Test
    fun `isInFertileWindow detects current fertile window`() {
        // Given: previsões com janela fértil hoje
        val today = LocalDate.now()
        val ovulation = today.plusDays(1)
        val fertileStart = today.minusDays(4)
        val fertileEnd = today.plusDays(1)
        
        val predictions = listOf(
            Prediction.createFertileWindow("u", fertileStart, fertileEnd, 0.8f, 6, 0f, PredictionSource.LOCAL).first,
            Prediction.createFertileWindow("u", fertileStart, fertileEnd, 0.8f, 6, 0f, PredictionSource.LOCAL).second,
            Prediction.createOvulation("u", ovulation, 0.8f, 6, 0f, PredictionSource.LOCAL)
        )
        
        // When/Then
        assertTrue(CyclePredictionEngine.isInFertileWindow(predictions))
    }

    @Test
    fun `isInFertileWindow returns false outside window`() {
        // Given: previsões com janela fértil no futuro
        val today = LocalDate.now()
        val ovulation = today.plusDays(10)
        val fertileStart = today.plusDays(5)
        val fertileEnd = today.plusDays(11)
        
        val predictions = listOf(
            Prediction.createFertileWindow("u", fertileStart, fertileEnd, 0.8f, 6, 0f, PredictionSource.LOCAL).first,
            Prediction.createFertileWindow("u", fertileStart, fertileEnd, 0.8f, 6, 0f, PredictionSource.LOCAL).second,
            Prediction.createOvulation("u", ovulation, 0.8f, 6, 0f, PredictionSource.LOCAL)
        )
        
        // When/Then
        assertFalse(CyclePredictionEngine.isInFertileWindow(predictions))
    }

    @Test
    fun `isInPms detects current PMS`() {
        // Given: previsões com TPM hoje
        val today = LocalDate.now()
        val pmsStart = today.minusDays(3)
        val pmsEnd = today.plusDays(3)
        
        val predictions = listOf(
            Prediction(
                id = "1", userId = "u", type = PredictionType.PMS_START,
                predictedDate = pmsStart, confidenceScore = 0.7f,
                algorithmVersion = "1", cycleCountUsed = 6, standardDeviation = 0f,
                metadata = "{}", source = PredictionSource.LOCAL,
                expiresAt = pmsEnd.plusDays(2).atStartOfDay().toEpochMilliseconds()
            ),
            Prediction(
                id = "2", userId = "u", type = PredictionType.PMS_END,
                predictedDate = pmsEnd, confidenceScore = 0.7f,
                algorithmVersion = "1", cycleCountUsed = 6, standardDeviation = 0f,
                metadata = "{}", source = PredictionSource.LOCAL,
                expiresAt = pmsEnd.plusDays(2).atStartOfDay().toEpochMilliseconds()
            )
        )
        
        // When/Then
        assertTrue(CyclePredictionEngine.isInPms(predictions))
    }

    @Test
    fun `daysUntilNextPeriod calculates correctly`() {
        // Given: previsão de menstruação em 5 dias
        val today = LocalDate.now()
        val nextPeriod = today.plusDays(5)
        
        val predictions = listOf(
            Prediction.createPeriodStart("u", nextPeriod, 0.9f, 6, 0f, PredictionSource.LOCAL)
        )
        
        // When
        val days = CyclePredictionEngine.daysUntilNextPeriod(predictions)
        
        // Then
        assertEquals(5, days)
    }

    @Test
    fun `filterValidCycles excludes predicted cycles`() {
        // Given: mistura de ciclos reais e previstos
        val cycles = mutableListOf<Cycle>()
        val real = createCycle(LocalDate.of(2024, 1, 1), 28)
        val predicted = createCycle(LocalDate.of(2024, 2, 1), 28).copy(isPredicted = true)
        cycles.add(real)
        cycles.add(predicted)
        
        // When: usando reflexão para testar método privado
        // O método filterValidCycles é privado, mas testamos indiretamente
        val predictions = CyclePredictionEngine.generatePredictions(cycles, "u", LocalDate.of(2024, 2, 1), 5, "none")
        
        // Then: deve usar apenas 1 ciclo real (menos que mínimo de 3 -> fallback)
        assertTrue(predictions.first { it.type == PredictionType.PERIOD_START }.confidenceScore < 0.5f)
    }

    @Test
    fun `lutealPhaseDefaultsTo14WhenNotAvailable`() {
        // Given: ciclos sem data de ovulação
        val cycles = mutableListOf<Cycle>()
        var start = LocalDate.of(2024, 1, 1)
        repeat(6) {
            cycles.add(createCycle(start, 28).copy(ovulationDate = null))
            start = start.plusDays(28)
        }
        
        // When
        val predictions = CyclePredictionEngine.generatePredictions(cycles, "u", start.minusDays(28), 5, "none")
        
        // Then: deve usar fase lútea padrão de 14 dias
        val ovulation = predictions.first { it.type == PredictionType.OVULATION }
        val periodStart = predictions.first { it.type == PredictionType.PERIOD_START }
        
        // Ovulação = início do próximo período - 14 dias
        val expectedOvulation = periodStart.predictedDate.minusDays(14)
        assertEquals(expectedOvulation, ovulation.predictedDate)
    }
}