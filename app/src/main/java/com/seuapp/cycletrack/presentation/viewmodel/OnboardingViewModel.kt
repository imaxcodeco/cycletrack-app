package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import timber.log.Timber

data class OnboardingAnswers(
    val goal: String = "",
    val birthYear: Int? = null,
    val cycleLength: Int = 28,
    val periodLength: Int = 5,
    val lastPeriodStart: LocalDate = LocalDate.now().minusDays(3),
    val heightCm: Int = 165,
    val weightKg: Float = 60f
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val cycleRepository: CycleRepository
) : ViewModel() {

    suspend fun finishOnboarding(userId: String, answers: OnboardingAnswers) {
        try {
            val dateOfBirth = answers.birthYear?.let { LocalDate.of(it, 1, 1) }

            val profile = UserProfile(
                id = userId,
                email = "",
                displayName = "",
                goal = answers.goal,
                dateOfBirth = dateOfBirth,
                averageCycleLength = answers.cycleLength,
                averagePeriodLength = answers.periodLength,
                lastPeriodStartDate = answers.lastPeriodStart,
                heightCm = answers.heightCm,
                weightKg = answers.weightKg,
                onboardingCompleted = true
            )
            userProfileRepository.upsertProfile(profile)

            val cycle = Cycle.createManual(
                userId = userId,
                startDate = answers.lastPeriodStart,
                periodLength = answers.periodLength,
                previousCycleLength = answers.cycleLength
            )
            cycleRepository.upsertCycle(cycle)

            Timber.d("Onboarding finalizado: goal=${answers.goal}, cycle=${cycle.id}")
        } catch (e: Exception) {
            Timber.e(e, "Erro ao finalizar onboarding")
        }
    }
}
