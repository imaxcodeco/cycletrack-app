package com.seuapp.cycletrack.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * DTOs para requisições de gravidez.
 */
@Serializable
data class CreatePregnancyRequest(
    val lmpDate: String, // ISO date
    val dueDate: String?, // ISO date
    val conceptionDate: String?, // ISO date
)

@Serializable
data class UpdatePregnancyRequest(
    val dueDate: String?, // ISO date
    val conceptionDate: String?, // ISO date
    val currentWeightKg: Float? = null,
    val bloodPressureSystolic: Int? = null,
    val bloodPressureDiastolic: Int? = null,
    val glucoseLevel: Float? = null
)

@Serializable
data class CompletePregnancyRequest(
    val outcome: String, // live_birth, miscarriage, abortion, stillbirth, ectopic, molar
    val outcomeDate: String, // ISO date
    val outcomeNotes: String = "",
    val babyName: String = "",
    val babyGender: String = "unknown", // male, female, unknown
    val babyWeightGrams: Int = 0,
    val babyHeightCm: Float = 0.0f,
    val babyApgarScore: Int = 0
)

@Serializable
data class CreateAppointmentRequest(
    val title: String,
    val appointmentDate: String, // ISO date
    val appointmentTime: String = "", // HH:mm
    val type: String = "prenatal", // prenatal, ultrasound, blood_test, glucose_test, gbs_test, other
    val location: String = "",
    val doctorName: String = "",
    val description: String = "",
    val reminderEnabled: Boolean = true,
    val reminderMinutesBefore: Int = 60
)

@Serializable
data class UpdateAppointmentRequest(
    val title: String? = null,
    val appointmentDate: String?, // ISO date
    val appointmentTime: String? = null,
    val type: String? = null,
    val location: String? = null,
    val doctorName: String? = null,
    val description: String? = null,
    val completed: Boolean? = null,
    val reminderEnabled: Boolean? = null,
    val reminderMinutesBefore: Int? = null
)

@Serializable
data class CreateTestRequest(
    val testType: String, // hcg, progesterone, glucose, gbs, ultrasound, nipt, other
    val testDate: String, // ISO date
    val resultValue: String = "",
    val resultUnit: String = "",
    val referenceRange: String = "",
    val isNormal: Boolean = true,
    val notes: String = ""
)