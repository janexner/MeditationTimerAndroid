package com.exner.tools.meditationtimer.data.persistence

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MeditationTimerProcess::class, MeditationTimerProcessCategory::class],
    views = [MeditationTimerCategoryIdNameCount::class],
    version = 12,
    exportSchema = true,
)
abstract class MeditationTimerRoomDatabase : RoomDatabase() {
    abstract fun processDAO(): MeditationTimerDataDAO

}