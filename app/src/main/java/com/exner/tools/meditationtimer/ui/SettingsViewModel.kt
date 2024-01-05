package com.exner.tools.meditationtimer.ui

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext context: Context
): ViewModel() {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val _beforeCountingWait: MutableLiveData<Boolean> = MutableLiveData(false)
    val beforeCountingWait: LiveData<Boolean> = _beforeCountingWait

    private val _howLongToWaitBeforeCounting: MutableLiveData<Int> = MutableLiveData(5)
    val howLongToWaitBeforeCounting: LiveData<Int> = _howLongToWaitBeforeCounting

    private val _countBackwards: MutableLiveData<Boolean> = MutableLiveData(false)
    val countBackwards: LiveData<Boolean> = _countBackwards

    private val _chainToSameCategoryOnly: MutableLiveData<Boolean> = MutableLiveData(false)
    val chainToSameCategoryOnly: LiveData<Boolean> = _chainToSameCategoryOnly

    private val _noSounds: MutableLiveData<Boolean> = MutableLiveData(false)
    val noSounds: LiveData<Boolean> = _noSounds

    private val _vibrateEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val vibrateEnabled: LiveData<Boolean> = _vibrateEnabled

    init {
        _beforeCountingWait.value = sharedPreferences.getBoolean("preference_before_counting_wait", false)
        _howLongToWaitBeforeCounting.value = sharedPreferences.getInt("preference_how_long_to_wait_before_counting", 5)
        _countBackwards.value = sharedPreferences.getBoolean("preference_count_backwards", false)
        _chainToSameCategoryOnly.value = sharedPreferences.getBoolean("preference_chain_to_same_category_only", false)
        _noSounds.value = sharedPreferences.getBoolean("preference_no_sounds", false)
        _vibrateEnabled.value = sharedPreferences.getBoolean("preference_vibrate_enabled", false)
    }

    fun updateBeforeCountingWait(newBeforeCountingWait: Boolean) {
        sharedPreferences.edit().putBoolean("preference_before_counting_wait", newBeforeCountingWait).apply()
        _beforeCountingWait.value = newBeforeCountingWait
    }

    fun updateHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting: Int) {
        sharedPreferences.edit().putInt("preference_how_long_to_wait_before_counting", newHowLongToWaitBeforeCounting).apply()
        _howLongToWaitBeforeCounting.value = newHowLongToWaitBeforeCounting
    }

    fun updateCountBackwards(newCountBackwards: Boolean) {
        sharedPreferences.edit().putBoolean("preference_count_backwards", newCountBackwards).apply()
        _countBackwards.value = newCountBackwards
    }

    fun updateChainToSameCategoryOnly(newChainToSameOnly: Boolean) {
        sharedPreferences.edit().putBoolean("preference_chain_to_same_category_only", newChainToSameOnly).apply()
        _chainToSameCategoryOnly.value = newChainToSameOnly
    }

    fun updateNoSounds(newNoSounds: Boolean) {
        sharedPreferences.edit().putBoolean("preference_no_sounds", newNoSounds).apply()
        _noSounds.value = newNoSounds
    }

    fun updateVibrateEnabled(newVibrateEnabled: Boolean) {
        sharedPreferences.edit().putBoolean("preference_vibrate_enabled", newVibrateEnabled).apply()
        _vibrateEnabled.value = newVibrateEnabled
    }
}