package com.exner.tools.meditationtimer.data.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MeditationTimerProcessCategory (
    @ColumnInfo(name = "name") var name : String,
    @PrimaryKey(autoGenerate = true) val uid: Long = 0
)