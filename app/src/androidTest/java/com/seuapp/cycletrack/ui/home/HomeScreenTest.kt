package com.seuapp.cycletrack.ui.home

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.presentation.state.HomeViewModel
import com.seuapp.cycletrack.presentation.ui.home.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Mock
    lateinit var viewModel: HomeViewModel

    private lateinit var context: Context

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext

        val currentCycle = Cycle(
            id = "cycle_1",
            userId = "user_1",
            startDate = LocalDate.now().minusDays(5),
            endDate = null,
            cycleLength = 28,
            periodLength = 5,
            isPredicted = false,
            symptoms = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val predictions = listOf(
            Prediction(
                id = "pred_1",
                userId = "user_1",
                cycleId = "cycle_1",
                predictedStartDate = LocalDate.now().plusDays(23),
                predictedEndDate = LocalDate.now().plusDays(27),
                predictedOvulationDate = LocalDate.now().plusDays(16),
                fertileWindowStart = LocalDate.now().plusDays(14),
                fertileWindowEnd = LocalDate.now().plusDays(18),
                confidence = 0.85,
                algorithmVersion = "1.0",
                createdAt = LocalDateTime.now()
            )
        )

        val symptoms = listOf(
            Symptom(
                id = "symptom_1",
                userId = "user_1",
                date = LocalDate.now(),
                type = Symptom.Type.CRAMPS,
                severity = Symptom.Severity.MODERATE,
                notes = "Cólicas leves",
                createdAt = LocalDateTime.now()
            )
        )

        val uiState = HomeViewModel.UiState(
            currentCycle = currentCycle,
            predictions = predictions,
            todaySymptoms = symptoms,
            pregnancyStatus = null,
            partnerStatus = null,
            cycleDay = 6,
            currentPhase = HomeViewModel.Phase.FOLLICULAR,
            cycleDayText = "Dia 6 do ciclo",
            isLoading = false,
            error = null,
            quickActions = listOf(
                HomeViewModel.QuickAction("Registrar período", "period", true),
                HomeViewModel.QuickAction("Registrar sintomas", "symptoms", true),
                HomeViewModel.QuickAction("Ver calendário", "calendar", true)
            )
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(uiState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = uiState
            )
        )
        whenever(viewModel.cycleDayText).thenReturn("Dia 6 do ciclo")
        whenever(viewModel.currentPhase).thenReturn(HomeViewModel.Phase.FOLLICULAR)
        whenever(viewModel.quickActions).thenReturn(uiState.quickActions)
    }

    @Test
    fun testHomeScreenRendersCycleCard() {
        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithText("Dia 6 do ciclo").assertExists()
        composeRule.onNodeWithText("Fase folicular").assertExists()
    }

    @Test
    fun testHomeScreenRendersPredictions() {
        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithText("Próxima menstruação").assertExists()
        composeRule.onNodeWithText("Janela fértil").assertExists()
        composeRule.onNodeWithText("Ovulação prevista").assertExists()
    }

    @Test
    fun testHomeScreenRendersQuickActions() {
        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithText("Registrar período").assertExists()
        composeRule.onNodeWithText("Registrar sintomas").assertExists()
        composeRule.onNodeWithText("Ver calendário").assertExists()
    }

    @Test
    fun testHomeScreenLogPeriodStart() {
        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithText("Registrar período").performClick()
        verify(viewModel).logPeriodStart()
    }

    @Test
    fun testHomeScreenNavigateToCalendar() {
        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithText("Ver calendário").performClick()
    }

    @Test
    fun testHomeScreenRendersSymptoms() {
        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithText("Cólicas").assertExists()
        composeRule.onNodeWithText("Moderado").assertExists()
    }

    @Test
    fun testHomeScreenLoadingState() {
        val loadingState = HomeViewModel.UiState(
            currentCycle = null,
            predictions = emptyList(),
            todaySymptoms = emptyList(),
            pregnancyStatus = null,
            partnerStatus = null,
            cycleDay = 0,
            currentPhase = HomeViewModel.Phase.UNKNOWN,
            cycleDayText = "",
            isLoading = true,
            error = null,
            quickActions = emptyList()
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(loadingState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = loadingState
            )
        )

        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithTag("loading_indicator").assertExists()
    }

    @Test
    fun testHomeScreenErrorState() {
        val errorState = HomeViewModel.UiState(
            currentCycle = null,
            predictions = emptyList(),
            todaySymptoms = emptyList(),
            pregnancyStatus = null,
            partnerStatus = null,
            cycleDay = 0,
            currentPhase = HomeViewModel.Phase.UNKNOWN,
            cycleDayText = "",
            isLoading = false,
            error = "Erro ao carregar dados",
            quickActions = emptyList()
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(errorState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = errorState
            )
        )

        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithText("Erro ao carregar dados").assertExists()
        composeRule.onNodeWithText("Tentar novamente").assertExists()
    }

    @Test
    fun testHomeScreenEmptyState() {
        val emptyState = HomeViewModel.UiState(
            currentCycle = null,
            predictions = emptyList(),
            todaySymptoms = emptyList(),
            pregnancyStatus = null,
            partnerStatus = null,
            cycleDay = 0,
            currentPhase = HomeViewModel.Phase.UNKNOWN,
            cycleDayText = "Sem dados de ciclo",
            isLoading = false,
            error = null,
            quickActions = listOf(
                HomeViewModel.QuickAction("Registrar primeiro período", "period", true)
            )
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(emptyState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = emptyState
            )
        )
        whenever(viewModel.cycleDayText).thenReturn("Sem dados de ciclo")
        whenever(viewModel.quickActions).thenReturn(emptyState.quickActions)

        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithText("Sem dados de ciclo").assertExists()
        composeRule.onNodeWithText("Registrar primeiro período").assertExists()
    }

    @Test
    fun testHomeScreenPregnancyMode() {
        val pregnancyStatus = HomeViewModel.PregnancyStatus(
            isPregnant = true,
            weeksPregnant = 12,
            dueDate = LocalDate.now().plusMonths(6),
            babySize = "Ameixa",
            babyWeight = 14.0
        )

        val pregnancyState = HomeViewModel.UiState(
            currentCycle = null,
            predictions = emptyList(),
            todaySymptoms = emptyList(),
            pregnancyStatus = pregnancyStatus,
            partnerStatus = null,
            cycleDay = 0,
            currentPhase = HomeViewModel.Phase.UNKNOWN,
            cycleDayText = "12 semanas de gestação",
            isLoading = false,
            error = null,
            quickActions = listOf(
                HomeViewModel.QuickAction("Ver gravidez", "pregnancy", true)
            )
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(pregnancyState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = pregnancyState
            )
        )
        whenever(viewModel.cycleDayText).thenReturn("12 semanas de gestação")
        whenever(viewModel.quickActions).thenReturn(pregnancyState.quickActions)

        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithText("12 semanas de gestação").assertExists()
        composeRule.onNodeWithText("Ameixa").assertExists()
        composeRule.onNodeWithText("Ver gravidez").assertExists()
    }

    @Test
    fun testHomeScreenPartnerStatus() {
        val partnerStatus = HomeViewModel.PartnerStatus(
            isConnected = true,
            partnerName = "João",
            sharingLevel = HomeViewModel.SharingLevel.FULL,
            lastSync = LocalDateTime.now().minusHours(2)
        )

        val partnerState = HomeViewModel.UiState(
            currentCycle = Cycle(
                id = "cycle_1",
                userId = "user_1",
                startDate = LocalDate.now().minusDays(5),
                endDate = null,
                cycleLength = 28,
                periodLength = 5,
                isPredicted = false,
                symptoms = emptyList(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            predictions = emptyList(),
            todaySymptoms = emptyList(),
            pregnancyStatus = null,
            partnerStatus = partnerStatus,
            cycleDay = 6,
            currentPhase = HomeViewModel.Phase.FOLLICULAR,
            cycleDayText = "Dia 6 do ciclo",
            isLoading = false,
            error = null,
            quickActions = listOf(
                HomeViewModel.QuickAction("Ver parceiro", "partner", true)
            )
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(partnerState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = partnerState
            )
        )
        whenever(viewModel.quickActions).thenReturn(partnerState.quickActions)

        composeRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToCalendar = {}, onNavigateToLogging = {}, onNavigateToInsights = {})
        }

        composeRule.onNodeWithText("Parceiro conectado: João").assertExists()
        composeRule.onNodeWithText("Ver parceiro").assertExists()
    }
}