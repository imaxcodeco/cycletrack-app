package com.seuapp.cycletrack.ui.logging

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.presentation.state.LoggingViewModel
import com.seuapp.cycletrack.presentation.ui.logging.LoggingScreen
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
class LoggingScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Mock
    lateinit var viewModel: LoggingViewModel

    private lateinit var context: Context

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext

        val periodState = LoggingViewModel.PeriodState(
            startDate = LocalDate.now().minusDays(2),
            endDate = LocalDate.now(),
            flowLevel = Symptom.FlowLevel.MEDIUM,
            isLoading = false,
            error = null
        )

        val symptomsState = LoggingViewModel.SymptomsState(
            selectedSymptoms = mapOf(
                Symptom.Type.CRAMPS to LoggingViewModel.SymptomEntry(
                    type = Symptom.Type.CRAMPS,
                    severity = Symptom.Severity.MODERATE,
                    notes = "Cólicas moderadas"
                ),
                Symptom.Type.HEADACHE to LoggingViewModel.SymptomEntry(
                    type = Symptom.Type.HEADACHE,
                    severity = Symptom.Severity.MILD,
                    notes = ""
                )
            ),
            availableSymptoms = Symptom.Type.values().toList(),
            isLoading = false,
            error = null
        )

        val weightState = LoggingViewModel.WeightState(
            weightKg = 60.5,
            date = LocalDate.now(),
            unit = LoggingViewModel.WeightUnit.KG,
            isLoading = false,
            error = null
        )

        val temperatureState = LoggingViewModel.TemperatureState(
            temperatureCelsius = 36.5,
            date = LocalDate.now(),
            time = LocalDateTime.now().toLocalTime(),
            unit = LoggingViewModel.TemperatureUnit.CELSIUS,
            isLoading = false,
            error = null
        )

        val moodState = LoggingViewModel.MoodState(
            selectedMood = LoggingViewModel.Mood.HAPPY,
            notes = "Dia ótimo!",
            isLoading = false,
            error = null
        )

        val uiState = LoggingViewModel.UiState(
            currentTab = LoggingViewModel.LoggingTab.PERIOD,
            periodState = periodState,
            symptomsState = symptomsState,
            weightState = weightState,
            temperatureState = temperatureState,
            moodState = moodState,
            isSaving = false,
            saveError = null
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(uiState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = uiState
            )
        )
        whenever(viewModel.currentTab).thenReturn(LoggingViewModel.LoggingTab.PERIOD)
        whenever(viewModel.periodState).thenReturn(periodState)
        whenever(viewModel.symptomsState).thenReturn(symptomsState)
        whenever(viewModel.weightState).thenReturn(weightState)
        whenever(viewModel.temperatureState).thenReturn(temperatureState)
        whenever(viewModel.moodState).thenReturn(moodState)
    }

    @Test
    fun testLoggingScreenRendersPeriodTab() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Período").assertExists()
        composeRule.onNodeWithText("Sintomas").assertExists()
        composeRule.onNodeWithText("Peso").assertExists()
        composeRule.onNodeWithText("Temperatura").assertExists()
        composeRule.onNodeWithText("Humor").assertExists()
    }

    @Test
    fun testLoggingScreenPeriodTabStartDate() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Início do período").assertExists()
        composeRule.onNodeWithText("Selecionar data").performClick()
        verify(viewModel).updatePeriodStartDate(any())
    }

    @Test
    fun testLoggingScreenPeriodTabEndDate() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Fim do período").assertExists()
        composeRule.onNodeWithText("Selecionar data").performClick()
        verify(viewModel).updatePeriodEndDate(any())
    }

    @Test
    fun testLoggingScreenPeriodTabFlowLevel() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Leve").performClick()
        verify(viewModel).updatePeriodFlowLevel(Symptom.FlowLevel.LIGHT)

        composeRule.onNodeWithText("Médio").performClick()
        verify(viewModel).updatePeriodFlowLevel(Symptom.FlowLevel.MEDIUM)

        composeRule.onNodeWithText("Intenso").performClick()
        verify(viewModel).updatePeriodFlowLevel(Symptom.FlowLevel.HEAVY)
    }

    @Test
    fun testLoggingScreenPeriodTabSave() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Salvar período").performClick()
        verify(viewModel).savePeriod()
    }

    @Test
    fun testLoggingScreenSymptomsTab() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Sintomas").performClick()

        composeRule.onNodeWithText("Cólicas").assertExists()
        composeRule.onNodeWithText("Dor de cabeça").assertExists()
    }

    @Test
    fun testLoggingScreenSymptomSelection() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Sintomas").performClick()

        composeRule.onNodeWithText("Cólicas").performClick()
        verify(viewModel).toggleSymptom(Symptom.Type.CRAMPS)

        composeRule.onNodeWithText("Moderado").performClick()
        verify(viewModel).updateSymptomSeverity(Symptom.Type.CRAMPS, Symptom.Severity.MODERATE)
    }

    @Test
    fun testLoggingScreenSymptomNotes() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Sintomas").performClick()
        composeRule.onNodeWithText("Cólicas").performClick()

        composeRule.onNodeWithText("Observações").performTextInput("Cólicas fortes hoje")
        verify(viewModel).updateSymptomNotes(Symptom.Type.CRAMPS, "Cólicas fortes hoje")
    }

    @Test
    fun testLoggingScreenSymptomsSave() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Sintomas").performClick()
        composeRule.onNodeWithText("Salvar sintomas").performClick()
        verify(viewModel).saveSymptoms()
    }

    @Test
    fun testLoggingScreenWeightTab() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Peso").performClick()

        composeRule.onNodeWithText("Peso (kg)").assertExists()
        composeRule.onNodeWithText("60.5").assertExists()
    }

    @Test
    fun testLoggingScreenWeightInput() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Peso").performClick()

        composeRule.onNodeWithTag("weight_input").performTextInput("61.0")
        verify(viewModel).updateWeight(61.0)
    }

    @Test
    fun testLoggingScreenWeightUnitToggle() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Peso").performClick()

        composeRule.onNodeWithText("lb").performClick()
        verify(viewModel).updateWeightUnit(LoggingViewModel.WeightUnit.LB)
    }

    @Test
    fun testLoggingScreenWeightSave() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Peso").performClick()
        composeRule.onNodeWithText("Salvar peso").performClick()
        verify(viewModel).saveWeight()
    }

    @Test
    fun testLoggingScreenTemperatureTab() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Temperatura").performClick()

        composeRule.onNodeWithText("Temperatura basal (°C)").assertExists()
        composeRule.onNodeWithText("36.5").assertExists()
    }

    @Test
    fun testLoggingScreenTemperatureInput() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Temperatura").performClick()

        composeRule.onNodeWithTag("temperature_input").performTextInput("36.7")
        verify(viewModel).updateTemperature(36.7)
    }

    @Test
    fun testLoggingScreenTemperatureUnitToggle() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Temperatura").performClick()

        composeRule.onNodeWithText("°F").performClick()
        verify(viewModel).updateTemperatureUnit(LoggingViewModel.TemperatureUnit.FAHRENHEIT)
    }

    @Test
    fun testLoggingScreenTemperatureTimePicker() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Temperatura").performClick()

        composeRule.onNodeWithText("Horário").performClick()
        verify(viewModel).updateTemperatureTime(any())
    }

    @Test
    fun testLoggingScreenTemperatureSave() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Temperatura").performClick()
        composeRule.onNodeWithText("Salvar temperatura").performClick()
        verify(viewModel).saveTemperature()
    }

    @Test
    fun testLoggingScreenMoodTab() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Humor").performClick()

        composeRule.onNodeWithText("Feliz").assertExists()
        composeRule.onNodeWithText("Triste").assertExists()
        composeRule.onNodeWithText("Ansiosa").assertExists()
        composeRule.onNodeWithText("Irritada").assertExists()
        composeRule.onNodeWithText("Cansada").assertExists()
        composeRule.onNodeWithText("Neutra").assertExists()
        composeRule.onNodeWithText("Eufórica").assertExists()
        composeRule.onNodeWithText("Sensível").assertExists()
    }

    @Test
    fun testLoggingScreenMoodSelection() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Humor").performClick()

        composeRule.onNodeWithText("Feliz").performClick()
        verify(viewModel).updateMood(LoggingViewModel.Mood.HAPPY)

        composeRule.onNodeWithText("Triste").performClick()
        verify(viewModel).updateMood(LoggingViewModel.Mood.SAD)
    }

    @Test
    fun testLoggingScreenMoodNotes() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Humor").performClick()
        composeRule.onNodeWithText("Feliz").performClick()

        composeRule.onNodeWithText("Observações").performTextInput("Dia muito bom!")
        verify(viewModel).updateMoodNotes("Dia muito bom!")
    }

    @Test
    fun testLoggingScreenMoodSave() {
        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Humor").performClick()
        composeRule.onNodeWithText("Salvar humor").performClick()
        verify(viewModel).saveMood()
    }

    @Test
    fun testLoggingScreenLoadingState() {
        val loadingState = LoggingViewModel.UiState(
            currentTab = LoggingViewModel.LoggingTab.PERIOD,
            periodState = LoggingViewModel.PeriodState(
                startDate = null,
                endDate = null,
                flowLevel = Symptom.FlowLevel.MEDIUM,
                isLoading = true,
                error = null
            ),
            symptomsState = LoggingViewModel.SymptomsState(
                selectedSymptoms = emptyMap(),
                availableSymptoms = emptyList(),
                isLoading = true,
                error = null
            ),
            weightState = LoggingViewModel.WeightState(
                weightKg = null,
                date = LocalDate.now(),
                unit = LoggingViewModel.WeightUnit.KG,
                isLoading = true,
                error = null
            ),
            temperatureState = LoggingViewModel.TemperatureState(
                temperatureCelsius = null,
                date = LocalDate.now(),
                time = LocalDateTime.now().toLocalTime(),
                unit = LoggingViewModel.TemperatureUnit.CELSIUS,
                isLoading = true,
                error = null
            ),
            moodState = LoggingViewModel.MoodState(
                selectedMood = LoggingViewModel.Mood.NEUTRAL,
                notes = "",
                isLoading = true,
                error = null
            ),
            isSaving = false,
            saveError = null
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(loadingState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = loadingState
            )
        )

        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithTag("loading_indicator").assertExists()
    }

    @Test
    fun testLoggingScreenSaveError() {
        val errorState = LoggingViewModel.UiState(
            currentTab = LoggingViewModel.LoggingTab.PERIOD,
            periodState = LoggingViewModel.PeriodState(
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                flowLevel = Symptom.FlowLevel.MEDIUM,
                isLoading = false,
                error = null
            ),
            symptomsState = LoggingViewModel.SymptomsState(
                selectedSymptoms = emptyMap(),
                availableSymptoms = emptyList(),
                isLoading = false,
                error = null
            ),
            weightState = LoggingViewModel.WeightState(
                weightKg = null,
                date = LocalDate.now(),
                unit = LoggingViewModel.WeightUnit.KG,
                isLoading = false,
                error = null
            ),
            temperatureState = LoggingViewModel.TemperatureState(
                temperatureCelsius = null,
                date = LocalDate.now(),
                time = LocalDateTime.now().toLocalTime(),
                unit = LoggingViewModel.TemperatureUnit.CELSIUS,
                isLoading = false,
                error = null
            ),
            moodState = LoggingViewModel.MoodState(
                selectedMood = LoggingViewModel.Mood.NEUTRAL,
                notes = "",
                isLoading = false,
                error = null
            ),
            isSaving = false,
            saveError = "Erro ao salvar período"
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(errorState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = errorState
            )
        )

        composeRule.setContent {
            LoggingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Erro ao salvar período").assertExists()
    }
}