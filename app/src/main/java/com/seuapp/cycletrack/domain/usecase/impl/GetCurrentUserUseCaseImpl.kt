package com.seuapp.cycletrack.domain.usecase.impl

import com.seuapp.cycletrack.domain.model.User
import com.seuapp.cycletrack.domain.repository.AuthRepository
import com.seuapp.cycletrack.domain.usecase.GetCurrentUserUseCase
import javax.inject.Inject

class GetCurrentUserUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : GetCurrentUserUseCase {
    override suspend fun invoke(): User? {
        return try {
            authRepository.getCurrentUser()
        } catch (e: Exception) {
            null
        }
    }
}
