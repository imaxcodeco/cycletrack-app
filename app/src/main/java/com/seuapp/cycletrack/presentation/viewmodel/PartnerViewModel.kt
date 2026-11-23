package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.PartnerConnection
import com.seuapp.cycletrack.domain.repository.PartnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

private const val DEFAULT_USER_ID = "user_123"

@HiltViewModel
class PartnerViewModel @Inject constructor(
    private val partnerRepository: PartnerRepository
) : ViewModel() {
    private val _partner = MutableStateFlow<PartnerConnection?>(null)
    val partner = _partner.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadPartner(DEFAULT_USER_ID)
    }

    fun loadPartner(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _partner.value = partnerRepository.getActivePartner(userId)
            } catch (e: Exception) {
                Timber.e(e, "Error loading partner")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Gera um convite local (sem backend real): cria uma conexao pendente
     * com um codigo de 6 digitos para compartilhar com o parceiro.
     */
    fun generateInvite() {
        viewModelScope.launch {
            try {
                val code = (100000..999999).random(Random).toString()
                val connection = PartnerConnection(
                    id = UUID.randomUUID().toString(),
                    userId = DEFAULT_USER_ID,
                    inviteCode = code,
                    status = PartnerConnection.STATUS_PENDING
                )
                partnerRepository.upsertPartner(connection)
                _partner.value = connection
            } catch (e: Exception) {
                Timber.e(e, "Error generating invite")
            }
        }
    }

    /**
     * Simula a aceitacao do convite pelo parceiro (nao ha segundo
     * dispositivo real nesta sessao de testes).
     */
    fun simulatePartnerAccepted(partnerName: String) {
        viewModelScope.launch {
            try {
                val current = _partner.value ?: return@launch
                val updated = current.copy(
                    partnerId = UUID.randomUUID().toString(),
                    partnerName = partnerName.ifBlank { "Seu parceiro" },
                    status = PartnerConnection.STATUS_CONNECTED,
                    shareLevel = "cycle_summary"
                )
                partnerRepository.upsertPartner(updated)
                _partner.value = updated
            } catch (e: Exception) {
                Timber.e(e, "Error simulating partner connection")
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            try {
                _partner.value?.let { partnerRepository.deletePartner(it.id) }
                _partner.value = null
            } catch (e: Exception) {
                Timber.e(e, "Error disconnecting partner")
            }
        }
    }
}
