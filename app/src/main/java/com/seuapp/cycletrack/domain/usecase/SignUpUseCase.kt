package com.seuapp.cycletrack.domain.usecase

import com.seuapp.cycletrack.domain.model.User

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String,
)

interface SignUpUseCase {
    suspend operator fun invoke(request: SignUpRequest): Result<User>
}
