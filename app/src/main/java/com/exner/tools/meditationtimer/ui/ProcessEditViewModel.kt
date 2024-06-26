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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProcessEditViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository,
) : ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _info: MutableLiveData<String> = MutableLiveData("Details")
    val info: LiveData<String> = _info

    private val _processTime: MutableLiveData<Int> = MutableLiveData(30)
    val processTime: LiveData<Int> = _processTime

    private val _intervalTime: MutableLiveData<Int> = MutableLiveData(5)
    val intervalTime: LiveData<Int> = _intervalTime

    private val _hasAutoChain: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasAutoChain: LiveData<Boolean> = _hasAutoChain

    private val _gotoUuid: MutableLiveData<String?> = MutableLiveData(null)
    private val _gotoName: MutableLiveData<String?> = MutableLiveData(null)
    val gotoName: LiveData<String?> = _gotoName

    private val _uuid: MutableLiveData<String?> = MutableLiveData(null)

    private val observeProcessesRaw = repository.observeProcesses

    private val _observeProcessesForCurrentCategory =
        MutableStateFlow(emptyList<MeditationTimerProcess>())
    val observeProcessesForCurrentCategory: StateFlow<List<MeditationTimerProcess>>
        get() = _observeProcessesForCurrentCategory

    val observeCategoriesRaw = repository.observeCategories

    private val _currentCategory = MutableStateFlow(MeditationTimerProcessCategory("All", -1L))
    val currentCategory: StateFlow<MeditationTimerProcessCategory>
        get() = _currentCategory

    init {
        viewModelScope.launch {
            observeProcessesRaw.collect { itemsList ->
                val filteredItemsList: List<MeditationTimerProcess> = itemsList.filter { item ->
                    item.categoryId == currentCategory.value.uid || currentCategory.value.uid == -1L
                }
                _observeProcessesForCurrentCategory.value = filteredItemsList
            }
        }
    }

    fun updateCategoryId(id: Long, filterProcessesForCurrentCategory: Boolean) {
        if (id == -1L) {
            _currentCategory.value = MeditationTimerProcessCategory("All", -1L)
        } else {
            viewModelScope.launch {
                _currentCategory.value =
                    repository.getCategoryById(id) ?: MeditationTimerProcessCategory("All", -1L)
            }
        }
        viewModelScope.launch {
            observeProcessesRaw.collect { itemsList ->
                val filteredItemsList: List<MeditationTimerProcess> = itemsList.filter { item ->
                    if (currentCategory.value.uid == -1L || !filterProcessesForCurrentCategory) {
                        true
                    } else {
                        item.categoryId == currentCategory.value.uid
                    }
                }
                _observeProcessesForCurrentCategory.value = filteredItemsList
            }
        }
    }

    fun getProcess(processUuid: String?, filterProcessesForCurrentCategory: Boolean) {
        if (processUuid != null) {
            _uuid.value = processUuid
            viewModelScope.launch {
                val process = repository.loadProcessByUuid(processUuid)
                if (process != null) {
                    _uid.value = process.uid
                    _name.value = process.name
                    _info.value = process.info
                    _processTime.value = process.processTime
                    _intervalTime.value = process.intervalTime
                    _hasAutoChain.value = process.hasAutoChain
                    _gotoUuid.value = process.gotoUuid
                    _gotoName.value = process.gotoName
                    updateCategoryId(process.categoryId ?: -1L, filterProcessesForCurrentCategory)
                    _uuid.value = process.uuid
                }
            }
        }
    }

    fun commitProcess() {
        viewModelScope.launch {
            val process = MeditationTimerProcess(
                uid = uid.value!!.toLong(),
                name = name.value.toString(),
                info = info.value.toString(),
                processTime = if (processTime.value != null) processTime.value!!.toInt() else 30,
                intervalTime = if (intervalTime.value != null) intervalTime.value!!.toInt() else 10,
                hasAutoChain = hasAutoChain.value == true,
                gotoUuid = _gotoUuid.value,
                gotoName = _gotoName.value,
                categoryId = currentCategory.value.uid,
                uuid = if (_uuid.value != null) _uuid.value!! else UUID.randomUUID().toString()
            )
            if (!repository.doesProcessWithUuidExist(uuid = process.uuid)) {
                repository.insert(
                    process.copy(
                        uid = 0
                    )
                )
            } else {
                repository.update(process)
            }
        }
    }

    fun updateName(name: String) {
        _name.value = name
    }

    fun updateInfo(info: String) {
        _info.value = info
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

    fun updateGotoUuidAndName(gotoUuid: String?, name: String?) {
        _gotoUuid.value = gotoUuid
        _gotoName.value = name
    }

    fun createNewCategory(newCategoryName: String) {
        viewModelScope.launch {
            val newCategory = MeditationTimerProcessCategory(newCategoryName, 0)
            repository.insertCategory(newCategory)
        }
    }
}
