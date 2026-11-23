package com.seuapp.cycletrack.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * DTOs para respostas de gravidez.
 */
@Serializable
data class PregnancyResponse(
    val id: String,
    val userId: String,
    val lmpDate: String,
    val conceptionDate: String?,
    val dueDate: String,
    val actualBirthDate: String?,
    val currentWeek: Int,
    val currentDay: Int,
    val trimester: Int,
    val status: String,
    val outcome: String?,
    val outcomeDate: String?,
    val outcomeNotes: String,
    val prePregnancyWeightKg: Float,
    val currentWeightKg: Float,
    val totalWeightGainKg: Float,
    val bloodPressureSystolic: Int,
    val bloodPressureDiastolic: Int,
    val glucoseLevel: Float,
    val appointments: List<AppointmentResponse>,
    val tests: List<TestResponse>,
    val babyName: String,
    val babyGender: String,
    val babyWeightGrams: Int,
    val babyHeightCm: Float,
    val babyApgarScore: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val serverId: String,
    val lastSyncedAt: Long,
    val version: Int
)

@Serializable
data class PregnancyListResponse(
    val pregnancies: List<PregnancyResponse>,
    val total: Int
)

@Serializable
data class AppointmentResponse(
    val id: String,
    val pregnancyId: String,
    val title: String,
    val description: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val type: String,
    val location: String,
    val doctorName: String,
    val notes: String,
    val completed: Boolean,
    val reminderEnabled: Boolean,
    val reminderMinutesBefore: Int,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class AppointmentListResponse(
    val appointments: List<AppointmentResponse>,
    val total: Int
)

@Serializable
data class TestResponse(
    val id: String,
    val pregnancyId: String,
    val testType: String,
    val testDate: String,
    val resultValue: String,
    val resultUnit: String,
    val referenceRange: String,
    val isNormal: Boolean,
    val notes: String,
    val createdAt: Long
)

@Serializable
data class TestListResponse(
    val tests: List<TestResponse>,
    val total: Int
)