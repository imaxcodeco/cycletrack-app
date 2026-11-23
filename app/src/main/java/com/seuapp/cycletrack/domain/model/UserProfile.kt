package com.seuapp.cycletrack.domain.model

import java.time.LocalDate

data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String = "",
    val goal: String = "",
    val dateOfBirth: LocalDate? = null,
    val averageCycleLength: Int = 28,
    val averagePeriodLength: Int = 5,
    val lastPeriodStartDate: LocalDate? = null,
    val heightCm: Int = 0,
    val weightKg: Float = 0f,
    val onboardingCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    class Builder {
        var id: String = ""
        var email: String = ""
        var displayName: String = ""
        var goal: String = ""
        var dateOfBirth: LocalDate? = null
        var averageCycleLength: Int = 28
        var averagePeriodLength: Int = 5
        var lastPeriodStartDate: LocalDate? = null
        var heightCm: Int = 0
        var weightKg: Float = 0f
        var onboardingCompleted: Boolean = false

        fun build(): UserProfile = UserProfile(
            id = id,
            email = email,
            displayName = displayName,
            goal = goal,
            dateOfBirth = dateOfBirth,
            averageCycleLength = averageCycleLength,
            averagePeriodLength = averagePeriodLength,
            lastPeriodStartDate = lastPeriodStartDate,
            heightCm = heightCm,
            weightKg = weightKg,
            onboardingCompleted = onboardingCompleted
        )
    }
}
