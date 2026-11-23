package com.seuapp.cycletrack.domain.repository

interface ExportRepository {
    suspend fun getExports(userId: String): List<Any>
    suspend fun createExport(userId: String, format: String): Any?
    suspend fun deleteExport(id: String)
}
