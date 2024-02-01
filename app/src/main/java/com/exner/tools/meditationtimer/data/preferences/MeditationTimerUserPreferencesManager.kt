package com.exner.tools.meditationtimer.data.preferences

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
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

    init {
        val am: AccountManager = AccountManager.get(appContext)
        val accounts: Array<out Account> = am.getAccountsByType("com.google")
        Log.d("ACCOUNTS", accounts.toString())
    }

    fun accountId(): Flow<String> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_ACCOUNT_ID] ?: ""
        }
    }

    suspend fun setAccountId(id: String) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_ACCOUNT_ID] = id
        }
    }

    fun beforeCountingWait(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_BEFORE_COUNTING_WAIT] ?: false
        }
    }

    suspend fun setBeforeCountingWait(newBeforeCountingWait: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_BEFORE_COUNTING_WAIT] = newBeforeCountingWait
        }
    }

    fun howLongToWaitBeforeCounting(): Flow<Int> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_HOW_LONG_TO_WAIT_BEFORE_COUNTING] ?: 5
        }
    }

    suspend fun setHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting: Int) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_HOW_LONG_TO_WAIT_BEFORE_COUNTING] = newHowLongToWaitBeforeCounting
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

    fun onlyShowFirstInChain(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_ONLY_SHOW_FIRST_IN_CHAIN] ?: false
        }
    }

    suspend fun setOnlyShowFirstInChain(newFirstOnly: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_ONLY_SHOW_FIRST_IN_CHAIN] = newFirstOnly
        }
    }

    private companion object {

        val KEY_BEFORE_COUNTING_WAIT = booleanPreferencesKey(name = "before_counting_wait")
        val KEY_HOW_LONG_TO_WAIT_BEFORE_COUNTING =
            intPreferencesKey(name = "how_long_to_wait_before_counting")
        val KEY_COUNT_BACKWARDS = booleanPreferencesKey(name = "count_backwards")
        val KEY_CHAIN_TO_SAME_CATEGORY_ONLY =
            booleanPreferencesKey(name = "chain_to_same_category_only")
        val KEY_NO_SOUNDS = booleanPreferencesKey(name = "no_sounds")
        val KEY_VIBRATE_ENABLED = booleanPreferencesKey(name = "vibrate_enabled")
        val KEY_ONLY_SHOW_FIRST_IN_CHAIN = booleanPreferencesKey(name = "only_show_first_in_chain")
        val KEY_ACCOUNT_ID = stringPreferencesKey(name = "account_id")
    }
}