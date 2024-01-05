package com.exner.tools.meditationtimer.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataDAO
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
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
        ).fallbackToDestructiveMigration().addCallback(ProcessDatabaseCallback(provider)).build()

    class ProcessDatabaseCallback(
        private val provider: Provider<MeditationTimerDataDAO>
    ) : RoomDatabase.Callback() {

        private val applicationScope = CoroutineScope(SupervisorJob())

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            applicationScope.launch(Dispatchers.IO) {
                populateDatabaseWithSampleProcesses()
            }
        }

        private suspend fun populateDatabaseWithSampleProcesses() {
            // Add sample words.
            var meditationTimerProcess =
                MeditationTimerProcess(
                    "Test Process 1",
                    30,
                    10,
                    true,
                    2L,
                    -1L
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                MeditationTimerProcess(
                    "Test Process 2",
                    15,
                    5,
                    false,
                    0,
                    -1L
                )
            provider.get().insert(meditationTimerProcess)
        }
    }
}
