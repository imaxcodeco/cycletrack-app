package com.seuapp.cycletrack.data.local.realm.dao

import javax.inject.Inject

class ContentDao @Inject constructor() {
    suspend fun getContent(language: String, region: String, includeExpired: Boolean) = emptyList<Any>()
    suspend fun getContentById(contentId: String) = null
}
