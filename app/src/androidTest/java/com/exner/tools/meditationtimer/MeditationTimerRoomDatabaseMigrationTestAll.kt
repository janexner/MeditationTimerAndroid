package com.exner.tools.meditationtimer

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.exner.tools.meditationtimer.data.persistence.MIGRATION_11_12
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerRoomDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MeditationTimerRoomDatabaseMigrationTestAll {
    private val TEST_DB = "migration-test"

    // Array of all migrations.
    private val ALL_MIGRATIONS = arrayOf(
        MIGRATION_11_12)

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        "schemas/com.exner.tools.meditationtimer.data.persistence.MeditationTimerRoomDatabase",
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        helper.createDatabase(TEST_DB, 11).apply {
            close()
        }

        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MeditationTimerRoomDatabase::class.java,
            TEST_DB
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase.close()
        }
    }
}