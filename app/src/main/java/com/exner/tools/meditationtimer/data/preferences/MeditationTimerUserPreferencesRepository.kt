package com.exner.tools.meditationtimer.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MeditationTimerUserPreferencesRepository @Inject constructor(
    private val userDataStorePreferences: DataStore<Preferences>
) : UserPreferencesRepository {

    val beforeCountingWait: Flow<Boolean> = userDataStorePreferences.data.map { preferences ->
        preferences[KEY_BEFORE_COUNTING_WAIT] ?: false
    }

    override suspend fun setBeforeCountingWait(newBeforeCountingWait: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_BEFORE_COUNTING_WAIT] = newBeforeCountingWait
        }
    }

    val howLongToWaitBeforeCounting: Flow<Int> = userDataStorePreferences.data.map { preferences ->
        preferences[KEY_HOW_LONG_TO_WAIT_BEFORE_COUNTING] ?: 5
    }

    override suspend fun setHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting: Int) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_HOW_LONG_TO_WAIT_BEFORE_COUNTING] = newHowLongToWaitBeforeCounting
        }
    }

    val countBackwards: Flow<Boolean> = userDataStorePreferences.data.map { preferences ->
        preferences[KEY_COUNT_BACKWARDS] ?: false
    }

    override suspend fun setCountBackwards(newCountBackwards: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_COUNT_BACKWARDS] = newCountBackwards
        }
    }

    val chainToSameCategoryOnly: Flow<Boolean> = userDataStorePreferences.data.map { preferences ->
        preferences[KEY_CHAIN_TO_SAME_CATEGORY_ONLY] ?: false
    }

    override suspend fun setChainToSameCategoryOnly(newChainToSameOnly: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_CHAIN_TO_SAME_CATEGORY_ONLY] = newChainToSameOnly
        }
    }

    val noSounds: Flow<Boolean> = userDataStorePreferences.data.map { preferences ->
        preferences[KEY_NO_SOUNDS] ?: false
    }

    override suspend fun setNoSounds(newNoSounds: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_NO_SOUNDS] = newNoSounds
        }
    }

    val vibrateEnabled: Flow<Boolean> = userDataStorePreferences.data.map { preferences ->
        preferences[KEY_VIBRATE_ENABLED] ?: false
    }

    override suspend fun setVibrateEnabled(newVibrateEnabled: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_VIBRATE_ENABLED] = newVibrateEnabled
        }
    }

    private companion object {

        val KEY_BEFORE_COUNTING_WAIT = booleanPreferencesKey(name = "before_counting_wait")
        val KEY_HOW_LONG_TO_WAIT_BEFORE_COUNTING = intPreferencesKey(name = "how_long_to_wait_before_counting")
        val KEY_COUNT_BACKWARDS = booleanPreferencesKey(name = "count_backwards")
        val KEY_CHAIN_TO_SAME_CATEGORY_ONLY = booleanPreferencesKey(name = "chain_to_same_category_only")
        val KEY_NO_SOUNDS = booleanPreferencesKey(name = "no_sounds")
        val KEY_VIBRATE_ENABLED = booleanPreferencesKey(name = "vibrate_enabled")
    }
}