package com.exner.tools.meditationtimer.data.di

import android.content.Context
import androidx.room.Room
import com.exner.tools.meditationtimer.data.persistence.MIGRATION_11_12
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataDAO
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        ).createFromAsset("database/seed.db")
            .addMigrations(MIGRATION_11_12)
            .fallbackToDestructiveMigration()
            .build()

}
