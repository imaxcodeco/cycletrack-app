package com.seuapp.cycletrack.presentation.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens

/**
 * Tela de detalhe de artigo: titulo, credito do revisor e corpo em
 * paragrafos (Fase 5).
 */
@Composable
fun ArticleDetailScreen(articleId: String, onBack: () -> Unit = {}) {
    val colors = CycleTrackColors()
    val article = findArticle(articleId)

    if (article == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Artigo não encontrado", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

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
                    text = article.section,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(CycleTrackDimens.avatarSm)
                        .background(colors.primaryContainer, CircleShape)
                )
                Spacer(Modifier.width(CycleTrackDimens.spacingSm))
                Column {
                    Text(
                        text = "Revisado por",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = article.reviewerName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            Icons.Filled.Verified,
                            contentDescription = null,
                            tint = colors.brandTeal,
                            modifier = Modifier.size(CycleTrackDimens.iconXs)
                        )
                    }
                    Text(
                        text = article.reviewerCredential,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(colors.primaryContainer, RoundedCornerShape(CycleTrackDimens.radiusLg)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.seuapp.cycletrack.R.drawable.illustration_uterus_detail),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .padding(CycleTrackDimens.spacingSm),
                    contentScale = ContentScale.Fit
                )
            }
        }

        items(article.body) { paragraph ->
            Text(
                text = paragraph,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
