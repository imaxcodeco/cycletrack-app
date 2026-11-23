package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.PredictionRealm
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.seuapp.cycletrack.data.local.realm.detached

/**
 * DAO for PredictionRealm operations.
 * Clean-room implementation for cycle predictions.
 */
class PredictionDao {

    /** A API Java do Realm e thread-confined: obtemos a instancia da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()


    /**
     * Get all predictions for a user.
     */
    suspend fun getPredictions(userId: String): List<PredictionRealm> = withContext(Dispatchers.IO) {
        realm.where(PredictionRealm::class.java)
            .equalTo("userId", userId)
            .sort("predictedDate", io.realm.Sort.ASCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get predictions by type.
     */
    suspend fun getPredictionsByType(userId: String, type: String): List<PredictionRealm> = withContext(Dispatchers.IO) {
        realm.where(PredictionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("predictionType", type)
            .sort("predictedDate", io.realm.Sort.ASCENDING)
            .findAll()
            .detached()
    }

    /**
     * Get upcoming predictions (future dates).
     */
    suspend fun getUpcomingPredictions(userId: String, fromDate: Long = System.currentTimeMillis()): List<PredictionRealm> = withContext(Dispatchers.IO) {
        realm.where(PredictionRealm::class.java)
            .equalTo("userId", userId)
            .greaterThanOrEqualTo("predictedDate", fromDate)
            .sort("predictedDate", io.realm.Sort.ASCENDING)
            .findAll()
    }

    /**
     * Get the next period start prediction.
     */
    suspend fun getNextPeriodStart(userId: String): PredictionRealm? = withContext(Dispatchers.IO) {
        realm.where(PredictionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("predictionType", PredictionRealm.TYPE_PERIOD_START)
            .greaterThanOrEqualTo("predictedDate", System.currentTimeMillis())
            .sort("predictedDate", io.realm.Sort.ASCENDING)
            .findFirst()
            .detached()
    }

    /**
     * Get the next ovulation prediction.
     */
    suspend fun getNextOvulation(userId: String): PredictionRealm? = withContext(Dispatchers.IO) {
        realm.where(PredictionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("predictionType", PredictionRealm.TYPE_OVULATION)
            .greaterThanOrEqualTo("predictedDate", System.currentTimeMillis())
            .sort("predictedDate", io.realm.Sort.ASCENDING)
            .findFirst()
            .detached()
    }

    /**
     * Get the next fertile window prediction.
     */
    suspend fun getNextFertileWindow(userId: String): Pair<PredictionRealm?, PredictionRealm?> = withContext(Dispatchers.IO) {
        val start = realm.where(PredictionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("predictionType", PredictionRealm.TYPE_FERTILE_WINDOW_START)
            .greaterThanOrEqualTo("predictedDate", System.currentTimeMillis())
            .sort("predictedDate", io.realm.Sort.ASCENDING)
            .findFirst()

        val end = realm.where(PredictionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("predictionType", PredictionRealm.TYPE_FERTILE_WINDOW_END)
            .greaterThanOrEqualTo("predictedDate", System.currentTimeMillis())
            .sort("predictedDate", io.realm.Sort.ASCENDING)
            .findFirst()

        Pair(start, end)
    }

    /**
     * Get predictions needing notification.
     */
    suspend fun getPredictionsNeedingNotification(userId: String, beforeMillis: Long = System.currentTimeMillis()): List<PredictionRealm> = withContext(Dispatchers.IO) {
        realm.where(PredictionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("isNotified", false)
            .lessThanOrEqualTo("predictedDate", beforeMillis)
            .sort("predictedDate", io.realm.Sort.ASCENDING)
            .findAll()
    }

    /**
     * Get expired predictions (for cleanup).
     */
    suspend fun getExpiredPredictions(userId: String, beforeDate: Long = System.currentTimeMillis()): List<PredictionRealm> = withContext(Dispatchers.IO) {
        realm.where(PredictionRealm::class.java)
            .equalTo("userId", userId)
            .lessThan("expiresAt", beforeDate)
            .findAll()
    }

    /**
     * Insert or update a prediction.
     */
    suspend fun upsertPrediction(prediction: PredictionRealm): PredictionRealm = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            prediction.updatedAt = System.currentTimeMillis()
            realm.copyToRealmOrUpdate(prediction)
        }
    }

    /**
     * Insert multiple predictions in a transaction.
     */
    suspend fun upsertPredictions(predictions: List<PredictionRealm>): List<PredictionRealm> = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            predictions.forEach { it.updatedAt = System.currentTimeMillis() }
            realm.copyToRealmOrUpdate(predictions)
        }
    }

    /**
     * Delete a prediction.
     */
    suspend fun deletePrediction(predictionId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val prediction = realm.where(PredictionRealm::class.java)
                .equalTo("id", predictionId)
                .findFirst()
            prediction?.deleteFromRealm() ?: false
        }
    }

    /**
     * Delete all predictions for a user.
     */
    suspend fun deleteAllPredictions(userId: String): Int = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val predictions = realm.where(PredictionRealm::class.java)
                .equalTo("userId", userId)
                .findAll()
            val count = predictions.size
            predictions.deleteAllFromRealm()
            count
        }
    }

    /**
     * Mark prediction as notified.
     */
    suspend fun markAsNotified(predictionId: String): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val prediction = realm.where(PredictionRealm::class.java)
                .equalTo("id", predictionId)
                .findFirst()
            prediction?.let {
                it.isNotified = true
                it.notifiedAt = System.currentTimeMillis()
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Update prediction with actual date (for accuracy tracking).
     */
    suspend fun updateActualDate(predictionId: String, actualDate: Long): Boolean = withContext(Dispatchers.IO) {
        writeTransaction { realm ->
            val prediction = realm.where(PredictionRealm::class.java)
                .equalTo("id", predictionId)
                .findFirst()
            prediction?.let {
                it.actualDate = actualDate
                it.accuracyDays = calculateAccuracyDays(it.predictedDate, actualDate)
                it.updatedAt = System.currentTimeMillis()
                true
            } ?: false
        }
    }

    /**
     * Get prediction accuracy statistics.
     */
    suspend fun getAccuracyStats(userId: String, type: String): AccuracyStats = withContext(Dispatchers.IO) {
        val predictions = realm.where(PredictionRealm::class.java)
            .equalTo("userId", userId)
            .equalTo("predictionType", type)
            .notEqualTo("actualDate", 0L)
            .findAll()

        if (predictions.isEmpty()) {
            return AccuracyStats(0, 0.0, 0, 0, 0.0)
        }

        val accuracies = predictions.map { it.accuracyDays }
        val within1Day = accuracies.count { it in -1..1 }
        val within3Days = accuracies.count { it in -3..3 }
        val avgAccuracy = accuracies.average()
        val minAccuracy = accuracies.minOrNull() ?: 0
        val maxAccuracy = accuracies.maxOrNull() ?: 0

        AccuracyStats(
            totalPredictions = predictions.size,
            averageAccuracyDays = avgAccuracy,
            within1DayCount = within1Day,
            within3DaysCount = within3Days,
            minAccuracyDays = minAccuracy.toDouble(),
            maxAccuracyDays = maxAccuracy.toDouble()
        )
    }

    /**
     * Calculate accuracy in days (positive = late, negative = early).
     */
    private fun calculateAccuracyDays(predicted: Long, actual: Long): Int {
        val diffMillis = actual - predicted
        return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    }
}

/**
 * Data class for prediction accuracy statistics.
 */
data class AccuracyStats(
    val totalPredictions: Int,
    val averageAccuracyDays: Double,
    val within1DayCount: Int,
    val within3DaysCount: Int,
    val minAccuracyDays: Double,
    val maxAccuracyDays: Double
) {
    val within1DayPercentage: Double
        get() = if (totalPredictions > 0) (within1DayCount * 100.0 / totalPredictions) else 0.0

    val within3DaysPercentage: Double
        get() = if (totalPredictions > 0) (within3DaysCount * 100.0 / totalPredictions) else 0.0

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