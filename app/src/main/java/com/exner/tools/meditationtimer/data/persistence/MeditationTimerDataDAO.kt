package com.exner.tools.meditationtimer.data.persistence

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationTimerDataDAO {
    @Query("SELECT * FROM meditationtimerprocess ORDER BY name ASC")
    fun observeProcessesAlphabeticallyOrdered(): Flow<List<MeditationTimerProcess>>

    @Query("SELECT * FROM meditationtimerprocess WHERE uid NOT IN (SELECT goto_uuid FROM meditationtimerprocess WHERE goto_uuid > 0) ORDER BY name ASC")
    fun observeFirstProcessesAlphabeticallyOrdered(): Flow<List<MeditationTimerProcess>>

    @Query("SELECT * FROM meditationtimerprocess WHERE category_id IN (:categoryId) ORDER BY name ASC")
    fun observeProcessesForCategoryAlphabeticallyOrdered(categoryId: Long): Flow<List<MeditationTimerProcess>>

    @Query("SELECT uuid, name FROM meditationtimerprocess ORDER BY name ASC")
    fun observeIdsAndNamesOfAllProcesses(): Flow<List<MeditationTimerDataIdAndName>>

    @Query("SELECT * FROM meditationtimerprocesscategory ORDER BY name ASC")
    fun observeCategoriesAlphabeticallyOrdered(): Flow<List<MeditationTimerProcessCategory>>

    @Query("SELECT meditationtimerprocesscategory.uid, meditationtimerprocesscategory.name, COUNT(meditationtimerprocess.uid) AS usageCount FROM meditationtimerprocesscategory LEFT JOIN meditationtimerprocess ON meditationtimerprocess.category_id = meditationtimerprocesscategory.uid")
    fun observeCategoryUsageCount(): Flow<List<MeditationTimerCategoryIdNameCount>>

    @Query("SELECT uuid FROM meditationtimerprocess WHERE goto_uuid=:uuid ORDER BY name ASC")
    suspend fun getUuidsOfDependantProcesses(uuid: String): List<String>

    @Query("SELECT * FROM meditationtimerprocess WHERE uid=:id")
    suspend fun getMeditationTimerProcess(id : Long): MeditationTimerProcess?

    @Query("SELECT * FROM meditationtimerprocess WHERE uuid=:uuid")
    suspend fun getMeditationTimerProcessByUuid(uuid: String): MeditationTimerProcess?

    @Query("SELECT name FROM meditationtimerprocess WHERE uid=:id")
    suspend fun getProcessNameById(id: Long): String?

    @Query("SELECT count(uid) FROM meditationtimerprocess")
    suspend fun getNumberOfProcesses(): Int

    @Query("SELECT * FROM meditationtimerprocesscategory WHERE uid=:id")
    suspend fun getCategoryById(id: Long): MeditationTimerProcessCategory?

    @Query("DELETE FROM meditationtimerprocesscategory WHERE uid IN (:listOfIds)")
    suspend fun deleteCategoriesByIdsFromList(listOfIds: List<Long>)

    @Insert
    suspend fun insert(fotoTimerProcess: MeditationTimerProcess)

    @Update
    suspend fun update(fotoTimerProcess: MeditationTimerProcess)

    @Delete
    suspend fun delete(fotoTimerProcess: MeditationTimerProcess)

    @Insert
    suspend fun insertCategory(category: MeditationTimerProcessCategory)

    @Update
    suspend fun updateCategory(category: MeditationTimerProcessCategory)

    @Delete
    suspend fun deleteCategory(category: MeditationTimerProcessCategory)
}

// see https://developer.android.com/training/data-storage/room#kts
// see https://developer.android.com/training/data-storage/room/async-queries
// see https://stackoverflow.com/questions/46935262/get-item-by-id-in-room