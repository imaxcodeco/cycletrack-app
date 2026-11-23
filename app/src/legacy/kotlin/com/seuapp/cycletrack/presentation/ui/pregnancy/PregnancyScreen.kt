package com.seuapp.cycletrack.presentation.ui.pregnancy

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
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seuapp.cycletrack.presentation.state.PregnancyViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormatter
import java.util.Locale

/**
 * Tela de Gravidez - Acompanhamento semanal com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun PregnancyScreen(
    onNavigateToAppointments: () -> Unit,
    onNavigateToTests: () -> Unit,
    onNavigateToWeight: () -> Unit
) {
    val viewModel: PregnancyViewModel = hiltViewModel()
    val activePregnancy by viewModel.activePregnancy.collectAsStateWithLifecycle()
    val currentWeek by viewModel.currentWeek.collectAsStateWithLifecycle()
    val trimester by viewModel.trimester.collectAsStateWithLifecycle()
    val daysUntilDue by viewModel.daysUntilDue.collectAsStateWithLifecycle()
    val babySize by viewModel.babySize.collectAsStateWithLifecycle()
    val babyWeight by viewModel.babyWeight.collectAsStateWithLifecycle()
    val babyLength by viewModel.babyLength.collectAsStateWithLifecycle()
    val milestones by viewModel.developmentMilestones.collectAsStateWithLifecycle()
    val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()
    val appointments by viewModel.upcomingAppointments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    val trimesterColor = viewModel.trimesterColor.collectAsStateWithLifecycle()
    val progressPercentage = viewModel.progressPercentage.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (activePregnancy == null) {
            PregnancyStartCard(
                onStartPregnancy = { /* navigate to start pregnancy flow */ }
            )
        } else {
            // Pregnancy Header
            PregnancyHeaderCard(
                currentWeek = currentWeek,
                trimester = trimester,
                daysUntilDue = daysUntilDue,
                babySize = babySize,
                babyWeight = babyWeight,
                babyLength = babyLength,
                progressPercentage = progressPercentage,
                trimesterColor = trimesterColor
            )
            
            // Quick Actions
            PregnancyActionsRow(
                onAppointments = onNavigateToAppointments,
                onTests = onNavigateToTests,
                onWeight = onNavigateToWeight,
                trimesterColor = trimesterColor
            )
            
            // Baby Development
            BabyDevelopmentCard(milestones = milestones)
            
            // Health Metrics
            HealthMetricsCard(
                currentWeight = weightHistory.lastOrNull()?.weight ?: 0.0,
                totalGain = weightHistory.lastOrNull()?.let { it.weight - weightHistory.first().weight } ?: 0.0,
                trimesterColor = trimesterColor
            )
            
            // Upcoming Appointments
            UpcomingAppointmentsCard(
                appointments = appointments,
                trimesterColor = trimesterColor
            )
        }
        
        error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
    }
}

