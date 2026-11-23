package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity representing a logged symptom.
 * Clean-room implementation with standardized symptom taxonomy.
 */
@Serializable
open class SymptomRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var userId: String = ""

    @Index
    var cycleId: String = ""

    @Index
    var date: Long = 0L

    @Required
    var category: String = "" // physical, emotional, discharge, pain, lifestyle, custom

    @Required
    var name: String = "" // Standardized name from taxonomy

    var severity: Int = 1 // 1=mild, 2=moderate, 3=severe

    var notes: String = ""

    var source: String = "manual" // manual, health_connect, partner

    var createdAt: Long = System.currentTimeMillis()

    var updatedAt: Long = System.currentTimeMillis()

    var deletedAt: Long = 0L

    var serverId: String = ""

    var lastSyncedAt: Long = 0L

    var version: Int = 1

    companion object {
        // Symptom Categories (clean-room taxonomy)
        const val CATEGORY_PHYSICAL = "physical"
        const val CATEGORY_EMOTIONAL = "emotional"
        const val CATEGORY_DISCHARGE = "discharge"
        const val CATEGORY_PAIN = "pain"
        const val CATEGORY_LIFESTYLE = "lifestyle"
        const val CATEGORY_CUSTOM = "custom"

        // Standardized symptom names (English keys, localized in UI)
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
    }
}