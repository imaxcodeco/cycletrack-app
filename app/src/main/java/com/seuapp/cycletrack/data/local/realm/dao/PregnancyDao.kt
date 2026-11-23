package com.seuapp.cycletrack.data.local.realm.dao

import javax.inject.Inject

class PregnancyDao @Inject constructor() {
    suspend fun getActivePregnancy(userId: String) = null
    suspend fun getPregnancies(userId: String) = emptyList<Any>()
    suspend fun getPregnancy(pregnancyId: String) = null
    suspend fun upsertPregnancy(pregnancy: Any) {}
    suspend fun deletePregnancy(pregnancyId: String) {}
    suspend fun hardDeletePregnancy(pregnancyId: String) {}
}
