package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Pregnancy
import com.seuapp.cycletrack.domain.model.PregnancyAppointment
import com.seuapp.cycletrack.domain.model.PregnancyTest
import com.seuapp.cycletrack.domain.model.PregnancyOutcome

/**
 * Interface do repositório de Gravidez.
 * Clean-room - define contrato sem expor implementação.
 */
interface PregnancyRepository {

    /**
     * Obtém gravidez ativa do usuário.
     */
    suspend fun getActivePregnancy(userId: String): Pregnancy?

    /**
     * Obtém todas as gravidezes do usuário.
     */
    suspend fun getPregnancies(userId: String): List<Pregnancy>

    /**
     * Obtém gravidez por ID.
     */
    suspend fun getPregnancy(pregnancyId: String): Pregnancy?

    /**
     * Insere ou atualiza uma gravidez.
     */
    suspend fun upsertPregnancy(pregnancy: Pregnancy): Pregnancy

    /**
     * Soft delete.
     */
    suspend fun deletePregnancy(pregnancyId: String): Boolean

    /**
     * Hard delete (LGPD/GDPR).
     */
    suspend fun hardDeletePregnancy(pregnancyId: String): Boolean

    /**
     * Atualiza semana/dia atual baseado na data atual.
     */
    suspend fun updateCurrentWeek(pregnancyId: String): Boolean

    /**
     * Obtém consultas de uma gravidez.
     */
    suspend fun getAppointments(pregnancyId: String): List<PregnancyAppointment>

    /**
     * Obtém consultas futuras.
     */
    suspend fun getUpcomingAppointments(pregnancyId: String, fromDate: Long = System.currentTimeMillis()): List<PregnancyAppointment>

    /**
     * Insere ou atualiza uma consulta.
     */
    suspend fun upsertAppointment(appointment: PregnancyAppointment): PregnancyAppointment

    /**
     * Deleta uma consulta.
     */
    suspend fun deleteAppointment(appointmentId: String): Boolean

    /**
     * Obtém exames de uma gravidez.
     */
    suspend fun getTests(pregnancyId: String): List<PregnancyTest>

    /**
     * Insere ou atualiza um exame.
     */
    suspend fun upsertTest(test: PregnancyTest): PregnancyTest

    /**
     * Deleta um exame.
     */
    suspend fun deleteTest(testId: String): Boolean

    /**
     * Finaliza gravidez com resultado.
     */
    suspend fun completePregnancy(
        pregnancyId: String,
        outcome: PregnancyOutcome,
        outcomeDate: Long,
        outcomeNotes: String = "",
        babyName: String = "",
        babyGender: String = "",
        babyWeightGrams: Int = 0,
        babyHeightCm: Float = 0.0f,
        babyApgarScore: Int = 0
    ): Boolean

    /**
     * Marca gravidez como perdida.
     */
    suspend fun markPregnancyLost(
        pregnancyId: String,
        outcome: PregnancyOutcome,
        outcomeDate: Long,
        notes: String = ""
    ): Boolean

    /**
     * Obtém gravidez para exportação (LGPD/GDPR).
     */
    suspend fun getPregnancyForExport(
        userId: String,
        pregnancyId: String,
        anonymize: Boolean
    ): Pregnancy?
}