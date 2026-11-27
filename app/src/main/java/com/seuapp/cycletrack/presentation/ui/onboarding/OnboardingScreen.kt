package com.seuapp.cycletrack.presentation.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seuapp.cycletrack.presentation.viewmodel.OnboardingAnswers
import com.seuapp.cycletrack.presentation.viewmodel.OnboardingViewModel
import java.time.LocalDate
import kotlinx.coroutines.launch

private val Rose = Color(0xFFE75480)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var step by remember { mutableIntStateOf(0) }
    var answers by remember { mutableStateOf(OnboardingAnswers()) }
    var goal by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val steps = remember(goal) { introSteps() + trackSteps(goal) + closingSteps() }
    val totalSteps = steps.size
    val current = steps[step.coerceIn(0, totalSteps - 1)]

    fun goNext() {
        if (step < totalSteps - 1) {
            step++
        } else {
            coroutineScope.launch {
                viewModel.finishOnboarding(answers)
                onFinish()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LinearProgressIndicator(
            progress = { (step + 1).toFloat() / totalSteps },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = Rose
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 0) {
                IconButton(onClick = { step-- }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Rose)
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "${step + 1}/$totalSteps", fontSize = 12.sp, color = Color.Gray)
        }

        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (current) {
                is OnboardingStep.Illustration -> FullIllustrationScreen(current) { goNext() }

                is OnboardingStep.SingleChoice -> SingleChoiceScreen(current) { option ->
                    if (current.resultKey == "goal") {
                        goal = option.key
                        answers = answers.copy(goal = option.key)
                    }
                    goNext()
                }

                is OnboardingStep.MultiChoiceGrid -> MultiChoiceGridScreen(current) { option ->
                    if (current.resultKey == "goal") {
                        goal = option.key
                        answers = answers.copy(goal = option.key)
                    }
                    goNext()
                }

                is OnboardingStep.WheelPicker -> WheelPickerScreen(current) { value ->
                    when (current.resultKey) {
                        "birthYear" -> answers = answers.copy(birthYear = value.toIntOrNull())
                        "cycleLength" -> answers = answers.copy(cycleLength = value.toIntOrNull() ?: 28)
                        "periodLength" -> answers = answers.copy(periodLength = value.toIntOrNull() ?: 5)
                    }
                    goNext()
                }

                is OnboardingStep.PeriodCalendar -> PeriodCalendarScreen(current) { date: LocalDate ->
                    answers = answers.copy(lastPeriodStart = date)
                    goNext()
                }

                is OnboardingStep.HeightWeight -> HeightWeightScreen(current) { h, w ->
                    answers = answers.copy(heightCm = h, weightKg = w)
                    goNext()
                }

                OnboardingStep.NotificationPriming -> NotificationPrimingScreen { goNext() }

                OnboardingStep.PercentLoading -> PercentLoadingScreen { goNext() }
            }
        }
    }
}
