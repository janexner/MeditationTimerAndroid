package com.exner.tools.meditationtimer.data.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MeditationTimerProcess (
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "process_time") val processTime : Int = 30,
    @ColumnInfo(name = "interval_time") val intervalTime: Int = 5,

    @ColumnInfo(name = "has_auto_chain") val hasAutoChain: Boolean = false,
    @ColumnInfo(name = "goto_id") val gotoId: Long?,

    @PrimaryKey(autoGenerate = true) val uid: Long = 0
)