package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerChainingDependencies
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessDeleteViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository
): ViewModel() {

    private val _processName: MutableLiveData<String> = MutableLiveData("")
    val processName: LiveData<String> = _processName

    private val _processIsTarget: MutableLiveData<Boolean> = MutableLiveData(false)
    val processIsTarget: LiveData<Boolean> = _processIsTarget

    private val _processChainingDependencies: MutableLiveData<MeditationTimerChainingDependencies> = MutableLiveData(null)
    val processChainingDependencies: LiveData<MeditationTimerChainingDependencies> = _processChainingDependencies

    fun checkProcess(processId: Long) {
        if (processId != -1L) {
            viewModelScope.launch {
                val process = repository.loadProcessById(processId)
                if (process != null) {
                    _processName.value = process.name
                    val newDependentProcesses = repository.getIdsAndNamesOfDependentProcesses(process)
                    val chainingDependencies = MeditationTimerChainingDependencies(true, newDependentProcesses)
                    _processChainingDependencies.value = chainingDependencies
                    _processIsTarget.value = true
                }
            }
        }
    }

    fun deleteProcess(processId: Long) {
        viewModelScope.launch {
            val ftp = repository.loadProcessById(processId)
            if (ftp != null) {
                repository.delete(ftp)
            }
        }
    }

}