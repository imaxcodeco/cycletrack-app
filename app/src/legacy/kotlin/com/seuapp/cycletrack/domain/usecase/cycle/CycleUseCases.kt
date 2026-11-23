package com.seuapp.cycletrack.domain.usecase.cycle

import com.seuapp.cycletrack.domain.algorithm.CyclePredictionEngine
import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.repository.PredictionRepository
import com.seuapp.cycletrack.domain.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import import kotlinx.datetime.Clock
import import kotlinx.datetime.TimeZone

/**
 * Casos de uso para Ciclos Menstruais.
 * Clean-room - orquestra repositórios e algoritmo de previsão.
 */
class CycleUseCases(
    private val cycleRepository: CycleRepository,
    private val predictionRepository: PredictionRepository,
    private val userProfileRepository: UserProfileRepository,
    private val predictionEngine: CyclePredictionEngine = CyclePredictionEngine
) {

    /**
     * Registra início de nova menstruação.
     */
    suspend fun logPeriodStart(
        userId: String,
        startDate: kotlinx.datetime.LocalDate,
        periodLength: Int? = null,
        notes: String = ""
    ): Cycle {
        val profile = userProfileRepository.getProfile(userId)
            ?: throw IllegalStateException("Perfil não encontrado")

        val previousCycle = cycleRepository.getLatestCycle(userId)
        val previousCycleLength = previousCycle?.cycleLength

        val cycle = Cycle.createManual(
            userId = userId,
            startDate = startDate,
            periodLength = periodLength ?: profile.averagePeriodLength,
            previousCycleLength = previousCycleLength,
            notes = notes
        )

        val savedCycle = cycleRepository.upsertCycle(cycle)
        
        // Regenera previsões após novo ciclo
        regeneratePredictions(userId)
        
        return savedCycle
    }

    /**
     * Atualiza fim da menstruação.
     */
    suspend fun logPeriodEnd(
        userId: String,
        cycleId: String,
        endDate: kotlinx.datetime.LocalDate,
        notes: String = ""
    ): Cycle? {
        val cycle = cycleRepository.getCycle(cycleId)
            ?: throw IllegalArgumentException("Ciclo não encontrado")

        if (cycle.userId != userId) {
            throw SecurityException("Acesso não autorizado")
        }

        val periodLength = cycle.startDate.daysUntil(endDate).toInt() + 1
        val updatedCycle = cycle.copy(
            endDate = endDate,
            periodLength = periodLength.coerceIn(2, 10),
            notes = notes,
            updatedAt = System.currentTimeMillis()
        )

        val savedCycle = cycleRepository.upsertCycle(updatedCycle)
        regeneratePredictions(userId)
        
        return savedCycle
    }

    /**
     * Obtém ciclo atual (em andamento).
     */
    suspend fun getCurrentCycle(userId: String): Cycle? {
        return cycleRepository.getCycles(userId)
            .firstOrNull()
            ?.filter { it.isCurrent }
            ?.firstOrNull()
    }

    /**
     * Obtém histórico de ciclos.
     */
    suspend fun getCycleHistory(userId: String, limit: Int = 20): List<Cycle> {
        return cycleRepository.getCycles(userId).take(limit)
    }

    /**
     * Obtém estatísticas dos ciclos.
     */
    suspend fun getCycleStatistics(userId: String) = cycleRepository.getCycleStats(userId)

    /**
     * Deleta ciclo (soft delete).
     */
    suspend fun deleteCycle(userId: String, cycleId: String): Boolean {
        val cycle = cycleRepository.getCycle(cycleId)
            ?: return false
        
        if (cycle.userId != userId) {
            throw SecurityException("Acesso não autorizado")
        }
        
        val result = cycleRepository.deleteCycle(cycleId)
        if (result) {
            regeneratePredictions(userId)
        }
        return result
    }

    /**
     * Hard delete ciclo (LGPD/GDPR).
     */
    suspend fun hardDeleteCycle(userId: String, cycleId: String): Boolean {
        val cycle = cycleRepository.getCycle(cycleId)
            ?: return false
        
        if (cycle.userId != userId) {
            throw SecurityException("Acesso não autorizado")
        }
        
        return cycleRepository.hardDeleteCycle(cycleId)
    }

    /**
     * Obtém próximas previsões.
     */
    suspend fun getUpcomingPredictions(userId: String): List<Prediction> {
        return predictionRepository.getUpcomingPredictions(userId)
    }

    /**
     * Obtém próxima menstruação prevista.
     */
    suspend fun getNextPeriodPrediction(userId: String): Prediction? {
        return predictionRepository.getNextPeriodStart(userId)
    }

    /**
     * Obtém próxima ovulação prevista.
     */
    suspend fun getNextOvulationPrediction(userId: String): Prediction? {
        return predictionRepository.getNextOvulation(userId)
    }

    /**
     * Obtém próxima janela fértil prevista.
     */
    suspend fun getNextFertileWindowPrediction(userId: String): Pair<Prediction?, Prediction?> {
        return predictionRepository.getNextFertileWindow(userId)
    }

    /**
     * Verifica se está na janela fértil hoje.
     */
    suspend fun isInFertileWindowToday(userId: String): Boolean {
        val predictions = predictionRepository.getUpcomingPredictions(userId)
        return predictionEngine.isInFertileWindow(predictions)
    }

    /**
     * Verifica se está na TPM hoje.
     */
    suspend fun isInPmsToday(userId: String): Boolean {
        val predictions = predictionRepository.getUpcomingPredictions(userId)
        return predictionEngine.isInPms(predictions)
    }

    /**
     * Obtém dias até próxima menstruação.
     */
    suspend fun getDaysUntilNextPeriod(userId: String): Int? {
        val predictions = predictionRepository.getUpcomingPredictions(userId)
        return predictionEngine.daysUntilNextPeriod(predictions)
    }

    /**
     * Regenera todas as previsões baseadas nos ciclos atuais.
     */
    private suspend fun regeneratePredictions(userId: String): List<Prediction> {
        return withContext(Dispatchers.IO) {
            val cycles = cycleRepository.getCycles(userId)
            val profile = userProfileRepository.getProfile(userId)
            
            val lastPeriodStart = cycles.firstOrNull()?.startDate 
                ?: profile?.lastPeriodDate 
                ?: kotlinx.datetime.Clock.System.todayIn(TimeZone.currentSystemDefault())
            
            val periodLength = profile?.averagePeriodLength ?: 5
            val contraception = profile?.contraceptionMethod?.key ?: "none"
            
            val predictions = predictionEngine.generatePredictions(
                cycles = cycles,
                userId = userId,
                lastPeriodStart = lastPeriodStart,
                averagePeriodLength = periodLength,
                contraceptionMethod = contraception
            )
            
            // Limpa previsões antigas e insere novas
            predictionRepository.deleteAllPredictions(userId)
            predictionRepository.upsertPredictions(predictions)
            
            predictions
        }
    }

    /**
     * Força regeneração de previsões (chamado após sync, etc).
     */
    suspend fun forceRegeneratePredictions(userId: String): List<Prediction> {
        return regeneratePredictions(userId)
    }

    /**
     * Obtém ciclos para exportação (LGPD/GDPR).
     */
    suspend fun getCyclesForExport(
        userId: String,
        startDate: Long,
        endDate: Long,
        anonymize: Boolean
    ): List<Cycle> {
        return cycleRepository.getCyclesForExport(userId, startDate, endDate, anonymize)
    }
}