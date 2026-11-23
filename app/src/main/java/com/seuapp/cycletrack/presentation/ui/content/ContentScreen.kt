package com.seuapp.cycletrack.presentation.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seuapp.cycletrack.domain.model.Article
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens

private data class CardPalette(val background: Color, val onBackground: Color)

/**
 * Feed de conteudo educativo organizado por secoes tematicas, cada uma um
 * carrossel horizontal de artigos (Fase 5).
 */
@Composable
fun ContentScreen(onArticleClick: (String) -> Unit = {}) {
    val colors = CycleTrackColors()
    val palettes = listOf(
        CardPalette(colors.primaryContainer, colors.onPrimaryContainer),
        CardPalette(colors.secondaryContainer, colors.onSecondaryContainer),
        CardPalette(colors.tertiaryContainer, colors.onTertiaryContainer),
        CardPalette(colors.categoryEmotionalContainer, colors.categoryEmotional)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(vertical = CycleTrackDimens.spacingLg),
        verticalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingLg)
    ) {
        item {
            Text(
                text = "Conteudo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = CycleTrackDimens.spacingMd)
            )
        }

        itemsIndexed(contentSections) { sectionIndex, section ->
            Column {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = CycleTrackDimens.spacingMd)
                )
                Spacer(Modifier.height(CycleTrackDimens.spacingSm))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = CycleTrackDimens.spacingMd),
                    horizontalArrangement = Arrangement.spacedBy(CycleTrackDimens.spacingSm)
                ) {
                    itemsIndexed(section.articles) { articleIndex, article ->
                        val palette = palettes[(sectionIndex + articleIndex) % palettes.size]
                        ArticleCard(article = article, palette = palette, onClick = { onArticleClick(article.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(article: Article, palette: CardPalette, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(170.dp)
            .aspectRatio(0.8f)
            .background(palette.background, RoundedCornerShape(CycleTrackDimens.radiusLg))
            .clickable(onClick = onClick)
            .padding(CycleTrackDimens.spacingSm),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = palette.onBackground,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
