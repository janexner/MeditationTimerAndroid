package com.exner.tools.meditationtimer.ui

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
class ProcessListViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository,
) : ViewModel() {

    val observeProcessesRaw = repository.observeProcesses

    val observeCategoriesRaw = repository.observeCategories

    private val _currentCategory = MutableStateFlow(MeditationTimerProcessCategory("All", CategoryListDefinitions.CATEGORY_UID_ALL))
    val currentCategory: StateFlow<MeditationTimerProcessCategory>
        get() = _currentCategory

    fun updateCategoryId(id: Long) {
        if (id == CategoryListDefinitions.CATEGORY_UID_ALL) {
            _currentCategory.value = MeditationTimerProcessCategory("All", CategoryListDefinitions.CATEGORY_UID_ALL)
        } else {
            viewModelScope.launch {
                _currentCategory.value =
                    repository.getCategoryById(id) ?: MeditationTimerProcessCategory("None", CategoryListDefinitions.CATEGORY_UID_NONE)
            }
        }
    }
}