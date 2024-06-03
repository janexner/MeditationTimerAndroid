package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.preferences.MeditationTimerUserPreferencesManager
import com.exner.tools.meditationtimer.ui.theme.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: MeditationTimerUserPreferencesManager
) : ViewModel() {

    val userSelectedTheme: StateFlow<Theme> = userPreferencesManager.theme().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Theme.Auto
    )
    val countBackwards: StateFlow<Boolean> = userPreferencesManager.countBackwards().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val chainToSameCategoryOnly: StateFlow<Boolean> = userPreferencesManager.chainToSameCategoryOnly().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val noSounds: StateFlow<Boolean> = userPreferencesManager.noSounds().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val vibrateEnabled: StateFlow<Boolean> = userPreferencesManager.vibrateEnabled().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val showSimpleDisplay: StateFlow<Boolean> = userPreferencesManager.showSimpleDisplay().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        true
    )

    fun updateUserSelectedTheme(newTheme: Theme) {
        viewModelScope.launch {
            userPreferencesManager.setTheme(newTheme)
        }
    }

    fun updateCountBackwards(newCountBackwards: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setCountBackwards(newCountBackwards)
        }
    }

    fun updateChainToSameCategoryOnly(newChainToSameOnly: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setChainToSameCategoryOnly(newChainToSameOnly)
        }
    }

    fun updateNoSounds(newNoSounds: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setNoSounds(newNoSounds)
        }
    }

    fun updateVibrateEnabled(newVibrateEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setVibrateEnabled(newVibrateEnabled)
        }
    }

    fun updateShowSimpleDisplay(newSimpleDisplay: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setShowSimpleDisplay(newSimpleDisplay)
        }
    }
}