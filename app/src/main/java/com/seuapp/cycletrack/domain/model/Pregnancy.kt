package com.seuapp.cycletrack.domain.model

import java.time.LocalDate

data class Pregnancy(
    val id: String,
    val userId: String,
    val startDate: LocalDate,
    val dueDate: LocalDate,
    val currentWeek: Int = 0,
    val status: String = "active",
    val createdAt: Long = System.currentTimeMillis()
)

data class PartnerConnection(
    val id: String,
    val userId: String,
    val partnerId: String? = null,
    val partnerName: String = "",
    val inviteCode: String = "",
    val status: String = STATUS_PENDING,
    val shareLevel: String = "none",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_CONNECTED = "connected"
    }
}
