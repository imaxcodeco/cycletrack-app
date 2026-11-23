package com.seuapp.cycletrack.domain.usecase.cycle

import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.model.PredictionSource
import com.seuapp.cycletrack.domain.model.PredictionType
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.repository.PredictionRepository
import com.seuapp.cycletrack.domain.repository.UserProfileRepository
import com.seuapp.cycletrack.domain.algorithm.CyclePredictionEngine
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.mockito.Mockito.*

/**
 * Testes unitários para CycleUseCases.
 * Clean-room - valida lógica de negócio de ciclos.
 */
class CycleUseCasesTest {

    private val mockCycleRepository = mockk<CycleRepository>(relaxed = true)
    private val mockPredictionRepository = mockk<PredictionRepository>(relaxed = true)
    private val mockUserProfileRepository = mockk<UserProfileRepository>(relaxed = true)
    private val mockPredictionEngine = mockk<CyclePredictionEngine>(relaxed = true)

    private val useCases = CycleUseCases(
        cycleRepository = mockCycleRepository,
        predictionRepository = mockPredictionRepository,
        userProfileRepository = mockUserProfileRepository,
        predictionEngine = mockPredictionEngine
    )

    private val testUserId = "test-user"
    private val testCycleId = "test-cycle-id"

    @Test
    fun `logPeriodStart creates cycle and regenerates predictions`() = runBlocking {
        // Given
        val startDate = LocalDate.now()
        val profile = UserProfile(
            id = testUserId,
            email = "test@test.com",
            displayName = "Test",
            averageCycleLength = 28,
            averagePeriodLength = 5
        )
        val createdCycle = Cycle.createManual(testUserId, startDate, 5)
        
        every { mockUserProfileRepository.getProfile(testUserId) } returns profile
        every { mockCycleRepository.getLatestCycle(testUserId) } returns null
        every { mockCycleRepository.upsertCycle(any()) } answers { _, args -> args.first() as Cycle }
        every { mockPredictionEngine.generatePredictions(any(), any(), any(), any(), any()) } returns emptyList()
        
        // When
        val result = useCases.logPeriodStart(testUserId, startDate)
        
        // Then
        assertEquals(startDate, result.startDate)
        assertEquals(5, result.periodLength)
        assertEquals(28, result.cycleLength)
        verify(mockPredictionRepository).deleteAllPredictions(testUserId)
        verify(mockPredictionRepository).upsertPredictions(emptyList())
    }

    @Test
    fun `logPeriodStart uses previous cycle length`() = runBlocking {
        // Given
        val startDate = LocalDate.now()
        val previousCycle = Cycle.createManual(testUserId, startDate.minusDays(30), 5).copy(cycleLength = 30)
        val profile = UserProfile(
            id = testUserId,
            email = "test@test.com",
            displayName = "Test",
            averageCycleLength = 28,
            averagePeriodLength = 5
        )
        
        every { mockUserProfileRepository.getProfile(testUserId) } returns profile
        every { mockCycleRepository.getLatestCycle(testUserId) } returns previousCycle
        every { mockCycleRepository.upsertCycle(any()) } answers { _, args -> args.first() as Cycle }
        every { mockPredictionEngine.generatePredictions(any(), any(), any(), any(), any()) } returns emptyList()
        
        // When
        val result = useCases.logPeriodStart(testUserId, startDate)
        
        // Then
        assertEquals(30, result.cycleLength) // Deve usar ciclo anterior
    }

    @Test
    fun `logPeriodEnd updates cycle and regenerates predictions`() = runBlocking {
        // Given
        val startDate = LocalDate.now().minusDays(5)
        val endDate = LocalDate.now()
        val existingCycle = Cycle.createManual(testUserId, startDate, 5).copy(id = testCycleId)
        
        every { mockCycleRepository.getCycle(testCycleId) } returns existingCycle
        every { mockCycleRepository.upsertCycle(any()) } answers { _, args -> args.first() as Cycle }
        every { mockPredictionEngine.generatePredictions(any(), any(), any(), any(), any()) } returns emptyList()
        
        // When
        val result = useCases.logPeriodEnd(testUserId, testCycleId, endDate)
        
        // Then
        assertNotNull(result)
        assertEquals(endDate, result?.endDate)
        assertEquals(6, result?.periodLength) // 5 dias + 1 = 6
    }

    @Test
    fun `logPeriodEnd throws if cycle not found`() = runBlocking {
        // Given
        every { mockCycleRepository.getCycle(testCycleId) } returns null
        
        // When/Then
        assertThrows<IllegalArgumentException> {
            useCases.logPeriodEnd(testUserId, testCycleId, LocalDate.now())
        }
    }

    @Test
    fun `logPeriodEnd throws if unauthorized`() = runBlocking {
        // Given
        val existingCycle = Cycle.createManual("other-user", LocalDate.now().minusDays(5), 5).copy(id = testCycleId)
        every { mockCycleRepository.getCycle(testCycleId) } returns existingCycle
        
        // When/Then
        assertThrows<SecurityException> {
            useCases.logPeriodEnd(testUserId, testCycleId, LocalDate.now())
        }
    }

