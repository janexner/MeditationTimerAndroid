package com.exner.tools.meditationtimer.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
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
        ).fallbackToDestructiveMigration().addCallback(ProcessDatabaseCallback(provider)).build()

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
                    name = "Test Process 1",
                    info = "A test process that runs for 30 minutes, then leads directly into 'Test Process 2'",
                    categoryId = null,
                    uuid = UUID.randomUUID().toString(),
                    processTime = 30,
                    intervalTime = 10,
                    hasAutoChain = true,
                    gotoUuid = secondUuid,
                    gotoName = "Test Process 2",
                    uid = 0L,
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                MeditationTimerProcess(
                    name = "Test Process 2",
                    info = "Test process that is launched by 'Test Process 2'. It runs for 15 minutes.",
                    categoryId = null,
                    uuid = secondUuid,
                    processTime = 15,
                    intervalTime = 5,
                    hasAutoChain = false,
                    gotoUuid = null,
                    gotoName = null,
                    uid = 0L,
                )
            provider.get().insert(meditationTimerProcess)
            val demoCategory = MeditationTimerProcessCategory(
                name = "Category 1",
                uid = 0
            )
            provider.get().insertCategory(demoCategory)
        }
    }
}
