package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository
): ViewModel() {

    val observeCategoriesRaw = repository.observeCategories

    val observeCategoryUsage = repository.observeCategoryUsageCount

    fun createNewCategory(newCategoryName: String) {
        viewModelScope.launch {
            val newCategory = MeditationTimerProcessCategory(newCategoryName, 0)
            repository.insertCategory(newCategory)
        }
    }
}