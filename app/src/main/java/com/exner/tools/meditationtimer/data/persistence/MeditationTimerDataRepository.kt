package com.exner.tools.meditationtimer.data.persistence

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MeditationTimerDataRepository @Inject constructor(private val meditationTimerProcessDAO: MeditationTimerDataDAO) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allProcesses: Flow<List<MeditationTimerProcess>> =
        meditationTimerProcessDAO.getAllAlphabeticallyOrdered()

    @WorkerThread
    fun getAllProcessesForCategory(categoryId: Long): Flow<List<MeditationTimerProcess>> =
        if (categoryId == -1L) {
            meditationTimerProcessDAO.getAllAlphabeticallyOrdered()
        } else {
            meditationTimerProcessDAO.getAllForCategoryAlphabeticallyOrdered(categoryId)
        }

    @WorkerThread
    fun processById(id: Long): Flow<MeditationTimerProcess> =
        meditationTimerProcessDAO.processById(id)

    @WorkerThread
    suspend fun loadProcessById(id: Long): MeditationTimerProcess? {
        return meditationTimerProcessDAO.getMeditationTimerProcess(id)
    }

    @WorkerThread
    fun loadIdsAndNamesForAllProcesses(): Flow<List<MeditationTimerDataIdAndName>> {
        return meditationTimerProcessDAO.getIdsAndNamesOfAllProcesses()
    }

    @WorkerThread
    fun loadIdsAndNamesForAllCategories(): Flow<List<MeditationTimerDataIdAndName>> {
        return meditationTimerProcessDAO.getIdsAndNamesOfAllCategories()
    }

    @WorkerThread
    suspend fun getIdsAndNamesOfDependentProcesses(fotoTimerProcess: MeditationTimerProcess): List<MeditationTimerDataIdAndName> {
        return meditationTimerProcessDAO.getIdsAndNamesOfDependantProcesses(fotoTimerProcess.uid)
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
}