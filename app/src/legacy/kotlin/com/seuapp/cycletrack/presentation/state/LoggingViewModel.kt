package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.model.SymptomSeverity
import com.seuapp.cycletrack.domain.usecase.cycle.CycleUseCases
import com.seuapp.cycletrack.domain.usecase.symptom.SymptomUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * ViewModel para a tela de Registro (Logging).
 * Gerencia registro de menstruação, sintomas, peso, temperatura basal e humor.
 */
@HiltViewModel
class LoggingViewModel @Inject constructor(
    private val cycleUseCases: CycleUseCases,
    private val symptomUseCases: SymptomUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ Period Logging State ============
    
    private val _periodLoggingState = MutableStateFlow(PeriodLoggingState())
    val periodLoggingState = _periodLoggingState.asStateFlow()

    data class PeriodLoggingState(
        val isLoggingStart: Boolean = true,
        val startDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        val endDate: LocalDate? = null,
        val flowLevel: FlowLevel = FlowLevel.MEDIUM,
        val notes: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false
    )

    enum class FlowLevel(val value: Int, val label: String) {
        SPOTTING(1, "Escape"),
        LIGHT(2, "Leve"),
        MEDIUM(3, "Médio"),
        HEAVY(4, "Intenso")
    }

    // ============ Symptom Logging State ============

    private val _symptomLoggingState = MutableStateFlow(SymptomLoggingState())
    val symptomLoggingState = _symptomLoggingState.asStateFlow()

    data class SymptomLoggingState(
        val selectedSymptoms: Map<String, SymptomInput> = emptyMap(),
        val selectedDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false
    )

    data class SymptomInput(
        val name: String,
        val category: String,
        val severity: SymptomSeverity = SymptomSeverity.MILD,
        val notes: String = ""
    )

    // ============ Weight Logging State ============

    private val _weightLoggingState = MutableStateFlow(WeightLoggingState())
    val weightLoggingState = _weightLoggingState.asStateFlow()

    data class WeightLoggingState(
        val weight: Double = 0.0,
        val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        val notes: String = "",
        val unit: String = "kg", // kg, lbs
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false
    )

    // ============ Temperature Logging State ============

    private val _temperatureLoggingState = MutableStateFlow(TemperatureLoggingState())
    val temperatureLoggingState = _temperatureLoggingState.asStateFlow()

    data class TemperatureLoggingState(
        val temperature: Double = 0.0,
        val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        val time: String = "06:00", // HH:mm
        val notes: String = "",
        val unit: String = "celsius", // celsius, fahrenheit
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false
    )

    // ============ Mood Logging State ============

    private val _moodLoggingState = MutableStateFlow(MoodLoggingState())
    val moodLoggingState = _moodLoggingState.asStateFlow()

    data class MoodLoggingState(
        val mood: MoodType = MoodType.NEUTRAL,
        val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        val notes: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false
    )

    enum class MoodType(val value: Int, val label: String, val emoji: String) {
        VERY_SAD(1, "Muito triste", "😭"),
        SAD(2, "Triste", "😢"),
        NEUTRAL(3, "Neutro", "😐"),
        HAPPY(4, "Feliz", "😊"),
        VERY_HAPPY(5, "Muito feliz", "😄"),
        ANXIOUS(6, "Ansiosa", "😰"),
        IRRITATED(7, "Irritada", "😠"),
        TIRED(8, "Cansada", "😴")
    }

    // ============ Symptom Catalog ============

    val symptomCatalog = SymptomCatalog.allCategories

    // ============ Actions - Period ============

    fun setPeriodLoggingStart(isStart: Boolean) {
        _periodLoggingState.value = _periodLoggingState.value.copy(isLoggingStart = isStart)
    }

    fun setPeriodStartDate(date: LocalDate) {
        _periodLoggingState.value = _periodLoggingState.value.copy(startDate = date)
    }

    fun setPeriodEndDate(date: LocalDate?) {
        _periodLoggingState.value = _periodLoggingState.value.copy(endDate = date)
    }

    fun setPeriodFlowLevel(level: FlowLevel) {
        _periodLoggingState.value = _periodLoggingState.value.copy(flowLevel = level)
    }

    fun setPeriodNotes(notes: String) {
        _periodLoggingState.value = _periodLoggingState.value.copy(notes = notes)
    }

    suspend fun savePeriodLog(): Boolean {
        _periodLoggingState.value = _periodLoggingState.value.copy(isLoading = true, error = null)
        
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val state = _periodLoggingState.value
            
            if (state.isLoggingStart) {
                // Início de menstruação
                val cycle = cycleUseCases.logPeriodStart(
                    userId = userId,
                    startDate = state.startDate,
                    notes = state.notes
                )
                _periodLoggingState.value = _periodLoggingState.value.copy(
                    isLoading = false,
                    success = true,
                    isLoggingStart = false,
                    endDate = state.startDate.plusDays(preferencesManager.getUserId()?.let { 
                        cycleUseCases.getCycleStatistics(userId).averagePeriodLength ?: 5 } ?: 5)
                )
            } else {
                // Fim de menstruação - precisa do cycleId do ciclo atual
                val currentCycle = cycleUseCases.getCurrentCycle(userId)
                if (currentCycle != null && state.endDate != null) {
                    cycleUseCases.logPeriodEnd(
                        userId = userId,
                        cycleId = currentCycle.id,
                        endDate = state.endDate!!,
                        notes = state.notes
                    )
                }
                _periodLoggingState.value = _periodLoggingState.value.copy(
                    isLoading = false,
                    success = true
                )
            }
            true
        } catch (e: Exception) {
            _periodLoggingState.value = _periodLoggingState.value.copy(
                isLoading = false,
                error = e.message ?: "Erro ao salvar menstruação"
            )
            false
        }
    }

    fun resetPeriodLogging() {
        _periodLoggingState.value = PeriodLoggingState()
    }

    // ============ Actions - Symptoms ============

    fun toggleSymptom(symptomName: String, category: String) {
        val current = _symptomLoggingState.value.selectedSymptoms
        if (current.containsKey(symptomName)) {
            _symptomLoggingState.value = _symptomLoggingState.value.copy(
                selectedSymptoms = current - symptomName
            )
        } else {
            _symptomLoggingState.value = _symptomLoggingState.value.copy(
                selectedSymptoms = current + (symptomName to SymptomInput(
                    name = symptomName,
                    category = category
                ))
            )
        }
    }

    fun updateSymptomSeverity(symptomName: String, severity: SymptomSeverity) {
        val current = _symptomLoggingState.value.selectedSymptoms
        current[symptomName]?.let { input ->
            _symptomLoggingState.value = _symptomLoggingState.value.copy(
                selectedSymptoms = current + (symptomName to input.copy(severity = severity))
            )
        }
    }

    fun updateSymptomNotes(symptomName: String, notes: String) {
        val current = _symptomLoggingState.value.selectedSymptoms
        current[symptomName]?.let { input ->
            _symptomLoggingState.value = _symptomLoggingState.value.copy(
                selectedSymptoms = current + (symptomName to input.copy(notes = notes))
            )
        }
    }

    fun setSymptomDate(date: LocalDate) {
        _symptomLoggingState.value = _symptomLoggingState.value.copy(selectedDate = date)
    }

    suspend fun saveSymptoms(): Boolean {
        _symptomLoggingState.value = _symptomLoggingState.value.copy(isLoading = true, error = null)
        
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val state = _symptomLoggingState.value
            val cycle = cycleUseCases.getCurrentCycle(userId)
            
            val cycleId = cycle?.id ?: java.util.UUID.randomUUID().toString()
            
            val symptoms = state.selectedSymptoms.values.map { input ->
                Symptom.create(
                    userId = userId,
                    cycleId = cycleId,
                    date = state.selectedDate,
                    category = input.category,
                    name = input.name,
                    severity = input.severity,
                    notes = input.notes
                )
            }
            
            symptomUseCases.logSymptoms(userId, symptoms)
            
            _symptomLoggingState.value = _symptomLoggingState.value.copy(
                isLoading = false,
                success = true,
                selectedSymptoms = emptyMap()
            )
            true
        } catch (e: Exception) {
            _symptomLoggingState.value = _symptomLoggingState.value.copy(
                isLoading = false,
                error = e.message ?: "Erro ao salvar sintomas"
            )
            false
        }
    }

    fun resetSymptomLogging() {
        _symptomLoggingState.value = SymptomLoggingState()
    }

    // ============ Actions - Weight ============

    fun setWeight(weight: Double) {
        _weightLoggingState.value = _weightLoggingState.value.copy(weight = weight)
    }

    fun setWeightDate(date: LocalDate) {
        _weightLoggingState.value = _weightLoggingState.value.copy(date = date)
    }

    fun setWeightNotes(notes: String) {
        _weightLoggingState.value = _weightLoggingState.value.copy(notes = notes)
    }

    fun setWeightUnit(unit: String) {
        _weightLoggingState.value = _weightLoggingState.value.copy(unit = unit)
    }

    suspend fun saveWeight(): Boolean {
        _weightLoggingState.value = _weightLoggingState.value.copy(isLoading = true, error = null)
        
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val state = _weightLoggingState.value
            
            // Converte para kg se necessário
            val weightKg = if (state.unit == "lbs") state.weight * 0.453592 else state.weight
            
            val symptom = Symptom.create(
                userId = userId,
                cycleId = cycleUseCases.getCurrentCycle(userId)?.id ?: java.util.UUID.randomUUID().toString(),
                date = state.date,
                category = Symptom.CATEGORY_LIFESTYLE,
                name = "weight",
                severity = SymptomSeverity.MILD,
                notes = "Peso: ${String.format("%.1f", weightKg)} kg${if (state.notes.isNotBlank()) " - ${state.notes}" else ""}"
            )
            
            symptomUseCases.logSymptom(userId, symptom)
            
            _weightLoggingState.value = _weightLoggingState.value.copy(
                isLoading = false,
                success = true
            )
            true
        } catch (e: Exception) {
            _weightLoggingState.value = _weightLoggingState.value.copy(
                isLoading = false,
                error = e.message ?: "Erro ao salvar peso"
            )
            false
        }
    }

    fun resetWeightLogging() {
        _weightLoggingState.value = WeightLoggingState()
    }

    // ============ Actions - Temperature ============

    fun setTemperature(temperature: Double) {
        _temperatureLoggingState.value = _temperatureLoggingState.value.copy(temperature = temperature)
    }

    fun setTemperatureDate(date: LocalDate) {
        _temperatureLoggingState.value = _temperatureLoggingState.value.copy(date = date)
    }

    fun setTemperatureTime(time: String) {
        _temperatureLoggingState.value = _temperatureLoggingState.value.copy(time = time)
    }

    fun setTemperatureNotes(notes: String) {
        _temperatureLoggingState.value = _temperatureLoggingState.value.copy(notes = notes)
    }

    fun setTemperatureUnit(unit: String) {
        _temperatureLoggingState.value = _temperatureLoggingState.value.copy(unit = unit)
    }

    suspend fun saveTemperature(): Boolean {
        _temperatureLoggingState.value = _temperatureLoggingState.value.copy(isLoading = true, error = null)
        
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val state = _temperatureLoggingState.value
            
            // Converte para Celsius se necessário
            val tempCelsius = if (state.unit == "fahrenheit") (state.temperature - 32) * 5/9 else state.temperature
            
            val symptom = Symptom.create(
                userId = userId,
                cycleId = cycleUseCases.getCurrentCycle(userId)?.id ?: java.util.UUID.randomUUID().toString(),
                date = state.date,
                category = Symptom.CATEGORY_PHYSICAL,
                name = "basal_temperature",
                severity = SymptomSeverity.MILD,
                notes = "Temperatura basal: ${String.format("%.2f", tempCelsius)}°C às ${state.time}${if (state.notes.isNotBlank()) " - ${state.notes}" else ""}"
            )
            
            symptomUseCases.logSymptom(userId, symptom)
            
            _temperatureLoggingState.value = _temperatureLoggingState.value.copy(
                isLoading = false,
                success = true
            )
            true
        } catch (e: Exception) {
            _temperatureLoggingState.value = _temperatureLoggingState.value.copy(
                isLoading = false,
                error = e.message ?: "Erro ao salvar temperatura"
            )
            false
        }
    }

    fun resetTemperatureLogging() {
        _temperatureLoggingState.value = TemperatureLoggingState()
    }

    // ============ Actions - Mood ============

    fun setMood(mood: MoodType) {
        _moodLoggingState.value = _moodLoggingState.value.copy(mood = mood)
    }

    fun setMoodDate(date: LocalDate) {
        _moodLoggingState.value = _moodLoggingState.value.copy(date = date)
    }

    fun setMoodNotes(notes: String) {
        _moodLoggingState.value = _moodLoggingState.value.copy(notes = notes)
    }

    suspend fun saveMood(): Boolean {
        _moodLoggingState.value = _moodLoggingState.value.copy(isLoading = true, error = null)
        
        return try {
            val userId = preferencesManager.getUserId() ?: return false
            val state = _moodLoggingState.value
            
            val symptom = Symptom.create(
                userId = userId,
                cycleId = cycleUseCases.getCurrentCycle(userId)?.id ?: java.util.UUID.randomUUID().toString(),
                date = state.date,
                category = Symptom.CATEGORY_EMOTIONAL,
                name = when (state.mood) {
                    MoodType.VERY_SAD, MoodType.SAD -> "sadness"
                    MoodType.HAPPY, MoodType.VERY_HAPPY -> "happy"
                    MoodType.ANXIOUS -> "anxiety"
                    MoodType.IRRITATED -> "irritability"
                    MoodType.TIRED -> "sleepiness"
                    else -> "mood_neutral"
                },
                severity = SymptomSeverity.MILD,
                notes = "Humor: ${state.mood.label}${if (state.notes.isNotBlank()) " - ${state.notes}" else ""}"
            )
            
            symptomUseCases.logSymptom(userId, symptom)
            
            _moodLoggingState.value = _moodLoggingState.value.copy(
                isLoading = false,
                success = true
            )
            true
        } catch (e: Exception) {
            _moodLoggingState.value = _moodLoggingState.value.copy(
                isLoading = false,
                error = e.message ?: "Erro ao salvar humor"
            )
            false
        }
    }

    fun resetMoodLogging() {
        _moodLoggingState.value = MoodLoggingState()
    }

    // ============ Helpers ============

    fun getSelectedSymptomsCount(): Int = _symptomLoggingState.value.selectedSymptoms.size

    fun getSymptomsByCategory(): Map<String, List<String>> {
        return SymptomCatalog.allCategories
    }
}

