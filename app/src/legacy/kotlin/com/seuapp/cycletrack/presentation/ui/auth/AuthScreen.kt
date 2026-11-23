package com.seuapp.cycletrack.presentation.ui.auth

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seuapp.cycletrack.presentation.state.AuthViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import com.seuapp.cycletrack.util.Constants
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seuapp.cycletrack.presentation.ui.auth.AuthScreen

/**
 * Telas de Autenticação - Login, Registro, Recuperação de senha com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun AuthScreen.Login(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val email by viewModel.loginEmail.collectAsStateWithLifecycle()
    val password by viewModel.loginPassword.collectAsStateWithLifecycle()
    val rememberMe by viewModel.rememberMe.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isValid by viewModel.isLoginValid.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    AuthTemplate(
        title = "Entrar",
        subtitle = "Acesse sua conta para continuar",
        illustration = "👋"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.setLoginEmail(it) },
                label = { Text("Email") },
                placeholder = { Text("seu@email.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = error != null && email.isNotBlank()
            )
            
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.setLoginPassword(it) },
                label = { Text("Senha") },
                placeholder = { Text("••••••••") },
                visualTransformation = androidx.compose.material3.PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = error != null && password.isNotBlank()
            )
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { viewModel.setRememberMe(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.primary,
                            uncheckedColor = colors.onSurfaceVariant
                        )
                    )
                    Text("Lembrar-me", fontSize = 14.sp, color = colors.onSurface)
                }
                
                Text(
                    text = "Esqueci a senha",
                    fontSize = 14.sp,
                    color = colors.primary,
                    fontWeight = FontWeight.Medium
                ).also {
                    androidx.compose.foundation.gestures.clickable {
                        onForgotPassword()
                    }
                }
            }
            
            Button(
                onClick = { viewModel.login() },
                enabled = isValid && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            error?.let { msg ->
                Text(msg, fontSize = 12.sp, color = colors.error, textAlign = TextAlign.Center)
            }
            
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Não tem conta? ", fontSize = 14.sp, color = colors.onSurfaceVariant)
                Text(
                    text = "Criar conta",
                    fontSize = 14.sp,
                    color = colors.primary,
                    fontWeight = FontWeight.Medium
                ).also {
                    androidx.compose.foundation.gestures.clickable {
                        onGoToRegister()
                    }
                }
            }
            
            // Anonymous option
            Spacer(16.dp)
            androidx.compose.material3.TextButton(
                onClick = { viewModel.continueAnonymous() },
                enabled = !isLoading
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👤", fontSize = 16.sp)
                    Text("Continuar anonimamente", fontSize = 14.sp, color = colors.primary, fontWeight = FontWeight.Medium)
                }
            }
            Text("Seus dados ficam só no dispositivo. Pode criar conta depois.", fontSize = 12.sp, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AuthScreen.Register(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val displayName by viewModel.registerDisplayName.collectAsStateWithLifecycle()
    val email by viewModel.registerEmail.collectAsStateWithLifecycle()
    val password by viewModel.registerPassword.collectAsStateWithLifecycle()
    val confirmPassword by viewModel.registerConfirmPassword.collectAsStateWithLifecycle()
    val acceptTerms by viewModel.acceptTerms.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isValid by viewModel.isRegisterValid.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    AuthTemplate(
        title = "Criar conta",
        subtitle = "Comece a acompanhar seu ciclo hoje",
        illustration = "✨"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { viewModel.setRegisterDisplayName(it) },
                label = { Text("Nome de exibição") },
                placeholder = { Text("Como você quer ser chamada") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = displayName.isBlank()
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.setRegisterEmail(it) },
                label = { Text("Email") },
                placeholder = { Text("seu@email.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = error != null && email.isNotBlank()
            )
            
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.setRegisterPassword(it) },
                label = { Text("Senha") },
                placeholder = { Text("Mínimo 8 caracteres") },
                visualTransformation = androidx.compose.material3.PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = password.isNotBlank() && password.length < 8
            )
            
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.setRegisterConfirmPassword(it) },
                label = { Text("Confirmar senha") },
                placeholder = { Text("Repita a senha") },
                visualTransformation = androidx.compose.material3.PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = confirmPassword.isNotBlank() && confirmPassword != password
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = { viewModel.setAcceptTerms(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colors.primary,
                        uncheckedColor = colors.onSurfaceVariant
                    )
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Aceito os ", fontSize = 12.sp, color = colors.onSurfaceVariant)
                    Row {
                        Text("Termos de Uso", fontSize = 12.sp, color = colors.primary, fontWeight = FontWeight.Medium)
                        Text(" e ", fontSize = 12.sp, color = colors.onSurfaceVariant)
                        Text("Política de Privacidade", fontSize = 12.sp, color = colors.primary, fontWeight = FontWeight.Medium)
                    }
                }
            }
            
            Button(
                onClick = { viewModel.register() },
                enabled = isValid && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Criar conta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            error?.let { msg ->
                Text(msg, fontSize = 12.sp, color = colors.error, textAlign = TextAlign.Center)
            }
            
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Já tem conta? ", fontSize = 14.sp, color = colors.onSurfaceVariant)
                Text(
                    text = "Entrar",
                    fontSize = 14.sp,
                    color = colors.primary,
                    fontWeight = FontWeight.Medium
                ).also {
                    androidx.compose.foundation.gestures.clickable {
                        onGoToLogin()
                    }
                }
            }
            
            // Google Sign In
            Spacer(16.dp)
            androidx.compose.material3.OutlinedButton(
                onClick = { /* Google Sign In */ },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔍", fontSize = 18.sp)
                    Text("Continuar com Google", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun AuthScreen.ForgotPassword(onBackToLogin: () -> Unit) {
    val viewModel: AuthViewModel = hiltViewModel()
    val email by viewModel.forgotEmail.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    AuthTemplate(
        title = "Recuperar senha",
        subtitle = "Envie um link para seu email",
        illustration = "🔑"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Digite seu email cadastrado para receber instruções de redefinição de senha.", fontSize = 14.sp, color = colors.onSurfaceVariant)
            
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.setForgotEmail(it) },
                label = { Text("Email") },
                placeholder = { Text("seu@email.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Button(
                onClick = { viewModel.sendResetEmail() },
                enabled = !isLoading && email.contains("@"),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Enviar link", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            error?.let { msg ->
                Text(msg, fontSize = 12.sp, color = colors.error, textAlign = TextAlign.Center)
            }
            
            success?.let {
                Text("Email enviado! Verifique sua caixa de entrada.", fontSize = 12.sp, color = colors.success, textAlign = TextAlign.Center)
            }
            
            Text(
                text = "Voltar ao login",
                fontSize = 14.sp,
                color = colors.primary,
                fontWeight = FontWeight.Medium
            ).also {
                androidx.compose.foundation.gestures.clickable {
                    onBackToLogin()
                }
            }
        }
    }
}

@Composable
fun AuthScreen.ResetPassword(onBackToLogin: () -> Unit) {
    val viewModel: AuthViewModel = hiltViewModel()
    val password by viewModel.resetPassword.collectAsStateWithLifecycle()
    val confirmPassword by viewModel.resetConfirmPassword.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()
    val isValid by viewModel.isResetValid.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    AuthTemplate(
        title = "Nova senha",
        subtitle = "Crie uma senha forte e segura",
        illustration = "🔐"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.setResetPassword(it) },
                label = { Text("Nova senha") },
                placeholder = { Text("Mínimo 8 caracteres") },
                visualTransformation = androidx.compose.material3.PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = password.isNotBlank() && password.length < 8
            )
            
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.setResetConfirmPassword(it) },
                label = { Text("Confirmar senha") },
                placeholder = { Text("Repita a senha") },
                visualTransformation = androidx.compose.material3.PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = confirmPassword.isNotBlank() && confirmPassword != password
            )
            
            Button(
                onClick = { 
                    viewModel.resetPassword()
                    onBackToLogin()
                },
                enabled = isValid && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Redefinir senha", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            error?.let { msg ->
                Text(msg, fontSize = 12.sp, color = colors.error, textAlign = TextAlign.Center)
            }
            
            success?.let {
                Text("Senha redefinida com sucesso!", fontSize = 12.sp, color = colors.success, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun AuthScreen.VerifyEmail(onVerified: () -> Unit) {
    val viewModel: AuthViewModel = hiltViewModel()
    val code by viewModel.verificationCode.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    AuthTemplate(
        title = "Verifique seu email",
        subtitle = "Enviamos um código para seu email",
        illustration = "📧"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Digite o código de 6 dígitos enviado para seu email.", fontSize = 14.sp, color = colors.onSurfaceVariant)
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) { i ->
                    OutlinedTextField(
                        value = if (code.length > i) code[i].toString() else "",
                        onValueChange = { 
                            val newCode = if (code.length > i) code.substring(0, i) + it + code.substring(i + 1) else code + it
                            if (newCode.length <= 6) viewModel.setVerificationCode(newCode)
                        },
                        label = { Text("") },
                        modifier = Modifier.size(48.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = androidx.compose.material3.PasswordVisualTransformation(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Button(
                onClick = { 
                    viewModel.verifyEmail()
                    onVerified()
                },
                enabled = code.length == 6 && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(color = colors.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Verificar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            error?.let { msg ->
                Text(msg, fontSize = 12.sp, color = colors.error, textAlign = TextAlign.Center)
            }
            
            success?.let {
                Text("Email verificado com sucesso!", fontSize = 12.sp, color = colors.success, textAlign = TextAlign.Center)
            }
            
            Text(
                text = "Não recebeu? Reenviar código",
                fontSize = 14.sp,
                color = colors.primary,
                fontWeight = FontWeight.Medium
            ).also {
                androidx.compose.foundation.gestures.clickable {
                    viewModel.resendVerificationEmail()
                }
            }
        }
    }
}

// ============ Template Base ============

@Composable
fun AuthTemplate(
    title: String,
    subtitle: String,
    illustration: String,
    content: @Composable () -> Unit
) {
    val colors = CycleTrackColors()
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(24.dp)
            Text(text = illustration, fontSize = 64.sp)
            Spacer(24.dp)
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(8.dp)
            Text(
                text = subtitle,
                fontSize = 16.sp,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(32.dp)
            content()
            Spacer(24.dp)
        }
    }
}