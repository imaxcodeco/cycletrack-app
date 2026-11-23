package com.seuapp.cycletrack.domain.usecase.pregnancy

import com.seuapp.cycletrack.domain.model.Pregnancy
import com.seuapp.cycletrack.domain.model.PregnancyAppointment
import com.seuapp.cycletrack.domain.model.PregnancyTest
import com.seuapp.cycletrack.domain.model.PregnancyOutcome
import com.seuapp.cycletrack.domain.model.BabyGender
import com.seuapp.cycletrack.domain.model.AppointmentType
import com.seuapp.cycletrack.domain.model.TestType
import com.seuapp.cycletrack.domain.repository.PregnancyRepository
import import com.seuapp.cycletrack.util.toEpochMilliseconds

/**
 * Casos de uso para Gravidez.
 * Clean-room - gerencia acompanhamento gestacional.
 */
class PregnancyUseCases(
    private val pregnancyRepository: PregnancyRepository
) {

    /**
     * Inicia acompanhamento de gravidez.
     */
    suspend fun startPregnancy(
        userId: String,
        lmpDate: kotlinx.datetime.LocalDate,
        dueDate: kotlinx.datetime.LocalDate? = null,
        conceptionDate: kotlinx.datetime.LocalDate? = null
    ): Pregnancy {
        val pregnancy = Pregnancy.create(
            userId = userId,
            lmpDate = lmpDate,
            dueDate = dueDate,
            conceptionDate = conceptionDate
        )
        return pregnancyRepository.upsertPregnancy(pregnancy)
    }

    /**
     * Obtém gravidez ativa.
     */
    suspend fun getActivePregnancy(userId: String): Pregnancy? {
        return pregnancyRepository.getActivePregnancy(userId)
    }

    /**
     * Obtém todas as gravidezes.
     */
    suspend fun getPregnancies(userId: String): List<Pregnancy> {
        return pregnancyRepository.getPregnancies(userId)
    }

    /**
     * Obtém gravidez por ID.
     */
    suspend fun getPregnancy(pregnancyId: String): Pregnancy? {
        return pregnancyRepository.getPregnancy(pregnancyId)
    }

    /**
     * Atualiza semana/dia atual.
     */
    suspend fun updateCurrentWeek(pregnancyId: String): Boolean {
        return pregnancyRepository.updateCurrentWeek(pregnancyId)
    }

    /**
     * Adiciona consulta pré-natal.
     */
    suspend fun addAppointment(
        pregnancyId: String,
        title: String,
        appointmentDate: kotlinx.datetime.LocalDate,
        appointmentTime: String = "",
        type: AppointmentType = AppointmentType.PRENATAL,
        location: String = "",
        doctorName: String = "",
        description: String = "",
        reminderEnabled: Boolean = true,
        reminderMinutesBefore: Int = 60
    ): PregnancyAppointment {
        val appointment = PregnancyAppointment(
            pregnancyId = pregnancyId,
            title = title,
            appointmentDate = appointmentDate,
            appointmentTime = appointmentTime,
            type = type,
            location = location,
            doctorName = doctorName,
            description = description,
            reminderEnabled = reminderEnabled,
            reminderMinutesBefore = reminderMinutesBefore
        )
        return pregnancyRepository.upsertAppointment(appointment)
    }

    /**
     * Atualiza consulta.
     */
    suspend fun updateAppointment(appointment: PregnancyAppointment): PregnancyAppointment {
        return pregnancyRepository.upsertAppointment(appointment)
    }

    /**
     * Remove consulta.
     */
    suspend fun removeAppointment(appointmentId: String): Boolean {
        return pregnancyRepository.deleteAppointment(appointmentId)
    }

    /**
     * Obtém consultas futuras.
     */
    suspend fun getUpcomingAppointments(pregnancyId: String): List<PregnancyAppointment> {
        return pregnancyRepository.getUpcomingAppointments(pregnancyId)
    }

    /**
     * Adiciona exame/teste.
     */
    suspend fun addTest(
        pregnancyId: String,
        testType: TestType,
        testDate: kotlinx.datetime.LocalDate,
        resultValue: String = "",
        resultUnit: String = "",
        referenceRange: String = "",
        isNormal: Boolean = true,
        notes: String = ""
    ): PregnancyTest {
        val test = PregnancyTest(
            pregnancyId = pregnancyId,
            testType = testType,
            testDate = testDate,
            resultValue = resultValue,
            resultUnit = resultUnit,
            referenceRange = referenceRange,
            isNormal = isNormal,
            notes = notes
        )
        return pregnancyRepository.upsertTest(test)
    }

    /**
     * Remove exame/teste.
     */
    suspend fun removeTest(testId: String): Boolean {
        return pregnancyRepository.deleteTest(testId)
    }

    /**
     * Finaliza gravidez com nascimento vivo.
     */
    suspend fun completePregnancy(
        pregnancyId: String,
        outcomeDate: kotlinx.datetime.LocalDate,
        outcomeNotes: String = "",
        babyName: String = "",
        babyGender: BabyGender = BabyGender.UNKNOWN,
        babyWeightGrams: Int = 0,
        babyHeightCm: Float = 0.0f,
        babyApgarScore: Int = 0
    ): Boolean {
        return pregnancyRepository.completePregnancy(
            pregnancyId = pregnancyId,
            outcome = PregnancyOutcome.LIVE_BIRTH,
            outcomeDate = outcomeDate.toEpochMilliseconds(),
            outcomeNotes = outcomeNotes,
            babyName = babyName,
            babyGender = babyGender.key,
            babyWeightGrams = babyWeightGrams,
            babyHeightCm = babyHeightCm,
            babyApgarScore = babyApgarScore
        )
    }

    /**
     * Registra perda gestacional.
     */
    suspend fun recordPregnancyLoss(
        pregnancyId: String,
        outcome: PregnancyOutcome,
        outcomeDate: kotlinx.datetime.LocalDate,
        notes: String = ""
    ): Boolean {
        return pregnancyRepository.markPregnancyLost(
            pregnancyId = pregnancyId,
            outcome = outcome,
            outcomeDate = outcomeDate.toEpochMilliseconds(),
            notes = notes
        )
    }

    /**
     * Atualiza métricas de saúde da gestante.
     */
    suspend fun updateHealthMetrics(
        pregnancyId: String,
        currentWeightKg: Float? = null,
        bloodPressureSystolic: Int? = null,
        bloodPressureDiastolic: Int? = null,
        glucoseLevel: Float? = null
    ): Boolean {
        return pregnancyRepository.updateProfile(pregnancyId) { pregnancy ->
            currentWeightKg?.let { pregnancy = pregnancy.copy(currentWeightKg = it) }
            bloodPressureSystolic?.let { pregnancy = pregnancy.copy(bloodPressureSystolic = it) }
            bloodPressureDiastolic?.let { pregnancy = pregnancy.copy(bloodPressureDiastolic = it) }
            glucoseLevel?.let { pregnancy = pregnancy.copy(glucoseLevel = it) }
            if (currentWeightKg != null && pregnancy.prePregnancyWeightKg > 0) {
                pregnancy = pregnancy.copy(totalWeightGainKg = currentWeightKg - pregnancy.prePregnancyWeightKg)
            }
        }
    }

    /**
     * Deleta gravidez (soft delete).
     */
    suspend fun deletePregnancy(pregnancyId: String): Boolean {
        return pregnancyRepository.deletePregnancy(pregnancyId)
    }

    /**
     * Hard delete gravidez (LGPD/GDPR).
     */
    suspend fun hardDeletePregnancy(pregnancyId: String): Boolean {
        return pregnancyRepository.hardDeletePregnancy(pregnancyId)
    }

    /**
     * Obtém gravidez para exportação (LGPD/GDPR).
     */
    suspend fun getPregnancyForExport(
        userId: String,
        pregnancyId: String,
        anonymize: Boolean
    ): Pregnancy? {
        return pregnancyRepository.getPregnancyForExport(userId, pregnancyId, anonymize)
    }
}