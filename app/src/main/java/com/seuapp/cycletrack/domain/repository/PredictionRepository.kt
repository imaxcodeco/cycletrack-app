package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Prediction
import kotlinx.coroutines.flow.Flow

interface PredictionRepository {
    fun getPredictions(userId: String): Flow<List<Prediction>>
    suspend fun getPrediction(id: String): Prediction?
    suspend fun getPredictionsByType(userId: String, type: String): List<Prediction>
    suspend fun getUpcomingPredictions(userId: String, fromDate: Long): List<Prediction>
    suspend fun upsertPrediction(prediction: Prediction)
    suspend fun upsertPredictions(predictions: List<Prediction>)
    suspend fun deletePrediction(id: String)
    suspend fun deleteAllPredictions(userId: String)
}