/**
 * Catálogo de sintomas organizados por categoria.
 * Clean-room - taxonomia própria baseada em padrões médicos.
 */
object SymptomCatalog {
    val allCategories = mapOf(
        "physical" to listOf(
            SymptomInfo("cramps", "Cólicas", "🤕"),
            SymptomInfo("headache", "Dor de cabeça", "🤯"),
            SymptomInfo("backache", "Dor nas costas", "🦴"),
            SymptomInfo("breast_tenderness", "Sensibilidade nos seios", "👙"),
            SymptomInfo("bloating", "Inchaço", "🎈"),
            SymptomInfo("acne", "Acne", "🔴"),
            SymptomInfo("fatigue", "Fadiga", "😴"),
            SymptomInfo("nausea", "Náusea", "🤢"),
            SymptomInfo("dizziness", "Tontura", "💫"),
            SymptomInfo("hot_flashes", "Calorões", "🔥"),
            SymptomInfo("night_sweats", "Suores noturnos", "💧")
        ),
        "emotional" to listOf(
            SymptomInfo("mood_swings", "Mudanças de humor", "🎭"),
            SymptomInfo("irritability", "Irritabilidade", "😠"),
            SymptomInfo("anxiety", "Ansiedade", "😰"),
            SymptomInfo("sadness", "Tristeza", "😢"),
            SymptomInfo("insomnia", "Insônia", "🌙"),
            SymptomInfo("sleepiness", "Sonolência", "😴")
        ),
        "discharge" to listOf(
            SymptomInfo("clear_discharge", "Corrimento claro", "💧"),
            SymptomInfo("white_discharge", "Corrimento branco", "⚪"),
            SymptomInfo("creamy_discharge", "Corrimento cremoso", "🥛"),
            SymptomInfo("egg_white_discharge", "Corrimento clara de ovo", "🥚"),
            SymptomInfo("spotting", "Escape/Spotting", "🩸")
        ),
        "pain" to listOf(
            SymptomInfo("low_back_pain", "Dor lombar", "🦴"),
            SymptomInfo("ovulation_pain", "Dor da ovulação", "🥚"),
            SymptomInfo("breast_pain", "Dor nos seios", "👙")
        ),
        "lifestyle" to listOf(
            SymptomInfo("exercise", "Exercício", "🏃"),
            SymptomInfo("sex", "Relação sexual", "🛌"),
            SymptomInfo("alcohol", "Álcool", "🍷"),
            SymptomInfo("caffeine", "Cafeína", "☕"),
            SymptomInfo("stress", "Estresse", "😰"),
            SymptomInfo("medication", "Medicação", "💊"),
            SymptomInfo("vitamins", "Vitaminas", "💊")
        )
    )

    data class SymptomInfo(
        val key: String,
        val label: String,
        val emoji: String
    )

    fun getCategoryLabel(category: String): String = when (category) {
        "physical" -> "Físicos"
        "emotional" -> "Emocionais"
        "discharge" -> "Corrimento"
        "pain" -> "Dores"
        "lifestyle" -> "Estilo de vida"
        else -> category
    }
}