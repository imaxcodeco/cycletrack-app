package com.seuapp.cycletrack.presentation.viewmodel

import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.repository.SymptomRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * Valida o fluxo de registrar menstruação (SymptomViewModel.logPeriodStart): criar o
 * primeiro ciclo, avançar fechando o ciclo anterior com a duração real, deduplicar
 * registros dentro do mesmo período e registrar ciclos passados (backfill).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SymptomViewModelPeriodTest {

    private val dispatcher = StandardTestDispatcher()
    private val symptomRepository = mockk<SymptomRepository>(relaxed = true)
    private val cycleRepository = mockk<CycleRepository>(relaxed = true)
    private lateinit var viewModel: SymptomViewModel

    private val userId = "user_123"
    private val start = LocalDate.of(2026, 1, 1)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = SymptomViewModel(symptomRepository, cycleRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sem ciclos - cria o primeiro ciclo na data informada`() = runTest(dispatcher) {
        coEvery { cycleRepository.getRecentCycles(userId, any()) } returns emptyList()
        val saved = slot<Cycle>()
        coEvery { cycleRepository.upsertCycle(capture(saved)) } returns Unit

        viewModel.logPeriodStart(start)
        advanceUntilIdle()

        assertEquals(start, saved.captured.startDate)
    }

    @Test
    fun `nova menstruacao - fecha ciclo anterior com duracao real e cria novo`() = runTest(dispatcher) {
        val previous = Cycle.createManual(userId, start, periodLength = 5, previousCycleLength = 28)
        val newStart = start.plusDays(30) // ciclo real de 30 dias, não os 28 estimados
        coEvery { cycleRepository.getRecentCycles(userId, any()) } returns listOf(previous)
        val saved = mutableListOf<Cycle>()
        coEvery { cycleRepository.upsertCycle(capture(saved)) } returns Unit

        viewModel.logPeriodStart(newStart)
        advanceUntilIdle()

        // Grava 2 ciclos: o anterior fechado + o novo.
        assertEquals(2, saved.size)
        val closedPrevious = saved.first { it.id == previous.id }
        val newCycle = saved.first { it.id != previous.id }
        assertEquals(30, closedPrevious.cycleLength) // duração real observada
        assertEquals(newStart, newCycle.startDate)
    }

    @Test
    fun `registro dentro do mesmo periodo - nao cria ciclo duplicado`() = runTest(dispatcher) {
        val previous = Cycle.createManual(userId, start, periodLength = 5, previousCycleLength = 28)
        coEvery { cycleRepository.getRecentCycles(userId, any()) } returns listOf(previous)

        // 3 dias depois do início: ainda é a mesma menstruação (abaixo do gap mínimo).
        viewModel.logPeriodStart(start.plusDays(3))
        advanceUntilIdle()

        coVerify(exactly = 0) { cycleRepository.upsertCycle(any()) }
    }

    @Test
    fun `data anterior ao ciclo atual - registra ciclo passado (backfill)`() = runTest(dispatcher) {
        val current = Cycle.createManual(userId, start, periodLength = 5, previousCycleLength = 28)
        val pastStart = start.minusDays(29)
        coEvery { cycleRepository.getRecentCycles(userId, any()) } returns listOf(current)
        val saved = slot<Cycle>()
        coEvery { cycleRepository.upsertCycle(capture(saved)) } returns Unit

        viewModel.logPeriodStart(pastStart)
        advanceUntilIdle()

        assertEquals(pastStart, saved.captured.startDate)
        assertTrue(saved.captured.id != current.id)
        assertEquals(29, saved.captured.cycleLength) // intervalo até o próximo início conhecido
    }
}
