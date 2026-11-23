package com.seuapp.cycletrack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.Content
import com.seuapp.cycletrack.domain.usecase.content.ContentUseCases
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.async

/**
 * ViewModel para a tela de Conteúdo.
 * Gerencia artigos, vídeos, dicas, favoritos e conteúdo personalizado por fase do ciclo.
 */
@HiltViewModel
class ContentViewModel @Inject constructor(
    private val contentUseCases: ContentUseCases,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // ============ State ============

    private val _featuredContent = MutableStateFlow<List<Content>>(emptyList())
    val featuredContent = _featuredContent.asStateFlow()

    private val _forYouContent = MutableStateFlow<List<Content>>(emptyList())
    val forYouContent = _forYouContent.asStateFlow()

    private val _articles = MutableStateFlow<List<Content>>(emptyList())
    val articles = _articles.asStateFlow()

    private val _videos = MutableStateFlow<List<Content>>(emptyList())
    val videos = _videos.asStateFlow()

    private val _tips = MutableStateFlow<List<Content>>(emptyList())
    val tips = _tips.asStateFlow()

    private val _premiumContent = MutableStateFlow<List<Content>>(emptyList())
    val premiumContent = _premiumContent.asStateFlow()

    private val _bookmarkedContent = MutableStateFlow<List<Content>>(emptyList())
    val bookmarkedContent = _bookmarkedContent.asStateFlow()

    private val _unreadContent = MutableStateFlow<List<Content>>(emptyList())
    val unreadContent = _unreadContent.asStateFlow()

    private val _cyclePhaseContent = MutableStateFlow<List<Content>>(emptyList())
    val cyclePhaseContent = _cyclePhaseContent.asStateFlow()

    private val _pregnancyWeekContent = MutableStateFlow<List<Content>>(emptyList())
    val pregnancyWeekContent = _pregnancyWeekContent.asStateFlow()

    private val _selectedContent = MutableStateFlow<Content?>(null)
    val selectedContent = _selectedContent.asStateFlow()

    private val _currentTab = MutableStateFlow("for_you") // for_you, articles, videos, tips, premium, bookmarks
    val currentTab = _currentTab.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore = _hasMore.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Content>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    // ============ Computed ============

    val currentContentList = combine(_currentTab, _forYouContent, _articles, _videos, _tips, _premiumContent, _bookmarkedContent) { tab, forYou, articles, videos, tips, premium, bookmarks ->
        when (tab) {
            "for_you" -> forYou
            "articles" -> articles
            "videos" -> videos
            "tips" -> tips
            "premium" -> premium
            "bookmarks" -> bookmarks
            else -> forYou
        }
    }.distinctUntilChanged()

    val tabs = listOf(
        TabInfo("for_you", "Para você", "🏠"),
        TabInfo("articles", "Artigos", "📖"),
        TabInfo("videos", "Vídeos", "🎥"),
        TabInfo("tips", "Dicas", "💡"),
        TabInfo("premium", "Premium", "✨"),
        TabInfo("bookmarks", "Salvos", "🔖")
    )

    // ============ Actions ============

    init {
        loadContent()
    }

    fun loadContent() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val userId = preferencesManager.getUserId()
                val language = preferencesManager.language.first()
                val region = "BR"
                val isPremium = userId?.let { preferencesManager.getUserId()?.let { 
                    // TODO: Verificar subscription status
                    false 
                } } ?: false
                
                val featuredDeferred = viewModelScope.async { contentUseCases.getContentForCyclePhase("follicular", language, region) }
                val forYouDeferred = viewModelScope.async { contentUseCases.getContent(language, region, false).take(10).toList() }
                val articlesDeferred = viewModelScope.async { contentUseCases.getContentByCategory("article", language, region) }
                val videosDeferred = viewModelScope.async { contentUseCases.getContentByCategory("video", language, region) }
                val tipsDeferred = viewModelScope.async { contentUseCases.getContentByCategory("tip", language, region) }
                val premiumDeferred = viewModelScope.async { if (isPremium) contentUseCases.getPremiumContent(language, region) else emptyList() }
                val bookmarksDeferred = viewModelScope.async { contentUseCases.getBookmarkedContent(language, region) }
                val unreadDeferred = viewModelScope.async { contentUseCases.getUnreadContent(language, region) }
                
                _featuredContent.value = featuredDeferred.await()
                _forYouContent.value = forYouDeferred.await()
                _articles.value = articlesDeferred.await()
                _videos.value = videosDeferred.await()
                _tips.value = tipsDeferred.await()
                _premiumContent.value = premiumDeferred.await()
                _bookmarkedContent.value = bookmarksDeferred.await()
                _unreadContent.value = unreadDeferred.await()
                
                // Carrega conteúdo para fase atual do ciclo
                loadCyclePhaseContent()
                
                // Carrega conteúdo para semana de gravidez se aplicável
                loadPregnancyWeekContent()
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar conteúdo"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadCyclePhaseContent() {
        viewModelScope.launch {
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                // TODO: Determinar fase atual do ciclo
                val phase = "follicular" // Placeholder
                val language = preferencesManager.language.first()
                val region = "BR"
                
                val content = contentUseCases.getContentForCyclePhase(phase, language, region)
                _cyclePhaseContent.value = content
            } catch (e: Exception) {
                // Silencioso
            }
        }
    }

    private fun loadPregnancyWeekContent() {
        viewModelScope.launch {
            try {
                val userId = preferencesManager.getUserId() ?: return@launch
                // TODO: Verificar se há gravidez ativa e obter semana
                // val week = pregnancyUseCases.getActivePregnancy(userId)?.currentWeek
                // if (week != null) { ... }
            } catch (e: Exception) {
                // Silencioso
            }
        }
    }

    fun setCurrentTab(tabId: String) {
        _currentTab.value = tabId
    }

    fun selectContent(content: Content) {
        _selectedContent.value = content
        // Marca como lido
        viewModelScope.launch {
            contentUseCases.markAsRead(content.id, 0f)
            // Atualiza lista local
            updateContentInLists(content.id) { it.copy(isRead = true) }
        }
    }

    fun clearSelection() {
        _selectedContent.value = null
    }

    fun toggleBookmark(contentId: String) {
        viewModelScope.launch {
            contentUseCases.toggleBookmark(contentId)
            // Atualiza listas locais
            updateContentInLists(contentId) { 
                it.copy(isBookmarked = !it.isBookmarked) 
            }
        }
    }

    fun recordContentView(contentId: String) {
        viewModelScope.launch {
            contentUseCases.incrementViewCount(contentId)
        }
    }

    fun recordContentLike(contentId: String) {
        viewModelScope.launch {
            contentUseCases.incrementLikeCount(contentId)
        }
    }

    fun recordContentShare(contentId: String) {
        viewModelScope.launch {
            contentUseCases.incrementShareCount(contentId)
        }
    }

    private fun updateContentInLists(contentId: String, transform: (Content) -> Content) {
        _forYouContent.value = _forYouContent.value.map { if (it.id == contentId) transform(it) else it }
        _articles.value = _articles.value.map { if (it.id == contentId) transform(it) else it }
        _videos.value = _videos.value.map { if (it.id == contentId) transform(it) else it }
        _tips.value = _tips.value.map { if (it.id == contentId) transform(it) else it }
        _premiumContent.value = _premiumContent.value.map { if (it.id == contentId) transform(it) else it }
        _bookmarkedContent.value = _bookmarkedContent.value.map { if (it.id == contentId) transform(it) else it }
        _cyclePhaseContent.value = _cyclePhaseContent.value.map { if (it.id == contentId) transform(it) else it }
        _pregnancyWeekContent.value = _pregnancyWeekContent.value.map { if (it.id == contentId) transform(it) else it }
    }

    fun searchContent(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            try {
                val language = preferencesManager.language.first()
                val region = "BR"
                val results = contentUseCases.searchContent(query, language, region, 20)
                _searchResults.value = results
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro na busca"
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun loadMore() {
        if (_isLoadingMore.value || !_hasMore.value) return
        
        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                // TODO: Implementar paginação
                _hasMore.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar mais"
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    fun refreshContent() {
        loadContent()
    }

    fun clearError() {
        _error.value = null
    }
}

data class TabInfo(
    val id: String,
    val title: String,
    val icon: String
)