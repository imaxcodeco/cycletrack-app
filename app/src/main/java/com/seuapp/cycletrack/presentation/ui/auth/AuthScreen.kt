package com.seuapp.cycletrack.presentation.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seuapp.cycletrack.domain.model.AuthState
import com.seuapp.cycletrack.presentation.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit,
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val signUpError by viewModel.signUpError.collectAsStateWithLifecycle()
    val signInError by viewModel.signInError.collectAsStateWithLifecycle()

    var isSignInMode by remember { mutableStateOf(true) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onAuthSuccess()
        }
    }

    if (isSignInMode) {
        SignInScreen(
            onSignInClick = { email, password ->
                viewModel.signIn(email, password)
            },
            onSignUpClick = {
                viewModel.clearErrors()
                isSignInMode = false
            },
            isLoading = isLoading,
            errorMessage = signInError,
        )
    } else {
        SignUpScreen(
            onSignUpClick = { email, password, name ->
                viewModel.signUp(email, password, name)
            },
            onSignInClick = {
                viewModel.clearErrors()
                isSignInMode = true
            },
            isLoading = isLoading,
            errorMessage = signUpError,
        )
    }
}
