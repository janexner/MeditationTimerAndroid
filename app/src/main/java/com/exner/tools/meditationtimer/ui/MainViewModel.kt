package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.preferences.MeditationTimerUserPreferencesManager
import com.exner.tools.meditationtimer.ui.theme.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferencesManager: MeditationTimerUserPreferencesManager
) : ViewModel() {

    val userSelectedTheme: StateFlow<Theme> = userPreferencesManager.theme().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = Theme.Auto
    )

    val enableExportToActivityTimer: StateFlow<Boolean> = userPreferencesManager.enableExportToActivityTimer().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = false
    )
}