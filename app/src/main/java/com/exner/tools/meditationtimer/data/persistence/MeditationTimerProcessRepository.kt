package com.exner.tools.meditationtimer.data.persistence

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MeditationTimerProcessRepository @Inject constructor(private val meditationTimerProcessDAO: MeditationTimerProcessDAO) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allProcesses: Flow<List<MeditationTimerProcess>> =
        meditationTimerProcessDAO.getAllAlphabeticallyOrdered()

    @WorkerThread
    suspend fun loadProcessById(id: Long): MeditationTimerProcess? {
        return meditationTimerProcessDAO.getFotoTimerProcess(id)
    }

    @WorkerThread
    suspend fun loadIdsAndNamesForAllProcesses(): List<MeditationTimerProcessIdAndName> {
        return meditationTimerProcessDAO.getIdsAndNamesOfAllProcesses()
    }

    @WorkerThread
    suspend fun getIdsAndNamesOfDependentProcesses(fotoTimerProcess: MeditationTimerProcess): List<MeditationTimerProcessIdAndName> {
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
}