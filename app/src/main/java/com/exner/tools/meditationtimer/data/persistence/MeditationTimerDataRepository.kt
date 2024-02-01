package com.exner.tools.meditationtimer.data.persistence

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MeditationTimerDataRepository @Inject constructor(private val meditationTimerProcessDAO: MeditationTimerDataDAO) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val observeProcesses: Flow<List<MeditationTimerProcess>> =
        meditationTimerProcessDAO.observeProcessesAlphabeticallyOrdered()

    val observeFirstProcesses: Flow<List<MeditationTimerProcess>> =
        meditationTimerProcessDAO.observeFirstProcessesAlphabeticallyOrdered()

    val observeCategories: Flow<List<MeditationTimerProcessCategory>> =
        meditationTimerProcessDAO.observeCategoriesAlphabeticallyOrdered()

    val observeCategoryUsageCount: Flow<List<MeditationTimerCategoryIdNameCount>> =
        meditationTimerProcessDAO.observeCategoryUsageCount()

    @WorkerThread
    suspend fun loadProcessByUuid(uuid: String): MeditationTimerProcess? {
        return meditationTimerProcessDAO.getMeditationTimerProcessByUuid(uuid)
    }

    @WorkerThread
    suspend fun getUuidsOfDependentProcesses(fotoTimerProcess: MeditationTimerProcess): List<String> {
        return meditationTimerProcessDAO.getUuidsOfDependantProcesses(fotoTimerProcess.uuid)
    }

    @WorkerThread
    suspend fun doesProcessWithUuidExist(uuid: String): Boolean {
        return (meditationTimerProcessDAO.getMeditationTimerProcessByUuid(uuid) !== null)
    }

    @WorkerThread
    suspend fun getCategoryById(id: Long): MeditationTimerProcessCategory? {
        return meditationTimerProcessDAO.getCategoryById(id)
    }

    @WorkerThread
    suspend fun insert(fotoTimerProcess: MeditationTimerProcess) {
        meditationTimerProcessDAO.insert(fotoTimerProcess)
    }

    @WorkerThread
    suspend fun update(fotoTimerProcess: MeditationTimerProcess) {
        meditationTimerProcessDAO.update(fotoTimerProcess)
    }

    @WorkerThread
    suspend fun delete(fotoTimerProcess: MeditationTimerProcess) {
        meditationTimerProcessDAO.delete(fotoTimerProcess)
    }

    @WorkerThread
    suspend fun insertCategory(category: MeditationTimerProcessCategory) {
        meditationTimerProcessDAO.insertCategory(category)
    }

    @WorkerThread
    suspend fun updateCategory(category: MeditationTimerProcessCategory) {
        meditationTimerProcessDAO.updateCategory(category)
    }

    @WorkerThread
    suspend fun deleteCategoriesByIdsFromList(listOfIds: List<Long>) {
        if (listOfIds.isNotEmpty()) {
            meditationTimerProcessDAO.deleteCategoriesByIdsFromList(listOfIds)
        }
    }
}