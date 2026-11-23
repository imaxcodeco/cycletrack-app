package com.seuapp.cycletrack.presentation.ui.content

import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Chip
import androidx.compose.material3.IconButton
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
import com.seuapp.cycletrack.domain.model.Content
import com.seuapp.cycletrack.presentation.state.ContentViewModel
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackColors
import com.seuapp.cycletrack.presentation.ui.theme.CycleTrackDimens
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormatter
import java.util.Locale

/**
 * Tela de Conteúdo - Artigos, vídeos, dicas com ViewModel.
 * Clean-room - implementação própria completa.
 */

@Composable
fun ContentScreen(
    onNavigateToArticle: (Content) -> Unit,
    onNavigateToVideo: (Content) -> Unit
) {
    val viewModel: ContentViewModel = hiltViewModel()
    val featuredContent by viewModel.featuredContent.collectAsStateWithLifecycle()
    val currentContentList by viewModel.currentContentList.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val tabs = viewModel.tabs
    val bookmarkedContent by viewModel.bookmarkedContent.collectAsStateWithLifecycle()
    val cyclePhaseContent by viewModel.cyclePhaseContent.collectAsStateWithLifecycle()
    val pregnancyWeekContent by viewModel.pregnancyWeekContent.collectAsStateWithLifecycle()
    val selectedContent by viewModel.selectedContent.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    
    val colors = CycleTrackColors()
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.searchContent(it) },
            onClear = { viewModel.clearSearch() },
            isSearching = searchQuery.isNotBlank()
        )
        
        // Tab bar
        TabBar(
            tabs = tabs,
            currentTab = currentTab,
            onTabChange = { viewModel.setCurrentTab(it) }
        )
        
        // Featured content (only on "for_you" tab)
        if (currentTab == "for_you" && featuredContent.isNotEmpty()) {
            FeaturedContentCarousel(content = featuredContent.first())
        }
        
        // Cycle phase content
        if (currentTab == "for_you" && cyclePhaseContent.isNotEmpty()) {
            CyclePhaseContentSection(content = cyclePhaseContent)
        }
        
        // Pregnancy week content
        if (currentTab == "for_you" && pregnancyWeekContent.isNotEmpty()) {
            PregnancyContentSection(content = pregnancyWeekContent)
        }
        
        // Main content list
        if (searchQuery.isNotBlank()) {
            SearchResultsList(
                results = searchResults,
                onItemClick = { viewModel.selectContent(it) }
            )
        } else {
            ContentList(
                content = currentContentList,
                onItemClick = { viewModel.selectContent(it) }
            )
        }
        
        // Load more
        if (isLoadingMore) {
            LoadingIndicator()
        }
        
        // Selected content detail
        selectedContent?.let { content ->
            ContentDetailBottomSheet(
                content = content,
                onClose = { viewModel.clearSelection() },
                onBookmark = { viewModel.toggleBookmark(content.id) },
                onShare = { viewModel.recordContentShare(content.id) }
            )
        }
        
        error?.let { msg ->
            Text(msg, fontSize = 12.sp, color = colors.error)
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    isSearching: Boolean
) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp
    ) {
        androidx.compose.material3.TextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Buscar artigos, vídeos...") },
            leadingIcon = { 
                if (isSearching) {
                    androidx.compose.material3.IconButton(onClick = onClear) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Close,
                            contentDescription = "Limpar"
                        )
                    }
                } else {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.Search,
                        contentDescription = "Buscar"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
    }
}

