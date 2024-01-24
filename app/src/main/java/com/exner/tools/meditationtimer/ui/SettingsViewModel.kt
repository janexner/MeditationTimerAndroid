package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.preferences.MeditationTimerUserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: MeditationTimerUserPreferencesRepository
) : ViewModel() {

    val beforeCountingWait = userPreferencesRepository.beforeCountingWait.asLiveData()
    val howLongToWaitBeforeCounting = userPreferencesRepository.howLongToWaitBeforeCounting.asLiveData()
    val countBackwards = userPreferencesRepository.countBackwards.asLiveData()
    val chainToSameCategoryOnly = userPreferencesRepository.chainToSameCategoryOnly.asLiveData()
    val noSounds = userPreferencesRepository.noSounds.asLiveData()
    val vibrateEnabled = userPreferencesRepository.vibrateEnabled.asLiveData()

    fun updateBeforeCountingWait(newBeforeCountingWait: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setBeforeCountingWait(newBeforeCountingWait)
        }
    }

    fun updateHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting)
        }
    }

    fun updateCountBackwards(newCountBackwards: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setCountBackwards(newCountBackwards)
        }
    }

    fun updateChainToSameCategoryOnly(newChainToSameOnly: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setChainToSameCategoryOnly(newChainToSameOnly)
        }
    }

    fun updateNoSounds(newNoSounds: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setNoSounds(newNoSounds)
        }
    }

    fun updateVibrateEnabled(newVibrateEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setVibrateEnabled(newVibrateEnabled)
        }
    }
}