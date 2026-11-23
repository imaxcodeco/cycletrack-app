package com.seuapp.cycletrack.data.local.realm.dao

import javax.inject.Inject

class ExportDao @Inject constructor() {
    suspend fun getExports(userId: String) = emptyList<Any>()
    suspend fun getExport(exportId: String) = null
    suspend fun createExport(userId: String, format: String, vararg args: Any) {}
    suspend fun markAsProcessing(exportId: String) {}
    suspend fun markAsCompleted(exportId: String, filePath: String, fileSize: Long, fileHash: String, downloadUrl: String) {}
    suspend fun deleteExport(exportId: String) {}
}
