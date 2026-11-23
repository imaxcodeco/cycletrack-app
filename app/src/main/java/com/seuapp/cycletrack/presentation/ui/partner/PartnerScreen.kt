package com.seuapp.cycletrack.presentation.ui.partner

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seuapp.cycletrack.domain.model.PartnerConnection
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import com.seuapp.cycletrack.presentation.viewmodel.HomeViewModel
import com.seuapp.cycletrack.presentation.viewmodel.PartnerViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val partnerTeal = Color(0xFF1EC4BC)
private val ptBr = Locale("pt", "BR")
private val dateFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM", ptBr)

/**
 * Tela do modo parceiro "Flo a Dois" (Fase 7). Vinculacao e sincronizacao
 * sao simuladas localmente via Realm, sem backend real (Firestore nao foi
 * conectado por decisao explicita nesta sessao).
 */
@Composable
fun PartnerScreen(
    onBack: () -> Unit = {},
    partnerViewModel: PartnerViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val partner by partnerViewModel.partner.collectAsStateWithLifecycle()

    when (partner?.status) {
        PartnerConnection.STATUS_CONNECTED -> PartnerConnectedContent(
            partner = partner!!,
            homeViewModel = homeViewModel,
            onBack = onBack,
            onDisconnect = { partnerViewModel.disconnect() }
        )
        PartnerConnection.STATUS_PENDING -> PartnerInviteContent(
            partner = partner!!,
            onBack = onBack,
            onSimulateAccept = { name -> partnerViewModel.simulatePartnerAccepted(name) },
            onCancelInvite = { partnerViewModel.disconnect() }
        )
        else -> PartnerLandingContent(
            onBack = onBack,
            onGenerateInvite = { partnerViewModel.generateInvite() }
        )
    }
}

@Composable
private fun PartnerLandingContent(onBack: () -> Unit, onGenerateInvite: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(partnerTeal)
                    .padding(CycleTrackDimens.spacingMd)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                Spacer(Modifier.height(CycleTrackDimens.spacingSm))
                Text(
                    text = "CycleTrack a Dois",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Compartilhe a jornada com seu parceiro",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Spacer(Modifier.height(CycleTrackDimens.spacingMd))
                listOf(
                    "Mostre dados do seu ciclo ao seu parceiro.",
                    "Vocês dois recebem orientações sobre os melhores dias.",
                    "Ele recebe informações sobre fertilidade e como pode ajudar."
                ).forEach { line ->
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 3.dp)) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.width(CycleTrackDimens.spacingXs))
                        Text(text = line, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(CycleTrackDimens.spacingMd)) {
                Text(
                    text = "O que seu parceiro pode ver",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(CycleTrackDimens.spacingSm))
                Card(
                    shape = RoundedCornerShape(CycleTrackDimens.radiusLg),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
                        PreviewLine("Dia do ciclo", "10")
                        PreviewLine("Possíveis sintomas", "Cólicas, fadiga")
                        PreviewLine("Chance de engravidar", "Alta")
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(CycleTrackDimens.spacingMd)
                    .background(partnerTeal, RoundedCornerShape(CycleTrackDimens.radiusLg))
                    .clickable(onClick = onGenerateInvite)
                    .padding(vertical = CycleTrackDimens.spacingMd),
                contentAlignment = Alignment.Center
            ) {
                Text("Vincular conta", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PartnerInviteContent(
    partner: PartnerConnection,
    onBack: () -> Unit,
    onSimulateAccept: (String) -> Unit,
    onCancelInvite: () -> Unit
) {
    var partnerName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(CycleTrackDimens.spacingMd),
        verticalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingMd)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Text("Convidar parceiro", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusLg),
                colors = CardDefaults.cardColors(containerColor = partnerTeal.copy(alpha = 0.12f))
            ) {
                Column(Modifier.padding(CycleTrackDimens.spacingMd), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Compartilhe este código", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(CycleTrackDimens.spacingSm))
                    Text(
                        text = partner.inviteCode,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = partnerTeal
                    )
                    Spacer(Modifier.height(CycleTrackDimens.spacingSm))
                    Text(
                        text = "Aguardando seu parceiro aceitar o convite...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Simulação de teste: como este ambiente não tem um segundo aparelho, " +
                    "digite um nome e confirme abaixo para simular o parceiro aceitando o convite.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OutlinedTextField(
                value = partnerName,
                onValueChange = { partnerName = it },
                label = { Text("Nome do parceiro (simulação)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(partnerTeal, RoundedCornerShape(CycleTrackDimens.radiusLg))
                    .clickable { onSimulateAccept(partnerName) }
                    .padding(vertical = CycleTrackDimens.spacingMd),
                contentAlignment = Alignment.Center
            ) {
                Text("Simular aceite do convite", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        item {
            Text(
                text = "Cancelar convite",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable(onClick = onCancelInvite)
            )
        }
    }
}

@Composable
private fun PartnerConnectedContent(
    partner: PartnerConnection,
    homeViewModel: HomeViewModel,
    onBack: () -> Unit,
    onDisconnect: () -> Unit
) {
    val dayOfCycle by homeViewModel.dayOfCycle.collectAsStateWithLifecycle()
    val nextPeriod by homeViewModel.nextPeriod.collectAsStateWithLifecycle()
    val fertileWindow by homeViewModel.fertileWindow.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(CycleTrackDimens.spacingMd),
        verticalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingMd)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Text("CycleTrack a Dois", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            }
        }

        item {
            Text(
                text = "Vinculado com ${partner.partnerName}",
                style = MaterialTheme.typography.bodyLarge,
                color = partnerTeal,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(CycleTrackDimens.radiusLg),
                colors = CardDefaults.cardColors(containerColor = partnerTeal)
            ) {
                Column(Modifier.padding(CycleTrackDimens.spacingMd)) {
                    Text("O que seu parceiro vê agora", color = Color.White, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(CycleTrackDimens.spacingSm))
                    PreviewLine("Dia do ciclo", "$dayOfCycle", color = Color.White)
                    PreviewLine("Próxima menstruação", nextPeriod?.format(dateFormatter) ?: "-", color = Color.White)
                    PreviewLine(
                        "Janela fértil",
                        if (fertileWindow != null) {
                            "${fertileWindow!!.start.format(dateFormatter)} - ${fertileWindow!!.end.format(dateFormatter)}"
                        } else {
                            "-"
                        },
                        color = Color.White
                    )
                }
            }
        }

        item {
            Text(
                text = "Desvincular parceiro",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable(onClick = onDisconnect)
            )
        }
    }
}

@Composable
private fun PreviewLine(label: String, value: String, color: Color = CycleTrackColors().onSurfaceVariant) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = color)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = color)
    }
}
