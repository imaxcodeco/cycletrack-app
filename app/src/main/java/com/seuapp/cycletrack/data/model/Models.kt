package com.seuapp.cycletrack.data.model

import java.time.LocalDate

/**
 * Modelos de dominio (clean-room) usados pelo app em modo demonstrativo.
 * Sao persistidos em memoria; a camada Realm (offline-first) sera conectada
 * a partir dos repositorios em `app/src/legacy/kotlin`.
 */

enum class CyclePhase(val key: String, val label: String) {
    MENSTRUAL("menstrual", "Menstrual"),
    FOLICULAR("follicular", "Folicular"),
    OVULATION("ovulation", "Ovulacao"),
    LUTERAL("luteal", "Luteo");

    companion object {
        fun fromKey(key: String): CyclePhase = entries.firstOrNull { it.key == key } ?: FOLICULAR
    }
}

enum class FlowLevel(val key: String, val label: String) {
    NONE("none", "Sem fluxo"),
    LIGHT("light", "Leve"),
    MEDIUM("medium", "Moderado"),
    HEAVY("heavy", "Intenso");

    companion object {
        fun fromKey(key: String): FlowLevel = entries.firstOrNull { it.key == key } ?: NONE
    }
}

enum class Mood(val key: String, val label: String, val emoji: String) {
    HAPPY("happy", "Feliz", "😊"),
    CALM("calm", "Calma", "😌"),
    TIRED("tired", "Cansada", "😴"),
    IRRITABLE("irritable", "Irritada", "😤"),
    SAD("sad", "Triste", "😔"),
    ANXIOUS("anxious", "Ansiosa", "😟"),
    ENERGETIC("energetic", "Com energia", "⚡"),
    CONFUSED("confused", "Confusa", "🤔");

    companion object {
        fun fromKey(key: String): Mood = entries.firstOrNull { it.key == key } ?: CALM
    }
}

data class Cycle(
    val id: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val cycleLength: Int,
    val periodLength: Int,
    val isPredicted: Boolean = false,
    val confidence: Float = 0f
)

data class Symptom(
    val id: String,
    val date: LocalDate,
    val category: String,
    val name: String,
    val severity: Int = 2,
    val notes: String = ""
)

data class Prediction(
    val type: String,
    val date: LocalDate,
    val confidence: Float
)

data class DailyLog(
    val date: LocalDate,
    val flow: FlowLevel = FlowLevel.NONE,
    val weightKg: Float? = null,
    val temperatureC: Float? = null,
    val mood: Mood? = null,
    val symptoms: Set<String> = emptySet()
)

data class CycleStats(
    val averageCycleLength: Int,
    val averagePeriodLength: Int,
    val cycleRegularity: String,
    val loggedDays: Int,
    val totalSymptoms: Int
)
