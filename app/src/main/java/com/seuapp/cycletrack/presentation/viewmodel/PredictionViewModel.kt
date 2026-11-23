package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.repository.PredictionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PredictionViewModel @Inject constructor(
    private val predictionRepository: PredictionRepository
) : ViewModel() {
    private val _predictions = MutableStateFlow<List<Prediction>>(emptyList())
    val predictions = _predictions.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    fun addPrediction(prediction: Prediction) {
        viewModelScope.launch {
            try {
                predictionRepository.upsertPrediction(prediction)
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error adding prediction")
            }
        }
    }
}
