package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.AuthState
import com.seuapp.cycletrack.domain.model.User
import com.seuapp.cycletrack.domain.usecase.GetCurrentUserUseCase
import com.seuapp.cycletrack.domain.usecase.SignInUseCase
import com.seuapp.cycletrack.domain.usecase.SignInRequest
import com.seuapp.cycletrack.domain.usecase.SignOutUseCase
import com.seuapp.cycletrack.domain.usecase.SignUpUseCase
import com.seuapp.cycletrack.domain.usecase.SignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val _signUpError = MutableStateFlow<String?>(null)
    val signUpError: StateFlow<String?> = _signUpError

    private val _signInError = MutableStateFlow<String?>(null)
    val signInError: StateFlow<String?> = _signInError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        checkCurrentUser()
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _signUpError.value = null

                val request = SignUpRequest(email, password, name)
                val result = signUpUseCase(request)

                result.onSuccess { user ->
                    _authState.value = AuthState.Authenticated(user)
                    Timber.d("Sign-up realizado com sucesso: ${user.email}")
                }.onFailure { e ->
                    val errorMessage = e.message ?: "Erro ao criar conta"
                    _signUpError.value = errorMessage
                    _authState.value = AuthState.Error(errorMessage)
                    Timber.e(e, "Erro no sign-up")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _signInError.value = null

                val request = SignInRequest(email, password)
                val result = signInUseCase(request)

                result.onSuccess { user ->
                    _authState.value = AuthState.Authenticated(user)
                    Timber.d("Sign-in realizado com sucesso: ${user.email}")
                }.onFailure { e ->
                    val errorMessage = e.message ?: "Erro ao fazer login"
                    _signInError.value = errorMessage
                    _authState.value = AuthState.Error(errorMessage)
                    Timber.e(e, "Erro no sign-in")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = signOutUseCase()

                result.onSuccess {
                    _authState.value = AuthState.Unauthenticated
                    _signUpError.value = null
                    _signInError.value = null
                    Timber.d("Sign-out realizado com sucesso")
                }.onFailure { e ->
                    Timber.e(e, "Erro no sign-out")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            try {
                val user = getCurrentUserUseCase()
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user)
                    Timber.d("Usuário já autenticado: ${user.email}")
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                Timber.e(e, "Erro ao verificar usuário atual")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun clearErrors() {
        _signUpError.value = null
        _signInError.value = null
    }
}
