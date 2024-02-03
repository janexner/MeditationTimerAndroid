package com.exner.tools.meditationtimer.data.persistence

import androidx.room.DatabaseView

@DatabaseView("SELECT meditationtimerprocesscategory.uid, meditationtimerprocesscategory.name, " +
        "COUNT(meditationtimerprocess.uid) AS usageCount FROM meditationtimerprocesscategory " +
        "LEFT JOIN meditationtimerprocess ON meditationtimerprocess.category_id = meditationtimerprocesscategory.uid")
data class MeditationTimerCategoryIdNameCount(
    var uid: Long,
    var name: String?,
    var usageCount: Int
)