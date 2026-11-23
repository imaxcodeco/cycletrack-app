package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Pregnancy
import com.seuapp.cycletrack.domain.model.PregnancyAppointment
import com.seuapp.cycletrack.domain.model.PregnancyTest
import com.seuapp.cycletrack.domain.usecase.pregnancy.PregnancyUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.combine
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * ViewModel para a tela de Gravidez.
 * Gerencia acompanhamento semanal, consultas, exames, peso e desenvolvimento do bebê.
 */
@HiltViewModel
class PregnancyViewModel @Inject constructor(
    private val pregnancyUseCases: PregnancyUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    private val _activePregnancy = MutableStateFlow<Pregnancy?>(null)
    val activePregnancy = _activePregnancy.asStateFlow()

    private val _allPregnancies = MutableStateFlow<List<Pregnancy>>(emptyList())
    val allPregnancies = _allPregnancies.asStateFlow()

    private val _appointments = MutableStateFlow<List<PregnancyAppointment>>(emptyList())
    val appointments = _appointments.asStateFlow()

    private val _upcomingAppointments = MutableStateFlow<List<PregnancyAppointment>>(emptyList())
    val upcomingAppointments = _upcomingAppointments.asStateFlow()

    private val _tests = MutableStateFlow<List<PregnancyTest>>(emptyList())
    val tests = _tests.asStateFlow()

    private val _currentWeek = MutableStateFlow(0)
    val currentWeek = _currentWeek.asStateFlow()

    private val _trimester = MutableStateFlow(0)
    val trimester = _trimester.asStateFlow()

    private val _daysUntilDue = MutableStateFlow(0)
    val daysUntilDue = _daysUntilDue.asStateFlow()

    private val _babySize = MutableStateFlow("")
    val babySize = _babySize.asStateFlow()

    private val _babyWeight = MutableStateFlow(0)
    val babyWeight = _babyWeight.asStateFlow()

    private val _babyLength = MutableStateFlow(0.0)
    val babyLength = _babyLength.asStateFlow()

    private val _developmentMilestones = MutableStateFlow<List<Milestone>>(emptyList())
    val developmentMilestones = _developmentMilestones.asStateFlow()

    private val _weightHistory = MutableStateFlow<List<WeightEntry>>(emptyList())
    val weightHistory = _weightHistory.asStateFlow()

    private val _symptoms = MutableStateFlow<List<com.seuapp.cycletrack.domain.model.Symptom>>(emptyList())
    val symptoms = _symptoms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // ============ Data Classes ============

    data class Milestone(
        val week: Int,
        val title: String,
        val description: String,
        val icon: String,
        val completed: Boolean
    )

    data class WeightEntry(
        val date: LocalDate,
        val weight: Double,
        val week: Int,
        val notes: String
    )

    // ============ Computed ============

    val progressPercentage = combine(_activePregnancy, _currentWeek) { pregnancy, week ->
        if (pregnancy != null) {
            (week.toDouble() / 40 * 100).coerceIn(0.0, 100.0)
        } else 0.0
    }.distinctUntilChanged()

    val trimesterColor = combine(_trimester) { trimester ->
        when (trimester) {
            1 -> "#3B82F6" // Azul
            2 -> "#22C55E" // Verde
            3 -> "#E84D6E" // Rosa/Vermelho
            else -> "#6366F1"
        }
    }.distinctUntilChanged()

    // ============ Actions ============

    init {
        loadPregnancyData()
    }

    fun loadPregnancyData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                
                val pregnancyDeferred = viewModelScope.async { pregnancyUseCases.getActivePregnancy(userId) }
                val allPregnanciesDeferred = viewModelScope.async { pregnancyUseCases.getPregnancies(userId) }
                val appointmentsDeferred = viewModelScope.async { 
                    pregnancyDeferred.await()?.let { pregnancyUseCases.getAppointments(it.id) } ?: emptyList() 
                }
                val upcomingDeferred = viewModelScope.async { 
                    pregnancyDeferred.await()?.let { pregnancyUseCases.getUpcomingAppointments(it.id, System.currentTimeMillis()) } ?: emptyList() 
                }
                val testsDeferred = viewModelScope.async { 
                    pregnancyDeferred.await()?.let { pregnancyUseCases.getTests(it.id) } ?: emptyList() 
                }
                
                val pregnancy = pregnancyDeferred.await()
                _activePregnancy.value = pregnancy
                _allPregnancies.value = allPregnanciesDeferred.await()
                _appointments.value = appointmentsDeferred.await()
                _upcomingAppointments.value = upcomingDeferred.await()
                _tests.value = testsDeferred.await()
                
                if (pregnancy != null) {
                    calculatePregnancyInfo(pregnancy)
                    loadMilestones(pregnancy)
                    loadWeightHistory(pregnancy)
                }
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar dados da gravidez"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculatePregnancyInfo(pregnancy: Pregnancy) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startDate = pregnancy.startDate
        val dueDate = pregnancy.dueDate
        
        // Semana atual
        val week = startDate.daysUntil(today).toInt() / 7 + 1
        _currentWeek.value = week.coerceIn(1, 42)
        
        // Trimestre
        _trimester.value = when {
            week <= 13 -> 1
            week <= 27 -> 2
            else -> 3
        }
        
        // Dias até o parto
        _daysUntilDue.value = today.daysUntil(dueDate).toInt().coerceAtLeast(0)
        
        // Tamanho do bebê (aproximado)
        _babySize.value = getBabySizeComparison(week)
        _babyWeight.value = getBabyWeightGrams(week)
        _babyLength.value = getBabyLengthCm(week)
    }

    private fun loadMilestones(pregnancy: Pregnancy) {
        val week = _currentWeek.value
        val milestones = mutableListOf<Milestone>()
        
        // Marcos de desenvolvimento por semana
        val allMilestones = mapOf(
            4 to Milestone(4, "Implantação", "O embrião se implanta no útero", "🌱", week >= 4),
            5 to Milestone(5, "Coração batendo", "Primeiros batimentos cardíacos detectáveis", "💓", week >= 5),
            8 to Milestone(8, "Formação dos órgãos", "Principais órgãos começam a se formar", "🫀", week >= 8),
            12 to Milestone(12, "Fim do 1º trimestre", "Risco de aborto espontâneo diminui significativamente", "✅", week >= 12),
            16 to Milestone(16, "Movimentos", "Pode começar a sentir os primeiros movimentos", "🤸", week >= 16),
            20 to Milestone(20, "Ultrassom morfológico", "Exame detalhado da anatomia do bebê", "📷", week >= 20),
            24 to Milestone(24, "Viabilidade", "Bebê tem chance de sobrevivência se nascer prematuro", "🏥", week >= 24),
            28 to Milestone(28, "Fim do 2º trimestre", "Início do 3º trimestre", "📅", week >= 28),
            32 to Milestone(32, "Pulmões amadurecendo", "Produção de surfactante pulmonar", "🫁", week >= 32),
            36 to Milestone(36, "Posição cefálica", "Bebê vira de cabeça para baixo", "🔄", week >= 36),
            37 to Milestone(37, "Termo precoce", "Bebê considerado a termo", "🎯", week >= 37),
            40 to Milestone(40, "Data prevista do parto", "Chegou a hora!", "👶", week >= 40)
        )
        
        allMilestones.entries.sortedBy { it.key }.forEach { (_, milestone) ->
            milestones.add(milestone)
        }
        
        _developmentMilestones.value = milestones
    }

    private fun loadWeightHistory(pregnancy: Pregnancy) {
        // TODO: Carregar do repositório de sintomas (peso registrado como sintoma lifestyle)
        // Por enquanto, dados mock
        _weightHistory.value = listOf(
            WeightEntry(pregnancy.startDate, 60.0, 0, "Peso pré-gestacional"),
            WeightEntry(pregnancy.startDate.plusWeeks(12), 62.5, 12, "+2.5 kg"),
            WeightEntry(pregnancy.startDate.plusWeeks(20), 65.0, 20, "+5.0 kg"),
            WeightEntry(Clock.System.todayIn(TimeZone.currentSystemDefault()), 67.0, _currentWeek.value, "+7.0 kg total")
        )
    }

    // ============ Pregnancy Management ============

    suspend fun startPregnancy(
        dueDate: LocalDate,
        startDate: LocalDate? = null,
        conceptionMethod: String = "natural"
    ): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            
            val calculatedStartDate = startDate ?: dueDate.minusWeeks(40)
            
            val pregnancy = pregnancyUseCases.startPregnancy(
                userId = userId,
                dueDate = dueDate,
                startDate = calculatedStartDate,
                conceptionMethod = conceptionMethod
            )
            
            _activePregnancy.value = pregnancy
            calculatePregnancyInfo(pregnancy)
            loadMilestones(pregnancy)
            
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao iniciar gravidez"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun endPregnancy(
        outcome: com.seuapp.cycletrack.domain.model.PregnancyOutcome,
        outcomeDate: LocalDate,
        notes: String = "",
        babyName: String = "",
        babyGender: String = "",
        babyWeightGrams: Int = 0,
        babyHeightCm: Float = 0f,
        babyApgarScore: Int = 0
    ): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val pregnancy = _activePregnancy.value ?: return false
            
            pregnancyUseCases.completePregnancy(
                pregnancyId = pregnancy.id,
                outcome = outcome,
                outcomeDate = outcomeDate,
                outcomeNotes = notes,
                babyName = babyName,
                babyGender = babyGender,
                babyWeightGrams = babyWeightGrams,
                babyHeightCm = babyHeightCm,
                babyApgarScore = babyApgarScore
            )
            
            _activePregnancy.value = null
            loadPregnancyData()
            
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao finalizar gravidez"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun markPregnancyLost(
        outcome: com.seuapp.cycletrack.domain.model.PregnancyOutcome,
        outcomeDate: LocalDate,
        notes: String
    ): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val pregnancy = _activePregnancy.value ?: return false
            
            pregnancyUseCases.markPregnancyLost(
                pregnancyId = pregnancy.id,
                outcome = outcome,
                outcomeDate = outcomeDate,
                notes = notes
            )
            
            _activePregnancy.value = null
            loadPregnancyData()
            
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao registrar perda"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // ============ Appointments ============

    suspend fun addAppointment(
        title: String,
        date: LocalDateTime,
        doctor: String,
        location: String,
        notes: String,
        type: String = "prenatal"
    ): Boolean {
        _isLoading.value = true
        return try {
            val pregnancy = _activePregnancy.value ?: return false
            
            val appointment = PregnancyAppointment.create(
                pregnancyId = pregnancy.id,
                title = title,
                dateTime = date,
                doctor = doctor,
                location = location,
                notes = notes,
                type = type
            )
            
            pregnancyUseCases.upsertAppointment(appointment)
            loadPregnancyData()
            
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao adicionar consulta"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun deleteAppointment(appointmentId: String): Boolean {
        _isLoading.value = true
        return try {
            pregnancyUseCases.deleteAppointment(appointmentId)
            loadPregnancyData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao excluir consulta"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // ============ Tests ============

    suspend fun addTest(
        name: String,
        date: LocalDate,
        result: String,
        notes: String,
        type: String = "blood"
    ): Boolean {
        _isLoading.value = true
        return try {
            val pregnancy = _activePregnancy.value ?: return false
            
            val test = PregnancyTest.create(
                pregnancyId = pregnancy.id,
                name = name,
                date = date,
                result = result,
                notes = notes,
                type = type
            )
            
            pregnancyUseCases.upsertTest(test)
            loadPregnancyData()
            
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao adicionar exame"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun deleteTest(testId: String): Boolean {
        _isLoading.value = true
        return try {
            pregnancyUseCases.deleteTest(testId)
            loadPregnancyData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao excluir exame"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // ============ Weight Tracking ============

    suspend fun logWeight(weight: Double, date: LocalDate, notes: String = ""): Boolean {
        _isLoading.value = true
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val pregnancy = _activePregnancy.value ?: return false
            
            // Registra como sintoma de lifestyle
            // TODO: Implementar uso do SymptomUseCases
            
            loadPregnancyData()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao registrar peso"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // ============ Helpers ============

    private fun getBabySizeComparison(week: Int): String {
        return when {
            week <= 4 -> "Grão de papoula 🌱"
            week <= 5 -> "Gergelim 🌰"
            week <= 6 -> "Lentilha 🟤"
            week <= 7 -> "Mirtilo 🫐"
            week <= 8 -> "Framboesa 🍇"
            week <= 9 -> "Cereja 🍒"
            week <= 10 -> "Morango 🍓"
            week <= 11 -> "Figo 🍈"
            week <= 12 -> "Lima 🍋"
            week <= 13 -> "Pêssego 🍑"
            week <= 14 -> "Limão 🍋"
            week <= 15 -> "Maçã 🍎"
            week <= 16 -> "Abacate 🥑"
            week <= 17 -> "Batata 🥔"
            week <= 18 -> "Pimentão 🌶️"
            week <= 19 -> "Manga 🥭"
            week <= 20 -> "Banana 🍌"
            week <= 21 -> "Cenoura 🥕"
            week <= 22 -> "Papaia 🍈"
            week <= 23 -> "Berinjela 🍆"
            week <= 24 -> "Milho 🌽"
            week <= 25 -> "Aipo 🥬"
            week <= 26 -> "Abobrinha 🥒"
            week <= 27 -> "Couve-flor 🥦"
            week <= 28 -> "Beringela grande 🍆"
            week <= 29 -> "Abóbora 🎃"
            week <= 30 -> "Repolho 🥬"
            week <= 31 -> "Coco 🥥"
            week <= 32 -> "Jaca 🍈"
            week <= 33 -> "Abacaxi 🍍"
            week <= 34 -> "Melão 🍈"
            week <= 35 -> "Melancia 🍉"
            week <= 36 -> "Alface romana 🥬"
            week <= 37 -> "Acelga 🥬"
            week <= 38 -> "Rabanete 🌱"
            week <= 39 -> "Abóbora pequena 🎃"
            week <= 40 -> "Melancia pequena 🍉"
            else -> "Pronto para nascer! 👶"
        }
    }

    private fun getBabyWeightGrams(week: Int): Int {
        // Aproximação baseada em curvas de crescimento fetal
        return when {
            week <= 8 -> 1
            week <= 12 -> 14
            week <= 16 -> 100
            week <= 20 -> 300
            week <= 24 -> 600
            week <= 28 -> 1100
            week <= 32 -> 1800
            week <= 36 -> 2700
            week <= 40 -> 3400
            else -> 3500
        }
    }

    private fun getBabyLengthCm(week: Int): Double {
        return when {
            week <= 8 -> 1.6
            week <= 12 -> 5.4
            week <= 16 -> 11.6
            week <= 20 -> 25.6
            week <= 24 -> 30.0
            week <= 28 -> 37.6
            week <= 32 -> 42.4
            week <= 36 -> 47.4
            week <= 40 -> 51.2
            else -> 52.0
        }
    }

    fun clearError() {
        _error.value = null
    }
}