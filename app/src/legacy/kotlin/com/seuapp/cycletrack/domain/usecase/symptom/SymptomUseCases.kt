package com.seuapp.cycletrack.domain.usecase.symptom

import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.model.SymptomSeverity
import com.seuapp.cycletrack.domain.repository.SymptomRepository
import com.seuapp.cycletrack.domain.repository.CycleRepository
import import com.seuapp.cycletrack.util.toEpochMilliseconds

/**
 * Casos de uso para Sintomas.
 * Clean-room - gerencia registro e análise de sintomas.
 */
class SymptomUseCases(
    private val symptomRepository: SymptomRepository,
    private val cycleRepository: CycleRepository
) {

    /**
     * Registra um sintoma.
     */
    suspend fun logSymptom(
        userId: String,
        date: kotlinx.datetime.LocalDate,
        category: String,
        name: String,
        severity: SymptomSeverity = SymptomSeverity.MILD,
        notes: String = "",
        cycleId: String? = null,
        source: String = Symptom.SOURCE_MANUAL
    ): Symptom {
        // Se não foi informado cycleId, tenta encontrar o ciclo da data
        val resolvedCycleId = cycleId ?: findCycleForDate(userId, date)
        
        val symptom = Symptom(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId,
            cycleId = resolvedCycleId ?: "",
            date = date,
            category = category,
            name = name,
            severity = severity,
            notes = notes,
            source = source
        )

        return symptomRepository.upsertSymptom(symptom)
    }

    /**
     * Registra múltiplos sintomas de uma vez.
     */
    suspend fun logSymptoms(
        userId: String,
        date: kotlinx.datetime.LocalDate,
        symptoms: List<SymptomInput>,
        cycleId: String? = null
    ): List<Symptom> {
        val resolvedCycleId = cycleId ?: findCycleForDate(userId, date)
        
        val symptomList = symptoms.map { input ->
            Symptom(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                cycleId = resolvedCycleId ?: "",
                date = date,
                category = input.category,
                name = input.name,
                severity = input.severity,
                notes = input.notes
            )
        }

        return symptomRepository.upsertSymptoms(symptomList)
    }

    /**
     * Remove um sintoma.
     */
    suspend fun removeSymptom(userId: String, symptomId: String): Boolean {
        val symptom = symptomRepository.getSymptoms(userId)
            .firstOrNull { it.id == symptomId }
            ?: return false

        if (symptom.userId != userId) {
            throw SecurityException("Acesso não autorizado")
        }

        return symptomRepository.deleteSymptom(symptomId)
    }

    /**
     * Obtém sintomas de uma data específica.
     */
    suspend fun getSymptomsForDate(userId: String, date: kotlinx.datetime.LocalDate): List<Symptom> {
        return symptomRepository.getSymptomsForDate(userId, date.toEpochMilliseconds())
    }

    /**
     * Obtém sintomas de um ciclo.
     */
    suspend fun getSymptomsForCycle(cycleId: String): List<Symptom> {
        return symptomRepository.getSymptomsForCycle(cycleId)
    }

    /**
     * Obtém sintomas em um intervalo.
     */
    suspend fun getSymptomsInRange(
        userId: String,
        startDate: kotlinx.datetime.LocalDate,
        endDate: kotlinx.datetime.LocalDate
    ): List<Symptom> {
        return symptomRepository.getSymptomsInRange(
            userId,
            startDate.toEpochMilliseconds(),
            endDate.toEpochMilliseconds()
        )
    }

    /**
     * Obtém frequência de sintomas em um período.
     */
    suspend fun getSymptomFrequency(
        userId: String,
        startDate: kotlinx.datetime.LocalDate,
        endDate: kotlinx.datetime.LocalDate
    ): Map<String, Int> {
        return symptomRepository.getSymptomFrequency(
            userId,
            startDate.toEpochMilliseconds(),
            endDate.toEpochMilliseconds()
        )
    }

    /**
     * Obtém os sintomas mais comuns.
     */
    suspend fun getMostCommonSymptoms(
        userId: String,
        limit: Int = 10,
        startDate: kotlinx.datetime.LocalDate? = null,
        endDate: kotlinx.datetime.LocalDate? = null
    ): List<Pair<String, Int>> {
        return symptomRepository.getMostCommonSymptoms(
            userId,
            limit,
            startDate?.toEpochMilliseconds() ?: 0L,
            endDate?.toEpochMilliseconds() ?: Long.MAX_VALUE
        )
    }

    /**
     * Obtém distribuição de severidade de um sintoma.
     */
    suspend fun getSymptomSeverityDistribution(
        userId: String,
        symptomName: String,
        startDate: kotlinx.datetime.LocalDate,
        endDate: kotlinx.datetime.LocalDate
    ): Map<Int, Int> {
        return symptomRepository.getSymptomSeverityDistribution(
            userId,
            symptomName,
            startDate.toEpochMilliseconds(),
            endDate.toEpochMilliseconds()
        )
    }

    /**
     * Obtém sintomas por categoria.
     */
    suspend fun getSymptomsByCategory(userId: String, category: String): List<Symptom> {
        return symptomRepository.getSymptomsByCategory(userId, category)
    }

    /**
     * Verifica se sintoma já foi registrado na data.
     */
    suspend fun hasSymptomOnDate(userId: String, date: kotlinx.datetime.LocalDate, symptomName: String): Boolean {
        return symptomRepository.hasSymptomOnDate(userId, date.toEpochMilliseconds(), symptomName)
    }

    /**
     * Obtém sintomas para exportação (LGPD/GDPR).
     */
    suspend fun getSymptomsForExport(
        userId: String,
        startDate: kotlinx.datetime.LocalDate,
        endDate: kotlinx.datetime.LocalDate,
        anonymize: Boolean
    ): List<Symptom> {
        return symptomRepository.getSymptomsForExport(
            userId,
            startDate.toEpochMilliseconds(),
            endDate.toEpochMilliseconds(),
            anonymize
        )
    }

    /**
     * Encontra ciclo para uma data.
     */
    private fun findCycleForDate(userId: String, date: kotlinx.datetime.LocalDate): String? {
        val cycles = cycleRepository.getCycles(userId)
        return cycles.firstOrNull { cycle ->
            cycle.startDate <= date && (cycle.endDate == null || cycle.endDate!! >= date)
        }?.id
    }
}

/**
 * Input para registro de sintoma.
 */
data class SymptomInput(
    val category: String,
    val name: String,
    val severity: SymptomSeverity = SymptomSeverity.MILD,
    val notes: String = ""
)