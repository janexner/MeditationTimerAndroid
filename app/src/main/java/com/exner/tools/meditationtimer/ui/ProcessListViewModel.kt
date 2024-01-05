package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataIdAndName
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProcessListViewModel @Inject constructor(
    repository: MeditationTimerDataRepository
): ViewModel() {

    private val _categoryId: MutableLiveData<Long?> = MutableLiveData(-1L)
    private val categoryId: LiveData<Long?> = _categoryId

    private val _categoryName: MutableLiveData<String?> = MutableLiveData("All")
    val categoryName: LiveData<String?> = _categoryName

    val categoryIdsAndNames: LiveData<List<MeditationTimerDataIdAndName>> =
        repository.loadIdsAndNamesForAllCategories().asLiveData()

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allProcesses: LiveData<List<MeditationTimerProcess>> =
        repository.getAllProcessesForCategory(categoryId.value ?: -1L).asLiveData()

    fun updateCategoryId(newCategoryId: Long?) {
        _categoryId.value = newCategoryId
    }

    fun updateCategoryName(newCategoryName: String?) {
        _categoryName.value = newCategoryName
    }


}