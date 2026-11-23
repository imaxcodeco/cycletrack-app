package com.seuapp.cycletrack.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.seuapp.cycletrack.data.local.room.entities.UserSession
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for UserSession.
 * Clean-room implementation for session analytics storage.
 */
@Dao
interface UserSessionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(session: UserSession): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(sessions: List<UserSession>): List<Long>

    @Query("SELECT * FROM user_sessions WHERE id = :id")
    suspend fun getById(id: Long): UserSession?

    @Query("SELECT * FROM user_sessions WHERE sessionId = :sessionId")
    suspend fun getBySessionId(sessionId: String): UserSession?

    @Query("SELECT * FROM user_sessions WHERE userId = :userId ORDER BY startTime DESC LIMIT :limit OFFSET :offset")
    suspend fun getSessionsByUser(
        userId: String,
        limit: Int = 50,
        offset: Int = 0
    ): List<UserSession>

    @Query("SELECT * FROM user_sessions WHERE userId = :userId AND startTime >= :startTime AND startTime <= :endTime ORDER BY startTime DESC")
    suspend fun getSessionsInRange(
        userId: String,
        startTime: Long,
        endTime: Long
    ): List<UserSession>

    @Query("SELECT * FROM user_sessions WHERE syncStatus = 'pending' ORDER BY startTime ASC LIMIT :limit")
    suspend fun getPendingSessions(limit: Int = 50): List<UserSession>

    @Query("SELECT * FROM user_sessions WHERE syncStatus = 'failed' ORDER BY startTime ASC LIMIT :limit")
    suspend fun getFailedSessions(limit: Int = 50): List<UserSession>

    @Query("UPDATE user_sessions SET endTime = :endTime, durationMs = :durationMs, screenViews = :screenViews, interactions = :interactions, crashes = :crashes, anrs = :anrs, wasBackgrounded = :wasBackgrounded, backgroundCount = :backgroundCount, totalBackgroundTimeMs = :totalBackgroundTimeMs WHERE sessionId = :sessionId")
    suspend fun updateSession(
        sessionId: String,
        endTime: Long,
        durationMs: Long,
        screenViews: Int,
        interactions: Int,
        crashes: Int,
        anrs: Int,
        wasBackgrounded: Boolean,
        backgroundCount: Int,
        totalBackgroundTimeMs: Long
    ): Int

    @Query("UPDATE user_sessions SET syncStatus = 'sent', sentAt = :sentAt WHERE id IN (:ids)")
    suspend fun markAsSent(ids: List<Long>, sentAt: Long): Int

    @Query("UPDATE user_sessions SET syncStatus = 'failed' WHERE id IN (:ids)")
    suspend fun markAsFailed(ids: List<Long>): Int

    @Query("DELETE FROM user_sessions WHERE startTime < :beforeTime AND syncStatus = 'sent'")
    suspend fun deleteOldSentSessions(beforeTime: Long): Int

    @Query("DELETE FROM user_sessions WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String): Int

    @Transaction
    suspend fun insertBatch(sessions: List<UserSession>) {
        insertAll(sessions)
    }

    // Flow queries for reactive UI
    @Query("SELECT * FROM user_sessions WHERE userId = :userId ORDER BY startTime DESC LIMIT :limit")
    fun getSessionsFlow(userId: String, limit: Int = 20): Flow<List<UserSession>>

    @Query("SELECT * FROM user_sessions WHERE syncStatus = 'pending' ORDER BY startTime ASC LIMIT :limit")
    fun getPendingSessionsFlow(limit: Int = 50): Flow<List<UserSession>>

    // Statistics
    @Query("SELECT COUNT(*), AVG(durationMs), SUM(screenViews), SUM(interactions) FROM user_sessions WHERE userId = :userId AND startTime >= :since")
    suspend fun getSessionStats(userId: String, since: Long): SessionStats?

    @Query("SELECT COUNT(DISTINCT userId) FROM user_sessions WHERE startTime >= :since")
    suspend fun getActiveUsersCount(since: Long): Int

    @Query("SELECT AVG(durationMs) FROM user_sessions WHERE startTime >= :since AND durationMs > 0")
    suspend fun getAverageSessionDuration(since: Long): Double
}

/**
 * Data class for session statistics.
 */
data class SessionStats(
    val sessionCount: Long,
    val averageDurationMs: Double?,
    val totalScreenViews: Long,
    val totalInteractions: Long
)