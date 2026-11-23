package com.seuapp.cycletrack.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Modelo de domínio para Sintoma.
 * Taxonomia padronizada clean-room.
 */
@Serializable
data class Symptom(
    val id: String,
    val userId: String,
    val cycleId: String,
    val date: LocalDate,
    val category: String,
    val name: String,
    val severity: SymptomSeverity = SymptomSeverity.MILD,
    val notes: String = "",
    val source: String = Symptom.SOURCE_MANUAL,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: String = "",
    val lastSyncedAt: Long = 0L,
    val version: Int = 1
) {
    companion object {
        const val SOURCE_MANUAL = "manual"
        const val SOURCE_HEALTH_CONNECT = "health_connect"
        const val SOURCE_PARTNER = "partner"

        // Categorias
        const val CATEGORY_PHYSICAL = "physical"
        const val CATEGORY_EMOTIONAL = "emotional"
        const val CATEGORY_DISCHARGE = "discharge"
        const val CATEGORY_PAIN = "pain"
        const val CATEGORY_LIFESTYLE = "lifestyle"
        const val CATEGORY_CUSTOM = "custom"

        // Nomes padronizados (chaves em inglês, localizados na UI)
        const val SYMPTOM_CRAMPS = "cramps"
        const val SYMPTOM_HEADACHE = "headache"
        const val SYMPTOM_BACKACHE = "backache"
        const val SYMPTOM_BREAST_TENDERNESS = "breast_tenderness"
        const val SYMPTOM_BLOATING = "bloating"
        const val SYMPTOM_ACNE = "acne"
        const val SYMPTOM_FATIGUE = "fatigue"
        const val SYMPTOM_NAUSEA = "nausea"
        const val SYMPTOM_DIZZINESS = "dizziness"
        const val SYMPTOM_HOT_FLASHES = "hot_flashes"
        const val SYMPTOM_NIGHT_SWEATS = "night_sweats"
        const val SYMPTOM_MOOD_SWINGS = "mood_swings"
        const val SYMPTOM_IRRITABILITY = "irritability"
        const val SYMPTOM_ANXIETY = "anxiety"
        const val SYMPTOM_SADNESS = "sadness"
        const val SYMPTOM_INSOMNIA = "insomnia"
        const val SYMPTOM_SLEEPINESS = "sleepiness"
        const val SYMPTOM_CLEAR_DISCHARGE = "clear_discharge"
        const val SYMPTOM_WHITE_DISCHARGE = "white_discharge"
        const val SYMPTOM_CREAMY_DISCHARGE = "creamy_discharge"
        const val SYMPTOM_EGG_WHITE_DISCHARGE = "egg_white_discharge"
        const val SYMPTOM_SPOTTING = "spotting"
        const val SYMPTOM_LOW_BACK_PAIN = "low_back_pain"
        const val SYMPTOM_OVULATION_PAIN = "ovulation_pain"
        const val SYMPTOM_BREAST_PAIN = "breast_pain"
        const val SYMPTOM_EXERCISE = "exercise"
        const val SYMPTOM_SEX = "sex"
        const val SYMPTOM_ALCOHOL = "alcohol"
        const val SYMPTOM_CAFFEINE = "caffeine"
        const val SYMPTOM_STRESS = "stress"
        const val SYMPTOM_MEDICATION = "medication"
        const val SYMPTOM_VITAMINS = "vitamins"

        // Mapeamento categoria -> sintomas
        val CATEGORY_SYMPTOMS = mapOf(
            CATEGORY_PHYSICAL to listOf(
                SYMPTOM_CRAMPS, SYMPTOM_HEADACHE, SYMPTOM_BACKACHE,
                SYMPTOM_BREAST_TENDERNESS, SYMPTOM_BLOATING, SYMPTOM_ACNE,
                SYMPTOM_FATIGUE, SYMPTOM_NAUSEA, SYMPTOM_DIZZINESS,
                SYMPTOM_HOT_FLASHES, SYMPTOM_NIGHT_SWEATS
            ),
            CATEGORY_EMOTIONAL to listOf(
                SYMPTOM_MOOD_SWINGS, SYMPTOM_IRRITABILITY, SYMPTOM_ANXIETY,
                SYMPTOM_SADNESS, SYMPTOM_INSOMNIA, SYMPTOM_SLEEPINESS
            ),
            CATEGORY_DISCHARGE to listOf(
                SYMPTOM_CLEAR_DISCHARGE, SYMPTOM_WHITE_DISCHARGE,
                SYMPTOM_CREAMY_DISCHARGE, SYMPTOM_EGG_WHITE_DISCHARGE,
                SYMPTOM_SPOTTING
            ),
            CATEGORY_PAIN to listOf(
                SYMPTOM_LOW_BACK_PAIN, SYMPTOM_OVULATION_PAIN, SYMPTOM_BREAST_PAIN
            ),
            CATEGORY_LIFESTYLE to listOf(
                SYMPTOM_EXERCISE, SYMPTOM_SEX, SYMPTOM_ALCOHOL,
                SYMPTOM_CAFFEINE, SYMPTOM_STRESS, SYMPTOM_MEDICATION, SYMPTOM_VITAMINS
            )
        )

        fun getCategoryForSymptom(symptomName: String): String? {
            return CATEGORY_SYMPTOMS.entries.firstOrNull { symptomName in it.value }?.key
        }
    }
}

/**
 * Enum para severidade do sintoma.
 */
enum class SymptomSeverity(val value: Int, val label: String) {
    MILD(1, "Leve"),
    MODERATE(2, "Moderado"),
    SEVERE(3, "Intenso")
}