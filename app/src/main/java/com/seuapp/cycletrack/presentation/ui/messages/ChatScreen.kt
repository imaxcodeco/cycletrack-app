package com.seuapp.cycletrack.presentation.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seuapp.cycletrack.domain.model.MoodOption
import com.seuapp.cycletrack.domain.usecase.GenerateCheckInUseCase
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import com.seuapp.cycletrack.presentation.viewmodel.HomeViewModel

private fun moodReply(moodKey: String): String = when (moodKey) {
    "bem" -> "Que bom ouvir isso! Continue assim."
    "sobrecarregada" -> "Entendo. Respire fundo — um passo de cada vez."
    "raiva" -> "Oscilações de humor são comuns nesta fase do ciclo. Cuide-se."
    "triste" -> "Sinto muito que esteja assim. Estou aqui se precisar de dicas de bem-estar."
    else -> "Obrigada por compartilhar como você está."
}

/**
 * Tela de chat em tela cheia do assistente de saude, com balao de
 * resposta rapida via radio buttons (Fase 6).
 */
@Composable
fun ChatScreen(onClose: () -> Unit = {}, viewModel: HomeViewModel = hiltViewModel()) {
    val colors = CycleTrackColors()
    val dayOfCycle by viewModel.dayOfCycle.collectAsStateWithLifecycle()
    val cyclePhase by viewModel.cyclePhase.collectAsStateWithLifecycle()
    val thread = remember(dayOfCycle, cyclePhase) {
        GenerateCheckInUseCase(dayOfCycle, cyclePhase)
    }

    var selectedMood by remember { mutableStateOf<MoodOption?>(null) }
    var sentMood by remember { mutableStateOf<MoodOption?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CycleTrackDimens.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "Fechar")
            }
            Box(
                modifier = Modifier
                    .size(CycleTrackDimens.avatarSm)
                    .background(colors.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.ChatBubble, contentDescription = null, tint = colors.primary, modifier = Modifier.size(CycleTrackDimens.iconSm))
            }
            Spacer(Modifier.width(CycleTrackDimens.spacingSm))
            Text(
                text = "Assistente CycleTrack",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(CycleTrackDimens.spacingMd),
            verticalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingSm)
        ) {
            items(thread.botLines) { line ->
                BotBubble(text = line, colors = colors)
            }
            if (sentMood != null) {
                item { UserBubble(text = sentMood!!.label, colors = colors) }
                item { BotBubble(text = moodReply(sentMood!!.key), colors = colors) }
            }
        }

        if (sentMood == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceVariant)
                    .padding(CycleTrackDimens.spacingMd)
            ) {
                thread.moodOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMood = option }
                            .padding(vertical = CycleTrackDimens.spacingXs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (selectedMood == option) {
                                Icons.Filled.RadioButtonChecked
                            } else {
                                Icons.Filled.RadioButtonUnchecked
                            },
                            contentDescription = null,
                            tint = colors.primary
                        )
                        Spacer(Modifier.width(CycleTrackDimens.spacingSm))
                        Text(text = option.label, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(Modifier.width(CycleTrackDimens.spacingSm))
                val enabled = selectedMood != null
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (enabled) colors.primary else colors.primary.copy(alpha = 0.35f),
                            RoundedCornerShape(CycleTrackDimens.radiusLg)
                        )
                        .clickable(enabled = enabled) {
                            sentMood = selectedMood
                            answeredThreads.add(thread.id)
                        }
                        .padding(vertical = CycleTrackDimens.spacingMd),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Enviar", color = colors.onPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun BotBubble(text: String, colors: CycleTrackColors) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            modifier = Modifier
                .background(colors.surfaceVariant, RoundedCornerShape(CycleTrackDimens.radiusLg))
                .padding(horizontal = CycleTrackDimens.spacingMd, vertical = CycleTrackDimens.spacingSm)
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun UserBubble(text: String, colors: CycleTrackColors) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .background(colors.primary, RoundedCornerShape(CycleTrackDimens.radiusLg))
                .padding(horizontal = CycleTrackDimens.spacingMd, vertical = CycleTrackDimens.spacingSm)
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = colors.onPrimary)
        }
    }
}
