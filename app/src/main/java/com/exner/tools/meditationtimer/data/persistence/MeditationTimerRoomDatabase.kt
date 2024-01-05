package com.exner.tools.meditationtimer.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MeditationTimerProcess::class, MeditationTimerProcessCategory::class],
    version = 4,
    exportSchema = false
)
abstract class MeditationTimerRoomDatabase : RoomDatabase() {
    abstract fun processDAO(): MeditationTimerDataDAO

}