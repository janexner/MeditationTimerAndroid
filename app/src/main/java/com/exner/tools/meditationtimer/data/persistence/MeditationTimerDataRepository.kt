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

    val observeCategories: Flow<List<MeditationTimerProcessCategory>> =
        meditationTimerProcessDAO.observeCategoriesAlphabeticallyOrdered()

    val observeCategoryUsageCount: Flow<List<MeditationTimerCategoryIdNameCount>> =
        meditationTimerProcessDAO.observeCategoryUsageCount()

    @WorkerThread
    suspend fun loadProcessById(id: Long): MeditationTimerProcess? {
        return meditationTimerProcessDAO.getMeditationTimerProcess(id)
    }

    @WorkerThread
    suspend fun getIdsAndNamesOfDependentProcesses(fotoTimerProcess: MeditationTimerProcess): List<MeditationTimerDataIdAndName> {
        return meditationTimerProcessDAO.getIdsAndNamesOfDependantProcesses(fotoTimerProcess.uid)
    }

    @WorkerThread
    suspend fun getIdsAndNamesOfAllCategories(): List<MeditationTimerDataIdAndName> {
        return meditationTimerProcessDAO.getIdsAndNamesOfAllCategories()
    }

    @WorkerThread
    suspend fun doesProcessWithIdExist(id: Long): Boolean {
        return (meditationTimerProcessDAO.getMeditationTimerProcess(id) !== null)
    }

    suspend fun getCategoryById(id: Long): MeditationTimerProcessCategory {
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
}