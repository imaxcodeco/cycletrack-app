package com.seuapp.cycletrack.domain.model

data class MoodOption(val key: String, val label: String)

data class CheckInThread(
    val id: String,
    val title: String,
    val preview: String,
    val timestamp: String,
    val botLines: List<String>,
    val moodOptions: List<MoodOption>
)
