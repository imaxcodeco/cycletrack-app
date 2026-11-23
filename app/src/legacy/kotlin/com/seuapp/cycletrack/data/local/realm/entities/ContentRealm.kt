package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity for educational content (articles, videos, tips).
 * Clean-room implementation with offline-first caching.
 */
@Serializable
open class ContentRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    var category: String = "" // article, video, tip, faq, guide, cycle_science, pregnancy, perimenopause

    @Index
    var tags: String = "" // Comma-separated tags for filtering

    @Required
    var title: String = ""

    var subtitle: String = ""

    var body: String = "" // HTML/Markdown content

    var summary: String = ""

    var author: String = ""

    var sourceUrl: String = ""

    var imageUrl: String = ""

    var videoUrl: String = ""

    var durationMinutes: Int = 0

    // Targeting
    var targetCyclePhase: String = "" // menstrual, follicular, ovulation, luteal, any
    var targetPregnancyWeekMin: Int = 0
    var targetPregnancyWeekMax: Int = 0
    var targetConditions: String = "" // pcos, endometriosis, etc.

    // Localization
    var language: String = "pt_BR"
    var region: String = "BR"

    // Access control
    var isPremium: Boolean = false
    var minAppVersion: String = ""
    var maxAppVersion: String = ""

    // Engagement
    var viewCount: Long = 0
    var likeCount: Long = 0
    var bookmarkCount: Long = 0
    var shareCount: Long = 0

    // User state
    var isBookmarked: Boolean = false
    var isRead: Boolean = false
    var readProgress: Float = 0.0f
    var lastReadAt: Long = 0L

    // Publishing
    var publishedAt: Long = 0L
    var expiresAt: Long = 0L
    var version: Int = 1

    // Metadata
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var deletedAt: Long = 0L
    var serverId: String = ""
    var lastSyncedAt: Long = 0L

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