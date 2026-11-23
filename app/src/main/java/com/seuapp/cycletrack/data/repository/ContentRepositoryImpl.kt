package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.data.local.realm.dao.ContentDao
import com.seuapp.cycletrack.domain.repository.ContentRepository
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    private val contentDao: ContentDao
) : ContentRepository {
    override suspend fun getContent(language: String) = emptyList<Any>()
    override suspend fun getContentById(id: String) = null
    override suspend fun upsertContent(content: Any) {}
}
