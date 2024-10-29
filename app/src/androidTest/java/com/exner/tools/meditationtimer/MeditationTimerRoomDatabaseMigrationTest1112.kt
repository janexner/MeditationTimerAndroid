package com.exner.tools.meditationtimer

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.exner.tools.meditationtimer.data.persistence.MIGRATION_11_12
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MeditationTimerRoomDatabaseMigrationTest1112 {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        "schemas/com.exner.tools.meditationtimer.data.persistence.MeditationTimerRoomDatabase",
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate11To12() {
        var db = helper.createDatabase(TEST_DB, 11).apply {
            // Database has schema version 1. Insert some data using SQL queries.
            // You can't use DAO classes because they expect the latest schema.
            execSQL("INSERT INTO MeditationTimerProcessCategory VALUES('Breathing',1);")
            execSQL("INSERT INTO MeditationTimerProcess VALUES('Basic 1 - Arriving','5 minutes for you to slow down and arrive.','6d460abf-d622-408b-90a7-33bc296ee66b',5,5,1,'f122c0ee-9469-4fe6-a206-89f9ff299414','Basic 1 - Mindful Breathing',1,1);")

            // Prepare for the next version.
            close()
        }

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 12, true, MIGRATION_11_12)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        assertTrue(db.isDatabaseIntegrityOk)
        assertTrue(doesColumnExistInTable(db, "MeditationTimerProcess", "background_uri"))
    }

    private fun doesColumnExistInTable(
        db: SupportSQLiteDatabase,
        tableName: String,
        columnToCheck: String
    ): Boolean {
        try {
            db.query("SELECT * FROM $tableName LIMIT 0", emptyArray())
                .use { cursor -> return cursor.getColumnIndex(columnToCheck) != -1 }
        } catch (e: Exception) {
            // Something went wrong. we'll assume false it doesn't exist
            return false
        }
    }
}