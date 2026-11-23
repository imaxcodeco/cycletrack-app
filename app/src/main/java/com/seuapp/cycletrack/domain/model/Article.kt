package com.seuapp.cycletrack.domain.model

data class Article(
    val id: String,
    val section: String,
    val title: String,
    val summary: String,
    val body: List<String>,
    val reviewerName: String,
    val reviewerCredential: String
)
