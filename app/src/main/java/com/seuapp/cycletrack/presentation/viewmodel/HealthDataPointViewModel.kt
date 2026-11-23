package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.repository.HealthDataPointRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HealthDataPointViewModel @Inject constructor(
    private val healthDataPointRepository: HealthDataPointRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun syncHealthData(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // TODO: Implement when HealthDataPointRepository is ready
            } catch (e: Exception) {
                Timber.e(e, "Error syncing health data")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
