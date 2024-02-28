package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.preferences.MeditationTimerUserPreferencesManager
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

    val nightMode: StateFlow<Boolean> = userPreferencesManager.nightMode().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val beforeCountingWait: StateFlow<Boolean> = userPreferencesManager.beforeCountingWait().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )
    val howLongToWaitBeforeCounting: StateFlow<Int> = userPreferencesManager.howLongToWaitBeforeCounting().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        5
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
    val onlyShowFirstInChain: StateFlow<Boolean> = userPreferencesManager.onlyShowFirstInChain().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val importAndUploadRestOfChainAutomatically: StateFlow<Boolean> = userPreferencesManager.importAndUploadRestOfChainAutomatically().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )

    fun updateNightMode(newNightMode: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setNightMode(newNightMode)
        }
    }

    fun updateBeforeCountingWait(newBeforeCountingWait: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setBeforeCountingWait(newBeforeCountingWait)
        }
    }

    fun updateHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting: Int) {
        viewModelScope.launch {
            userPreferencesManager.setHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting)
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

    fun updateOnlyShowFirstInChain(newFirstOnly: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setOnlyShowFirstInChain(newFirstOnly)
        }
    }

    fun updateImportAndUploadRestOfChainAutomatically(doThemAll: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setImportAndUploadRestOfChainAutomatically(doThemAll)
        }
    }
}