package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.Pregnancy

interface PregnancyRepository {
    suspend fun getActivePregnancy(userId: String): Pregnancy?
    suspend fun getPregnancies(userId: String): List<Pregnancy>
    suspend fun getPregnancy(id: String): Pregnancy?
    suspend fun upsertPregnancy(pregnancy: Pregnancy)
    suspend fun deletePregnancy(id: String)
}
