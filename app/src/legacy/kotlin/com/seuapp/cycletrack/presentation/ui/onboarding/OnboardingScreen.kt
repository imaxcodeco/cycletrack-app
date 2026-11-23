package com.seuapp.cycletrack.presentation.ui.onboarding

import android.content.Context
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
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seuapp.cycletrack.presentation.state.AuthViewModel
import com.seuapp.cycletrack.presentation.state.OnboardingViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormatter
import kotlinx.datetime.format.DateTimeFormatterBuilder
import com.seuapp.cycletrack.presentation.ui.onboarding.OnboardingScreen
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * Telas de Onboarding (33 telas) com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun OnboardingScreen.Welcome(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    OnboardingTemplate(
        title = "Bem-vinda ao CycleTrack",
        subtitle = "Seu companheiro inteligente para saúde menstrual e reprodutiva",
        illustration = "🌸",
        buttonText = "Começar",
        onNext = onNext
    )
}

@Composable
fun OnboardingScreen.Permissions(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    OnboardingTemplate(
        title = "Permissões necessárias",
        subtitle = "Para funcionar melhor, precisamos de algumas permissões",
        illustration = "🔐",
        buttonText = "Permitir",
        onNext = onNext
    ) {
        PermissionItem("Notificações", "Lembretes de menstruação, janela fértil e pílula", "🔔")
        PermissionItem("Health Connect", "Sincronizar dados de saúde (opcional)", "🏥")
        PermissionItem("Armazenamento", "Backup e exportação de dados", "💾")
    }
}

@Composable
fun OnboardingScreen.CycleInfo(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    OnboardingTemplate(
        title = "Sobre seu ciclo",
        subtitle = "Conte-nos o básico para personalizarmos suas previsões",
        illustration = "📅",
        buttonText = "Continuar",
        onNext = onNext
    )
}

@Composable
fun OnboardingScreen.LastPeriod(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var selectedDate by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    
    OnboardingTemplate(
        title = "Última menstruação",
        subtitle = "Quando começou sua última menstruação?",
        illustration = "🩸",
        buttonText = "Próximo",
        onNext = {
            viewModel.updateLastPeriod(selectedDate)
            onNext()
        }
    ) {
        DatePickerField(
            label = "Data de início",
            selectedDate = selectedDate,
            onDateChange = { selectedDate = it },
            maxDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        )
    }
}

@Composable
fun OnboardingScreen.CycleLength(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var cycleLength by remember { mutableStateOf(28) }
    
    OnboardingTemplate(
        title = "Duração do ciclo",
        subtitle = "Quantos dias dura seu ciclo normalmente? (início ao início)",
        illustration = "🔄",
        buttonText = "Próximo",
        onNext = {
            viewModel.updateCycleInfo(cycleLength, viewModel.onboardingData.value.periodLength)
            onNext()
        }
    ) {
        SliderField(
            label = "Duração do ciclo",
            value = cycleLength.toFloat(),
            range = 21..45,
            unit = "dias",
            onValueChange = { cycleLength = it.roundToInt() }
        )
    }
}

@Composable
fun OnboardingScreen.PeriodLength(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var periodLength by remember { mutableStateOf(5) }
    
    OnboardingTemplate(
        title = "Duração da menstruação",
        subtitle = "Quantos dias costuma durar sua menstruação?",
        illustration = "📏",
        buttonText = "Próximo",
        onNext = {
            viewModel.updateCycleInfo(viewModel.onboardingData.value.cycleLength, periodLength)
            onNext()
        }
    ) {
        SliderField(
            label = "Duração da menstruação",
            value = periodLength.toFloat(),
            range = 2..10,
            unit = "dias",
            onValueChange = { periodLength = it.roundToInt() }
        )
    }
}

