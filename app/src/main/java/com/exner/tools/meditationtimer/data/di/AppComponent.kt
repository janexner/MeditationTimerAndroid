package com.exner.tools.meditationtimer.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataDAO
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppComponent {

    @Singleton
    @Provides
    fun provideDao(ftDatabase: MeditationTimerRoomDatabase): MeditationTimerDataDAO =
        ftDatabase.processDAO()

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        provider: Provider<MeditationTimerDataDAO>
    ): MeditationTimerRoomDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            MeditationTimerRoomDatabase::class.java,
            "meditation_timer_process_database"
        ).addMigrations(MIGRATION_11_12).fallbackToDestructiveMigration()
            .addCallback(ProcessDatabaseCallback(provider)).build()

    private val MIGRATION_11_12 = object : Migration(11, 12) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE MeditationTimerProcess ADD COLUMN background_uri TEXT DEFAULT 'https://fototimer.net/assets/activitytimer/bg-none.png';")
        }
    }

    class ProcessDatabaseCallback(
        private val provider: Provider<MeditationTimerDataDAO>
    ) : RoomDatabase.Callback() {

        private val applicationScope = CoroutineScope(SupervisorJob())

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            applicationScope.launch(Dispatchers.IO) {
                populateDatabaseWithSampleData()
            }
        }

        private suspend fun populateDatabaseWithSampleData() {
            // Add sample words.
            val secondUuid = UUID.randomUUID().toString()
            var meditationTimerProcess =
                MeditationTimerProcess(
                    name = "Basic 1 - Arriving",
                    info = "5 minutes for you to slow down and arrive.",
                    categoryId = 1L,
                    uuid = UUID.randomUUID().toString(),
                    processTime = 5,
                    intervalTime = 5,
                    hasAutoChain = true,
                    gotoUuid = secondUuid,
                    gotoName = "Basic 1 - Mindful Breathing",
                    backgroundUri = "https://fototimer.net/assets/activitytimer/bg-default.png",
                    uid = 0L,
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                MeditationTimerProcess(
                    name = "Basic 1 - Mindful Breathing",
                    info = "15 minutes for mindful breathing.",
                    categoryId = 1L,
                    uuid = secondUuid,
                    processTime = 15,
                    intervalTime = 15,
                    hasAutoChain = false,
                    gotoUuid = null,
                    gotoName = null,
                    backgroundUri = "https://fototimer.net/assets/activitytimer/bg-default.png",
                    uid = 0L,
                )
            provider.get().insert(meditationTimerProcess)
            val demoCategory = MeditationTimerProcessCategory(
                name = "Breathing",
                uid = 0L
            )
            provider.get().insertCategory(demoCategory)
        }
    }
}
