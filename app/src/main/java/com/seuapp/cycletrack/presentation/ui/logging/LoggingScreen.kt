@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.seuapp.cycletrack.presentation.ui.logging

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import com.seuapp.cycletrack.presentation.viewmodel.SymptomViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val ptBr = Locale("pt", "BR")

private data class LogCategory(val title: String, val subtitle: String? = null, val options: List<String>)

private val categories = listOf(
    LogCategory(
        title = "Fluxo menstrual",
        subtitle = "Estime seu fluxo médio diário",
        options = listOf("Leve", "Médio", "Intenso", "Coágulos")
    ),
    LogCategory(
        title = "Testes de ovulação",
        subtitle = "Registre testes para saber seus dias férteis",
        options = listOf("Não fiz testes", "Teste: positivo", "Teste: negativo", "Confirmado por outro método")
    ),
    LogCategory(
        title = "Sexo e libido",
        options = listOf(
            "Não fiz sexo", "Sexo com proteção", "Sexo sem proteção", "Sexo oral", "Sexo anal",
            "Masturbação", "Toque sensual", "Brinquedos sexuais", "Orgasmo",
            "Libido alta", "Libido neutra", "Libido baixa"
        )
    ),
    LogCategory(
        title = "Humor",
        options = listOf(
            "Calma", "Feliz", "Energética", "Alegre", "Mudanças de humor", "Irritada", "Triste",
            "Ansiosa", "Desanimada", "Culpada", "Pensamentos obsessivos", "Pouca energia",
            "Apática", "Confusa", "Muito autocrítica"
        )
    ),
    LogCategory(
        title = "Sintomas",
        options = listOf(
            "Está tudo bem", "Cólicas", "Seios sensíveis", "Dor de cabeça", "Acne", "Dor nas costas",
            "Fadiga", "Desejos", "Insônia", "Dor abdominal", "Coceira vaginal", "Ressecamento vaginal"
        )
    ),
    LogCategory(
        title = "Secreção vaginal",
        options = listOf(
            "Sem secreção", "Pastosa", "Aquosa", "Viscosa", "Clara de ovo",
            "Sangramento de escape", "Corrimento incomum", "Branca grumosa", "Cinza"
        )
    ),
    LogCategory(
        title = "Digestão e fezes",
        options = listOf("Náusea", "Inchaço", "Prisão de ventre", "Diarreia")
    ),
    LogCategory(
        title = "Testes de gravidez",
        options = listOf("Não fiz testes", "Positivo", "Negativo", "Linha fraca")
    ),
    LogCategory(
        title = "Outros",
        options = listOf(
            "Viagem", "Estresse", "Meditação", "Registros em diário",
            "Exercícios de Kegel", "Exercícios de respiração", "Doença ou ferimento", "Álcool"
        )
    ),
    LogCategory(
        title = "Atividade física",
        options = listOf(
            "Não me exercitei", "Ioga", "Academia", "Aeróbica e dança",
            "Natação", "Esportes de equipe", "Corrida", "Ciclismo", "Caminhada"
        )
    )
)

/**
 * Tela de registro diario: fluxo, ovulacao, sexo, humor, sintomas, secrecao,
 * digestao, testes de gravidez, estilo de vida, atividade fisica, peso e temperatura.
 */
@Composable
fun LoggingScreen(viewModel: SymptomViewModel = hiltViewModel()) {
    val colors = CycleTrackColors()
    val symptoms by viewModel.symptoms.collectAsStateWithLifecycle()
    val periodLogMessage by viewModel.periodLogMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var weightText by remember { mutableStateOf("") }
    var temperatureText by remember { mutableStateOf("") }

    LaunchedEffect(selectedDate) {
        viewModel.loadSymptoms(selectedDate)
    }

    LaunchedEffect(symptoms) {
        weightText = symptoms.find { it.category == "Peso" }?.value?.let(::formatMeasurement) ?: ""
        temperatureText = symptoms.find { it.category == "Temperatura" }?.value?.let(::formatMeasurement) ?: ""
    }

    LaunchedEffect(periodLogMessage) {
        periodLogMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumePeriodLogMessage()
        }
    }

    fun isSelected(category: String, option: String): Boolean =
        symptoms.any { it.category == category && it.name == option }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            horizontal = CycleTrackDimens.spacingMd,
            vertical = CycleTrackDimens.spacingLg
        ),
        verticalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingMd)
    ) {
        item {
            Text(
                text = "Registrar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Escolha o dia e preencha o que quiser acompanhar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(
                        horizontal = CycleTrackDimens.spacingSm,
                        vertical = CycleTrackDimens.spacingSm
                    ),
                    horizontalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingXs)
                ) {
                    items((0..13).map { LocalDate.now().minusDays(it.toLong()) }) { date ->
                        val isSelectedDate = date == selectedDate
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(
                                    if (isSelectedDate) colors.primary else Color.Transparent,
                                    RoundedCornerShape(CycleTrackDimens.radiusMd)
                                )
                                .clickable { selectedDate = date }
                                .padding(horizontal = CycleTrackDimens.spacingSm, vertical = 6.dp)
                        ) {
                            Text(
                                text = date.month.getDisplayName(TextStyle.SHORT, ptBr),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelectedDate) colors.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${date.dayOfMonth}",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelectedDate) colors.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.logPeriodStart(selectedDate) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.phaseMenstrual,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Filled.WaterDrop, contentDescription = null)
                Spacer(Modifier.width(CycleTrackDimens.spacingSm))
                Text("Minha menstruação começou neste dia")
            }
        }

        items(categories) { category ->
            SectionCard(title = category.title, subtitle = category.subtitle) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingXs),
                    verticalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingXs)
                ) {
                    category.options.forEach { option ->
                        val selected = isSelected(category.title, option)
                        LogChip(
                            text = option,
                            selected = selected,
                            onClick = { viewModel.toggleSymptom(selectedDate, category.title, option) }
                        )
                    }
                }
            }
        }

        item {
            SectionCard(title = "Peso e temperatura") {
                Row(horizontalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingSm)) {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { text ->
                            weightText = text
                            viewModel.saveMeasurement(selectedDate, "Peso", text.toMeasurementOrNull())
                        },
                        label = { Text("Peso (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = temperatureText,
                        onValueChange = { text ->
                            temperatureText = text
                            viewModel.saveMeasurement(selectedDate, "Temperatura", text.toMeasurementOrNull())
                        },
                        label = { Text("Temp. (°C)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = "As informações são salvas automaticamente no aparelho " +
                        "para ${selectedDate.format(DateTimeFormatter.ofPattern("d 'de' MMMM", ptBr))}.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(CycleTrackDimens.spacingMd)
                )
            }
        }
    }
    }
}

@Composable
private fun SectionCard(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(CycleTrackDimens.spacingSm))
            content()
        }
    }
}

private fun String.toMeasurementOrNull(): Double? =
    trim().replace(",", ".").toDoubleOrNull()

private fun formatMeasurement(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

@Composable
private fun LogChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = CycleTrackColors()
    Box(
        modifier = Modifier
            .background(
                if (selected) colors.primary else colors.surfaceVariant,
                RoundedCornerShape(CycleTrackDimens.radiusSm)
            )
            .clickable { onClick() }
            .padding(horizontal = CycleTrackDimens.spacingSm, vertical = CycleTrackDimens.spacingXs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) colors.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
