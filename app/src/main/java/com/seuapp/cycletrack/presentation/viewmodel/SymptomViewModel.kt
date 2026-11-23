package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.repository.SymptomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

private const val DEFAULT_USER_ID = "user_123"

@HiltViewModel
class SymptomViewModel @Inject constructor(
    private val symptomRepository: SymptomRepository
) : ViewModel() {

    private val _symptoms = MutableStateFlow<List<Symptom>>(emptyList())
    val symptoms = _symptoms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadSymptoms(date: LocalDate) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _symptoms.value = symptomRepository.getSymptomsForDate(DEFAULT_USER_ID, date.toEpochDay())
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error loading symptoms")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Alterna um item (chip) de uma categoria para a data informada: se já estiver
     * marcado, remove; senão, cria. O id é determinístico (userId+data+categoria+nome)
     * para que marcar/desmarcar seja idempotente.
     */
    fun toggleSymptom(date: LocalDate, category: String, name: String) {
        viewModelScope.launch {
            try {
                val id = "${DEFAULT_USER_ID}_${date.toEpochDay()}_${category}_$name"
                val existing = _symptoms.value.find { it.id == id }
                if (existing != null) {
                    symptomRepository.deleteSymptom(id)
                    _symptoms.value = _symptoms.value.filterNot { it.id == id }
                } else {
                    val symptom = Symptom(
                        id = id,
                        userId = DEFAULT_USER_ID,
                        date = date,
                        name = name,
                        category = category
                    )
                    symptomRepository.upsertSymptom(symptom)
                    _symptoms.value = _symptoms.value + symptom
                }
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error toggling symptom")
            }
        }
    }

    fun addSymptom(symptom: Symptom) {
        viewModelScope.launch {
            try {
                symptomRepository.upsertSymptom(symptom)
                _symptoms.value = _symptoms.value + symptom
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error adding symptom")
            }
        }
    }
}
