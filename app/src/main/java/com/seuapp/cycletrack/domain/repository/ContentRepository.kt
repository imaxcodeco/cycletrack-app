package com.seuapp.cycletrack.domain.repository

interface ContentRepository {
    suspend fun getContent(language: String): List<Any>
    suspend fun getContentById(id: String): Any?
    suspend fun upsertContent(content: Any)
}
