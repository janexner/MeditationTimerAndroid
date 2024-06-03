package com.exner.tools.meditationtimer.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.audio.SoundPoolHolder
import com.exner.tools.meditationtimer.audio.VibratorHolder
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.preferences.MeditationTimerUserPreferencesManager
import com.exner.tools.meditationtimer.steps.ProcessDisplayStepAction
import com.exner.tools.meditationtimer.steps.ProcessGotoAction
import com.exner.tools.meditationtimer.steps.ProcessJumpbackAction
import com.exner.tools.meditationtimer.steps.ProcessLeadInDisplayStepAction
import com.exner.tools.meditationtimer.steps.ProcessSoundAction
import com.exner.tools.meditationtimer.steps.ProcessStartAction
import com.exner.tools.meditationtimer.steps.ProcessStepAction
import com.exner.tools.meditationtimer.steps.STEP_LENGTH_IN_MILLISECONDS
import com.exner.tools.meditationtimer.steps.getProcessStepListForOneProcess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessRunViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository,
    private val userPreferencesRepository: MeditationTimerUserPreferencesManager
) : ViewModel() {

    private val _displayAction: MutableLiveData<ProcessStepAction> = MutableLiveData(null)
    val displayAction: LiveData<ProcessStepAction> = _displayAction

    private val _numberOfSteps: MutableLiveData<Int> = MutableLiveData(0)
    val numberOfSteps: LiveData<Int> = _numberOfSteps

    private val _currentStepNumber: MutableLiveData<Int> = MutableLiveData(0)
    val currentStepNumber: LiveData<Int> = _currentStepNumber

    private val _hasLoop: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasLoop: LiveData<Boolean> = _hasLoop

    private val _hasHours: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasHours: LiveData<Boolean> = _hasHours

    private val _showStages: MutableLiveData<Boolean> = MutableLiveData(false)
    val showStages: LiveData<Boolean> = _showStages

    private var job: Job? = null

    private var isRunning: Boolean = false

    private var doneEventHandler: () -> Unit = {}

    @OptIn(DelicateCoroutinesApi::class)
    fun initialiseRun(
        processUuid: String,
    ) {
        val result = mutableListOf<List<ProcessStepAction>>()

        _showStages.value = false // bcs default for simple screen is yes

        if (!isRunning) {
            isRunning = true

            // create list of list of actions and run it
            viewModelScope.launch {
                // loop detection
                val processIdList = mutableListOf<String>()
                var currentID: String? = processUuid
                var noLoopDetectedSoFar = true
                var firstRound = true

                while (currentID != null && noLoopDetectedSoFar) {
                    val process = repository.loadProcessByUuid(currentID)
                    if (process != null) {
                        processIdList.add(currentID)
                        val partialResult =
                            getProcessStepListForOneProcess(
                                process = process,
                                countBackwards = userPreferencesRepository.countBackwards().firstOrNull() ?: false,
                            )
                        result.addAll(partialResult)
                        // do we need hours in the display?
                        _hasHours.value = hasHours.value == true || process.processTime > 60
                        // do we want to simplify the display?
                        _showStages.value = !(userPreferencesRepository.showSimpleDisplay().firstOrNull() ?: true) || (process.processTime != process.intervalTime)
                        // prepare for the next iteration
                        firstRound = false
                        if (process.gotoUuid != null && process.gotoUuid != "" && repository.doesProcessWithUuidExist(
                                process.gotoUuid
                            )
                        ) {
                            currentID = process.gotoUuid
                            if (processIdList.contains(currentID)) {
                                noLoopDetectedSoFar = false // LOOP!
                                _hasLoop.value = true
                                var earliestStepNumberForLoop = -1
                                var i = 0
                                while (i < result.size && earliestStepNumberForLoop < 0) {
                                    val checkPoint = result[i]
                                    checkPoint.forEach { action ->
                                        if (action is ProcessStartAction) {
                                            if (action.processUuiD == currentID) {
                                                earliestStepNumberForLoop = i
                                            }
                                        }
                                    }
                                    i++
                                }
                                if (earliestStepNumberForLoop >= 0) {
                                    // this has to replace the latest GotoAction
                                    val latestActionList = result[result.lastIndex]
                                    val lastAction =
                                        latestActionList[latestActionList.lastIndex]
                                    if (lastAction is ProcessGotoAction) { // it should be!
                                        // remove the action list, it is not mutable
                                        result.removeLast() // remove the action list, bcs we need a new one
                                        val newActionsList = mutableListOf<ProcessStepAction>()
                                        latestActionList.forEach { processStepAction ->
                                            if (processStepAction !is ProcessGotoAction) {
                                                newActionsList.add(processStepAction)
                                            }
                                        }
                                        val ftpJumpAction = ProcessJumpbackAction(
                                            process.name,
                                            earliestStepNumberForLoop
                                        )
                                        newActionsList.add(ftpJumpAction)
                                        result.add(newActionsList)
                                    }
                                }
                            }
                        } else {
                            // that's it, no chain
                            currentID = null
                        }
                    }
                }
                // this is where the list is ready
                _numberOfSteps.value = result.size

                // go into a loop, but in a coroutine
                job = GlobalScope.launch(Dispatchers.Main) {
                    val startTime = System.currentTimeMillis()
                    var actualStep = 0
                    while (isActive) {
                        val step: Int = currentStepNumber.value?.toInt() ?: 0
                        if (step >= result.size) {
                            break
                        } else {
                            Log.d(
                                "ProcessRunVM",
                                "${System.currentTimeMillis() - startTime}: step $step"
                            )
                            // update display action and do sounds
                            val actionsList = result[step]
                            actionsList.forEach { action ->
                                when (action) {
                                    is ProcessLeadInDisplayStepAction, is ProcessDisplayStepAction -> {
                                        _displayAction.value = action
                                    }

                                    is ProcessJumpbackAction -> {
                                        _currentStepNumber.value = action.stepNumber - 1 // aim left
                                        // bcs 4 lines down, we count up by one
                                    }

                                    is ProcessSoundAction -> {
                                        if (userPreferencesRepository.noSounds().firstOrNull() != true) {
                                            SoundPoolHolder.playSound(action.soundId)
                                        }
                                        if (userPreferencesRepository.vibrateEnabled().firstOrNull() == true) {
                                            VibratorHolder.vibrate(action.soundId)
                                        }
                                    }
                                }
                            }
                            // count up
                            _currentStepNumber.value = currentStepNumber.value!! + 1
                            // sleep till next step
                            actualStep++
                            val targetTimeForNextStep =
                                startTime + (actualStep * STEP_LENGTH_IN_MILLISECONDS)
                            val newDelay = targetTimeForNextStep - System.currentTimeMillis()
                            delay(newDelay)
                        }
                    }
                    // done
                    doneEventHandler()
                }
            }
        }
    }

    fun setDoneEventHandler(handler: () -> Unit) {
        doneEventHandler = handler
    }

    fun cancel() {
        job?.cancel()
    }
}