@Composable
fun PregnancyStartCard(onStartPregnancy: () -> Unit) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        colors = CardDefaults.cardColors(
            containerColor = colors.trimester2.copy(alpha = 0.1f),
            contentColor = colors.trimester2
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🤰", fontSize = 64.sp)
            Text("Modo Gravidez", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Acompanhe semana a semana o desenvolvimento do seu bebê", fontSize = 16.sp, textAlign = TextAlign.Center)
            Text("Registre consultas, exames, peso e muito mais", fontSize = 14.sp, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
            
            Button(onClick = onStartPregnancy, shape = RoundedCornerShape(12.dp)) {
                Text("Iniciar acompanhamento", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PregnancyHeaderCard(
    currentWeek: Int,
    trimester: Int,
    daysUntilDue: Int,
    babySize: String,
    babyWeight: Int,
    babyLength: Float,
    progressPercentage: Float,
    trimesterColor: Color
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        colors = CardDefaults.cardColors(
            containerColor = trimesterColor,
            contentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🤰 Semana $currentWeek", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${getTrimesterLabel(trimester)} • Faltam $daysUntilDue dias", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                }
                Text("👶", fontSize = 48.sp)
            }
            
            // Progress
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progresso da gestação", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    Text("${progressPercentage.roundToInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                LinearProgressIndicator(
                    progress = progressPercentage / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    trackThickness = 8.dp,
                    indicatorColor = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    cornerRadius = 4.dp
                )
            }
            
            // Baby size comparison
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tamanho do bebê", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    Text(babySize, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Peso estimado", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    Text("${babyWeight}g", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Comprimento", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    Text("${String.format("%.1f", babyLength)} cm", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PregnancyActionsRow(
    onAppointments: () -> Unit,
    onTests: () -> Unit,
    onWeight: () -> Unit,
    trimesterColor: Color
) {
    val colors = CycleTrackColors()
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PregnancyActionButton("📅", "Consultas", colors.trimester1, onAppointments)
        PregnancyActionButton("🧪", "Exames", colors.trimester2, onTests)
        PregnancyActionButton("⚖️", "Peso", colors.trimester3, onWeight)
    }
}

@Composable
fun PregnancyActionButton(icon: String, label: String, color: Color, onClick: () -> Unit) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth(0.3f)
            .size(100.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = icon, fontSize = 28.sp)
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun BabyDevelopmentCard(milestones: List<PregnancyViewModel.Milestone>) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Desenvolvimento do bebê", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                milestones.forEach { milestone ->
                    DevRow(
                        icon = milestone.icon,
                        title = milestone.title,
                        detail = milestone.description,
                        completed = milestone.completed,
                        color = if (milestone.completed) colors.success else colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DevRow(icon: String, title: String, detail: String, completed: Boolean, color: Color) {
    val colors = CycleTrackColors()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
                if (completed) {
                    Text(text = "✓", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.success)
                }
            }
            Text(text = detail, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun HealthMetricsCard(
    currentWeight: Double,
    totalGain: Double,
    trimesterColor: Color
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Suas métricas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard("⚖️", "Peso atual", "${String.format("%.1f", currentWeight)} kg", "${if (totalGain >= 0) "+" else ""}${String.format("%.1f", totalGain)} kg total", trimesterColor)
                MetricCard("💓", "Pressão", "110/70", "Normal", colors.success)
                MetricCard("🍬", "Glicose", "85 mg/dL", "Jejum normal", colors.phaseFollicular)
            }
        }
    }
}

@Composable
fun MetricCard(icon: String, label: String, value: String, detail: String, color: Color) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth(0.3f)
            .size(100.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 24.sp)
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(text = detail, fontSize = 10.sp, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun UpcomingAppointmentsCard(
    appointments: List<com.seuapp.cycletrack.domain.model.PregnancyAppointment>,
    trimesterColor: Color
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Próximas consultas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                Text("Ver todas", fontSize = 14.sp, color = colors.primary, fontWeight = FontWeight.Medium)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (appointments.isEmpty()) {
                    Text("Nenhuma consulta agendada", fontSize = 14.sp, color = colors.onSurfaceVariant)
                } else {
                    appointments.forEach { appointment ->
                        AppointmentRow(
                            icon = "📅",
                            title = appointment.title,
                            detail = "${appointment.dateTime.format(DateTimeFormatter.ofPattern("dd/MM • HH:mm"))} • ${appointment.doctor}",
                            color = trimesterColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentRow(icon: String, title: String, detail: String, color: Color) {
    val colors = CycleTrackColors()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface)
            Text(text = detail, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.fillMaxWidth())
        androidx.compose.material3.Icon(
            imageVector = androidx.compose.material.icons.Icons.Filled.ChevronRight,
            contentDescription = "",
            tint = colors.onSurfaceVariant
        )
    }
}

private fun getTrimesterLabel(trimester: Int): String = when (trimester) {
    1 -> "1º Trimestre"
    2 -> "2º Trimestre"
    3 -> "3º Trimestre"
    else -> "Gravidez"
}

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer