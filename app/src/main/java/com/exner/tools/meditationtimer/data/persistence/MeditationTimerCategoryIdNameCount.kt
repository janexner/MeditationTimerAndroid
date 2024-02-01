package com.exner.tools.meditationtimer.data.persistence

data class MeditationTimerCategoryIdNameCount(
    var uid: Long,
    var name: String?,
    var usageCount: Int
)