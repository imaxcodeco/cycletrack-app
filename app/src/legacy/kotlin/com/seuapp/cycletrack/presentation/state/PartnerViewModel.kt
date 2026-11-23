package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.PartnerConnection
import com.seuapp.cycletrack.domain.model.PartnerShareLevel
import com.seuapp.cycletrack.domain.usecase.partner.PartnerUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.async

/**
 * ViewModel para o modo Parceiro.
 * Gerencia convites, conexão ativa, preferências de compartilhamento e notificações.
 */
@HiltViewModel
class PartnerViewModel @Inject constructor(
    private val partnerUseCases: PartnerUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    private val _activePartner = MutableStateFlow<PartnerConnection?>(null)
    val activePartner = _activePartner.asStateFlow()

    private val _allPartners = MutableStateFlow<List<PartnerConnection>>(emptyList())
    val allPartners = _allPartners.asStateFlow()

    private val _pendingInvitationsSent = MutableStateFlow<List<PartnerConnection>>(emptyList())
    val pendingInvitationsSent = _pendingInvitationsSent.asStateFlow()

    private val _pendingInvitationsReceived = MutableStateFlow<List<PartnerConnection>>(emptyList())
    val pendingInvitationsReceived = _pendingInvitationsReceived.asStateFlow()

    private val _sharedData = MutableStateFlow<PartnerSharedData?>(null)
    val sharedData = _sharedData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _inviteEmail = MutableStateFlow("")
    val inviteEmail = _inviteEmail.asStateFlow()

    private val _selectedShareLevel = MutableStateFlow(PartnerShareLevel.CYCLE_ONLY)
    val selectedShareLevel = _selectedShareLevel.asStateFlow()

    // ============ Computed ============

    val isConnected = _activePartner.map { it != null && it.status == PartnerConnection.STATUS_ACTIVE }.distinctUntilChanged()

    val partnerName = _activePartner.map { it?.partnerDisplayName ?: "" }.distinctUntilChanged()

    val partnerAvatar = _activePartner.map { it?.partnerAvatarUrl ?: "" }.distinctUntilChanged()

    val shareLevelOptions = listOf(
        ShareLevelOption(PartnerShareLevel.FULL, "Completo", "Tudo: ciclos, sintomas, previsões, gravidez, humor, peso", "🔓"),
        ShareLevelOption(PartnerShareLevel.CYCLE_ONLY, "Apenas ciclo", "Datas de menstruação, duração do ciclo", "🩸"),
        ShareLevelOption(PartnerShareLevel.FERTILITY_ONLY, "Apenas fertilidade", "Janela fértil, ovulação, TPM", "🌱"),
        ShareLevelOption(PartnerShareLevel.INSIGHTS_ONLY, "Apenas insights", "Relatórios e estatísticas agregadas", "📊"),
        ShareLevelOption(PartnerShareLevel.CUSTOM, "Personalizado", "Você escolhe o que compartilhar", "⚙️")
    )

    // ============ Actions ============

    init {
        loadPartnerData()
    }

    fun loadPartnerData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                
                val activeDeferred = viewModelScope.async { partnerUseCases.getActivePartner(userId) }
                val allDeferred = viewModelScope.async { partnerUseCases.getPartners(userId) }
                val sentDeferred = viewModelScope.async { partnerUseCases.getPendingInvitationsSent(userId) }
                val receivedDeferred = viewModelScope.async { 
                    val email = preferencesManager.getUserEmail()
                    email?.let { partnerUseCases.getPendingInvitationsReceived(it) } ?: emptyList()
                }
                val sharedDeferred = viewModelScope.async { 
                    activeDeferred.await()?.let { partnerUseCases.getPartnerSharedData(it.id) } 
                }
                
                _activePartner.value = activeDeferred.await()
                _allPartners.value = allDeferred.await()
                _pendingInvitationsSent.value = sentDeferred.await()
                _pendingInvitationsReceived.value = receivedDeferred.await()
                _sharedData.value = sharedDeferred.await()
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar dados do parceiro"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ============ Invitation Actions ============

    fun setInviteEmail(email: String) {
        _inviteEmail.value = email
    }

    fun setSelectedShareLevel(level: PartnerShareLevel) {
        _selectedShareLevel.value = level
    }

    suspend fun invitePartner(): Boolean {
        _isLoading.value = true
        _error.value = null
        
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val email = _inviteEmail.value.trim()
            
            if (email.isBlank()) {
                _error.value = "Digite o email do parceiro"
                return false
            }
            
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _error.value = "Email inválido"
                return false
            }
            
            partnerUseCases.invitePartner(
                userId = userId,
                partnerEmail = email,
                partnerDisplayName = "",
                shareLevel = _selectedShareLevel.value
            )
            
            _inviteEmail.value = ""
            loadPartnerData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao enviar convite"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun resendInvitation(invitationCode: String): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            // TODO: Implementar reenvio de convite
            loadPartnerData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao reenviar convite"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun cancelInvitation(invitationCode: String): Boolean {
        _isLoading.value = true
        return try {
            partnerUseCases.declineInvitation(invitationCode)
            loadPartnerData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao cancelar convite"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun acceptInvitation(invitationCode: String): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val displayName = preferencesManager.getUserEmail()?.split("@").first() ?: "Usuário"
            
            partnerUseCases.acceptInvitation(
                invitationCode = invitationCode,
                partnerUserId = userId,
                partnerDisplayName = displayName,
                partnerAvatarUrl = ""
            )
            
            loadPartnerData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao aceitar convite"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun declineInvitation(invitationCode: String): Boolean {
        _isLoading.value = true
        return try {
            partnerUseCases.declineInvitation(invitationCode)
            loadPartnerData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao recusar convite"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // ============ Connection Management ============

    suspend fun revokePartner(partnerId: String): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            partnerUseCases.revokePartner(userId, partnerId)
            loadPartnerData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao remover parceiro"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // ============ Sharing Preferences ============

    suspend fun updateSharingPreferences(
        shareCycle: Boolean? = null,
        sharePeriodDates: Boolean? = null,
        shareOvulation: Boolean? = null,
        shareFertileWindow: Boolean? = null,
        sharePms: Boolean? = null,
        shareSymptoms: Boolean? = null,
        sharePredictions: Boolean? = null,
        sharePregnancy: Boolean? = null,
        shareInsights: Boolean? = null,
        shareMood: Boolean? = null,
        shareWeight: Boolean? = null,
        shareNotes: Boolean? = null
    ): Boolean {
        _isLoading.value = true
        return try {
            val partner = _activePartner.value ?: return false
            
            partnerUseCases.updateSharingPreferences(
                partnerId = partner.id,
                shareCycle = shareCycle,
                sharePeriodDates = sharePeriodDates,
                shareOvulation = shareOvulation,
                shareFertileWindow = shareFertileWindow,
                sharePms = sharePms,
                shareSymptoms = shareSymptoms,
                sharePredictions = sharePredictions,
                sharePregnancy = sharePregnancy,
                shareInsights = shareInsights,
                shareMood = shareMood,
                shareWeight = shareWeight,
                shareNotes = shareNotes
            )
            
            loadPartnerData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao atualizar compartilhamento"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun updateNotificationPreferences(
        notifyPeriodStart: Boolean? = null,
        notifyPeriodEnd: Boolean? = null,
        notifyFertileWindow: Boolean? = null,
        notifyOvulation: Boolean? = null,
        notifyPms: Boolean? = null,
        notifyPregnancy: Boolean? = null,
        notifySymptoms: Boolean? = null
    ): Boolean {
        _isLoading.value = true
        return try {
            val partner = _activePartner.value ?: return false
            
            partnerUseCases.updateNotificationPreferences(
                partnerId = partner.id,
                notifyPeriodStart = notifyPeriodStart,
                notifyPeriodEnd = notifyPeriodEnd,
                notifyFertileWindow = notifyFertileWindow,
                notifyOvulation = notifyOvulation,
                notifyPms = notifyPms,
                notifyPregnancy = notifyPregnancy,
                notifySymptoms = notifySymptoms
            )
            
            loadPartnerData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao atualizar notificações"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun updatePartnerDisplayInfo(displayName: String? = null, avatarUrl: String? = null): Boolean {
        _isLoading.value = true
        return try {
            val partner = _activePartner.value ?: return false
            
            partnerUseCases.updatePartnerDisplayInfo(
                partnerId = partner.id,
                displayName = displayName,
                avatarUrl = avatarUrl
            )
            
            loadPartnerData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao atualizar informações"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // ============ Shared Data ============

    data class PartnerSharedData(
        val currentCycle: com.seuapp.cycletrack.domain.model.Cycle? = null,
        val nextPeriod: com.seuapp.cycletrack.domain.model.Prediction? = null,
        val nextOvulation: com.seuapp.cycletrack.domain.model.Prediction? = null,
        val fertileWindow: Pair<com.seuapp.cycletrack.domain.model.Prediction?, com.seuapp.cycletrack.domain.model.Prediction?>? = null,
        val todaySymptoms: List<com.seuapp.cycletrack.domain.model.Symptom> = emptyList(),
        val isInFertileWindow: Boolean = false,
        val isInPms: Boolean = false,
        val pregnancy: com.seuapp.cycletrack.domain.model.Pregnancy? = null,
        val insights: com.seuapp.cycletrack.presentation.state.InsightsViewModel.CycleStatistics? = null
    )

    fun clearError() {
        _error.value = null
    }
}

data class ShareLevelOption(
    val level: PartnerShareLevel,
    val title: String,
    val description: String,
    val icon: String
)