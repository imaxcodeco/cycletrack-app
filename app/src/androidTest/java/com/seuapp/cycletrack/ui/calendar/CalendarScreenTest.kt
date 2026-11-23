package com.seuapp.cycletrack.ui.calendar

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.presentation.state.CalendarViewModel
import com.seuapp.cycletrack.presentation.ui.calendar.CalendarScreen
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
import java.time.YearMonth

@RunWith(AndroidJUnit4::class)
class CalendarScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Mock
    lateinit var viewModel: CalendarViewModel

    private lateinit var context: Context

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext

        val currentMonth = YearMonth.now()
        val cycles = listOf(
            Cycle(
                id = "cycle_1",
                userId = "user_1",
                startDate = LocalDate.now().withDayOfMonth(1),
                endDate = LocalDate.now().withDayOfMonth(5),
                cycleLength = 28,
                periodLength = 5,
                isPredicted = false,
                symptoms = emptyList(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
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
                date = LocalDate.now().withDayOfMonth(10),
                type = Symptom.Type.HEADACHE,
                severity = Symptom.Severity.MILD,
                notes = "Dor de cabeça leve",
                createdAt = LocalDateTime.now()
            )
        )

        val calendarDays = (1..28).map { day ->
            CalendarViewModel.CalendarDay(
                date = currentMonth.atDay(day),
                isCurrentMonth = true,
                isToday = day == LocalDate.now().dayOfMonth,
                hasPeriod = day in 1..5,
                hasPredictedPeriod = day in 23..27,
                hasOvulation = day == 16,
                hasFertileWindow = day in 14..18,
                hasSymptoms = day == 10,
                hasPregnancy = false,
                periodFlowLevel = if (day in 1..5) Symptom.FlowLevel.MEDIUM else null,
                symptomCount = if (day == 10) 1 else 0
            )
        }

        val uiState = CalendarViewModel.UiState(
            currentMonth = currentMonth,
            viewMode = CalendarViewModel.ViewMode.MONTH,
            calendarDays = calendarDays,
            selectedDate = LocalDate.now(),
            selectedDayDetails = null,
            isLoading = false,
            error = null
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(uiState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = uiState
            )
        )
        whenever(viewModel.currentMonth).thenReturn(currentMonth)
        whenever(viewModel.viewMode).thenReturn(CalendarViewModel.ViewMode.MONTH)
        whenever(viewModel.selectedDate).thenReturn(LocalDate.now())
    }

    @Test
    fun testCalendarScreenRendersMonthView() {
        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithText(currentMonth.month.name()).assertExists()
        composeRule.onNodeWithText(currentMonth.year.toString()).assertExists()
    }

    @Test
    fun testCalendarScreenRendersDays() {
        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithText("1").assertExists()
        composeRule.onNodeWithText("28").assertExists()
    }

    @Test
    fun testCalendarScreenTodayHighlighted() {
        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithTag("today_indicator").assertExists()
    }

    @Test
    fun testCalendarScreenPeriodIndicators() {
        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithTag("period_indicator_day_1").assertExists()
        composeRule.onNodeWithTag("period_indicator_day_5").assertExists()
    }

    @Test
    fun testCalendarScreenFertileWindowIndicators() {
        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithTag("fertile_indicator_day_14").assertExists()
        composeRule.onNodeWithTag("fertile_indicator_day_18").assertExists()
    }

    @Test
    fun testCalendarScreenOvulationIndicator() {
        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithTag("ovulation_indicator_day_16").assertExists()
    }

    @Test
    fun testCalendarScreenSymptomIndicators() {
        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithTag("symptom_indicator_day_10").assertExists()
    }

    @Test
    fun testCalendarScreenMonthNavigation() {
        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithContentDescription("Mês anterior").performClick()
        verify(viewModel).previousMonth()

        composeRule.onNodeWithContentDescription("Próximo mês").performClick()
        verify(viewModel).nextMonth()
    }

    @Test
    fun testCalendarScreenViewModeToggle() {
        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithText("Semana").performClick()
        verify(viewModel).setViewMode(CalendarViewModel.ViewMode.WEEK)

        composeRule.onNodeWithText("Lista").performClick()
        verify(viewModel).setViewMode(CalendarViewModel.ViewMode.LIST)
    }

    @Test
    fun testCalendarScreenDayClickOpensDetail() {
        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithText("10").performClick()
        verify(viewModel).selectDate(any())
    }

    @Test
    fun testCalendarScreenDayDetailShowsPeriod() {
        val dayWithPeriod = CalendarViewModel.CalendarDay(
            date = LocalDate.now().withDayOfMonth(3),
            isCurrentMonth = true,
            isToday = false,
            hasPeriod = true,
            hasPredictedPeriod = false,
            hasOvulation = false,
            hasFertileWindow = false,
            hasSymptoms = false,
            hasPregnancy = false,
            periodFlowLevel = Symptom.FlowLevel.HEAVY,
            symptomCount = 0
        )

        val detailState = CalendarViewModel.UiState(
            currentMonth = YearMonth.now(),
            viewMode = CalendarViewModel.ViewMode.MONTH,
            calendarDays = listOf(dayWithPeriod),
            selectedDate = dayWithPeriod.date,
            selectedDayDetails = CalendarViewModel.DayDetails(
                date = dayWithPeriod.date,
                cycles = listOf(Cycle(
                    id = "cycle_1",
                    userId = "user_1",
                    startDate = dayWithPeriod.date,
                    endDate = dayWithPeriod.date.plusDays(4),
                    cycleLength = 28,
                    periodLength = 5,
                    isPredicted = false,
                    symptoms = emptyList(),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )),
                predictions = emptyList(),
                symptoms = emptyList(),
                pregnancy = null
            ),
            isLoading = false,
            error = null
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(detailState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = detailState
            )
        )

        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithText("3").performClick()

        composeRule.onNodeWithText("Menstruação").assertExists()
        composeRule.onNodeWithText("Fluxo intenso").assertExists()
    }

    @Test
    fun testCalendarScreenDayDetailShowsSymptoms() {
        val dayWithSymptoms = CalendarViewModel.CalendarDay(
            date = LocalDate.now().withDayOfMonth(10),
            isCurrentMonth = true,
            isToday = false,
            hasPeriod = false,
            hasPredictedPeriod = false,
            hasOvulation = false,
            hasFertileWindow = false,
            hasSymptoms = true,
            hasPregnancy = false,
            periodFlowLevel = null,
            symptomCount = 2
        )

        val detailState = CalendarViewModel.UiState(
            currentMonth = YearMonth.now(),
            viewMode = CalendarViewModel.ViewMode.MONTH,
            calendarDays = listOf(dayWithSymptoms),
            selectedDate = dayWithSymptoms.date,
            selectedDayDetails = CalendarViewModel.DayDetails(
                date = dayWithSymptoms.date,
                cycles = emptyList(),
                predictions = emptyList(),
                symptoms = listOf(
                    Symptom(
                        id = "s1",
                        userId = "user_1",
                        date = dayWithSymptoms.date,
                        type = Symptom.Type.HEADACHE,
                        severity = Symptom.Severity.MILD,
                        notes = "Dor de cabeça",
                        createdAt = LocalDateTime.now()
                    ),
                    Symptom(
                        id = "s2",
                        userId = "user_1",
                        date = dayWithSymptoms.date,
                        type = Symptom.Type.FATIGUE,
                        severity = Symptom.Severity.MODERATE,
                        notes = "Cansaço",
                        createdAt = LocalDateTime.now()
                    )
                ),
                pregnancy = null
            ),
            isLoading = false,
            error = null
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(detailState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = detailState
            )
        )

        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithText("10").performClick()

        composeRule.onNodeWithText("Sintomas (2)").assertExists()
        composeRule.onNodeWithText("Dor de cabeça").assertExists()
        composeRule.onNodeWithText("Cansaço").assertExists()
    }

    @Test
    fun testCalendarScreenLoadingState() {
        val loadingState = CalendarViewModel.UiState(
            currentMonth = YearMonth.now(),
            viewMode = CalendarViewModel.ViewMode.MONTH,
            calendarDays = emptyList(),
            selectedDate = LocalDate.now(),
            selectedDayDetails = null,
            isLoading = true,
            error = null
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(loadingState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = loadingState
            )
        )

        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithTag("loading_indicator").assertExists()
    }

    @Test
    fun testCalendarScreenErrorState() {
        val errorState = CalendarViewModel.UiState(
            currentMonth = YearMonth.now(),
            viewMode = CalendarViewModel.ViewMode.MONTH,
            calendarDays = emptyList(),
            selectedDate = LocalDate.now(),
            selectedDayDetails = null,
            isLoading = false,
            error = "Erro ao carregar calendário"
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(errorState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = errorState
            )
        )

        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithText("Erro ao carregar calendário").assertExists()
        composeRule.onNodeWithText("Tentar novamente").assertExists()
    }

    @Test
    fun testCalendarScreenWeekView() {
        val weekState = CalendarViewModel.UiState(
            currentMonth = YearMonth.now(),
            viewMode = CalendarViewModel.ViewMode.WEEK,
            calendarDays = (1..7).map { day ->
                CalendarViewModel.CalendarDay(
                    date = LocalDate.now().withDayOfMonth(day),
                    isCurrentMonth = true,
                    isToday = day == LocalDate.now().dayOfMonth,
                    hasPeriod = day in 1..3,
                    hasPredictedPeriod = false,
                    hasOvulation = false,
                    hasFertileWindow = false,
                    hasSymptoms = day == 5,
                    hasPregnancy = false,
                    periodFlowLevel = if (day in 1..3) Symptom.FlowLevel.MEDIUM else null,
                    symptomCount = if (day == 5) 1 else 0
                )
            },
            selectedDate = LocalDate.now(),
            selectedDayDetails = null,
            isLoading = false,
            error = null
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(weekState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = weekState
            )
        )
        whenever(viewModel.viewMode).thenReturn(CalendarViewModel.ViewMode.WEEK)

        composeRule.setContent {
            CalendarScreen(viewModel = viewModel, onNavigateToLogging = {})
        }

        composeRule.onNodeWithText("Semana").assertExists()
        composeRule.onNodeWithText("1").assertExists()
        composeRule.onNodeWithText("7").assertExists()
    }
}