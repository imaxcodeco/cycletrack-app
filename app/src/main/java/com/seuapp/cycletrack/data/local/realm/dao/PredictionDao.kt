package com.seuapp.cycletrack.data.local.realm.dao

import javax.inject.Inject

class PredictionDao @Inject constructor() {
    suspend fun getPredictions(userId: String) = emptyList<Any>()
    suspend fun getPredictionsByType(userId: String, type: String) = emptyList<Any>()
    suspend fun getUpcomingPredictions(userId: String, fromDate: Long) = emptyList<Any>()
    suspend fun upsertPrediction(prediction: Any) {}
    suspend fun upsertPredictions(predictions: List<Any>) {}
    suspend fun deletePrediction(predictionId: String) {}
    suspend fun deleteAllPredictions(userId: String) {}
}
