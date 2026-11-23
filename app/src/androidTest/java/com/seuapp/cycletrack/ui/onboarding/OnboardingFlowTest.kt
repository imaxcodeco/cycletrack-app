package com.seuapp.cycletrack.ui.onboarding

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.presentation.state.OnboardingViewModel
import com.seuapp.cycletrack.presentation.ui.onboarding.OnboardingScreen
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

@RunWith(AndroidJUnit4::class)
class OnboardingFlowTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Mock
    lateinit var viewModel: OnboardingViewModel

    @Mock
    lateinit var preferencesManager: PreferencesManager

    private lateinit var context: Context

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        context = InstrumentationRegistry.getInstrumentation().targetContext

        val onboardingData = OnboardingViewModel.OnboardingData()
        val uiState = OnboardingViewModel.UiState(
            currentStep = 0,
            totalSteps = 33,
            data = onboardingData,
            isLoading = false,
            error = null,
            canGoNext = true,
            canGoBack = false,
            isComplete = false
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(uiState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = uiState
            )
        )
        whenever(viewModel.currentStep).thenReturn(0)
        whenever(viewModel.totalSteps).thenReturn(33)
        whenever(viewModel.onboardingData).thenReturn(onboardingData)
        whenever(viewModel.canGoNext).thenReturn(true)
        whenever(viewModel.canGoBack).thenReturn(false)
        whenever(viewModel.isComplete).thenReturn(false)
    }

    @Test
    fun testOnboardingScreenRenders() {
        composeRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Bem-vinda ao CycleTrack").assertExists()
        composeRule.onNodeWithText("Próximo").assertExists()
    }

    @Test
    fun testOnboardingStepNavigation() {
        composeRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Próximo").performClick()
        verify(viewModel).goNext()
    }

    @Test
    fun testOnboardingBackNavigation() {
        whenever(viewModel.currentStep).thenReturn(5)
        whenever(viewModel.canGoBack).thenReturn(true)

        composeRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Voltar").performClick()
        verify(viewModel).goBack()
    }

    @Test
    fun testOnboardingCompleteFlow() {
        val completeData = OnboardingViewModel.OnboardingData().apply {
            birthDate = java.time.LocalDate.now().minusYears(25)
            cycleLength = 28
            periodLength = 5
            lastPeriodStart = java.time.LocalDate.now().minusDays(10)
            contraceptionMethod = UserProfile.ContraceptionMethod.NONE
            goal = UserProfile.Goal.TRACK_CYCLE
            hasRegularCycles = true
            heightCm = 165
            weightKg = 60.0
            notificationsEnabled = true
            partnerInviteEnabled = false
        }
        val completeState = OnboardingViewModel.UiState(
            currentStep = 32,
            totalSteps = 33,
            data = completeData,
            isLoading = false,
            error = null,
            canGoNext = true,
            canGoBack = true,
            isComplete = false
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(completeState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = completeState
            )
        )
        whenever(viewModel.currentStep).thenReturn(32)
        whenever(viewModel.canGoNext).thenReturn(true)
        whenever(viewModel.canGoBack).thenReturn(true)
        whenever(viewModel.onboardingData).thenReturn(completeData)

        composeRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Concluir").assertExists()
        composeRule.onNodeWithText("Concluir").performClick()
        verify(viewModel).completeOnboarding()
    }

    @Test
    fun testOnboardingDatePickerField() {
        composeRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Data de nascimento").assertExists()
        composeRule.onNodeWithText("Selecionar data").performClick()
        verify(viewModel).updateBirthDate(any())
    }

    @Test
    fun testOnboardingRadioGroupSelection() {
        composeRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Nenhum").performClick()
        verify(viewModel).updateContraceptionMethod(UserProfile.ContraceptionMethod.NONE)
    }

    @Test
    fun testOnboardingCheckboxGroupSelection() {
        composeRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Cólicas").performClick()
        verify(viewModel).updateCommonSymptoms(any())
    }

    @Test
    fun testOnboardingSliderField() {
        composeRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Altura").assertExists()
    }

    @Test
    fun testOnboardingErrorDisplay() {
        val errorState = OnboardingViewModel.UiState(
            currentStep = 0,
            totalSteps = 33,
            data = OnboardingViewModel.OnboardingData(),
            isLoading = false,
            error = "Erro ao salvar dados",
            canGoNext = false,
            canGoBack = false,
            isComplete = false
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(errorState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = errorState
            )
        )

        composeRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithText("Erro ao salvar dados").assertExists()
    }

    @Test
    fun testOnboardingLoadingState() {
        val loadingState = OnboardingViewModel.UiState(
            currentStep = 32,
            totalSteps = 33,
            data = OnboardingViewModel.OnboardingData(),
            isLoading = true,
            error = null,
            canGoNext = false,
            canGoBack = false,
            isComplete = false
        )

        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(loadingState).stateIn(
                scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
                started = SharingStarted.Eagerly,
                initialValue = loadingState
            )
        )

        composeRule.setContent {
            OnboardingScreen(viewModel = viewModel)
        }

        composeRule.onNodeWithTag("loading_indicator").assertExists()
    }
}