package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Content

/**
 * Interface do repositório de Conteúdo Educacional.
 * Clean-room - define contrato sem expor implementação.
 */
interface ContentRepository {

    /**
     * Obtém todo conteúdo para idioma/região.
     */
    suspend fun getContent(
        language: String = "pt_BR",
        region: String = "BR",
        includeExpired: Boolean = false
    ): List<Content>

    /**
     * Obtém conteúdo por categoria.
     */
    suspend fun getContentByCategory(
        category: String,
        language: String = "pt_BR",
        region: String = "BR"
    ): List<Content>

    /**
     * Obtém conteúdo por ID.
     */
    suspend fun getContentById(contentId: String): Content?

    /**
     * Obtém conteúdo para fase do ciclo atual.
     */
    suspend fun getContentForCyclePhase(
        phase: String,
        language: String = "pt_BR",
        region: String = "BR"
    ): List<Content>

    /**
     * Obtém conteúdo para semana de gravidez.
     */
    suspend fun getContentForPregnancyWeek(
        week: Int,
        language: String = "pt_BR",
        region: String = "BR"
    ): List<Content>

    /**
     * Obtém conteúdo premium.
     */
    suspend fun getPremiumContent(
        language: String = "pt_BR",
        region: String = "BR"
    ): List<Content>

    /**
     * Obtém conteúdo favoritado.
     */
    suspend fun getBookmarkedContent(
        language: String = "pt_BR",
        region: String = "BR"
    ): List<Content>

    /**
     * Obtém conteúdo não lido.
     */
    suspend fun getUnreadContent(
        language: String = "pt_BR",
        region: String = "BR"
    ): List<Content>

    /**
     * Insere ou atualiza conteúdo.
     */
    suspend fun upsertContent(content: Content): Content

    /**
     * Insere múltiplos conteúdos.
     */
    suspend fun upsertContentList(contents: List<Content>): List<Content>

    /**
     * Alterna favorito.
     */
    suspend fun toggleBookmark(contentId: String): Boolean

    /**
     * Marca como lido.
     */
    suspend fun markAsRead(contentId: String, progress: Float = 1.0f): Boolean

    /**
     * Incrementa visualizações.
     */
    suspend fun incrementViewCount(contentId: String): Boolean

    /**
     * Incrementa curtidas.
     */
    suspend fun incrementLikeCount(contentId: String): Boolean

    /**
     * Incrementa compartilhamentos.
     */
    suspend fun incrementShareCount(contentId: String): Boolean

    /**
     * Deleta conteúdo expirado.
     */
    suspend fun deleteExpiredContent(): Int

    /**
     * Busca conteúdo por título/resumo.
     */
    suspend fun searchContent(
        query: String,
        language: String = "pt_BR",
        region: String = "BR",
        limit: Int = 20
    ): List<Content>
}

/**
 * Modelo de domínio para Conteúdo.
 */
@Serializable
data class Content(
    val id: String,
    val category: String,
    val tags: String,
    val title: String,
    val subtitle: String = "",
    val body: String,
    val summary: String = "",
    val author: String = "",
    val sourceUrl: String = "",
    val imageUrl: String = "",
    val videoUrl: String = "",
    val durationMinutes: Int = 0,
    val targetCyclePhase: String = "",
    val targetPregnancyWeekMin: Int = 0,
    val targetPregnancyWeekMax: Int = 0,
    val targetConditions: String = "",
    val language: String = "pt_BR",
    val region: String = "BR",
    val isPremium: Boolean = false,
    val minAppVersion: String = "",
    val maxAppVersion: String = "",
    val viewCount: Long = 0,
    val likeCount: Long = 0,
    val bookmarkCount: Long = 0,
    val shareCount: Long = 0,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val readProgress: Float = 0.0f,
    val lastReadAt: Long = 0L,
    val publishedAt: Long = 0L,
    val expiresAt: Long = 0L,
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: String = "",
    val lastSyncedAt: Long = 0L
) {
    companion object {
        const val CATEGORY_ARTICLE = "article"
        const val CATEGORY_VIDEO = "video"
        const val CATEGORY_TIP = "tip"
        const val CATEGORY_FAQ = "faq"
        const val CATEGORY_GUIDE = "guide"
        const val CATEGORY_CYCLE_SCIENCE = "cycle_science"
        const val CATEGORY_PREGNANCY = "pregnancy"
        const val CATEGORY_PERIMENOPAUSE = "perimenopause"
        const val CATEGORY_NUTRITION = "nutrition"
        const val CATEGORY_EXERCISE = "exercise"
        const val CATEGORY_MENTAL_HEALTH = "mental_health"
        const val CATEGORY_SEXUAL_HEALTH = "sexual_health"
    }
}