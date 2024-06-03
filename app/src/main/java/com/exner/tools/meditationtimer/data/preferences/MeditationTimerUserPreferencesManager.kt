package com.exner.tools.meditationtimer.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.exner.tools.meditationtimer.ui.theme.Theme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("preferences")

@Singleton
class MeditationTimerUserPreferencesManager @Inject constructor(
    @ApplicationContext appContext: Context
) {

    private val userDataStorePreferences = appContext.dataStore

    fun theme(): Flow<Theme> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            val wasDark = preferences[KEY_NIGHT_MODE] ?: false
            val default = if (wasDark) Theme.Dark.name else Theme.Auto.name
            Theme.valueOf(preferences[KEY_THEME] ?: default)
        }
    }

    suspend fun setTheme(newTheme: Theme) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_THEME] = newTheme.name
        }
    }

    fun countBackwards(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_COUNT_BACKWARDS] ?: false
        }
    }

    suspend fun setCountBackwards(newCountBackwards: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_COUNT_BACKWARDS] = newCountBackwards
        }
    }

    fun chainToSameCategoryOnly(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_CHAIN_TO_SAME_CATEGORY_ONLY] ?: false
        }
    }

    suspend fun setChainToSameCategoryOnly(newChainToSameOnly: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_CHAIN_TO_SAME_CATEGORY_ONLY] = newChainToSameOnly
        }
    }

    fun noSounds(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_NO_SOUNDS] ?: false
        }
    }

    suspend fun setNoSounds(newNoSounds: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_NO_SOUNDS] = newNoSounds
        }
    }

    fun vibrateEnabled(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_VIBRATE_ENABLED] ?: false
        }
    }

    suspend fun setVibrateEnabled(newVibrateEnabled: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_VIBRATE_ENABLED] = newVibrateEnabled
        }
    }

    fun showSimpleDisplay(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_SIMPLE_DISPLAY] ?: true
        }
    }

    suspend fun setShowSimpleDisplay(newShowSimpleDisplay: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_SIMPLE_DISPLAY] = newShowSimpleDisplay
        }
    }

    private companion object {

        val KEY_NIGHT_MODE = booleanPreferencesKey(name = "preference_night_mode")
        val KEY_BEFORE_COUNTING_WAIT = booleanPreferencesKey(name = "before_counting_wait")
        val KEY_HOW_LONG_TO_WAIT_BEFORE_COUNTING =
            intPreferencesKey(name = "how_long_to_wait_before_counting")
        val KEY_COUNT_BACKWARDS = booleanPreferencesKey(name = "count_backwards")
        val KEY_CHAIN_TO_SAME_CATEGORY_ONLY =
            booleanPreferencesKey(name = "chain_to_same_category_only")
        val KEY_NO_SOUNDS = booleanPreferencesKey(name = "no_sounds")
        val KEY_VIBRATE_ENABLED = booleanPreferencesKey(name = "vibrate_enabled")
        val KEY_THEME = stringPreferencesKey(name = "preference_theme")
        val KEY_SIMPLE_DISPLAY = booleanPreferencesKey(name = "simple_display")
    }
}