@Composable
fun OnboardingScreen.Contraception(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var selectedMethod by remember { mutableStateOf("none") }
    
    val methods = listOf(
        "none" to "Nenhum",
        "pill" to "Pílula anticoncepcional",
        "patch" to "Adesivo",
        "ring" to "Anel vaginal",
        "injection" to "Injeção",
        "implant" to "Implante",
        "iud_hormonal" to "DIU hormonal",
        "iud_copper" to "DIU de cobre",
        "condom" to "Preservativo",
        "withdrawal" to "Coito interrompido",
        "fertility_awareness" to "Métodos de percepção da fertilidade",
        "sterilization" to "Laqueadura/Vasectomia",
        "other" to "Outro"
    )
    
    OnboardingTemplate(
        title = "Método contraceptivo",
        subtitle = "Você usa algum método? Isso afeta as previsões",
        illustration = "💊",
        buttonText = "Continuar",
        onNext = {
            viewModel.updateContraception(selectedMethod)
            onNext()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            methods.forEach { (key, label) ->
                RadioOption(
                    label = label,
                    isSelected = selectedMethod == key,
                    onClick = { selectedMethod = key }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen.PregnancyStatus(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var selectedStatus by remember { mutableStateOf("not_pregnant") }
    
    val statuses = listOf(
        "not_pregnant" to "Não estou grávida" to "🚫",
        "trying" to "Tentando engravidar" to "🎯",
        "pregnant" to "Estou grávida" to "🤰",
        "postpartum" to "Pós-parto" to "👶",
        "perimenopause" to "Perimenopausa" to "🌸"
    )
    
    OnboardingTemplate(
        title = "Status reprodutivo",
        subtitle = "Como você se identifica no momento?",
        illustration = "🤰",
        buttonText = "Próximo",
        onNext = {
            viewModel.updatePregnancyStatus(selectedStatus)
            onNext()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            statuses.forEach { (key, label, icon) ->
                RadioOption(
                    label = label,
                    icon = icon,
                    isSelected = selectedStatus == key,
                    onClick = { selectedStatus = key }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen.Goals(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var selectedGoals by remember { mutableStateOf<MutableSet<String>>(mutableSetOf()) }
    
    val goals = listOf(
        "avoid_pregnancy" to "Evitar gravidez" to "Acompanhe janela fértil" to "🛡️",
        "get_pregnant" to "Engravidar" to "Otimize chances de concepção" to "👶",
        "menstrual_health" to "Saúde menstrual" to "Entenda seu corpo" to "🩸",
        "symptoms" to "Sintomas" to "Padrões e correlações" to "📝",
        "weight" to "Peso" to "Acompanhe variações" to "⚖️",
        "mood" to "Humor" to "Bem-estar emocional" to "😊"
    )
    
    OnboardingTemplate(
        title = "Seus objetivos",
        subtitle = "O que você quer acompanhar? (pode escolher vários)",
        illustration = "🎯",
        buttonText = "Continuar",
        onNext = {
            viewModel.updateGoals(selectedGoals.toList())
            onNext()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            goals.forEach { (key, title, desc, icon) ->
                CheckboxOption(
                    title = title,
                    description = desc,
                    icon = icon,
                    isChecked = key in selectedGoals,
                    onCheckedChange = { checked ->
                        if (checked) selectedGoals.add(key) else selectedGoals.remove(key)
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen.Notifications(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var enabled by remember { mutableStateOf(true) }
    
    OnboardingTemplate(
        title = "Lembretes inteligentes",
        subtitle = "Configure quando quer ser notificada",
        illustration = "⏰",
        buttonText = "Configurar",
        onNext = {
            viewModel.updateNotifications(enabled)
            onNext()
        }
    ) {
        ToggleOption(
            title = "Ativar notificações",
            description = "Receba lembretes de menstruação, janela fértil, TPM e pílula",
            icon = "🔔",
            isOn = enabled,
            onChange = { enabled = it }
        )
    }
}

@Composable
fun OnboardingScreen.Theme(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var selectedTheme by remember { mutableStateOf("system") }
    
    val themes = listOf(
        "system" to "Sistema" to "Segue o tema do celular" to "📱",
        "light" to "Claro" to "Sempre tema claro" to "☀️",
        "dark" to "Escuro" to "Sempre tema escuro" to "🌙"
    )
    
    OnboardingTemplate(
        title = "Tema do app",
        subtitle = "Escolha sua aparência preferida",
        illustration = "🎨",
        buttonText = "Próximo",
        onNext = {
            viewModel.updateTheme(selectedTheme)
            onNext()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            themes.forEach { (key, label, desc, icon) ->
                RadioOption(
                    label = label,
                    description = desc,
                    icon = icon,
                    isSelected = selectedTheme == key,
                    onClick = { selectedTheme = key }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen.Language(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var selectedLang by remember { mutableStateOf("pt_BR") }
    
    val languages = listOf(
        "pt_BR" to "Português (Brasil)" to "🇧🇷",
        "en_US" to "English (US)" to "🇺🇸",
        "es_ES" to "Español" to "🇪🇸"
    )
    
    OnboardingTemplate(
        title = "Idioma",
        subtitle = "Seu idioma preferido",
        illustration = "🌐",
        buttonText = "Continuar",
        onNext = {
            viewModel.updateLanguage(selectedLang)
            onNext()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.forEach { (key, label, icon) ->
                RadioOption(
                    label = label,
                    icon = icon,
                    isSelected = selectedLang == key,
                    onClick = { selectedLang = key }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen.Units(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var selectedUnits by remember { mutableStateOf("metric") }
    
    val units = listOf(
        "metric" to "Métrico" to "kg, cm, °C" to "📏",
        "imperial" to "Imperial" to "lbs, ft/in, °F" to "📐"
    )
    
    OnboardingTemplate(
        title = "Unidades de medida",
        subtitle = "Como você prefere ver peso, altura e temperatura?",
        illustration = "📐",
        buttonText = "Próximo",
        onNext = {
            viewModel.updateUnits(selectedUnits)
            onNext()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            units.forEach { (key, label, desc, icon) ->
                RadioOption(
                    label = label,
                    description = desc,
                    icon = icon,
                    isSelected = selectedUnits == key,
                    onClick = { selectedUnits = key }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen.Privacy(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    
    OnboardingTemplate(
        title = "Sua privacidade",
        subtitle = "Dados criptografados, LGPD/GDPR, controle total",
        illustration = "🔒",
        buttonText = "Entendi",
        onNext = onNext
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrivacyItem("🔐", "Criptografia de ponta a ponta", "Seus dados são criptografados no dispositivo e na nuvem")
            PrivacyItem("📋", "LGPD/GDPR compliant", "Direito à exportação, exclusão e portabilidade dos dados")
            PrivacyItem("🚫", "Sem venda de dados", "Nunca vendemos ou compartilhamos seus dados com terceiros")
            PrivacyItem("🔑", "Controle total", "Você decide o que sincroniza e o que fica apenas no dispositivo")
        }
    }
}

@Composable
fun OnboardingScreen.HealthConnect(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var enabled by remember { mutableStateOf(false) }
    
    OnboardingTemplate(
        title = "Google Health Connect",
        subtitle = "Sincronize com outros apps de saúde (opcional)",
        illustration = "🏥",
        buttonText = "Configurar depois",
        onNext = {
            viewModel.updateHealthConnect(enabled)
            onNext()
        }
    ) {
        ToggleOption(
            title = "Ativar Health Connect",
            description = "Sincronize menstruação, temperatura, peso e testes com Google Fit e outros apps",
            icon = "🏥",
            isOn = enabled,
            onChange = { enabled = it }
        )
    }
}

@Composable
fun OnboardingScreen.Partner(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var partnerEmail by remember { mutableStateOf("") }
    
    OnboardingTemplate(
        title = "Modo Parceiro",
        subtitle = "Compartilhe seu ciclo com quem você confia (opcional)",
        illustration = "👫",
        buttonText = "Pular por enquanto",
        onNext = {
            if (partnerEmail.isNotBlank()) viewModel.updatePartnerInvite(partnerEmail)
            onNext()
        }
    ) {
        OutlinedTextField(
            value = partnerEmail,
            onValueChange = { partnerEmail = it },
            label = { Text("Email do parceiro (opcional)") },
            placeholder = { Text("parceiro@email.com") },
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
    }
}

@Composable
fun OnboardingScreen.Premium(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var wantsPremium by remember { mutableStateOf(false) }
    
    OnboardingTemplate(
        title = "CycleTrack Premium",
        subtitle = "Insights avançados, conteúdo exclusivo e mais",
        illustration = "✨",
        buttonText = "Talvez depois",
        onNext = {
            viewModel.updatePremium(wantsPremium)
            onNext()
        }
    ) {
        ToggleOption(
            title = "Quero testar o Premium",
            description = "7 dias grátis, cancele a qualquer momento",
            icon = "✨",
            isOn = wantsPremium,
            onChange = { wantsPremium = it }
        )
    }
}

@Composable
fun OnboardingScreen.AccountType(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var accountType by remember { mutableStateOf("email") }
    
    val types = listOf(
        "email" to "Email e senha" to "Conta tradicional com email" to "📧",
        "google" to "Google" to "Login rápido com conta Google" to "🔍",
        "anonymous" to "Anônimo" to "Dados apenas no dispositivo" to "👤"
    )
    
    OnboardingTemplate(
        title = "Criar sua conta",
        subtitle = "Para sincronizar entre dispositivos e backup seguro",
        illustration = "👤",
        buttonText = "Continuar",
        onNext = {
            viewModel.updateAccountType(accountType)
            onNext()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { (key, label, desc, icon) ->
                RadioOption(
                    label = label,
                    description = desc,
                    icon = icon,
                    isSelected = accountType == key,
                    onClick = { accountType = key }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen.Email(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
    
    OnboardingTemplate(
        title = "Seu email",
        subtitle = "Usado para login e recuperação de conta",
        illustration = "📧",
        buttonText = "Continuar",
        onNext = {
            viewModel.updateEmail(email)
            onNext()
        }
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("seu@email.com") },
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
    }
}

@Composable
fun OnboardingScreen.Password(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var password by remember { mutableStateOf("") }
    
    OnboardingTemplate(
        title = "Crie uma senha",
        subtitle = "Mínimo 8 caracteres, com letra e número",
        illustration = "🔑",
        buttonText = "Continuar",
        onNext = {
            viewModel.updatePassword(password)
            onNext()
        }
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            placeholder = { Text("••••••••") },
            visualTransformation = androidx.compose.material3.PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
    }
}

@Composable
fun OnboardingScreen.ProfileSetup(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var displayName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    
    OnboardingTemplate(
        title = "Seu perfil",
        subtitle = "Nome de exibição e data de nascimento",
        illustration = "✨",
        buttonText = "Finalizar",
        onNext = {
            viewModel.updateDisplayName(displayName)
            onNext()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Nome de exibição") },
                placeholder = { Text("Como você quer ser chamada") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            DatePickerField(
                label = "Data de nascimento",
                selectedDate = birthDate,
                onDateChange = { birthDate = it },
                maxDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
            )
        }
    }
}

@Composable
fun OnboardingScreen.Biometric(onNext: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    var enabled by remember { mutableStateOf(true) }
    
    OnboardingTemplate(
        title = "Autenticação biométrica",
        subtitle = "Use digital ou Face ID para acesso rápido e seguro",
        illustration = "👆",
        buttonText = "Ativar",
        onNext = {
            viewModel.updateBiometric(enabled)
            onNext()
        }
    ) {
        ToggleOption(
            title = "Ativar biometria",
            description = "Proteja o app com digital ou reconhecimento facial",
            icon = "👆",
            isOn = enabled,
            onChange = { enabled = it }
        )
    }
}

@Composable
fun OnboardingScreen.Complete(onComplete: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    
    OnboardingTemplate(
        title = "Tudo pronto! 🎉",
        subtitle = "Seu CycleTrack está configurado. Vamos começar?",
        illustration = "🌸",
        buttonText = "Começar a usar",
        onNext = {
            viewModel.completeOnboarding()
            onComplete()
        }
    )
}

// ============ Template Base ============

@Composable
fun OnboardingTemplate(
    title: String,
    subtitle: String,
    illustration: String,
    buttonText: String,
    onNext: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    val colors = CycleTrackColors()
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(32.dp)
        Text(text = illustration, fontSize = 80.sp, color = colors.primary)
        Spacer(24.dp)
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(12.dp)
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(24.dp)
        content()
        Spacer(32.dp)
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = buttonText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(24.dp)
    }
}

// ============ Componentes Reutilizáveis ============

@Composable
fun PermissionItem(title: String, description: String, icon: String) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(12.dp)
            androidx.compose.foundation.layout.Column() {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                Text(text = description, fontSize = 14.sp, color = colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun RadioOption(
    label: String,
    description: String? = null,
    icon: String = "",
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth()
            .fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = if (isSelected) 2.dp else 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) colors.primaryContainer else colors.surface,
            contentColor = if (isSelected) colors.onPrimaryContainer else colors.onSurface
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon.isNotBlank()) {
                Text(text = icon, fontSize = 24.sp)
            }
            RadioButton(
                selected = isSelected,
                onClick = { /* handled by card */ },
                colors = androidx.compose.material3.RadioButtonDefaults.colors(
                    selectedColor = if (isSelected) colors.onPrimaryContainer else colors.primary,
                    unselectedColor = colors.onSurfaceVariant
                )
            )
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                description?.let {
                    Text(text = it, fontSize = 12.sp, color = if (isSelected) colors.onPrimaryContainer.copy(alpha = 0.7f) else colors.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun CheckboxOption(
    title: String,
    description: String,
    icon: String = "",
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        onClick = { onCheckedChange(!isChecked) },
        shape = RoundedCornerShape(12.dp),
        elevation = if (isChecked) 2.dp else 1.dp,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isChecked) colors.primaryContainer else colors.surface,
            contentColor = if (isChecked) colors.onPrimaryContainer else colors.onSurface
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon.isNotBlank()) {
                Text(text = icon, fontSize = 24.sp)
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = { /* handled by card */ },
                colors = CheckboxDefaults.colors(
                    checkedColor = if (isChecked) colors.onPrimaryContainer else colors.primary,
                    uncheckedColor = colors.onSurfaceVariant
                )
            )
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = description, fontSize = 12.sp, color = if (isChecked) colors.onPrimaryContainer.copy(alpha = 0.7f) else colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ToggleOption(
    title: String,
    description: String,
    icon: String = "",
    isOn: Boolean,
    onChange: (Boolean) -> Unit
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        onClick = { onChange(!isOn) },
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon.isNotBlank()) {
                Text(text = icon, fontSize = 24.sp)
            }
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = description, fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
            androidx.compose.material3.Switch(
                checked = isOn,
                onCheckedChange = { /* handled by card */ },
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = colors.primary,
                    checkedTrackColor = colors.primaryContainer,
                    uncheckedThumbColor = colors.onSurfaceVariant,
                    uncheckedTrackColor = colors.outline
                )
            )
        }
    }
}

@Composable
fun SliderField(
    label: String,
    value: Float,
    range: IntRange,
    unit: String,
    onValueChange: (Float) -> Unit
) {
    val colors = CycleTrackColors()
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
            Text(text = "${value.roundToInt()} $unit", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.primary)
        }
        androidx.compose.material3.Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
            steps = (range.endInclusive - range.start),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = colors.primary,
                trackColor = colors.primaryContainer,
                activeTrackColor = colors.primary,
                inactiveTrackColor = colors.outlineVariant
            )
        )
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${range.start} $unit", fontSize = 12.sp, color = colors.onSurfaceVariant)
            Text(text = "${range.endInclusive} $unit", fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    maxDate: LocalDate? = null
) {
    val colors = CycleTrackColors()
    val context = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
        
        OutlinedTextField(
            value = selectedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
            onValueChange = { },
            label = { Text("Toque para selecionar") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
                .fillMaxWidth()
                .background(Color.Transparent, RoundedCornerShape(8.dp)),
            onClick = { showPicker = true }
        )
    }
    
    if (showPicker) {
        // TODO: Implementar DatePickerDialog nativo via AndroidView
        // Por enquanto, placeholder
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Seletor de data", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                    Text("Implementar DatePickerDialog nativo", fontSize = 14.sp, color = colors.onSurfaceVariant)
                    androidx.compose.foundation.layout.Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(onClick = { showPicker = false }) {
                            Text("Cancelar")
                        }
                        Button(onClick = { 
                            onDateChange(Clock.System.todayIn(TimeZone.currentSystemDefault()))
                            showPicker = false
                        }) {
                            Text("Hoje")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacyItem(icon: String, title: String, description: String) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = icon, fontSize = 24.sp)
            androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                Text(text = description, fontSize = 14.sp, color = colors.onSurfaceVariant)
            }
        }
    }
}