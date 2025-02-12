package com.exner.tools.meditationtimer.data.persistence.tools

import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RootData(
    val processes: List<MeditationTimerProcess>,
    val categories: List<MeditationTimerProcessCategory>
)
