package com.exner.tools.meditationtimer.data.persistence

import androidx.room.Entity

@Entity(primaryKeys = ["processUid", "tagUid"])
data class ProcessTagLink(
    val processUid: Long?,
    val tagUid: Long?,
)