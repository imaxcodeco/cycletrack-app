package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.usecase.auth.AuthUseCases
import com.seuapp.cycletrack.domain.usecase.cycle.CycleUseCases
import com.seuapp.cycletrack.domain.usecase.symptom.SymptomUseCases
import com.seuapp.cycletrack.domain.usecase.pregnancy.PregnancyUseCases
import com.seuapp.cycletrack.domain.usecase.partner.PartnerUseCases
import com.seuapp.cycletrack.domain.usecase.subscription.SubscriptionUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.async
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * ViewModel para a tela de Perfil (aba Profile).
 * Gerencia informações do usuário, estatísticas gerais e navegação para recursos.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val cycleUseCases: CycleUseCases,
    private val symptomUseCases: SymptomUseCases,
    private val pregnancyUseCases: PregnancyUseCases,
    private val partnerUseCases: PartnerUseCases,
    private val subscriptionUseCases: SubscriptionUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _cycleCount = MutableStateFlow(0)
    val cycleCount = _cycleCount.asStateFlow()

    private val _symptomCount = MutableStateFlow(0)
    val symptomCount = _symptomCount.asStateFlow()

    private val _ovulationCount = MutableStateFlow(0)
    val ovulationCount = _ovulationCount.asStateFlow()

    private val _daysSinceFirstCycle = MutableStateFlow(0)
    val daysSinceFirstCycle = _daysSinceFirstCycle.asStateFlow()

    private val _subscriptionStatus = MutableStateFlow<String>("free")
    val subscriptionStatus = _subscriptionStatus.asStateFlow()

    private val _activePregnancy = MutableStateFlow<com.seuapp.cycletrack.domain.model.Pregnancy?>(null)
    val activePregnancy = _activePregnancy.asStateFlow()

    private val _partnerConnection = MutableStateFlow<com.seuapp.cycletrack.domain.model.PartnerConnection?>(null)
    val partnerConnection = _partnerConnection.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // ============ Actions ============

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                
                // Carrega em paralelo
                val profileDeferred = viewModelScope.async { authUseCases.getCurrentUser() }
                val cycleCountDeferred = viewModelScope.async { cycleUseCases.getCycleCount(userId) }
                val symptomCountDeferred = viewModelScope.async { symptomUseCases.getSymptomCount(userId) }
                val subscriptionDeferred = viewModelScope.async { subscriptionUseCases.getDisplayStatus(userId) }
                val pregnancyDeferred = viewModelScope.async { pregnancyUseCases.getActivePregnancy(userId) }
                val partnerDeferred = viewModelScope.async { partnerUseCases.getActivePartner(userId) }
                
                _userProfile.value = profileDeferred.await()
                _cycleCount.value = cycleCountDeferred.await()
                _symptomCount.value = symptomCountDeferred.await()
                _subscriptionStatus.value = subscriptionDeferred.await()
                _activePregnancy.value = pregnancyDeferred.await()
                _partnerConnection.value = partnerDeferred.await()
                
                // Calcula ovulações e dias desde primeiro ciclo
                val cycles = cycleUseCases.getCycleHistory(userId, 100)
                _ovulationCount.value = cycles.count { it.ovulationDate != null }
                
                val firstCycle = cycles.lastOrNull()
                if (firstCycle != null) {
                    _daysSinceFirstCycle.value = firstCycle.startDate.daysUntil(javax.datetime.Clock.System.todayIn(TimeZone.currentSystemDefault())).toInt()
                }
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar perfil"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ============ Account Actions ============

    suspend fun updateDisplayName(name: String): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val profile = _userProfile.value?.copy(displayName = name) ?: return false
            authUseCases.updateProfile(userId, profile)
            _userProfile.value = profile
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao atualizar nome"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun updateEmail(email: String): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            authUseCases.updateEmail(userId, email)
            preferencesManager.setUserEmail(email)
            _userProfile.value = _userProfile.value?.copy(email = email)
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao atualizar email"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            authUseCases.changePassword(userId, currentPassword, newPassword)
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao alterar senha"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun logout(): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            authUseCases.logout(userId)
            preferencesManager.clearAuthTokens()
            preferencesManager.clearAllSensitiveData()
            _userProfile.value = null
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao sair"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun deleteAccount(password: String): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            authUseCases.deleteAccount(userId, password)
            preferencesManager.clearAllSensitiveData()
            preferencesManager.setOnboardingCompleted(false)
            _userProfile.value = null
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao excluir conta"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // ============ Navigation Actions ============

    data class FeatureItem(
        val id: String,
        val icon: String,
        val title: String,
        val subtitle: String,
        val color: String,
        val badge: String? = null,
        val action: () -> Unit
    )

    fun getFeatureItems(
        onPregnancy: () -> Unit,
        onPartner: () -> Unit,
        onPremium: () -> Unit,
        onSettings: () -> Unit
    ): List<FeatureItem> {
        val colors = com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors()
        val isPremium = _subscriptionStatus.value != "free"
        val isPregnant = _activePregnancy.value != null
        val hasPartner = _partnerConnection.value != null
        
        return listOf(
            FeatureItem(
                id = "pregnancy",
                icon = "🤰",
                title = "Gravidez",
                subtitle = if (isPregnant) "Acompanhamento ativo" else "Modo gravidez",
                color = "#${colors.trimester2.toArgb().toString(16).substring(2).uppercase()}",
                badge = if (isPregnant) "Ativo" else null,
                action = onPregnancy
            ),
            FeatureItem(
                id = "partner",
                icon = "👫",
                title = "Parceiro",
                subtitle = if (hasPartner) "Compartilhando ciclo" else "Convidar parceiro",
                color = "#${colors.tertiary.toArgb().toString(16).substring(2).uppercase()}",
                badge = if (hasPartner) "Conectado" else null,
                action = onPartner
            ),
            FeatureItem(
                id = "premium",
                icon = "✨",
                title = "Premium",
                subtitle = if (isPremium) "Assinatura ativa" else "Desbloquear recursos",
                color = "#${colors.primary.toArgb().toString(16).substring(2).uppercase()}",
                badge = if (isPremium) "Pro" else null,
                action = onPremium
            ),
            FeatureItem(
                id = "settings",
                icon = "⚙️",
                title = "Configurações",
                subtitle = "Personalize seu app",
                color = "#${colors.onSurfaceVariant.toArgb().toString(16).substring(2).uppercase()}",
                action = onSettings
            )
        )
    }

    data class QuickLinkItem(
        val id: String,
        val icon: String,
        val title: String,
        val subtitle: String,
        val action: () -> Unit
    )

    fun getQuickLinks(
        onMedicalReport: () -> Unit,
        onExportData: () -> Unit,
        onSyncNow: () -> Unit,
        onHelp: () -> Unit,
        onLegal: () -> Unit
    ): List<QuickLinkItem> {
        return listOf(
            QuickLinkItem("medical_report", "📊", "Relatório médico", "Gerar PDF para consulta", onMedicalReport),
            QuickLinkItem("export_data", "📤", "Exportar dados", "JSON, CSV ou PDF (LGPD)", onExportData),
            QuickLinkItem("sync_now", "🔄", "Sincronizar agora", "Backup e Health Connect", onSyncNow),
            QuickLinkItem("help", "❓", "Ajuda e suporte", "FAQ e contato", onHelp),
            QuickLinkItem("legal", "📄", "Termos e privacidade", "Políticas do app", onLegal)
        )
    }

    // ============ Stats ============

    data class StatItem(
        val icon: String,
        val value: String,
        val label: String
    )

    fun getStats(): List<StatItem> {
        return listOf(
            StatItem("🩸", _cycleCount.value.toString(), "Ciclos"),
            StatItem("📝", _symptomCount.value.toString(), "Sintomas"),
            StatItem("🥚", _ovulationCount.value.toString(), "Ovulações"),
            StatItem("📅", "${_daysSinceFirstCycle.value / 365}a ${(_daysSinceFirstCycle.value % 365) / 30}m", "Usuária")
        )
    }

    fun clearError() {
        _error.value = null
    }
}