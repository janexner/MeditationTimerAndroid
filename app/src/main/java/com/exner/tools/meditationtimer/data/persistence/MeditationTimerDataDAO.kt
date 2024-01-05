package com.exner.tools.meditationtimer.data.persistence

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationTimerDataDAO {
    @Query("SELECT * FROM meditationtimerprocess")
    fun getAll(): Flow<List<MeditationTimerProcess>>

    @Query("SELECT * FROM meditationtimerprocess ORDER BY name ASC")
    fun getAllAlphabeticallyOrdered(): Flow<List<MeditationTimerProcess>>

    @Query("SELECT * FROM meditationtimerprocess WHERE category_id IN (:categoryId) ORDER BY name ASC")
    fun getAllForCategoryAlphabeticallyOrdered(categoryId: Long): Flow<List<MeditationTimerProcess>>

    @Query("SELECT uid, name FROM meditationtimerprocess ORDER BY name ASC")
    suspend fun getIdsAndNamesOfAllProcesses(): List<MeditationTimerDataIdAndName>

    @Query("SELECT uid, name FROM meditationtimerprocesscategory ORDER BY name ASC")
    suspend fun getIdsAndNamesOfAllCategories(): List<MeditationTimerDataIdAndName>

    @Query("SELECT uid, name FROM meditationtimerprocess WHERE goto_id=:id ORDER BY name ASC")
    suspend fun getIdsAndNamesOfDependantProcesses(id: Long): List<MeditationTimerDataIdAndName>

    @Query("SELECT * FROM meditationtimerprocess WHERE uid=:id")
    suspend fun getMeditationTimerProcess(id : Long): MeditationTimerProcess?

    @Query("SELECT name FROM meditationtimerprocess WHERE uid=:id")
    suspend fun getProcessNameById(id: Long): String

    @Query("SELECT count(uid) FROM meditationtimerprocess")
    suspend fun getNumberOfProcesses(): Int

    @Insert
    suspend fun insert(fotoTimerProcess: MeditationTimerProcess)

    @Update
    suspend fun update(fotoTimerProcess: MeditationTimerProcess)

    @Delete
    suspend fun delete(fotoTimerProcess: MeditationTimerProcess)

    @Insert
    suspend fun insertCategory(category: MeditationTimerProcessCategory)
}

// see https://developer.android.com/training/data-storage/room#kts
// see https://developer.android.com/training/data-storage/room/async-queries
// see https://stackoverflow.com/questions/46935262/get-item-by-id-in-room