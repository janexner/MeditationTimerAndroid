package com.exner.tools.meditationtimer.data.persistence

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE MeditationTimerProcess ADD COLUMN background_uri TEXT DEFAULT 'https://fototimer.net/assets/activitytimer/bg-none.png';")
    }
}

