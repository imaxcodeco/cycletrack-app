package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity representing pregnancy tracking.
 * Clean-room implementation with week-by-week development data.
 */
@Serializable
open class PregnancyRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var userId: String = ""

    // Dates
    var lmpDate: Long = 0L // Last menstrual period
    var conceptionDate: Long = 0L
    var dueDate: Long = 0L
    var actualBirthDate: Long = 0L

    // Current state
    var currentWeek: Int = 0
    var currentDay: Int = 0
    var trimester: Int = 1 // 1, 2, 3
    var status: String = "active" // active, ended, lost

    // Pregnancy outcome
    var outcome: String = "" // live_birth, miscarriage, abortion, stillbirth, ectopic, molar
    var outcomeDate: Long = 0L
    var outcomeNotes: String = ""

    // Health metrics
    var prePregnancyWeightKg: Float = 0.0f
    var currentWeightKg: Float = 0.0f
    var totalWeightGainKg: Float = 0.0f
    var bloodPressureSystolic: Int = 0
    var bloodPressureDiastolic: Int = 0
    var glucoseLevel: Float = 0.0f

    // Appointments & Tests
    var appointments: RealmList<PregnancyAppointmentRealm> = RealmList()
    var tests: RealmList<PregnancyTestRealm> = RealmList()

    // Symptoms specific to pregnancy
    var symptoms: RealmList<SymptomRealm> = RealmList()

    // Baby info (after birth)
    var babyName: String = ""
    var babyGender: String = "" // male, female, unknown
    var babyWeightGrams: Int = 0
    var babyHeightCm: Float = 0.0f
    var babyApagarScore: Int = 0

    // Metadata
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var deletedAt: Long = 0L
    var serverId: String = ""
    var lastSyncedAt: Long = 0L
    var version: Int = 1

    companion object {
        const val STATUS_ACTIVE = "active"
        const val STATUS_ENDED = "ended"
        const val STATUS_LOST = "lost"

        const val OUTCOME_LIVE_BIRTH = "live_birth"
        const val OUTCOME_MISCARRIAGE = "miscarriage"
        const val OUTCOME_ABORTION = "abortion"
        const val OUTCOME_STILLBIRTH = "stillbirth"
        const val OUTCOME_ECTOPIC = "ectopic"
        const val OUTCOME_MOLAR = "molar"
    }
}

/**
 * Embedded object for pregnancy appointments.
 */
@Serializable
open class PregnancyAppointmentRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    var pregnancyId: String = ""

    var title: String = ""

    var description: String = ""

    var appointmentDate: Long = 0L

    var type: String = "" // prenatal, ultrasound, blood_test, glucose_test, gbs_test, other

    var location: String = ""

    var doctorName: String = ""

    var notes: String = ""

    var completed: Boolean = false

    var reminderEnabled: Boolean = true

    var reminderMinutesBefore: Int = 60

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
}

/**
 * Embedded object for pregnancy tests/measurements.
 */
@Serializable
open class PregnancyTestRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    var pregnancyId: String = ""

    var testType: String = "" // hcg, progesterone, glucose, gbs, ultrasound, ntdna, other

    var testDate: Long = 0L

    var resultValue: String = ""

    var resultUnit: String = ""

    var referenceRange: String = ""

    var isNormal: Boolean = true

    var notes: String = ""

    var createdAt: Long = System.currentTimeMillis()
}