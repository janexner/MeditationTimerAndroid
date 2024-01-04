package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessIdAndName
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessEditViewModel @Inject constructor(
    private val repository: MeditationTimerProcessRepository,
): ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _processTime: MutableLiveData<Int> = MutableLiveData(30)
    val processTime: LiveData<Int> = _processTime

    private val _intervalTime: MutableLiveData<Int> = MutableLiveData(10)
    val intervalTime: LiveData<Int> = _intervalTime

    private val _hasAutoChain: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasAutoChain: LiveData<Boolean> = _hasAutoChain

    private val _gotoId: MutableLiveData<Long?> = MutableLiveData(-1L)

    private val _nextProcessesName: MutableLiveData<String?> = MutableLiveData("")
    val nextProcessesName: LiveData<String?> = _nextProcessesName

    private val _processIdsAndNames: MutableLiveData<List<MeditationTimerProcessIdAndName>> = MutableLiveData(
        emptyList()
    )
    val processIdsAndNames: LiveData<List<MeditationTimerProcessIdAndName>> = _processIdsAndNames

    fun getProcess(processId: Long) {
        if (processId != -1L) {
            _uid.value = processId
            viewModelScope.launch {
                val process = repository.loadProcessById(processId)
                if (process != null) {
                    _name.value = process.name
                    _processTime.value = process.processTime
                    _intervalTime.value = process.intervalTime
                    _hasAutoChain.value = process.hasAutoChain
                    _gotoId.value = process.gotoId ?: -1L
                    if (process.gotoId != null && process.gotoId != -1L) {
                        val nextProcess = repository.loadProcessById(process.gotoId)
                        if (nextProcess != null) {
                            _nextProcessesName.value = nextProcess.name
                        }
                    }
                }
            }
        }
    }

    fun getProcessIdsAndNames() {
        viewModelScope.launch {
            val temp = repository.loadIdsAndNamesForAllProcesses()
            _processIdsAndNames.value = temp
        }
    }

    fun commitProcess() {
        if (_uid.value != null) {
            viewModelScope.launch {
                val process = MeditationTimerProcess(
                    uid = _uid.value!!.toLong(),
                    name = _name.value.toString(),
                    processTime = if (_processTime.value != null) _processTime.value!!.toInt() else 30,
                    intervalTime = if (_intervalTime.value != null) _intervalTime.value!!.toInt() else 10,
                    hasAutoChain =  _hasAutoChain.value == true,
                    gotoId = if (_gotoId.value != null) _gotoId.value!!.toLong() else null,
                )
                if (_uid.value == -1L) {
                    repository.insert(process.copy(
                        uid = 0
                    ))
                } else {
                    repository.update(process)
                }
            }
        }
    }

    fun updateName(name: String) {
        _name.value = name
    }

    fun updateProcessTime(processTime: Int) {
        _processTime.value = processTime
    }

    fun updateIntervalTime(intervalTime: Int) {
        _intervalTime.value = intervalTime
    }

    fun updateHasAutoChain(hasAutoChain: Boolean) {
        _hasAutoChain.value = hasAutoChain
    }

    fun updateGotoId(gotoId: Long?) {
        _gotoId.value = gotoId
    }

    fun updateNextProcessesName(nextProcessesName: String?) {
        _nextProcessesName.value = nextProcessesName
    }
}