    @Test
    fun `getCurrentCycle returns current cycle`() = runBlocking {
        // Given
        val currentCycle = Cycle.createManual(testUserId, LocalDate.now().minusDays(2), 5)
        every { mockCycleRepository.getCycles(testUserId) } returns listOf(currentCycle).asSequence()
        
        // When
        val result = useCases.getCurrentCycle(testUserId)
        
        // Then
        assertNotNull(result)
        assertEquals(currentCycle.id, result?.id)
    }

    @Test
    fun `getCycleHistory returns limited cycles`() = runBlocking {
        // Given
        val cycles = (1..25).map { i ->
            Cycle.createManual(testUserId, LocalDate.now().minusMonths(i), 5)
        }
        every { mockCycleRepository.getCycles(testUserId) } returns cycles.asSequence()
        
        // When
        val result = useCases.getCycleHistory(testUserId, 20)
        
        // Then
        assertEquals(20, result.size)
    }

    @Test
    fun `deleteCycle soft deletes and regenerates predictions`() = runBlocking {
        // Given
        val cycle = Cycle.createManual(testUserId, LocalDate.now().minusDays(10), 5).copy(id = testCycleId)
        every { mockCycleRepository.getCycle(testCycleId) } returns cycle
        every { mockCycleRepository.deleteCycle(testCycleId) } returns true
        every { mockPredictionEngine.generatePredictions(any(), any(), any(), any(), any()) } returns emptyList()
        
        // When
        val result = useCases.deleteCycle(testUserId, testCycleId)
        
        // Then
        assertTrue(result)
        verify(mockCycleRepository).deleteCycle(testCycleId)
        verify(mockPredictionRepository).deleteAllPredictions(testUserId)
    }

    @Test
    fun `hardDeleteCycle permanently removes cycle`() = runBlocking {
        // Given
        val cycle = Cycle.createManual(testUserId, LocalDate.now().minusDays(10), 5).copy(id = testCycleId)
        every { mockCycleRepository.getCycle(testCycleId) } returns cycle
        every { mockCycleRepository.hardDeleteCycle(testCycleId) } returns true
        
        // When
        val result = useCases.hardDeleteCycle(testUserId, testCycleId)
        
        // Then
        assertTrue(result)
        verify(mockCycleRepository).hardDeleteCycle(testCycleId)
    }

    @Test
    fun `getUpcomingPredictions delegates to repository`() = runBlocking {
        // Given
        val predictions = listOf(
            Prediction.createPeriodStart(testUserId, LocalDate.now().plusDays(5), 0.9f, 6, 0f, PredictionSource.LOCAL)
        )
        every { mockPredictionRepository.getUpcomingPredictions(testUserId) } returns predictions
        
        // When
        val result = useCases.getUpcomingPredictions(testUserId)
        
        // Then
        assertEquals(predictions, result)
    }

    @Test
    fun `isInFertileWindowToday delegates to engine`() = runBlocking {
        // Given
        val predictions = listOf<Prediction>()
        every { mockPredictionRepository.getUpcomingPredictions(testUserId) } returns predictions
        every { mockPredictionEngine.isInFertileWindow(predictions) } returns true
        
        // When
        val result = useCases.isInFertileWindowToday(testUserId)
        
        // Then
        assertTrue(result)
    }

    @Test
    fun `isInPmsToday delegates to engine`() = runBlocking {
        // Given
        val predictions = listOf<Prediction>()
        every { mockPredictionRepository.getUpcomingPredictions(testUserId) } returns predictions
        every { mockPredictionEngine.isInPms(predictions) } returns true
        
        // When
        val result = useCases.isInPmsToday(testUserId)
        
        // Then
        assertTrue(result)
    }

    @Test
    fun `getDaysUntilNextPeriod delegates to engine`() = runBlocking {
        // Given
        val predictions = listOf<Prediction>()
        every { mockPredictionRepository.getUpcomingPredictions(testUserId) } returns predictions
        every { mockPredictionEngine.daysUntilNextPeriod(predictions) } returns 5
        
        // When
        val result = useCases.getDaysUntilNextPeriod(testUserId)
        
        // Then
        assertEquals(5, result)
    }

    @Test
    fun `forceRegeneratePredictions calls engine and repository`() = runBlocking {
        // Given
        val cycles = listOf(Cycle.createManual(testUserId, LocalDate.now().minusDays(28), 5))
        val profile = UserProfile(id = testUserId, email = "test@test.com", displayName = "Test", averageCycleLength = 28, averagePeriodLength = 5, lastPeriodDate = LocalDate.now().minusDays(28))
        val predictions = listOf(Prediction.createPeriodStart(testUserId, LocalDate.now().plusDays(5), 0.9f, 6, 0f, PredictionSource.LOCAL))
        
        every { mockCycleRepository.getCycles(testUserId) } returns cycles.asSequence()
        every { mockUserProfileRepository.getProfile(testUserId) } returns profile
        every { mockPredictionEngine.generatePredictions(any(), any(), any(), any(), any()) } returns predictions
        
        // When
        val result = useCases.forceRegeneratePredictions(testUserId)
        
        // Then
        assertEquals(predictions, result)
        verify(mockPredictionRepository).deleteAllPredictions(testUserId)
        verify(mockPredictionRepository).upsertPredictions(predictions)
    }
}