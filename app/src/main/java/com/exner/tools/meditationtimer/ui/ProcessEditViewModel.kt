package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessEditViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository,
): ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _processTime: MutableLiveData<Int> = MutableLiveData(30)
    val processTime: LiveData<Int> = _processTime

    private val _intervalTime: MutableLiveData<Int> = MutableLiveData(5)
    val intervalTime: LiveData<Int> = _intervalTime

    private val _hasAutoChain: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasAutoChain: LiveData<Boolean> = _hasAutoChain

    private val _gotoId: MutableLiveData<Long?> = MutableLiveData(-1L)

    private val _nextProcessesName: MutableLiveData<String?> = MutableLiveData("")
    val nextProcessesName: LiveData<String?> = _nextProcessesName

    private val observeProcessesRaw = repository.observeProcesses

    private val _observeProcessesForCurrentCategory = MutableStateFlow(emptyList<MeditationTimerProcess>())
    val observeProcessesForCurrentCategory: StateFlow<List<MeditationTimerProcess>>
        get() = _observeProcessesForCurrentCategory

    val observeCategoriesRaw = repository.observeCategories

    private val _currentCategory = MutableStateFlow(MeditationTimerProcessCategory("All", -1L))
    val currentCategory: StateFlow<MeditationTimerProcessCategory>
        get() = _currentCategory

    init {
        viewModelScope.launch {
            observeProcessesRaw.collect {itemsList ->
                val filteredItemsList: List<MeditationTimerProcess> = itemsList.filter { item ->
                    item.categoryId == currentCategory.value.uid || currentCategory.value.uid == -1L
                }
                _observeProcessesForCurrentCategory.value = filteredItemsList
            }
        }
    }

    fun updateCategoryId(id: Long) {
        if (id == -1L) {
            _currentCategory.value = MeditationTimerProcessCategory("All", -1L)
        } else {
            viewModelScope.launch {
                _currentCategory.value = repository.getCategoryById(id)
            }
        }
        viewModelScope.launch {
            observeProcessesRaw.collect {itemsList ->
                val filteredItemsList: List<MeditationTimerProcess> = itemsList.filter { item ->
                    if (currentCategory.value.uid == -1L) {
                        true
                    } else {
                        item.categoryId == currentCategory.value.uid
                    }
                }
                _observeProcessesForCurrentCategory.value = filteredItemsList
            }
        }
    }

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
                    updateCategoryId(process.categoryId ?: -1L)
                }
            }
        }
    }

    fun commitProcess() {
        if (uid.value != null) {
            viewModelScope.launch {
                val process = MeditationTimerProcess(
                    uid = uid.value!!.toLong(),
                    name = name.value.toString(),
                    processTime = if (processTime.value != null) processTime.value!!.toInt() else 30,
                    intervalTime = if (intervalTime.value != null) intervalTime.value!!.toInt() else 10,
                    hasAutoChain =  hasAutoChain.value == true,
                    gotoId = if (_gotoId.value != null) _gotoId.value!!.toLong() else null,
                    categoryId = currentCategory.value.uid
                )
                if (uid.value == -1L) {
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

    fun createNewCategory(newCategoryName: String) {
        viewModelScope.launch {
            val newCategory = MeditationTimerProcessCategory(newCategoryName, 0)
            repository.insertCategory(newCategory)
        }
    }
}