package com.seuapp.cycletrack.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.seuapp.cycletrack.domain.model.AuthState
import com.seuapp.cycletrack.domain.model.User
import com.seuapp.cycletrack.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    override suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            val task = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val authResult = task.await()
            val firebaseUser = authResult.user
                ?: throw Exception("Falha ao criar usuário no Firebase")

            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdate).await()

            val user = firebaseUser.toUser()
            Timber.d("Sign-up realizado: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "Erro no sign-up")
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val task = firebaseAuth.signInWithEmailAndPassword(email, password)
            val authResult = task.await()
            val firebaseUser = authResult.user
                ?: throw Exception("Falha ao fazer login")

            val user = firebaseUser.toUser()
            Timber.d("Sign-in realizado: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "Erro no sign-in")
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val task = firebaseAuth.signInWithCustomToken(idToken)
            val authResult = task.await()
            val firebaseUser = authResult.user
                ?: throw Exception("Falha ao fazer login com Google")

            val user = firebaseUser.toUser()
            Timber.d("Google sign-in realizado: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "Erro no Google sign-in")
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Timber.d("Sign-out realizado")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Erro no sign-out")
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            firebaseUser?.let {
                Timber.d("Usuário atual: ${it.email}")
                it.toUser()
            }
        } catch (e: Exception) {
            Timber.e(e, "Erro ao buscar usuário atual")
            null
        }
    }

    override fun observeAuthState(): Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val state = if (auth.currentUser != null) {
                AuthState.Authenticated(auth.currentUser!!.toUser())
            } else {
                AuthState.Unauthenticated
            }
            trySend(state)
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: throw Exception("Nenhum usuário autenticado")
            user.delete().await()
            Timber.d("Conta deletada")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Erro ao deletar conta")
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(name: String?, photoUrl: String?): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: throw Exception("Nenhum usuário autenticado")

            val profileUpdate = UserProfileChangeRequest.Builder()
                .apply {
                    if (name != null) setDisplayName(name)
                    if (photoUrl != null) setPhotoUri(android.net.Uri.parse(photoUrl))
                }
                .build()

            user.updateProfile(profileUpdate).await()
            Timber.d("Perfil atualizado")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Erro ao atualizar perfil")
            Result.failure(e)
        }
    }

    private fun FirebaseUser.toUser(): User {
        return User(
            uid = uid,
            email = email ?: "",
            name = displayName,
            photoUrl = photoUrl?.toString(),
        )
    }
}
