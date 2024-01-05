package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataIdAndName
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessEditViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository,
): ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    val process: LiveData<MeditationTimerProcess> =
        repository.processById(uid.value ?: -1).asLiveData()

    private val _nextProcessesName: MutableLiveData<String?> = MutableLiveData("")
    val nextProcessesName: LiveData<String?> = _nextProcessesName

    private val _categoryId: MutableLiveData<Long?> = MutableLiveData(-1L)
    private val categoryId: LiveData<Long?> = _categoryId

    private val _categoryName: MutableLiveData<String?> = MutableLiveData("None")
    val categoryName: LiveData<String?> = _categoryName

    val processIdsAndNames: LiveData<List<MeditationTimerDataIdAndName>> =
        repository.loadIdsAndNamesForAllProcesses().asLiveData()

    val categoryIdsAndNames: LiveData<List<MeditationTimerDataIdAndName>> =
        repository.loadIdsAndNamesForAllCategories().asLiveData()

    fun commitProcess() {
        if (uid.value != null && process.value !== null) {
            viewModelScope.launch {
                val tempProcess = process.value
                if (uid.value == -1L) {
                    repository.insert(tempProcess!!.copy(
                        uid = 0
                    ))
                } else {
                    repository.update(tempProcess!!)
                }
            }
        }
    }

    fun updateUid(newUid: Long) {
        _uid.value = newUid
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

    fun updateCategoryId(newCategoryId: Long?) {
        _categoryId.value = newCategoryId
    }

    fun updateCategoryName(newCategoryName: String?) {
        _categoryName.value = newCategoryName
    }

    fun createNewCategory(newCategoryName: String) {
        viewModelScope.launch {
            val newCategory = MeditationTimerProcessCategory(newCategoryName, 0)
            repository.insertCategory(newCategory)
        }
    }
}