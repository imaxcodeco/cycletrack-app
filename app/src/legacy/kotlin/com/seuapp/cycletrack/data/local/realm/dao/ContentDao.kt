package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.ContentRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.seuapp.cycletrack.data.local.realm.detached

/**
 * DAO for ContentRealm operations.
 * Clean-room implementation for educational content caching.
 */
class ContentDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get all content for a language/region.
     */
    suspend fun getContent(
        language: String = "pt_BR",
        region: String = "BR",
        includeExpired: Boolean = false
    ): List<ContentRealm> = withContext(Dispatchers.IO) {
        val query = realm.where(ContentRealm::class.java)
            .equalTo("language", language)
            .equalTo("region", region)

        if (!includeExpired) {
            val now = System.currentTimeMillis()
            query.and()
                .beginGroup()
                    .equalTo("expiresAt", 0L)
                    .or()
                    .greaterThan("expiresAt", now)
                .endGroup()
        }

        return query
            .sort("publishedAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get content by category.
     */
    suspend fun getContentByCategory(
        category: String,
        language: String = "pt_BR",
        region: String = "BR"
    ): List<ContentRealm> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        realm.where(ContentRealm::class.java)
            .equalTo("category", category)
            .equalTo("language", language)
            .equalTo("region", region)
            .beginGroup()
                .equalTo("expiresAt", 0L)
                .or()
                .greaterThan("expiresAt", now)
            .endGroup()
            .sort("publishedAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get content by ID.
     */
    suspend fun getContentById(contentId: String): ContentRealm? = withContext(Dispatchers.IO) {
        realm.where(ContentRealm::class.java)
            .equalTo("id", contentId)
            .findFirst()
            .detached()
    }

    /**
     * Get content for current cycle phase.
     */
    suspend fun getContentForCyclePhase(
        phase: String,
        language: String = "pt_BR",
        region: String = "BR"
    ): List<ContentRealm> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        realm.where(ContentRealm::class.java)
            .beginGroup()
                .equalTo("targetCyclePhase", phase)
                .or()
                .equalTo("targetCyclePhase", "any")
            .endGroup()
            .equalTo("language", language)
            .equalTo("region", region)
            .beginGroup()
                .equalTo("expiresAt", 0L)
                .or()
                .greaterThan("expiresAt", now)
            .endGroup()
            .sort("publishedAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get content for pregnancy week.
     */
    suspend fun getContentForPregnancyWeek(
        week: Int,
        language: String = "pt_BR",
        region: String = "BR"
    ): List<ContentRealm> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        realm.where(ContentRealm::class.java)
            .lessThanOrEqualTo("targetPregnancyWeekMin", week)
            .greaterThanOrEqualTo("targetPregnancyWeekMax", week)
            .equalTo("language", language)
            .equalTo("region", region)
            .beginGroup()
                .equalTo("expiresAt", 0L)
                .or()
                .greaterThan("expiresAt", now)
            .endGroup()
            .sort("publishedAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get premium content.
     */
    suspend fun getPremiumContent(
        language: String = "pt_BR",
        region: String = "BR"
    ): List<ContentRealm> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        realm.where(ContentRealm::class.java)
            .equalTo("isPremium", true)
            .equalTo("language", language)
            .equalTo("region", region)
            .beginGroup()
                .equalTo("expiresAt", 0L)
                .or()
                .greaterThan("expiresAt", now)
            .endGroup()
            .sort("publishedAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get bookmarked content.
     */
    suspend fun getBookmarkedContent(
        language: String = "pt_BR",
        region: String = "BR"
    ): List<ContentRealm> = withContext(Dispatchers.IO) {
        realm.where(ContentRealm::class.java)
            .equalTo("isBookmarked", true)
            .equalTo("language", language)
            .equalTo("region", region)
            .sort("lastReadAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get unread content.
     */
    suspend fun getUnreadContent(
        language: String = "pt_BR",
        region: String = "BR"
    ): List<ContentRealm> = withContext(Dispatchers.IO) {
        realm.where(ContentRealm::class.java)
            .equalTo("isRead", false)
            .equalTo("language", language)
            .equalTo("region", region)
            .sort("publishedAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Insert or update content.
     */
    suspend fun upsertContent(content: ContentRealm): ContentRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            content.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(content)
        }
    }

    /**
     * Insert multiple content items.
     */
    suspend fun upsertContentList(contents: List<ContentRealm>): List<ContentRealm> = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            contents.forEach { it.updatedAt = System.currentTimeMillis() }
            realm.copyToRealmOrUpdate(contents)
        }
    }

    /**
     * Toggle bookmark.
     */
    suspend fun toggleBookmark(contentId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val content = realm.where(ContentRealm::class.java)
                .equalTo("id", contentId)
                .findFirst()
            content?.let {
                it.isBookmarked = !it.isBookmarked
                if (it.isBookmarked) {
                    it.bookmarkCount++
                } else {
                    it.bookmarkCount = maxOf(0, it.bookmarkCount - 1)
                }
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Mark content as read.
     */
    suspend fun markAsRead(contentId: String, progress: Float = 1.0f): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val content = realm.where(ContentRealm::class.java)
                .equalTo("id", contentId)
                .findFirst()
            content?.let {
                it.isRead = true
                it.readProgress = progress.coerceIn(0.0f, 1.0f)
                it.lastReadAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Increment view count.
     */
    suspend fun incrementViewCount(contentId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val content = realm.where(ContentRealm::class.java)
                .equalTo("id", contentId)
                .findFirst()
            content?.let {
                it.viewCount++
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Increment like count.
     */
    suspend fun incrementLikeCount(contentId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val content = realm.where(ContentRealm::class.java)
                .equalTo("id", contentId)
                .findFirst()
            content?.let {
                it.likeCount++
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Increment share count.
     */
    suspend fun incrementShareCount(contentId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val content = realm.where(ContentRealm::class.java)
                .equalTo("id", contentId)
                .findFirst()
            content?.let {
                it.shareCount++
                it.updatedAt = System.currentTimeMillis()
                it.version++
                true
            } ?: false
        }
    }

    /**
     * Delete expired content.
     */
    suspend fun deleteExpiredContent(): Int = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        writeTransaction { realm ->
            val expired = realm.where(ContentRealm::class.java)
                .greaterThan("expiresAt", 0L)
                .lessThan("expiresAt", now)
                .findAll()
            val count = expired.size
            expired.deleteAllFromRealm()
            count
        }
    }

    /**
     * Search content by title/summary.
     */
    suspend fun searchContent(
        query: String,
        language: String = "pt_BR",
        region: String = "BR",
        limit: Int = 20
    ): List<ContentRealm> = withContext(Dispatchers.IO) {
        val lowerQuery = query.lowercase()
        val now = System.currentTimeMillis()
        val results = realm.where(ContentRealm::class.java)
            .equalTo("language", language)
            .equalTo("region", region)
            .beginGroup()
                .equalTo("expiresAt", 0L)
                .or()
                .greaterThan("expiresAt", now)
            .endGroup()
            .findAll()
            .detached()

        return results
            .filter { it.title.lowercase().contains(lowerQuery) || it.summary.lowercase().contains(lowerQuery) }
            .sortedByDescending { it.publishedAt }
            .take(limit)
            .toList()
    }

    /**
     * Executa uma transacao de escrita devolvendo o valor produzido pelo bloco.
     * A API Java do Realm nao retorna valor de `executeTransaction`.
     */
    private suspend fun <T> writeTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
        var result: T? = null
        realm.executeTransaction { transactionRealm ->
            result = block(transactionRealm)
        }
        @Suppress("UNCHECKED_CAST")
        result as T
    }

}