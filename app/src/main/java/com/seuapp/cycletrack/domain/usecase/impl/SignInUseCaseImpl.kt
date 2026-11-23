package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.model.User
import com.seuapp.cycletrack.domain.repository.AuthRepository
import com.seuapp.cycletrack.domain.usecase.SignInRequest
import com.seuapp.cycletrack.domain.usecase.SignInUseCase
import javax.inject.Inject

class SignInUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : SignInUseCase {
    override suspend fun invoke(request: SignInRequest): Result<User> {
        return try {
            if (request.email.isBlank() || request.password.isBlank()) {
                Result.failure(IllegalArgumentException("Email and password are required"))
            } else {
                authRepository.signIn(request.email, request.password)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
