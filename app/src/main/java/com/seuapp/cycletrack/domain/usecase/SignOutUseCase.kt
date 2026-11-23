package com.seuapp.cycletrack.domain.usecase

interface SignOutUseCase {
    suspend operator fun invoke(): Result<Unit>
}
