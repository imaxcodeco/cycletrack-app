package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.usecase.auth.AuthUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para a tela de Configurações.
 * Gerencia todas as preferências do usuário: conta, ciclo, notificações, aparência, privacidade, segurança, dados.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    // Account
    private val _displayName = MutableStateFlow("")
    val displayName = _displayName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _dateOfBirth = MutableStateFlow<kotlinx.datetime.LocalDate?>(null)
    val dateOfBirth = _dateOfBirth.asStateFlow()

    private val _height = MutableStateFlow(0)
    val height = _height.asStateFlow()

    private val _weight = MutableStateFlow(0.0)
    val weight = _weight.asStateFlow()

    private val _gender = MutableStateFlow("")
    val gender = _gender.asStateFlow()

    // Cycle
    private val _averageCycleLength = MutableStateFlow(28)
    val averageCycleLength = _averageCycleLength.asStateFlow()

    private val _averagePeriodLength = MutableStateFlow(5)
    val averagePeriodLength = _averagePeriodLength.asStateFlow()

    private val _cycleRegularity = MutableStateFlow("regular")
    val cycleRegularity = _cycleRegularity.asStateFlow()

    private val _contraceptionMethod = MutableStateFlow("none")
    val contraceptionMethod = _contraceptionMethod.asStateFlow()

    private val _lastPeriodDate = MutableStateFlow<kotlinx.datetime.LocalDate?>(null)
    val lastPeriodDate = _lastPeriodDate.asStateFlow()

    // Notifications
    private val _periodReminderEnabled = MutableStateFlow(true)
    val periodReminderEnabled = _periodReminderEnabled.asStateFlow()

    private val _periodReminderDaysBefore = MutableStateFlow(2)
    val periodReminderDaysBefore = _periodReminderDaysBefore.asStateFlow()

    private val _fertileWindowReminderEnabled = MutableStateFlow(true)
    val fertileWindowReminderEnabled = _fertileWindowReminderEnabled.asStateFlow()

    private val _ovulationReminderEnabled = MutableStateFlow(true)
    val ovulationReminderEnabled = _ovulationReminderEnabled.asStateFlow()

    private val _pmsReminderEnabled = MutableStateFlow(true)
    val pmsReminderEnabled = _pmsReminderEnabled.asStateFlow()

    private val _pillReminderEnabled = MutableStateFlow(false)
    val pillReminderEnabled = _pillReminderEnabled.asStateFlow()

    private val _pillReminderTime = MutableStateFlow("21:00")
    val pillReminderTime = _pillReminderTime.asStateFlow()

    private val _partnerNotificationsEnabled = MutableStateFlow(true)
    val partnerNotificationsEnabled = _partnerNotificationsEnabled.asStateFlow()

    // Display
    private val _themeMode = MutableStateFlow("system")
    val themeMode = _themeMode.asStateFlow()

    private val _language = MutableStateFlow("pt_BR")
    val language = _language.asStateFlow()

    private val _temperatureUnit = MutableStateFlow("celsius")
    val temperatureUnit = _temperatureUnit.asStateFlow()

    private val _weightUnit = MutableStateFlow("kg")
    val weightUnit = _weightUnit.asStateFlow()

    private val _heightUnit = MutableStateFlow("cm")
    val heightUnit = _heightUnit.asStateFlow()

    private val _firstDayOfWeek = MutableStateFlow(1) // 1=Dom, 2=Seg...
    val firstDayOfWeek = _firstDayOfWeek.asStateFlow()

    private val _calendarViewMode = MutableStateFlow("month")
    val calendarViewMode = _calendarViewMode.asStateFlow()

    // Privacy
    private val _analyticsOptIn = MutableStateFlow(true)
    val analyticsOptIn = _analyticsOptIn.asStateFlow()

    private val _crashReportingOptIn = MutableStateFlow(true)
    val crashReportingOptIn = _crashReportingOptIn.asStateFlow()

    private val _marketingOptIn = MutableStateFlow(false)
    val marketingOptIn = _marketingOptIn.asStateFlow()

    private val _healthConnectEnabled = MutableStateFlow(false)
    val healthConnectEnabled = _healthConnectEnabled.asStateFlow()

    private val _dataSharingConsentVersion = MutableStateFlow("")
    val dataSharingConsentVersion = _dataSharingConsentVersion.asStateFlow()

    // Security
    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled = _biometricEnabled.asStateFlow()

    private val _autoLockEnabled = MutableStateFlow(true)
    val autoLockEnabled = _autoLockEnabled.asStateFlow()

    private val _autoLockTimeoutMinutes = MutableStateFlow(5)
    val autoLockTimeoutMinutes = _autoLockTimeoutMinutes.asStateFlow()

    // Data & Sync
    private val _autoSyncEnabled = MutableStateFlow(true)
    val autoSyncEnabled = _autoSyncEnabled.asStateFlow()

    private val _syncFrequencyMinutes = MutableStateFlow(15)
    val syncFrequencyMinutes = _syncFrequencyMinutes.asStateFlow()

    private val _autoBackupEnabled = MutableStateFlow(true)
    val autoBackupEnabled = _autoBackupEnabled.asStateFlow()

    private val _backupFrequency = MutableStateFlow("daily") // daily, weekly, monthly
    val backupFrequency = _backupFrequency.asStateFlow()

    // State
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    // ============ Actions ============

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = preferencesManager.getUserId()
                
                // Carrega preferências do DataStore
                _themeMode.value = preferencesManager.themeMode.first()
                _language.value = preferencesManager.language.first()
                _firstDayOfWeek.value = preferencesManager.firstDayOfWeek.first()
                _calendarViewMode.value = preferencesManager.calendarViewMode.first()
                _temperatureUnit.value = preferencesManager.temperatureUnit.first()
                _weightUnit.value = preferencesManager.weightUnit.first()
                _heightUnit.value = preferencesManager.heightUnit.first()
                _analyticsOptIn.value = preferencesManager.analyticsOptIn.first()
                _crashReportingOptIn.value = preferencesManager.crashReportingOptIn.first()
                _marketingOptIn.value = preferencesManager.marketingOptIn.first()
                _healthConnectEnabled.value = preferencesManager.healthConnectEnabled.first()
                _biometricEnabled.value = preferencesManager.biometricEnabled.first()
                _autoLockEnabled.value = preferencesManager.autoLockEnabled.first()
                _autoLockTimeoutMinutes.value = preferencesManager.autoLockTimeoutMinutes.first()
                _autoSyncEnabled.value = preferencesManager.autoSyncEnabled.first()
                _syncFrequencyMinutes.value = preferencesManager.syncFrequencyMinutes.first()
                
                // Carrega perfil do usuário se logado
                if (userId != null) {
                    // TODO: Carregar do AuthUseCases.getCurrentUser()
                    // Por enquanto usa valores padrão
                }
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar configurações"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ============ Account Settings ============

    fun setDisplayName(name: String) {
        _displayName.value = name
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setDateOfBirth(date: kotlinx.datetime.LocalDate?) {
        _dateOfBirth.value = date
    }

    fun setHeight(heightCm: Int) {
        _height.value = heightCm
    }

    fun setWeight(weightKg: Double) {
        _weight.value = weightKg
    }

    fun setGender(gender: String) {
        _gender.value = gender
    }

    // ============ Cycle Settings ============

    fun setAverageCycleLength(length: Int) {
        _averageCycleLength.value = length.coerceIn(21, 45)
    }

    fun setAveragePeriodLength(length: Int) {
        _averagePeriodLength.value = length.coerceIn(2, 10)
    }

    fun setCycleRegularity(regularity: String) {
        _cycleRegularity.value = regularity
    }

    fun setContraceptionMethod(method: String) {
        _contraceptionMethod.value = method
    }

    fun setLastPeriodDate(date: kotlinx.datetime.LocalDate?) {
        _lastPeriodDate.value = date
    }

    // ============ Notification Settings ============

    fun setPeriodReminderEnabled(enabled: Boolean) {
        _periodReminderEnabled.value = enabled
    }

    fun setPeriodReminderDaysBefore(days: Int) {
        _periodReminderDaysBefore.value = days.coerceIn(1, 7)
    }

    fun setFertileWindowReminderEnabled(enabled: Boolean) {
        _fertileWindowReminderEnabled.value = enabled
    }

    fun setOvulationReminderEnabled(enabled: Boolean) {
        _ovulationReminderEnabled.value = enabled
    }

    fun setPmsReminderEnabled(enabled: Boolean) {
        _pmsReminderEnabled.value = enabled
    }

    fun setPillReminderEnabled(enabled: Boolean) {
        _pillReminderEnabled.value = enabled
    }

    fun setPillReminderTime(time: String) {
        _pillReminderTime.value = time
    }

    fun setPartnerNotificationsEnabled(enabled: Boolean) {
        _partnerNotificationsEnabled.value = enabled
    }

    // ============ Display Settings ============

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        preferencesManager.setThemeMode(mode)
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        preferencesManager.setLanguage(lang)
    }

    fun setTemperatureUnit(unit: String) {
        _temperatureUnit.value = unit
    }

    fun setWeightUnit(unit: String) {
        _weightUnit.value = unit
    }

    fun setHeightUnit(unit: String) {
        _heightUnit.value = unit
    }

    fun setFirstDayOfWeek(day: Int) {
        _firstDayOfWeek.value = day.coerceIn(1, 7)
        preferencesManager.setFirstDayOfWeek(day)
    }

    fun setCalendarViewMode(mode: String) {
        _calendarViewMode.value = mode
        preferencesManager.setCalendarViewMode(mode)
    }

    // ============ Privacy Settings ============

    fun setAnalyticsOptIn(optIn: Boolean) {
        _analyticsOptIn.value = optIn
        preferencesManager.setAnalyticsOptIn(optIn)
    }

    fun setCrashReportingOptIn(optIn: Boolean) {
        _crashReportingOptIn.value = optIn
        preferencesManager.setCrashReportingOptIn(optIn)
    }

    fun setMarketingOptIn(optIn: Boolean) {
        _marketingOptIn.value = optIn
        preferencesManager.setMarketingOptIn(optIn)
    }

    fun setHealthConnectEnabled(enabled: Boolean) {
        _healthConnectEnabled.value = enabled
        preferencesManager.setHealthConnectEnabled(enabled)
    }

    // ============ Security Settings ============

    fun setBiometricEnabled(enabled: Boolean) {
        _biometricEnabled.value = enabled
        preferencesManager.setBiometricEnabled(enabled)
    }

    fun setAutoLockEnabled(enabled: Boolean) {
        _autoLockEnabled.value = enabled
        preferencesManager.setAutoLockEnabled(enabled)
    }

    fun setAutoLockTimeoutMinutes(minutes: Int) {
        _autoLockTimeoutMinutes.value = minutes.coerceIn(1, 60)
        preferencesManager.setAutoLockTimeoutMinutes(minutes)
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        _isSaving.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            authUseCases.changePassword(userId, currentPassword, newPassword)
            _saveSuccess.value = true
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao alterar senha"
            false
        } finally {
            _isSaving.value = false
        }
    }

    // ============ Data & Sync Settings ============

    fun setAutoSyncEnabled(enabled: Boolean) {
        _autoSyncEnabled.value = enabled
        preferencesManager.setAutoSyncEnabled(enabled)
    }

    fun setSyncFrequencyMinutes(minutes: Int) {
        _syncFrequencyMinutes.value = minutes.coerceIn(1, 1440)
        preferencesManager.setSyncFrequencyMinutes(minutes)
    }

    fun setAutoBackupEnabled(enabled: Boolean) {
        _autoBackupEnabled.value = enabled
    }

    fun setBackupFrequency(frequency: String) {
        _backupFrequency.value = frequency
    }

    // ============ Save All ============

    suspend fun saveAllSettings(): Boolean {
        _isSaving.value = true
        _error.value = null
        _saveSuccess.value = false
        
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            
            // Atualiza perfil com configurações de ciclo
            // TODO: Implementar updateProfile com todas as configurações
            
            _saveSuccess.value = true
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao salvar configurações"
            false
        } finally {
            _isSaving.value = false
        }
    }

    // ============ Dangerous Actions ============

    suspend fun exportData(format: String, options: ExportOptions): Boolean {
        _isSaving.value = true
        return try {
            // TODO: Implementar exportação via ExportUseCases
            _saveSuccess.value = true
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao exportar dados"
            false
        } finally {
            _isSaving.value = false
        }
    }

    suspend fun deleteAccount(password: String): Boolean {
        _isSaving.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            authUseCases.deleteAccount(userId, password)
            preferencesManager.clearAllSensitiveData()
            preferencesManager.setOnboardingCompleted(false)
            _saveSuccess.value = true
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao excluir conta"
            false
        } finally {
            _isSaving.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSaveSuccess() {
        _saveSuccess.value = false
    }
}

data class ExportOptions(
    val format: String = "json", // json, csv, pdf
    val includeCycles: Boolean = true,
    val includeSymptoms: Boolean = true,
    val includePredictions: Boolean = true,
    val includePregnancy: Boolean = true,
    val includePartnerData: Boolean = false,
    val includeHealthConnectData: Boolean = false,
    val includeSettings: Boolean = false,
    val includeContent: Boolean = false,
    val dateRangeStart: Long = 0,
    val dateRangeEnd: Long = System.currentTimeMillis(),
    val anonymize: Boolean = false
)