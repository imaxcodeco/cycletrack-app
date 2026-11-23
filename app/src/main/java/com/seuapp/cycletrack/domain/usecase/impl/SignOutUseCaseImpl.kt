package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.repository.AuthRepository
import com.seuapp.cycletrack.domain.usecase.SignOutUseCase
import javax.inject.Inject

class SignOutUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : SignOutUseCase {
    override suspend fun invoke(): Result<Unit> {
        return try {
            authRepository.signOut()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
