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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.seuapp.cycletrack.domain.usecase.GenerateCheckInUseCase
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import com.seuapp.cycletrack.presentation.viewmodel.HomeViewModel

/**
 * Lista de mensagens do assistente de saude conversacional (Fase 6). Hoje
 * existe uma unica thread, gerada por regras fixas a partir da fase do
 * ciclo atual.
 */
@Composable
fun MessagesScreen(onOpenChat: (String) -> Unit = {}, viewModel: HomeViewModel = hiltViewModel()) {
    val colors = CycleTrackColors()
    val dayOfCycle by viewModel.dayOfCycle.collectAsStateWithLifecycle()
    val cyclePhase by viewModel.cyclePhase.collectAsStateWithLifecycle()

    val thread = remember(dayOfCycle, cyclePhase) {
        GenerateCheckInUseCase(dayOfCycle, cyclePhase)
    }
    val answered = answeredThreads.contains(thread.id)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            horizontal = CycleTrackDimens.spacingMd,
            vertical = CycleTrackDimens.spacingLg
        ),
        verticalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingMd)
    ) {
        item {
            Text(
                text = "Mensagens",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenChat(thread.id) }
                    .padding(vertical = CycleTrackDimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(CycleTrackDimens.avatarLg)
                        .background(colors.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ChatBubble, contentDescription = null, tint = colors.primary)
                }
                Spacer(Modifier.width(CycleTrackDimens.spacingSm))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Assistente de Saude",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = thread.preview,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                Spacer(Modifier.width(CycleTrackDimens.spacingSm))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = thread.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!answered) {
                        Spacer(Modifier.width(CycleTrackDimens.spacingXxs))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(colors.primary, CircleShape)
                        )
                    }
                }
            }
        }
    }
}
