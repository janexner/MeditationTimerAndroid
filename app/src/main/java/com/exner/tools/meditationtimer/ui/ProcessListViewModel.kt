package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import com.exner.tools.meditationtimer.data.preferences.MeditationTimerUserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessListViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository,
    private val userPreferencesRepository: MeditationTimerUserPreferencesManager
) : ViewModel() {

    private val observeProcessesRaw = repository.observeProcesses
    private val observeFirstProcessesRaw = repository.observeFirstProcesses

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
            userPreferencesRepository.onlyShowFirstInChain().collect { onlyShowFirstInChain ->
                if (onlyShowFirstInChain) {
                    observeFirstProcessesRaw.collect { itemsList ->
                        val filteredItemsList: List<MeditationTimerProcess> =
                            itemsList.filter { item ->
                                item.categoryId == currentCategory.value.uid || currentCategory.value.uid == -1L
                            }
                        _observeProcessesForCurrentCategory.value = filteredItemsList
                    }
                } else {
                    observeProcessesRaw.collect { itemsList ->
                        val filteredItemsList: List<MeditationTimerProcess> =
                            itemsList.filter { item ->
                                item.categoryId == currentCategory.value.uid || currentCategory.value.uid == -1L
                            }
                        _observeProcessesForCurrentCategory.value = filteredItemsList
                    }
                }
            }
        }
    }

    fun updateCategoryId(id: Long) {
        if (id == -2L) {
            _currentCategory.value = MeditationTimerProcessCategory("All", -2L)
        } else {
            viewModelScope.launch {
                _currentCategory.value =
                    repository.getCategoryById(id) ?: MeditationTimerProcessCategory("None", -1L)
            }
        }
        viewModelScope.launch {
            observeProcessesRaw.collect { itemsList ->
                val filteredItemsList: List<MeditationTimerProcess> = itemsList.filter { item ->
                    if (currentCategory.value.uid == -2L) {
                        true
                    } else {
                        item.categoryId == currentCategory.value.uid
                    }
                }
                _observeProcessesForCurrentCategory.value = filteredItemsList
            }
        }
    }

}