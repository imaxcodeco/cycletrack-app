package com.seuapp.cycletrack.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Valida que os cálculos de ovulação e janela fértil de [Cycle.createManual] seguem
 * o padrão clínico ACOG/ASRM.
 *
 * Referência: ASRM, "Optimizing natural fertility: a committee opinion" — a janela fértil
 * é "o intervalo de 6 dias terminando no dia da ovulação", e os exemplos publicados são:
 * ciclo de 28 dias -> dias 9–14; ciclo de 30 dias -> dias 11–16. A fase lútea é tratada
 * como ~14 dias constantes (padrão ACOG), independente do comprimento total do ciclo.
 *
 * Convenção: startDate é o dia 1 do ciclo (1º dia de menstruação), então o dia de ciclo N
 * corresponde a startDate + (N-1) dias.
 */
class CycleClinicalAccuracyTest {

    private val start = LocalDate.of(2026, 1, 1)

    private fun cycleDayOf(date: LocalDate): Long =
        ChronoUnit.DAYS.between(start, date) + 1

    @Test
    fun `ciclo de 28 dias - ovulacao no dia 14 e janela fertil dias 9 a 14 (ASRM)`() {
        val cycle = Cycle.createManual(
            userId = "u",
            startDate = start,
            periodLength = 5,
            previousCycleLength = 28
        )

        assertEquals(14L, cycleDayOf(cycle.ovulationDate!!))
        assertEquals(9L, cycleDayOf(cycle.fertileWindowStart!!))
        assertEquals(14L, cycleDayOf(cycle.fertileWindowEnd!!))
    }

    @Test
    fun `ciclo de 30 dias - ovulacao no dia 16 e janela fertil dias 11 a 16 (ASRM)`() {
        val cycle = Cycle.createManual(
            userId = "u",
            startDate = start,
            periodLength = 5,
            previousCycleLength = 30
        )

        assertEquals(16L, cycleDayOf(cycle.ovulationDate!!))
        assertEquals(11L, cycleDayOf(cycle.fertileWindowStart!!))
        assertEquals(16L, cycleDayOf(cycle.fertileWindowEnd!!))
    }

    @Test
    fun `ciclo longo de 35 dias - ovulacao no dia 21, nao no 'meio' do ciclo`() {
        val cycle = Cycle.createManual(
            userId = "u",
            startDate = start,
            periodLength = 5,
            previousCycleLength = 35
        )

        // 35 - 14 = dia 21. O cálculo antigo (cycleLength / 2) daria ~dia 17, clinicamente errado.
        assertEquals(21L, cycleDayOf(cycle.ovulationDate!!))
        assertEquals(16L, cycleDayOf(cycle.fertileWindowStart!!))
        assertEquals(21L, cycleDayOf(cycle.fertileWindowEnd!!))
    }

    @Test
    fun `janela fertil tem exatamente 6 dias terminando na ovulacao (ASRM)`() {
        val cycle = Cycle.createManual(
            userId = "u",
            startDate = start,
            periodLength = 4,
            previousCycleLength = 28
        )

        val windowLength = ChronoUnit.DAYS.between(
            cycle.fertileWindowStart!!,
            cycle.fertileWindowEnd!!
        ) + 1
        assertEquals(6L, windowLength)
        assertEquals(cycle.ovulationDate, cycle.fertileWindowEnd)
    }

    @Test
    fun `default de cicloLength ausente usa 28 dias (padrao clinico)`() {
        val cycle = Cycle.createManual(
            userId = "u",
            startDate = start,
            periodLength = 5
        )

        assertEquals(28, cycle.cycleLength)
        assertEquals(14, cycle.lutealPhaseLength)
        assertEquals(14L, cycleDayOf(cycle.ovulationDate!!))
    }
}
