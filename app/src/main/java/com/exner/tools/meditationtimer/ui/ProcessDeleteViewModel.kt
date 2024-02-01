package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerChainingDependencies
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataIdAndName
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

    fun checkProcess(processUuid: String?) {
        if (processUuid != null) {
            viewModelScope.launch {
                val process = repository.loadProcessByUuid(processUuid)
                if (process != null) {
                    _processName.value = process.name
                    val newDependentProcessUuids = repository.getUuidsOfDependentProcesses(process)
                    val newDependentProcesses = mutableListOf<MeditationTimerDataIdAndName>()
                    newDependentProcessUuids.forEach {
                        val tmpProcess = repository.loadProcessByUuid(it)
                        if (tmpProcess != null) {
                            newDependentProcesses.add(MeditationTimerDataIdAndName(it, tmpProcess.name))
                        }
                    }
                    val chainingDependencies = MeditationTimerChainingDependencies(
                        newDependentProcesses
                    )
                    _processChainingDependencies.value = chainingDependencies
                    _processIsTarget.value = true
                }
            }
        }
    }

    fun deleteProcess(processUuid: String) {
        viewModelScope.launch {
            val ftp = repository.loadProcessByUuid(processUuid)
            if (ftp != null) {
                repository.delete(ftp)
            }
        }
    }
}