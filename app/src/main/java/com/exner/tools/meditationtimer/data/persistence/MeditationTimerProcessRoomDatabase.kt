package com.exner.tools.meditationtimer.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MeditationTimerProcess::class],
    version = 1,
    exportSchema = false
)
abstract class MeditationTimerProcessRoomDatabase : RoomDatabase() {
    abstract fun processDAO(): MeditationTimerProcessDAO

}