@Composable
fun TabBar(tabs: List<ContentViewModel.TabInfo>, currentTab: String, onTabChange: (String) -> Unit) {
    val colors = CycleTrackColors()
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
            .background(colors.surfaceVariant, RoundedCornerShape(12.dp)),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = currentTab == tab.id
            Chip(
                onClick = { onTabChange(tab.id) },
                modifier = Modifier.fillMaxWidth(1f / tabs.size.toFloat())
                    .padding(4.dp),
                label = { 
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(tab.icon, fontSize = 14.sp)
                        Text(tab.title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                },
                colors = ChipDefaults.colors(
                    containerColor = if (isSelected) colors.primary else colors.surfaceVariant,
                    contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun FeaturedContentCarousel(content: Content) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Image placeholder
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(180.dp)
                    .background(colors.primaryContainer, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(content.type.icon, fontSize = 48.sp)
            }
            
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Chip(
                    onClick = { },
                    modifier = Modifier.padding(0.dp),
                    label = { Text(content.category, fontSize = 10.sp) },
                    colors = ChipDefaults.colors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                )
                
                Text(content.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                Text(content.excerpt ?: content.description, fontSize = 14.sp, color = colors.onSurfaceVariant, maxLines = 2)
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Chip(
                        onClick = { },
                        modifier = Modifier.padding(0.dp),
                        label = { Text("${content.readTimeMinutes} min", fontSize = 10.sp) }
                    )
                    if (content.isPremium) {
                        Chip(
                            onClick = { },
                            modifier = Modifier.padding(0.dp),
                            label = { Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) { Text("✨"); Text("Premium", fontSize = 10.sp) } },
                            colors = ChipDefaults.colors(
                                containerColor = colors.warningContainer,
                                contentColor = colors.onWarningContainer
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CyclePhaseContentSection(content: List<Content>) {
    val colors = CycleTrackColors()
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Para sua fase do ciclo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0)
        ) {
            items(content.take(4)) { item ->
                ContentCard(item = item, onClick = { /* navigate */ })
            }
        }
    }
}

@Composable
fun PregnancyContentSection(content: List<Content>) {
    val colors = CycleTrackColors()
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Para sua semana de gravidez", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0)
        ) {
            items(content.take(4)) { item ->
                ContentCard(item = item, onClick = { /* navigate */ })
            }
        }
    }
}

@Composable
fun ContentList(content: List<Content>, onItemClick: (Content) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(content) { item ->
            ContentCard(item = item, onClick = { onItemClick(item) })
        }
    }
}

@Composable
fun SearchResultsList(results: List<Content>, onItemClick: (Content) -> Unit) {
    val colors = CycleTrackColors()
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(results) { item ->
            ContentCard(item = item, onClick = { onItemClick(item) })
        }
        if (results.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("🔍", fontSize = 48.sp)
                    Text("Nenhum resultado encontrado", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Tente buscar com outros termos", fontSize = 14.sp, color = colors.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ContentCard(item: Content, onClick: () -> Unit) {
    val colors = CycleTrackColors()
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Type icon
            Box(
                modifier = Modifier.size(48.dp)
                    .background(item.type.color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.type.icon, fontSize = 24.sp)
            }
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.category, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = item.type.color)
                    if (item.isPremium) {
                        Text("✨ Premium", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = colors.warning)
                    }
                }
                Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.onSurface, maxLines = 2)
                Text(item.excerpt ?: item.description, fontSize = 12.sp, color = colors.onSurfaceVariant, maxLines = 2)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.readTimeMinutes > 0 ? "${item.readTimeMinutes} min" : "${item.watchTimeMinutes} min", fontSize = 10.sp, color = colors.onSurfaceVariant)
                    Text("${item.viewCount} visualizações", fontSize = 10.sp, color = colors.onSurfaceVariant)
                }
            }
            
            if (item.isBookmarked) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Bookmark,
                    contentDescription = "Salvo",
                    tint = colors.primary
                )
            }
        }
    }
}

@Composable
fun ContentDetailBottomSheet(
    content: Content,
    onClose: () -> Unit,
    onBookmark: () -> Unit,
    onShare: () -> Unit
) {
    val colors = CycleTrackColors()
    
    androidx.compose.material3.ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
                    .background(colors.surface, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Handle bar
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(4.dp)
                        .background(colors.outlineVariant, RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                )
                
                // Content
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Chip(
                            onClick = { },
                            modifier = Modifier.padding(0.dp),
                            label = { Text(content.category, fontSize = 10.sp) },
                            colors = ChipDefaults.colors(
                                containerColor = content.type.color,
                                contentColor = Color.White
                            )
                        )
                        Text(content.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                        Text(content.description, fontSize = 14.sp, color = colors.onSurfaceVariant)
                        
                        if (content.type == com.seuapp.cycletrack.domain.model.ContentType.ARTICLE) {
                            Text("${content.readTimeMinutes} min de leitura", fontSize = 12.sp, color = colors.onSurfaceVariant)
                        } else {
                            Text("${content.watchTimeMinutes} min de vídeo", fontSize = 12.sp, color = colors.onSurfaceVariant)
                        }
                        
                        if (content.isPremium) {
                            Chip(
                                onClick = { },
                                modifier = Modifier.padding(0.dp),
                                label = { Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) { Text("✨"); Text("Conteúdo Premium", fontSize = 10.sp) } },
                                colors = ChipDefaults.colors(
                                    containerColor = colors.warningContainer,
                                    contentColor = colors.onWarningContainer
                                )
                            )
                        }
                    }
                    
                    IconButton(onClick = onClose) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Close,
                            contentDescription = "Fechar"
                        )
                    }
                }
                
                // Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onBookmark, modifier = Modifier.fillMaxWidth(0.33f), shape = RoundedCornerShape(12.dp)) {
                        Text(content.isBookmarked ? "🔖 Salvo" : "🔖 Salvar", fontSize = 12.sp)
                    }
                    Button(onClick = onShare, modifier = Modifier.fillMaxWidth(0.33f), shape = RoundedCornerShape(12.dp)) {
                        Text("📤 Compartilhar", fontSize = 12.sp)
                    }
                    Button(onClick = { /* open full */ }, modifier = Modifier.fillMaxWidth(0.33f), shape = RoundedCornerShape(12.dp)) {
                        Text("📖 Ler/Assistir", fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    )
}

@Composable
fun LoadingIndicator() {
    val colors = CycleTrackColors()
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(color = colors.primary)
    }
}

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Chip
import androidx.compose.material3.ChipDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.TextField
import androidx.compose.material3.Icon
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seuapp.cycletrack.presentation.state.TabInfo
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer