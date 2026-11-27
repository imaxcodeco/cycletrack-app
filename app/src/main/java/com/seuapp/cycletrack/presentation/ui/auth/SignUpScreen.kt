package com.seuapp.cycletrack.presentation.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens

private const val PRIVACY_POLICY_URL = "https://imaxcodeco.github.io/cycletrack-app/"
private const val TERMS_URL = "https://imaxcodeco.github.io/cycletrack-app/terms.html"

@Composable
fun SignUpScreen(
    onSignUpClick: (email: String, password: String, name: String) -> Unit,
    onSignInClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    var consentChecked by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(CycleTrackDimens.spacingMd),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Criar Conta",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(Modifier.height(CycleTrackDimens.spacingLg))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome completo") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
        )

        Spacer(Modifier.height(CycleTrackDimens.spacingMd))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )

        Spacer(Modifier.height(CycleTrackDimens.spacingMd))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha (mín. 6 caracteres)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        Spacer(Modifier.height(CycleTrackDimens.spacingMd))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar senha") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        if (localError != null || errorMessage != null) {
            Spacer(Modifier.height(CycleTrackDimens.spacingMd))
            Text(
                text = localError ?: errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(CycleTrackDimens.spacingMd))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = consentChecked,
                onCheckedChange = { consentChecked = it },
                enabled = !isLoading,
            )
            Spacer(Modifier.width(CycleTrackDimens.spacingXs))
            val consentText = buildAnnotatedString {
                append("Li e aceito a ")
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.primary)) {
                    append("Política de Privacidade")
                }
                append(" e os ")
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.primary)) {
                    append("Termos de Serviço")
                }
            }
            Text(
                text = consentText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) }) {
                Text("Ver Política", style = MaterialTheme.typography.labelSmall)
            }
            TextButton(onClick = { uriHandler.openUri(TERMS_URL) }) {
                Text("Ver Termos", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(Modifier.height(CycleTrackDimens.spacingMd))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        localError = when {
                            password.length < 6 -> "A senha precisa ter pelo menos 6 caracteres."
                            password != confirmPassword -> "As senhas não coincidem."
                            !consentChecked -> "Você precisa aceitar a Política de Privacidade e os Termos de Serviço."
                            else -> null
                        }
                        if (localError == null) {
                            onSignUpClick(email, password, name)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && consentChecked,
                ) {
                    Text("Criar Conta")
                }
            }
        }

        Spacer(Modifier.height(CycleTrackDimens.spacingMd))

        TextButton(onClick = onSignInClick) {
            Text("Já tem conta? Faça login")
        }
    }
}
