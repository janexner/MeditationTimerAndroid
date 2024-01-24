package com.exner.tools.meditationtimer.data.preferences

interface UserPreferencesRepository {

    suspend fun setBeforeCountingWait(newBeforeCountingWait: Boolean)

    suspend fun setHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting: Int)

    suspend fun setCountBackwards(newCountBackwards: Boolean)

    suspend fun setChainToSameCategoryOnly(newChainToSameOnly: Boolean)

    suspend fun setNoSounds(newNoSounds: Boolean)

    suspend fun setVibrateEnabled(newVibrateEnabled: Boolean)
}
