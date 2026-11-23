package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.domain.model.Pregnancy
import com.seuapp.cycletrack.domain.repository.PregnancyRepository
import javax.inject.Inject

class PregnancyRepositoryImpl @Inject constructor() : PregnancyRepository {
    override suspend fun getActivePregnancy(userId: String): Pregnancy? = null
    override suspend fun getPregnancies(userId: String): List<Pregnancy> = emptyList()
    override suspend fun getPregnancy(id: String): Pregnancy? = null
    override suspend fun upsertPregnancy(pregnancy: Pregnancy) {}
    override suspend fun deletePregnancy(id: String) {}
}
