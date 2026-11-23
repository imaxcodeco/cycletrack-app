package com.seuapp.cycletrack.ui.insights

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.presentation.state.InsightsViewModel
import com.seuapp.cycletrack.presentation.ui.insights.InsightsScreen
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
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class InsightsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Mock
    lateinit var viewModel: InsightsViewModel

    private lateinit var context: Context

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext

        val cycleStats = InsightsViewModel.CycleStatistics(
            averageLength = 28.5,
            medianLength = 28,
            minLength = 24,
            maxLength = 35,
            standardDeviation = 2.3,
            regularityScore = 0.85,
            totalCycles = 12,
            cyclesUsedForCalculation = 10
        )

        val periodStats = InsightsViewModel.PeriodStatistics(
            averageLength = 5.2,
            medianLength = 5,
            minLength = 3,
            maxLength = 7,
            averageFlowScore = 2.1,
            totalPeriods = 12
        )

        val fertilityInsights = InsightsViewModel.FertilityInsights(
            averageOvulationDay = 14,
            averageFertileWindowStart = 10,
            averageFertileWindowEnd = 16,
            lutealPhaseLength = 14,
            cycleLengthVariability = 2.3,
            conceptionProbability = 0.25
        )

        val symptomPatterns = InsightsViewModel.SymptomPatterns(
            mostCommonSymptoms = listOf(
                InsightsViewModel.SymptomFrequency(Symptom.Type.CRAMPS, 45),
                InsightsViewModel.SymptomFrequency(Symptom.Type.HEADACHE, 32),
                InsightsViewModel.SymptomFrequency(Symptom.Type.FATIGUE, 28),
                InsightsViewModel.SymptomFrequency(Symptom.Type.BLOATING, 25),
                InsightsViewModel.SymptomFrequency(Symptom.Type.MOOD_SWINGS, 22)
            ),
            symptomsByPhase = mapOf(
                InsightsViewModel.Phase.MENSTRUAL to listOf(
                    InsightsViewModel.SymptomFrequency(Symptom.Type.CRAMPS, 40),
                    InsightsViewModel.SymptomFrequency(Symptom.Type.FATIGUE, 35),
                    InsightsViewModel.SymptomFrequency(Symptom.Type.BLOATING, 30)
                ),
                InsightsViewModel.Phase.FOLLICULAR to listOf(
                    InsightsViewModel.SymptomFrequency(Symptom.Type.HEADACHE, 20),
                    InsightsViewModel.SymptomFrequency(Symptom.Type.ENERGY_INCREASE, 18)
                ),
                InsightsViewModel.Phase.OVULATORY to listOf(
                    InsightsViewModel.SymptomFrequency(Symptom.Type.OVULATION_PAIN, 25),
                    InsightsViewModel.SymptomFrequency(Symptom.Type.LIBIDO_INCREASE, 22)
                ),
                InsightsViewModel.Phase.LUTEAL to listOf(
                    InsightsViewModel.SymptomFrequency(Symptom.Type.MOOD_SWINGS, 38),
                    InsightsViewModel.SymptomFrequency(Symptom.Type.BREAST_TENDERNESS, 35),
                    InsightsViewModel.SymptomFrequency(Symptom.Type.ACNE, 20)
                )
            ),
            pmsSymptoms = listOf(
                InsightsViewModel.SymptomFrequency(Symptom.Type.MOOD_SWINGS, 38),
                InsightsViewModel.SymptomFrequency(Symptom.Type.BREAST_TENDERNESS, 35),
                InsightsViewModel.SymptomFrequency(Symptom.Type.IRRITABILITY, 30),
                InsightsViewModel.SymptomFrequency(Symptom.Type.FATIGUE, 28)
            )
        )

        val trends = InsightsViewModel.Trends(
            cycleLengthTrend = InsightsViewModel.TrendDirection.STABLE,
            periodLengthTrend = InsightsViewModel.TrendDirection.STABLE,
            symptomTrend = InsightsViewModel.TrendDirection.IMPROVING,
            weightTrend = InsightsViewModel.TrendDirection.STABLE,
            temperatureTrend = InsightsViewModel.TrendDirection.STABLE
        )

        val uiState = InsightsViewModel.UiState(
            cycleStatistics = cycleStats,
            periodStatistics = periodStats,
            fertilityInsights = fertilityInsights,
            symptomPatterns = symptomPatterns,
            trends = trends,
            isLoading = false,
            error = null,
            lastCalculated = LocalDate.now()
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(uiState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = uiState
            )
        )
        whenever(viewModel.cycleStatistics).thenReturn(cycleStats)
        whenever(viewModel.periodStatistics).thenReturn(periodStats)
        whenever(viewModel.fertilityInsights).thenReturn(fertilityInsights)
        whenever(viewModel.symptomPatterns).thenReturn(symptomPatterns)
        whenever(viewModel.trends).thenReturn(trends)
    }

    @Test
    fun testInsightsScreenRendersCycleStats() {
        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithText("Estatísticas do ciclo").assertExists()
        composeRule.onNodeWithText("Média: 28,5 dias").assertExists()
        composeRule.onNodeWithText("Regularidade: 85%").assertExists()
        composeRule.onNodeWithText("Total de ciclos: 12").assertExists()
    }

    @Test
    fun testInsightsScreenRendersPeriodStats() {
        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithText("Estatísticas do período").assertExists()
        composeRule.onNodeWithText("Média: 5,2 dias").assertExists()
        composeRule.onNodeWithText("Fluxo médio: 2,1").assertExists()
    }

    @Test
    fun testInsightsScreenRendersFertilityInsights() {
        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithText("Insights de fertilidade").assertExists()
        composeRule.onNodeWithText("Ovulação média: Dia 14").assertExists()
        composeRule.onNodeWithText("Fase lútea: 14 dias").assertExists()
        composeRule.onNodeWithText("Probabilidade de concepção: 25%").assertExists()
    }

    @Test
    fun testInsightsScreenRendersTopSymptoms() {
        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithText("Sintomas mais frequentes").assertExists()
        composeRule.onNodeWithText("Cólicas (45)").assertExists()
        composeRule.onNodeWithText("Dor de cabeça (32)").assertExists()
        composeRule.onNodeWithText("Fadiga (28)").assertExists()
    }

    @Test
    fun testInsightsScreenRendersPhaseDistribution() {
        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithText("Distribuição por fase").assertExists()
        composeRule.onNodeWithText("Menstrual").assertExists()
        composeRule.onNodeWithText("Folicular").assertExists()
        composeRule.onNodeWithText("Ovulatório").assertExists()
        composeRule.onNodeWithText("Lútea").assertExists()
    }

    @Test
    fun testInsightsScreenRendersTrends() {
        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithText("Tendências").assertExists()
        composeRule.onNodeWithText("Estável").assertExists()
        composeRule.onNodeWithText("Melhorando").assertExists()
    }

    @Test
    fun testInsightsScreenChartBars() {
        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithTag("cycle_length_chart").assertExists()
    }

    @Test
    fun testInsightsScreenGenerateReport() {
        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithText("Gerar relatório completo").performClick()
        verify(viewModel).generateReport()
    }

    @Test
    fun testInsightsScreenLoadingState() {
        val loadingState = InsightsViewModel.UiState(
            cycleStatistics = InsightsViewModel.CycleStatistics(
                averageLength = 0.0,
                medianLength = 0,
                minLength = 0,
                maxLength = 0,
                standardDeviation = 0.0,
                regularityScore = 0.0,
                totalCycles = 0,
                cyclesUsedForCalculation = 0
            ),
            periodStatistics = InsightsViewModel.PeriodStatistics(
                averageLength = 0.0,
                medianLength = 0,
                minLength = 0,
                maxLength = 0,
                averageFlowScore = 0.0,
                totalPeriods = 0
            ),
            fertilityInsights = InsightsViewModel.FertilityInsights(
                averageOvulationDay = 0,
                averageFertileWindowStart = 0,
                averageFertileWindowEnd = 0,
                lutealPhaseLength = 0,
                cycleLengthVariability = 0.0,
                conceptionProbability = 0.0
            ),
            symptomPatterns = InsightsViewModel.SymptomPatterns(
                mostCommonSymptoms = emptyList(),
                symptomsByPhase = emptyMap(),
                pmsSymptoms = emptyList()
            ),
            trends = InsightsViewModel.Trends(
                cycleLengthTrend = InsightsViewModel.TrendDirection.STABLE,
                periodLengthTrend = InsightsViewModel.TrendDirection.STABLE,
                symptomTrend = InsightsViewModel.TrendDirection.STABLE,
                weightTrend = InsightsViewModel.TrendDirection.STABLE,
                temperatureTrend = InsightsViewModel.TrendDirection.STABLE
            ),
            isLoading = true,
            error = null,
            lastCalculated = LocalDate.now()
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(loadingState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = loadingState
            )
        )

        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithTag("loading_indicator").assertExists()
    }

    @Test
    fun testInsightsScreenErrorState() {
        val errorState = InsightsViewModel.UiState(
            cycleStatistics = InsightsViewModel.CycleStatistics(
                averageLength = 0.0,
                medianLength = 0,
                minLength = 0,
                maxLength = 0,
                standardDeviation = 0.0,
                regularityScore = 0.0,
                totalCycles = 0,
                cyclesUsedForCalculation = 0
            ),
            periodStatistics = InsightsViewModel.PeriodStatistics(
                averageLength = 0.0,
                medianLength = 0,
                minLength = 0,
                maxLength = 0,
                averageFlowScore = 0.0,
                totalPeriods = 0
            ),
            fertilityInsights = InsightsViewModel.FertilityInsights(
                averageOvulationDay = 0,
                averageFertileWindowStart = 0,
                averageFertileWindowEnd = 0,
                lutealPhaseLength = 0,
                cycleLengthVariability = 0.0,
                conceptionProbability = 0.0
            ),
            symptomPatterns = InsightsViewModel.SymptomPatterns(
                mostCommonSymptoms = emptyList(),
                symptomsByPhase = emptyMap(),
                pmsSymptoms = emptyList()
            ),
            trends = InsightsViewModel.Trends(
                cycleLengthTrend = InsightsViewModel.TrendDirection.STABLE,
                periodLengthTrend = InsightsViewModel.TrendDirection.STABLE,
                symptomTrend = InsightsViewModel.TrendDirection.STABLE,
                weightTrend = InsightsViewModel.TrendDirection.STABLE,
                temperatureTrend = InsightsViewModel.TrendDirection.STABLE
            ),
            isLoading = false,
            error = "Dados insuficientes para calcular insights",
            lastCalculated = LocalDate.now()
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(errorState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = errorState
            )
        )

        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithText("Dados insuficientes para calcular insights").assertExists()
    }

    @Test
    fun testInsightsScreenEmptyState() {
        val emptyState = InsightsViewModel.UiState(
            cycleStatistics = InsightsViewModel.CycleStatistics(
                averageLength = 0.0,
                medianLength = 0,
                minLength = 0,
                maxLength = 0,
                standardDeviation = 0.0,
                regularityScore = 0.0,
                totalCycles = 0,
                cyclesUsedForCalculation = 0
            ),
            periodStatistics = InsightsViewModel.PeriodStatistics(
                averageLength = 0.0,
                medianLength = 0,
                minLength = 0,
                maxLength = 0,
                averageFlowScore = 0.0,
                totalPeriods = 0
            ),
            fertilityInsights = InsightsViewModel.FertilityInsights(
                averageOvulationDay = 0,
                averageFertileWindowStart = 0,
                averageFertileWindowEnd = 0,
                lutealPhaseLength = 0,
                cycleLengthVariability = 0.0,
                conceptionProbability = 0.0
            ),
            symptomPatterns = InsightsViewModel.SymptomPatterns(
                mostCommonSymptoms = emptyList(),
                symptomsByPhase = emptyMap(),
                pmsSymptoms = emptyList()
            ),
            trends = InsightsViewModel.Trends(
                cycleLengthTrend = InsightsViewModel.TrendDirection.STABLE,
                periodLengthTrend = InsightsViewModel.TrendDirection.STABLE,
                symptomTrend = InsightsViewModel.TrendDirection.STABLE,
                weightTrend = InsightsViewModel.TrendDirection.STABLE,
                temperatureTrend = InsightsViewModel.TrendDirection.STABLE
            ),
            isLoading = false,
            error = null,
            lastCalculated = LocalDate.now()
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(emptyState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = emptyState
            )
        )

        composeRule.setContent {
            InsightsScreen(viewModel = viewModel, onNavigateToExport = {})
        }

        composeRule.onNodeWithText("Registre mais ciclos para ver insights").assertExists()
    }
}