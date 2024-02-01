package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessDetailsViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository
): ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _uuid: MutableLiveData<String> = MutableLiveData("")
    val uuid: LiveData<String> = _uuid

    private val _processTime: MutableLiveData<String> = MutableLiveData("30")
    val processTime: LiveData<String> = _processTime

    private val _intervalTime: MutableLiveData<String> = MutableLiveData("10")
    val intervalTime: LiveData<String> = _intervalTime

    private val _hasAutoChain: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasAutoChain: LiveData<Boolean> = _hasAutoChain

    private val _gotoUuid: MutableLiveData<String?> = MutableLiveData(null)
    val gotoUuid: LiveData<String?> = _gotoUuid
    private val _gotoName: MutableLiveData<String?> = MutableLiveData(null)
    val gotoName: LiveData<String?> = _gotoName

    private val _currentCategory = MutableStateFlow(MeditationTimerProcessCategory("None", -1L))
    val currentCategory: StateFlow<MeditationTimerProcessCategory>
        get() = _currentCategory

    private fun updateCategoryId(id: Long) {
        if (id == -1L) {
            _currentCategory.value = MeditationTimerProcessCategory("None", -1L)
        } else {
            viewModelScope.launch {
                _currentCategory.value = repository.getCategoryById(id) ?: MeditationTimerProcessCategory("None", -1L)
            }
        }
    }

    fun getProcess(processUuid: String) {
        _uuid.value = processUuid
        viewModelScope.launch {
            val process = repository.loadProcessByUuid(processUuid)
            if (process != null) {
                _name.value = process.name
                _processTime.value = process.processTime.toString()
                _intervalTime.value = process.intervalTime.toString()
                _hasAutoChain.value = process.hasAutoChain
                _gotoUuid.value = process.gotoUuid
                _gotoName.value = process.gotoName
                if (process.gotoUuid != null && process.gotoUuid != "") {
                    val nextProcess = repository.loadProcessByUuid(process.gotoUuid)
                    if (nextProcess != null) {
                        if (_gotoName.value != nextProcess.name) {
                            // this is weird!
                            _gotoName.value = nextProcess.name
                        }
                    }
                }
                updateCategoryId(process.categoryId ?: -1L)
            }
        }
    }
}