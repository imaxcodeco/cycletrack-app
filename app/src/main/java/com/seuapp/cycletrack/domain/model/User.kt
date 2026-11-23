package com.seuapp.cycletrack.domain.model

import java.time.LocalDateTime

data class User(
    val uid: String,
    val email: String,
    val name: String? = null,
    val photoUrl: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastSyncedAt: LocalDateTime = LocalDateTime.now(),
)
