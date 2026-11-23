package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.usecase.auth.AuthUseCases
import com.seuapp.cycletrack.domain.usecase.cycle.CycleUseCases
import com.seuapp.cycletrack.domain.usecase.symptom.SymptomUseCases
import com.seuapp.cycletrack.domain.usecase.prediction.PredictionUseCases
import com.seuapp.cycletrack.domain.usecase.pregnancy.PregnancyUseCases
import com.seuapp.cycletrack.domain.usecase.partner.PartnerUseCases
import com.seuapp.cycletrack.domain.usecase.subscription.SubscriptionUseCases
import com.seuapp.cycletrack.domain.usecase.export.ExportUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

/**
 * ViewModel para o fluxo de Onboarding (33 telas).
 * Gerencia estado do onboarding, perfil do usuário e preferências iniciais.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val cycleUseCases: CycleUseCases,
    private val symptomUseCases: SymptomUseCases,
    private val predictionUseCases: PredictionUseCases,
    private val pregnancyUseCases: PregnancyUseCases,
    private val partnerUseCases: PartnerUseCases,
    private val subscriptionUseCases: SubscriptionUseCases,
    private val exportUseCases: ExportUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============
    
    private val _currentStep = MutableStateFlow(0)
    val currentStep = _currentStep.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile.Builder?>(UserProfile.Builder())
    val userProfile = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted = _onboardingCompleted.asStateFlow()

    // ============ Onboarding Data ============
    
    data class OnboardingData(
        val cycleLength: Int = 28,
        val periodLength: Int = 5,
        val lastPeriodStart: LocalDate? = null,
        val contraceptionMethod: String = "none",
        val pregnancyStatus: String = "not_pregnant",
        val goals: List<String> = emptyList(),
        val notificationsEnabled: Boolean = true,
        val themeMode: String = "system",
        val language: String = "pt_BR",
        val units: String = "metric",
        val healthConnectEnabled: Boolean = false,
        val partnerInviteEmail: String? = null,
        val wantsPremium: Boolean = false,
        val accountType: String = "email", // email, google, anonymous
        val email: String = "",
        val password: String = "",
        val displayName: String = "",
        val biometricEnabled: Boolean = false
    )

    private val _onboardingData = MutableStateFlow(OnboardingData())
    val onboardingData = _onboardingData.asStateFlow()

    // ============ Actions ============

    fun nextStep() {
        _currentStep.value++
    }

    fun previousStep() {
        _currentStep.value = max(0, _currentStep.value - 1)
    }

    fun goToStep(step: Int) {
        _currentStep.value = step
    }

    fun updateCycleInfo(cycleLength: Int, periodLength: Int) {
        _onboardingData.value = _onboardingData.value.copy(
            cycleLength = cycleLength.coerceIn(21, 45),
            periodLength = periodLength.coerceIn(2, 10)
        )
    }

    fun updateLastPeriod(startDate: LocalDate) {
        _onboardingData.value = _onboardingData.value.copy(lastPeriodStart = startDate)
    }

    fun updateContraception(method: String) {
        _onboardingData.value = _onboardingData.value.copy(contraceptionMethod = method)
    }

    fun updatePregnancyStatus(status: String) {
        _onboardingData.value = _onboardingData.value.copy(pregnancyStatus = status)
    }

    fun updateGoals(goals: List<String>) {
        _onboardingData.value = _onboardingData.value.copy(goals = goals)
    }

    fun updateNotifications(enabled: Boolean) {
        _onboardingData.value = _onboardingData.value.copy(notificationsEnabled = enabled)
    }

    fun updateTheme(mode: String) {
        _onboardingData.value = _onboardingData.value.copy(themeMode = mode)
        preferencesManager.setThemeMode(mode)
    }

    fun updateLanguage(lang: String) {
        _onboardingData.value = _onboardingData.value.copy(language = lang)
        preferencesManager.setLanguage(lang)
    }

    fun updateUnits(units: String) {
        _onboardingData.value = _onboardingData.value.copy(units = units)
    }

    fun updateHealthConnect(enabled: Boolean) {
        _onboardingData.value = _onboardingData.value.copy(healthConnectEnabled = enabled)
        preferencesManager.setHealthConnectEnabled(enabled)
    }

    fun updatePartnerInvite(email: String?) {
        _onboardingData.value = _onboardingData.value.copy(partnerInviteEmail = email)
    }

    fun updatePremium(wants: Boolean) {
        _onboardingData.value = _onboardingData.value.copy(wantsPremium = wants)
    }

    fun updateAccountType(type: String) {
        _onboardingData.value = _onboardingData.value.copy(accountType = type)
    }

    fun updateEmail(email: String) {
        _onboardingData.value = _onboardingData.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _onboardingData.value = _onboardingData.value.copy(password = password)
    }

    fun updateDisplayName(name: String) {
        _onboardingData.value = _onboardingData.value.copy(displayName = name)
    }

    fun updateBiometric(enabled: Boolean) {
        _onboardingData.value = _onboardingData.value.copy(biometricEnabled = enabled)
        preferencesManager.setBiometricEnabled(enabled)
    }

    /**
     * Completa o onboarding e cria o perfil do usuário.
     */
    suspend fun completeOnboarding(): Boolean {
        _isLoading.value = true
        _error.value = null
        
        try {
            val data = _onboardingData.value
            val builder = UserProfile.Builder()
                .id(java.util.UUID.randomUUID().toString())
                .displayName(data.displayName)
                .email(data.email)
                .averageCycleLength(data.cycleLength)
                .averagePeriodLength(data.periodLength)
                .contraceptionMethod(data.contraceptionMethod)
                .lastPeriodDate(data.lastPeriodStart)
                .themeMode(data.themeMode)
                .language(data.language)
                .temperatureUnit(if (data.units == "metric") "celsius" else "fahrenheit")
                .weightUnit(if (data.units == "metric") "kg" else "lbs")
                .heightUnit(if (data.units == "metric") "cm" else "ft_in")
                .notificationsEnabled(data.notificationsEnabled)
                .biometricEnabled(data.biometricEnabled)
                .analyticsOptIn(true)
                .crashReportingOptIn(true)
                .marketingOptIn(false)
                .healthConnectEnabled(data.healthConnectEnabled)

            // Cria conta conforme tipo
            when (data.accountType) {
                "email" -> {
                    val result = authUseCases.register(
                        email = data.email,
                        password = data.password,
                        displayName = data.displayName
                    )
                    builder.id(result.userId)
                    preferencesManager.setUserId(result.userId)
                    preferencesManager.setUserEmail(data.email)
                    preferencesManager.setAuthToken(result.accessToken)
                    if (result.refreshToken != null) {
                        preferencesManager.setRefreshToken(result.refreshToken!!)
                    }
                }
                "google" -> {
                    // TODO: Implementar Google Sign-In
                }
                "anonymous" -> {
                    builder.id(java.util.UUID.randomUUID().toString())
                    preferencesManager.setUserId(builder.build().id)
                }
            }

            val profile = builder.build()
            _userProfile.value = UserProfile.Builder().apply {
                this.id = profile.id
                this.displayName = profile.displayName
                this.email = profile.email
                this.averageCycleLength = profile.averageCycleLength
                this.averagePeriodLength = profile.averagePeriodLength
                this.contraceptionMethod = profile.contraceptionMethod
                this.lastPeriodDate = profile.lastPeriodDate
                this.themeMode = profile.themeMode
                this.language = profile.language
                this.temperatureUnit = profile.temperatureUnit
                this.weightUnit = profile.weightUnit
                this.heightUnit = profile.heightUnit
                this.notificationsEnabled = profile.notificationsEnabled
                this.biometricEnabled = profile.biometricEnabled
                this.analyticsOptIn = profile.analyticsOptIn
                this.crashReportingOptIn = profile.crashReportingOptIn
                this.marketingOptIn = profile.marketingOptIn
                this.healthConnectEnabled = profile.healthConnectEnabled
            }

            // Salva preferências
            preferencesManager.setOnboardingCompleted(true)
            preferencesManager.setOnboardingStep(33)
            
            // Se tem último período, cria ciclo inicial
            if (data.lastPeriodStart != null) {
                cycleUseCases.logPeriodStart(
                    userId = profile.id,
                    startDate = data.lastPeriodStart!!,
                    periodLength = data.periodLength
                )
            }

            // Convida parceiro se email fornecido
            if (data.partnerInviteEmail != null && data.partnerInviteEmail!!.isNotBlank()) {
                partnerUseCases.invitePartner(
                    userId = profile.id,
                    partnerEmail = data.partnerInviteEmail!!,
                    partnerDisplayName = "",
                    shareLevel = com.seuapp.cycletrack.domain.model.PartnerShareLevel.CYCLE_ONLY
                )
            }

            _onboardingCompleted.value = true
            return true
            
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao completar onboarding"
            return false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        const val TOTAL_STEPS = 33
    }
}