package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Export
import com.seuapp.cycletrack.domain.usecase.export.ExportUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import import com.seuapp.cycletrack.util.toEpochMilliseconds
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * ViewModel para a tela de Exportação (LGPD/GDPR Art. 20).
 * Gerena criação, progresso e download de exportações de dados.
 */
@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportUseCases: ExportUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    private val _exports = MutableStateFlow<List<Export>>(emptyList())
    val exports = _exports.asStateFlow()

    private val _pendingExports = MutableStateFlow<List<Export>>(emptyList())
    val pendingExports = _pendingExports.asStateFlow()

    private val _currentExport = MutableStateFlow<Export?>(null)
    val currentExport = _currentExport.asStateFlow()

    private val _exportProgress = MutableStateFlow(0)
    val exportProgress = _exportProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting = _isExporting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Form options
    private val _selectedFormat = MutableStateFlow("json")
    val selectedFormat = _selectedFormat.asStateFlow()

    private val _includeCycles = MutableStateFlow(true)
    val includeCycles = _includeCycles.asStateFlow()

    private val _includeSymptoms = MutableStateFlow(true)
    val includeSymptoms = _includeSymptoms.asStateFlow()

    private val _includePredictions = MutableStateFlow(true)
    val includePredictions = _includePredictions.asStateFlow()

    private val _includePregnancy = MutableStateFlow(true)
    val includePregnancy = _includePregnancy.asStateFlow()

    private val _includePartnerData = MutableStateFlow(false)
    val includePartnerData = _includePartnerData.asStateFlow()

    private val _includeHealthConnectData = MutableStateFlow(false)
    val includeHealthConnectData = _includeHealthConnectData.asStateFlow()

    private val _includeSettings = MutableStateFlow(false)
    val includeSettings = _includeSettings.asStateFlow()

    private val _includeContent = MutableStateFlow(false)
    val includeContent = _includeContent.asStateFlow()

    private val _dateRangeStart = MutableStateFlow<LocalDate?>(null)
    val dateRangeStart = _dateRangeStart.asStateFlow()

    private val _dateRangeEnd = MutableStateFlow<LocalDate?>(null)
    val dateRangeEnd = _dateRangeEnd.asStateFlow()

    private val _anonymize = MutableStateFlow(false)
    val anonymize = _anonymize.asStateFlow()

    // ============ Computed ============

    val formatOptions = listOf(
        FormatOption("json", "JSON (completo)", "Estrutura completa com todos os dados aninhados", "📋"),
        FormatOption("csv", "CSV (planilhas)", "Tabelas planas para Excel/Google Sheets", "📊"),
        FormatOption("pdf", "PDF (relatório)", "Documento formatado para impressão/médico", "📄")
    )

    val dateRangeOptions = listOf(
        DateRangeOption("all", "Todos os dados", null, null),
        DateRangeOption("year", "Último ano", Clock.System.todayIn(TimeZone.currentSystemDefault()).minusYears(1), Clock.System.todayIn(TimeZone.currentSystemDefault())),
        DateRangeOption("6months", "Últimos 6 meses", Clock.System.todayIn(TimeZone.currentSystemDefault()).minusMonths(6), Clock.System.todayIn(TimeZone.currentSystemDefault())),
        DateRangeOption("3months", "Últimos 3 meses", Clock.System.todayIn(TimeZone.currentSystemDefault()).minusMonths(3), Clock.System.todayIn(TimeZone.currentSystemDefault())),
        DateRangeOption("custom", "Período personalizado", null, null)
    )

    val canExport = combine(_includeCycles, _includeSymptoms, _includePredictions, _includePregnancy, _includePartnerData, _includeHealthConnectData, _includeSettings, _includeContent) { 
        cycles, symptoms, predictions, pregnancy, partner, health, settings, content ->
        cycles || symptoms || predictions || pregnancy || partner || health || settings || content
    }.distinctUntilChanged()

    // ============ Actions ============

    init {
        loadExports()
    }

    fun loadExports() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                val allExports = exportUseCases.getExports(userId)
                _exports.value = allExports
                _pendingExports.value = allExports.filter { it.status == Export.STATUS_PENDING || it.status == Export.STATUS_PROCESSING }
                
                // Se há exportação em andamento, monitora progresso
                val processing = allExports.firstOrNull { it.status == Export.STATUS_PROCESSING }
                if (processing != null) {
                    _currentExport.value = processing
                    monitorExportProgress(processing.id)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar exportações"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun monitorExportProgress(exportId: String) {
        viewModelScope.launch {
            while (true) {
                try {
                    val export = exportUseCases.getExport(exportId)
                    export?.let {
                        _currentExport.value = it
                        _exportProgress.value = it.progress
                        
                        if (it.status == Export.STATUS_COMPLETED || it.status == Export.STATUS_FAILED || it.status == Export.STATUS_EXPIRED) {
                            loadExports() // Recarrega lista
                            break
                        }
                    } ?: break
                    
                    kotlinx.coroutines.delay(2000) // Poll a cada 2s
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    fun setFormat(format: String) {
        _selectedFormat.value = format
    }

    fun setIncludeCycles(include: Boolean) {
        _includeCycles.value = include
    }

    fun setIncludeSymptoms(include: Boolean) {
        _includeSymptoms.value = include
    }

    fun setIncludePredictions(include: Boolean) {
        _includePredictions.value = include
    }

    fun setIncludePregnancy(include: Boolean) {
        _includePregnancy.value = include
    }

    fun setIncludePartnerData(include: Boolean) {
        _includePartnerData.value = include
    }

    fun setIncludeHealthConnectData(include: Boolean) {
        _includeHealthConnectData.value = include
    }

    fun setIncludeSettings(include: Boolean) {
        _includeSettings.value = include
    }

    fun setIncludeContent(include: Boolean) {
        _includeContent.value = include
    }

    fun setDateRange(optionId: String) {
        val option = dateRangeOptions.firstOrNull { it.id == optionId }
        if (option != null) {
            _dateRangeStart.value = option.startDate
            _dateRangeEnd.value = option.endDate
        }
    }

    fun setCustomDateRange(start: LocalDate?, end: LocalDate?) {
        _dateRangeStart.value = start
        _dateRangeEnd.value = end
    }

    fun setAnonymize(anonymize: Boolean) {
        _anonymize.value = anonymize
    }

    suspend fun requestExport(): Boolean {
        _isExporting.value = true
        _error.value = null
        
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            
            if (!canExport.value) {
                _error.value = "Selecione pelo menos um tipo de dado para exportar"
                return false
            }
            
            val startMillis = _dateRangeStart.value?.atStartOfDay()?.toEpochMilliseconds() ?: 0
            val endMillis = _dateRangeEnd.value?.atTime(23, 59, 59)?.toEpochMilliseconds() ?: System.currentTimeMillis()
            
            val export = exportUseCases.createExport(
                userId = userId,
                format = _selectedFormat.value,
                includeCycles = _includeCycles.value,
                includeSymptoms = _includeSymptoms.value,
                includePredictions = _includePredictions.value,
                includePregnancy = _includePregnancy.value,
                includePartnerData = _includePartnerData.value,
                includeHealthConnectData = _includeHealthConnectData.value,
                includeSettings = _includeSettings.value,
                includeContent = _includeContent.value,
                dateRangeStart = startMillis,
                dateRangeEnd = endMillis,
                anonymize = _anonymize.value
            )
            
            _currentExport.value = export
            _exportProgress.value = 0
            monitorExportProgress(export.id)
            
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao solicitar exportação"
            false
        } finally {
            _isExporting.value = false
        }
    }

    suspend fun downloadExport(exportId: String): String? {
        _isLoading.value = true
        return try {
            val export = exportUseCases.getExport(exportId)
            if (export != null && export.status == Export.STATUS_COMPLETED && export.downloadUrl != null) {
                export.downloadUrl
            } else {
                _error.value = "Exportação não está pronta para download"
                null
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao obter download"
            null
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun deleteExport(exportId: String): Boolean {
        _isLoading.value = true
        return try {
            exportUseCases.deleteExport(exportId)
            loadExports()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao excluir exportação"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun retryExport(exportId: String): Boolean {
        _isLoading.value = true
        return try {
            exportUseCases.retryExport(exportId)
            loadExports()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao tentar novamente"
            false
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetForm() {
        _selectedFormat.value = "json"
        _includeCycles.value = true
        _includeSymptoms.value = true
        _includePredictions.value = true
        _includePregnancy.value = true
        _includePartnerData.value = false
        _includeHealthConnectData.value = false
        _includeSettings.value = false
        _includeContent.value = false
        _dateRangeStart.value = null
        _dateRangeEnd.value = null
        _anonymize.value = false
    }
}

data class FormatOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: String
)

data class DateRangeOption(
    val id: String,
    val title: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)