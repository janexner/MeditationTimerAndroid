package com.exner.tools.meditationtimer.data.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tag (
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "info") val info : String,

    @PrimaryKey(autoGenerate = true) val uid: Long = 0
)