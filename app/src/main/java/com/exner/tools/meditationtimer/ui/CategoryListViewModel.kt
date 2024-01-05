package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.ViewModel
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    repository: MeditationTimerDataRepository
): ViewModel() {
}