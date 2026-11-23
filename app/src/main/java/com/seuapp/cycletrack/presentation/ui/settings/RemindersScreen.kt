package com.seuapp.cycletrack.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens

private data class ReminderCategory(
    val title: String,
    val subtitle: String,
    val times: List<String>
)

private val reminderCategories = listOf(
    ReminderCategory(
        title = "Lembrete de menstruação",
        subtitle = "Avisa alguns dias antes do período previsto",
        times = listOf("08:00", "12:00", "20:00")
    ),
    ReminderCategory(
        title = "Lembrete de ovulação",
        subtitle = "Avisa no início da janela fértil",
        times = listOf("08:00", "12:00", "20:00")
    ),
    ReminderCategory(
        title = "Dica diária",
        subtitle = "Conteúdo personalizado sobre seu ciclo",
        times = listOf("08:00", "12:00", "20:00")
    )
)

/**
 * Tela de lembretes com categorias e horarios (Fase 4). A persistencia e
 * apenas local (preferencias), sem agendamento real via WorkManager.
 */
@Composable
fun RemindersScreen(onBack: () -> Unit = {}) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Text(
                    text = "Lembretes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        items(reminderCategories) { category ->
            ReminderCard(category)
        }
    }
}

@Composable
private fun ReminderCard(category: ReminderCategory) {
    var enabled by remember { mutableStateOf(true) }
    var selectedTime by remember { mutableStateOf(category.times.first()) }

    Card(
        shape = RoundedCornerShape(CycleTrackDimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = category.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = enabled, onCheckedChange = { enabled = it })
            }

            if (enabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = CycleTrackDimens.spacingSm),
                    horizontalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingXs)
                ) {
                    category.times.forEach { time ->
                        TimeChip(
                            time = time,
                            selected = time == selectedTime,
                            onClick = { selectedTime = time }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeChip(time: String, selected: Boolean, onClick: () -> Unit) {
    val colors = CycleTrackColors()
    Box(
        modifier = Modifier
            .background(
                if (selected) colors.primary else colors.surfaceVariant,
                RoundedCornerShape(CycleTrackDimens.radiusSm)
            )
            .clickable { onClick() }
            .padding(horizontal = CycleTrackDimens.spacingSm, vertical = 6.dp)
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) colors.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
