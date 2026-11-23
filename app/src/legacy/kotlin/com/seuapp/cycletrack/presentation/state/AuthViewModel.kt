package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.usecase.auth.AuthUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.combine

/**
 * ViewModel para telas de autenticação (Login, Registro, Recuperação de senha, Verificação).
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    // Login
    private val _loginEmail = MutableStateFlow("")
    val loginEmail = _loginEmail.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword = _loginPassword.asStateFlow()

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe = _rememberMe.asStateFlow()

    // Register
    private val _registerDisplayName = MutableStateFlow("")
    val registerDisplayName = _registerDisplayName.asStateFlow()

    private val _registerEmail = MutableStateFlow("")
    val registerEmail = _registerEmail.asStateFlow()

    private val _registerPassword = MutableStateFlow("")
    val registerPassword = _registerPassword.asStateFlow()

    private val _registerConfirmPassword = MutableStateFlow("")
    val registerConfirmPassword = _registerConfirmPassword.asStateFlow()

    private val _acceptTerms = MutableStateFlow(false)
    val acceptTerms = _acceptTerms.asStateFlow()

    // Forgot Password
    private val _forgotEmail = MutableStateFlow("")
    val forgotEmail = _forgotEmail.asStateFlow()

    // Reset Password
    private val _resetPassword = MutableStateFlow("")
    val resetPassword = _resetPassword.asStateFlow()

    private val _resetConfirmPassword = MutableStateFlow("")
    val resetConfirmPassword = _resetConfirmPassword.asStateFlow()

    // Verify Email
    private val _verificationCode = MutableStateFlow("")
    val verificationCode = _verificationCode.asStateFlow()

    // Common
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success = _success.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.UNAUTHENTICATED)
    val authState = _authState.asStateFlow()

    // ============ Computed ============

    val isLoginValid = combine(_loginEmail, _loginPassword) { email, password ->
        email.contains("@") && password.length >= 8
    }.distinctUntilChanged()

    val isRegisterValid = combine(_registerDisplayName, _registerEmail, _registerPassword, _registerConfirmPassword, _acceptTerms) { name, email, pass, confirm, terms ->
        name.isNotBlank() && email.contains("@") && pass.length >= 8 && pass == confirm && terms
    }.distinctUntilChanged()

    val isResetValid = combine(_resetPassword, _resetConfirmPassword) { pass, confirm ->
        pass.length >= 8 && pass == confirm
    }.distinctUntilChanged()

    // ============ Actions ============

    // Login
    fun setLoginEmail(email: String) {
        _loginEmail.value = email
    }

    fun setLoginPassword(password: String) {
        _loginPassword.value = password
    }

    fun setRememberMe(remember: Boolean) {
        _rememberMe.value = remember
    }

    suspend fun login(): Boolean {
        _isLoading.value = true
        _error.value = null
        _success.value = false
        
        return try {
            val result = authUseCases.login(
                email = _loginEmail.value.trim(),
                password = _loginPassword.value,
                rememberMe = _rememberMe.value
            )
            
            preferencesManager.setUserId(result.userId)
            preferencesManager.setUserEmail(_loginEmail.value.trim())
            preferencesManager.setAuthToken(result.accessToken)
            if (result.refreshToken != null) {
                preferencesManager.setRefreshToken(result.refreshToken!!)
            }
            
            if (result.requiresEmailVerification) {
                _authState.value = AuthState.EMAIL_VERIFICATION_PENDING
            } else {
                _authState.value = AuthState.AUTHENTICATED
            }
            
            _success.value = true
            true
        } catch (e: Exception) {
            _error.value = when (e.message) {
                null -> "Erro ao fazer login"
                else -> if (e.message!!.contains("401") || e.message!!.contains("credencial")) "Email ou senha inválidos" else e.message!!
            }
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun loginWithGoogle(idToken: String): Boolean {
        _isLoading.value = true
        _error.value = null
        _success.value = false
        
        return try {
            val result = authUseCases.loginWithGoogle(idToken)
            
            preferencesManager.setUserId(result.userId)
            preferencesManager.setUserEmail(result.email)
            preferencesManager.setAuthToken(result.accessToken)
            if (result.refreshToken != null) {
                preferencesManager.setRefreshToken(result.refreshToken!!)
            }
            
            _authState.value = AuthState.AUTHENTICATED
            _success.value = true
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao fazer login com Google"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun continueAnonymous(): Boolean {
        _isLoading.value = true
        _error.value = null
        _success.value = false
        
        return try {
            val result = authUseCases.createAnonymousAccount()
            
            preferencesManager.setUserId(result.userId)
            preferencesManager.setAuthToken(result.accessToken)
            if (result.refreshToken != null) {
                preferencesManager.setRefreshToken(result.refreshToken!!)
            }
            
            _authState.value = AuthState.ANONYMOUS
            _success.value = true
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao continuar anonimamente"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // Register
    fun setRegisterDisplayName(name: String) {
        _registerDisplayName.value = name
    }

    fun setRegisterEmail(email: String) {
        _registerEmail.value = email
    }

    fun setRegisterPassword(password: String) {
        _registerPassword.value = password
    }

    fun setRegisterConfirmPassword(password: String) {
        _registerConfirmPassword.value = password
    }

    fun setAcceptTerms(accept: Boolean) {
        _acceptTerms.value = accept
    }

    suspend fun register(): Boolean {
        _isLoading.value = true
        _error.value = null
        _success.value = false
        
        return try {
            val result = authUseCases.register(
                email = _registerEmail.value.trim(),
                password = _registerPassword.value,
                displayName = _registerDisplayName.value.trim()
            )
            
            preferencesManager.setUserId(result.userId)
            preferencesManager.setUserEmail(_registerEmail.value.trim())
            preferencesManager.setAuthToken(result.accessToken)
            if (result.refreshToken != null) {
                preferencesManager.setRefreshToken(result.refreshToken!!)
            }
            
            if (result.requiresEmailVerification) {
                _authState.value = AuthState.EMAIL_VERIFICATION_PENDING
            } else {
                _authState.value = AuthState.AUTHENTICATED
            }
            
            _success.value = true
            true
        } catch (e: Exception) {
            _error.value = when (e.message) {
                null -> "Erro ao criar conta"
                else -> if (e.message!!.contains("409") || e.message!!.contains("exist")) "Este email já está cadastrado" else e.message!!
            }
            false
        } finally {
            _isLoading.value = false
        }
    }

    // Forgot Password
    fun setForgotEmail(email: String) {
        _forgotEmail.value = email
    }

    suspend fun sendResetEmail(): Boolean {
        _isLoading.value = true
        _error.value = null
        _success.value = false
        
        return try {
            authUseCases.sendPasswordResetEmail(_forgotEmail.value.trim())
            _success.value = true
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao enviar email de recuperação"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // Reset Password
    fun setResetPassword(password: String) {
        _resetPassword.value = password
    }

    fun setResetConfirmPassword(password: String) {
        _resetConfirmPassword.value = password
    }

    suspend fun resetPassword(): Boolean {
        _isLoading.value = true
        _error.value = null
        _success.value = false
        
        return try {
            authUseCases.resetPassword(_resetPassword.value)
            _success.value = true
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao redefinir senha"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // Verify Email
    fun setVerificationCode(code: String) {
        _verificationCode.value = code
    }

    suspend fun verifyEmail(): Boolean {
        _isLoading.value = true
        _error.value = null
        _success.value = false
        
        return try {
            authUseCases.verifyEmail(_verificationCode.value)
            _authState.value = AuthState.AUTHENTICATED
            _success.value = true
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Código inválido ou expirado"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun resendVerificationEmail(): Boolean {
        _isLoading.value = true
        return try {
            val email = _registerEmail.value.ifBlank { _loginEmail.value }.ifBlank { _forgotEmail.value }
            if (email.isNotBlank()) {
                authUseCases.resendVerificationEmail(email)
            }
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao reenviar código"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // Common
    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = false
    }

    fun clearForms() {
        _loginEmail.value = ""
        _loginPassword.value = ""
        _rememberMe.value = false
        _registerDisplayName.value = ""
        _registerEmail.value = ""
        _registerPassword.value = ""
        _registerConfirmPassword.value = ""
        _acceptTerms.value = false
        _forgotEmail.value = ""
        _resetPassword.value = ""
        _resetConfirmPassword.value = ""
        _verificationCode.value = ""
    }

    fun checkAuthState() {
        val isLoggedIn = preferencesManager.isLoggedIn
        _authState.value = if (isLoggedIn) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
    }
}

sealed class AuthState {
    object UNAUTHENTICATED : AuthState()
    object AUTHENTICATED : AuthState()
    object ANONYMOUS : AuthState()
    object EMAIL_VERIFICATION_PENDING : AuthState()
}