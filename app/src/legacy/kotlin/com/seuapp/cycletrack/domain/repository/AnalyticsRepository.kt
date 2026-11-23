package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.AnalyticsEvent
import com.seuapp.cycletrack.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de Analytics (Room).
 * Clean-room - define contrato para analytics com privacidade.
 */
interface AnalyticsRepository {

    /**
     * Registra evento de analytics.
     */
    suspend fun logEvent(event: AnalyticsEvent)

    /**
     * Registra múltiplos eventos em lote.
     */
    suspend fun logEvents(events: List<AnalyticsEvent>)

    /**
     * Obtém eventos pendentes de envio.
     */
    suspend fun getPendingEvents(limit: Int = 100): List<AnalyticsEvent>

    /**
     * Obtém eventos falhados para retry.
     */
    suspend fun getFailedEvents(maxRetries: Int = 3, limit: Int = 50): List<AnalyticsEvent>

    /**
     * Marca eventos como enviados.
     */
    suspend fun markEventsAsSent(eventIds: List<Long>)

    /**
     * Marca eventos como falhados.
     */
    suspend fun markEventsAsFailed(eventIds: List<Long>)

    /**
     * Deleta eventos antigos já enviados.
     */
    suspend fun deleteOldSentEvents(beforeTime: Long): Int

    /**
     * Deleta todos os eventos de um usuário (LGPD/GDPR).
     */
    suspend fun deleteAllEventsForUser(userId: String): Int

    /**
     * Conta eventos pendentes.
     */
    suspend fun countPendingEvents(): Int

    // Session tracking

    /**
     * Inicia nova sessão.
     */
    suspend fun startSession(session: UserSession)

    /**
     * Finaliza sessão.
     */
    suspend fun endSession(
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
    )

    /**
     * Obtém sessões pendentes de envio.
     */
    suspend fun getPendingSessions(limit: Int = 50): List<UserSession>

    /**
     * Obtém sessões falhadas.
     */
    suspend fun getFailedSessions(limit: Int = 50): List<UserSession>

    /**
     * Marca sessões como enviadas.
     */
    suspend fun markSessionsAsSent(sessionIds: List<Long>)

    /**
     * Marca sessões como falhadas.
     */
    suspend fun markSessionsAsFailed(sessionIds: List<Long>)

    /**
     * Deleta sessões antigas já enviadas.
     */
    suspend fun deleteOldSentSessions(beforeTime: Long): Int

    /**
     * Deleta todas sessões de um usuário (LGPD/GDPR).
     */
    suspend fun deleteAllSessionsForUser(userId: String): Int

    // Statistics

    /**
     * Obtém estatísticas de sessão do usuário.
     */
    suspend fun getSessionStats(userId: String, since: Long): SessionStats?

    /**
     * Obtém contagem de usuários ativos.
     */
    suspend fun getActiveUsersCount(since: Long): Int

    /**
     * Obtém duração média de sessão.
     */
    suspend fun getAverageSessionDuration(since: Long): Double

    // Flow para UI reativa

    /**
     * Flow de eventos do usuário.
     */
    fun getEventsFlow(userId: String, limit: Int = 50): Flow<List<AnalyticsEvent>>

    /**
     * Flow de eventos pendentes.
     */
    fun getPendingEventsFlow(limit: Int = 100): Flow<List<AnalyticsEvent>>

    /**
     * Flow de sessões do usuário.
     */
    fun getSessionsFlow(userId: String, limit: Int = 20): Flow<List<UserSession>>

    /**
     * Flow de sessões pendentes.
     */
    fun getPendingSessionsFlow(limit: Int = 50): Flow<List<UserSession>>
}

/**
 * Estatísticas de sessão.
 */
data class SessionStats(
    val sessionCount: Long,
    val averageDurationMs: Double?,
    val totalScreenViews: Long,
    val totalInteractions: Long
)