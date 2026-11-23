package com.seuapp.cycletrack.domain.usecase

import com.seuapp.cycletrack.domain.model.User

interface GetCurrentUserUseCase {
    suspend operator fun invoke(): User?
}
