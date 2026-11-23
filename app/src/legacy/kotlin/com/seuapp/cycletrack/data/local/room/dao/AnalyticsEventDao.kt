package com.seuapp.cycletrack.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.seuapp.cycletrack.data.local.room.entities.AnalyticsEvent
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for AnalyticsEvent.
 * Clean-room implementation for privacy-compliant analytics storage.
 */
@Dao
interface AnalyticsEventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: AnalyticsEvent): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(events: List<AnalyticsEvent>): List<Long>

    @Query("SELECT * FROM analytics_events WHERE id = :id")
    suspend fun getById(id: Long): AnalyticsEvent?

    @Query("SELECT * FROM analytics_events WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getEventsByUser(
        userId: String,
        limit: Int = 100,
        offset: Int = 0
    ): List<AnalyticsEvent>

    @Query("SELECT * FROM analytics_events WHERE userId = :userId AND eventName = :eventName ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getEventsByName(
        userId: String,
        eventName: String,
        limit: Int = 50
    ): List<AnalyticsEvent>

    @Query("SELECT * FROM analytics_events WHERE userId = :userId AND eventCategory = :category ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getEventsByCategory(
        userId: String,
        category: String,
        limit: Int = 50
    ): List<AnalyticsEvent>

    @Query("SELECT * FROM analytics_events WHERE userId = :userId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    suspend fun getEventsInRange(
        userId: String,
        startTime: Long,
        endTime: Long
    ): List<AnalyticsEvent>

    @Query("SELECT * FROM analytics_events WHERE userId = :userId AND sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getEventsBySession(userId: String, sessionId: String): List<AnalyticsEvent>

    @Query("SELECT * FROM analytics_events WHERE syncStatus = 'pending' ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getPendingEvents(limit: Int = 100): List<AnalyticsEvent>

    @Query("SELECT * FROM analytics_events WHERE syncStatus = 'failed' AND retryCount < :maxRetries ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getFailedEvents(maxRetries: Int = 3, limit: Int = 50): List<AnalyticsEvent>

    @Query("SELECT COUNT(*) FROM analytics_events WHERE userId = :userId AND timestamp >= :startTime")
    suspend fun countEventsSince(userId: String, startTime: Long): Int

    @Query("SELECT COUNT(*) FROM analytics_events WHERE syncStatus = 'pending'")
    suspend fun countPendingEvents(): Int

    @Query("UPDATE analytics_events SET syncStatus = 'sent', sentAt = :sentAt WHERE id IN (:ids)")
    suspend fun markAsSent(ids: List<Long>, sentAt: Long): Int

    @Query("UPDATE analytics_events SET syncStatus = 'failed', retryCount = retryCount + 1 WHERE id IN (:ids)")
    suspend fun markAsFailed(ids: List<Long>): Int

    @Query("DELETE FROM analytics_events WHERE timestamp < :beforeTime AND syncStatus = 'sent'")
    suspend fun deleteOldSentEvents(beforeTime: Long): Int

    @Query("DELETE FROM analytics_events WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String): Int

    @Transaction
    suspend fun insertBatch(events: List<AnalyticsEvent>) {
        insertAll(events)
    }

    // Flow queries for reactive UI
    @Query("SELECT * FROM analytics_events WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getEventsFlow(userId: String, limit: Int = 50): Flow<List<AnalyticsEvent>>

    @Query("SELECT * FROM analytics_events WHERE syncStatus = 'pending' ORDER BY timestamp ASC LIMIT :limit")
    fun getPendingEventsFlow(limit: Int = 100): Flow<List<AnalyticsEvent>>
}