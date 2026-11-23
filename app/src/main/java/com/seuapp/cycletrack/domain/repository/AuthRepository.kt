package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.AuthState
import com.seuapp.cycletrack.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(email: String, password: String, name: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
    fun observeAuthState(): Flow<AuthState>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun updateProfile(name: String?, photoUrl: String?): Result<Unit>
}
