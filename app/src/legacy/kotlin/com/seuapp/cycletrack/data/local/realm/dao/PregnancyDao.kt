package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.PregnancyRealm
import com.seuapp.cycletrack.data.local.realm.entities.PregnancyAppointmentRealm
import com.seuapp.cycletrack.data.local.realm.entities.PregnancyTestRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.seuapp.cycletrack.data.local.realm.detached

/**
 * DAO for PregnancyRealm operations.
 * Clean-room implementation for pregnancy tracking.
 */
class PregnancyDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get active pregnancy for a user.
     */
    suspend fun getActivePregnancy(userId: String): PregnancyRealm? = withContext(Dispatchers.IO) {
        realm.where(PregnancyRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .equalTo("status", PregnancyRealm.STATUS_ACTIVE)
            .findFirst()
            .detached()
    }

    /**
     * Get all pregnancies for a user.
     */
    suspend fun getPregnancies(userId: String): List<PregnancyRealm> = withContext(Dispatchers.IO) {
        realm.where(PregnancyRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("deletedAt", 0L)
            .sort("createdAt", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get pregnancy by ID.
     */
    suspend fun getPregnancy(pregnancyId: String): PregnancyRealm? = withContext(Dispatchers.IO) {
        realm.where(PregnancyRealm::class.java)
            .equalTo("id", pregnancyId)
            .equalTo("deletedAt", 0L)
            .findFirst()
            .detached()
    }

    /**
     * Insert or update a pregnancy.
     */
    suspend fun upsertPregnancy(pregnancy: PregnancyRealm): PregnancyRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            pregnancy.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(pregnancy)
        }
    }

    /**
     * Soft delete a pregnancy.
     */
    suspend fun deletePregnancy(pregnancyId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val pregnancy = realm.where(PregnancyRealm::class.java)
                .equalTo("id", pregnancyId)
                .findFirst()
            pregnancy?.let {
                it.deletedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Hard delete a pregnancy (GDPR/LGPD).
     */
    suspend fun hardDeletePregnancy(pregnancyId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val pregnancy = realm.where(PregnancyRealm::class.java)
                .equalTo("id", pregnancyId)
                .findFirst()
            pregnancy?.deleteFromRealm() ?: false
        }
    }

    /**
     * Update current week/day based on current date.
     */
    suspend fun updateCurrentWeek(pregnancyId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val pregnancy = realm.where(PregnancyRealm::class.java)
                .equalTo("id", pregnancyId)
                .findFirst()
            pregnancy?.let {
                val now = System.currentTimeMillis()
                val daysSinceLmp = (now - it.lmpDate) / (1000 * 60 * 60 * 24)
                it.currentWeek = (daysSinceLmp / 7) + 1
                it.currentDay = (daysSinceLmp % 7) + 1
                it.trimester = when {
                    it.currentWeek <= 13 -> 1
                    it.currentWeek <= 27 -> 2
                    else -> 3
                }
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Get appointments for a pregnancy.
     */
    suspend fun getAppointments(pregnancyId: String): List<PregnancyAppointmentRealm> = withContext(Dispatchers.IO) {
        realm.where(PregnancyAppointmentRealm::class.java)
            .equalTo("pregnancyId", pregnancyId)
            .sort("appointmentDate", io.realm.Sort.ASCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get upcoming appointments.
     */
    suspend fun getUpcomingAppointments(pregnancyId: String, fromDate: Long = System.currentTimeMillis()): List<PregnancyAppointmentRealm> = withContext(Dispatchers.IO) {
        realm.where(PregnancyAppointmentRealm::class.java)
            .equalTo("pregnancyId", pregnancyId)
            .greaterThanOrEqualTo("appointmentDate", fromDate)
            .equalTo("completed", false)
            .sort("appointmentDate", io.realm.Sort.ASCENDING)
            .findAll()
    }

    /**
     * Insert or update an appointment.
     */
    suspend fun upsertAppointment(appointment: PregnancyAppointmentRealm): PregnancyAppointmentRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            appointment.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(appointment)
        }
    }

    /**
     * Delete an appointment.
     */
    suspend fun deleteAppointment(appointmentId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val appointment = realm.where(PregnancyAppointmentRealm::class.java)
                .equalTo("id", appointmentId)
                .findFirst()
            appointment?.deleteFromRealm() ?: false
        }
    }

    /**
     * Get tests for a pregnancy.
     */
    suspend fun getTests(pregnancyId: String): List<PregnancyTestRealm> = withContext(Dispatchers.IO) {
        realm.where(PregnancyTestRealm::class.java)
            .equalTo("pregnancyId", pregnancyId)
            .sort("testDate", io.realm.Sort.DESCENDING)
            .findAll()
            .detached()
    }

    /**
     * Insert or update a test.
     */
    suspend fun upsertTest(test: PregnancyTestRealm): PregnancyTestRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            test.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(test)
        }
    }

    /**
     * Delete a test.
     */
    suspend fun deleteTest(testId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val test = realm.where(PregnancyTestRealm::class.java)
                .equalTo("id", testId)
                .findFirst()
            test?.deleteFromRealm() ?: false
        }
    }

    /**
     * Complete pregnancy with outcome.
     */
    suspend fun completePregnancy(
        pregnancyId: String,
        outcome: String,
        outcomeDate: Long,
        outcomeNotes: String = "",
        babyName: String = "",
        babyGender: String = "",
        babyWeightGrams: Int = 0,
        babyHeightCm: Float = 0.0f,
        babyApagarScore: Int = 0
    ): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val pregnancy = realm.where(PregnancyRealm::class.java)
                .equalTo("id", pregnancyId)
                .findFirst()
            pregnancy?.let {
                it.status = PregnancyRealm.STATUS_ENDED
                it.outcome = outcome
                it.outcomeDate = outcomeDate
                it.outcomeNotes = outcomeNotes
                it.actualBirthDate = outcomeDate
                it.babyName = babyName
                it.babyGender = babyGender
                it.babyWeightGrams = babyWeightGrams
                it.babyHeightCm = babyHeightCm
                it.babyApagarScore = babyApagarScore
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Mark pregnancy as lost (miscarriage, etc.).
     */
    suspend fun markPregnancyLost(pregnancyId: String, outcome: String, outcomeDate: Long, notes: String = ""): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val pregnancy = realm.where(PregnancyRealm::class.java)
                .equalTo("id", pregnancyId)
                .findFirst()
            pregnancy?.let {
                it.status = PregnancyRealm.STATUS_LOST
                it.outcome = outcome
                it.outcomeDate = outcomeDate
                it.outcomeNotes = notes
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
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