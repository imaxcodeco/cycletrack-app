package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.repository.PredictionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PredictionRepositoryImpl @Inject constructor() : PredictionRepository {
    override fun getPredictions(userId: String): Flow<List<Prediction>> = flowOf(emptyList())
    override suspend fun getPrediction(id: String): Prediction? = null
    override suspend fun getPredictionsByType(userId: String, type: String): List<Prediction> = emptyList()
    override suspend fun getUpcomingPredictions(userId: String, fromDate: Long): List<Prediction> = emptyList()
    override suspend fun upsertPrediction(prediction: Prediction) {}
    override suspend fun upsertPredictions(predictions: List<Prediction>) {}
    override suspend fun deletePrediction(id: String) {}
    override suspend fun deleteAllPredictions(userId: String) {}
}
