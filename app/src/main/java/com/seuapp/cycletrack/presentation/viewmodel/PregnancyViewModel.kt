package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Pregnancy
import com.seuapp.cycletrack.domain.repository.PregnancyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PregnancyViewModel @Inject constructor(
    private val pregnancyRepository: PregnancyRepository
) : ViewModel() {
    private val _pregnancy = MutableStateFlow<Pregnancy?>(null)
    val pregnancy = _pregnancy.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    fun loadPregnancy(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val pregnancy = pregnancyRepository.getActivePregnancy(userId)
                _pregnancy.value = pregnancy
            } catch (e: Exception) {
                Timber.e(e, "Error loading pregnancy")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
