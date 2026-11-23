package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.data.local.realm.dao.ExportDao
import com.seuapp.cycletrack.domain.repository.ExportRepository
import javax.inject.Inject

class ExportRepositoryImpl @Inject constructor(
    private val exportDao: ExportDao
) : ExportRepository {
    override suspend fun getExports(userId: String) = emptyList<Any>()
    override suspend fun createExport(userId: String, format: String) = null
    override suspend fun deleteExport(id: String) {}
}
