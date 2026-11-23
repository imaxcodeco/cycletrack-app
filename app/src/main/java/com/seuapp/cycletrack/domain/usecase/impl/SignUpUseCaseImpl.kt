package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.model.User
import com.seuapp.cycletrack.domain.repository.AuthRepository
import com.seuapp.cycletrack.domain.usecase.SignUpRequest
import com.seuapp.cycletrack.domain.usecase.SignUpUseCase
import javax.inject.Inject

class SignUpUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : SignUpUseCase {
    override suspend fun invoke(request: SignUpRequest): Result<User> {
        return try {
            if (request.email.isBlank() || request.password.isBlank() || request.name.isBlank()) {
                Result.failure(IllegalArgumentException("Email, password, and name are required"))
            } else if (request.password.length < 6) {
                Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
            } else {
                authRepository.signUp(request.email, request.password, request.name)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
