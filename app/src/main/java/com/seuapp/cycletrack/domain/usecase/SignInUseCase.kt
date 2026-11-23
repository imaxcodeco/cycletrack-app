package com.seuapp.cycletrack.domain.usecase

import com.seuapp.cycletrack.domain.model.User

data class SignInRequest(
    val email: String,
    val password: String,
)

interface SignInUseCase {
    suspend operator fun invoke(request: SignInRequest): Result<User>
}
