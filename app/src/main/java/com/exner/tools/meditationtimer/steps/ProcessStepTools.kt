package com.exner.tools.meditationtimer.steps

import com.exner.tools.meditationtimer.audio.SoundIDs
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import kotlin.math.ceil

const val STEP_LENGTH_IN_MILLISECONDS = 1000

fun getProcessStepListForOneProcess(
    process: MeditationTimerProcess,
    useSounds: Boolean = true,
    countBackwards: Boolean = false,
): MutableList<List<ProcessStepAction>> {

    val result = mutableListOf<List<ProcessStepAction>>()

    var processParameters = ""
    processParameters += "${process.processTime} / ${process.intervalTime}"

    // how many steps do we need for the regular interval?
    val howManySteps = process.processTime * 60 * 1000 / STEP_LENGTH_IN_MILLISECONDS
    for (i in 1..howManySteps) {
        val actionsList = mutableListOf<ProcessStepAction>()
        // add actions as needed
        if (i == 1) {
            // first things first
            val ftpstartAction = ProcessStartAction(
                process.name,
                process.uuid
            )
            actionsList.add(ftpstartAction)
        }
        // calculate round and times and create the display action
        val currentProcessTime = if (countBackwards) (howManySteps + 1) - i * STEP_LENGTH_IN_MILLISECONDS / 1000 else i * STEP_LENGTH_IN_MILLISECONDS / 1000
        val currentIntervalTime = currentProcessTime % (process.intervalTime * 60)
        val ftpdAction = ProcessDisplayStepAction(
            process.name,
            processParameters,
            1 + currentProcessTime / (process.intervalTime * 60),
            ceil(process.processTime.toDouble() / process.intervalTime).toInt(),
            currentProcessTime,
            currentIntervalTime,
            process.info
        )
        actionsList.add(ftpdAction)
        // any sounds?
        if (i == 1 && useSounds) {
            val ftpssAction = ProcessSoundAction(
                process.name,
                SoundIDs.SOUND_ID_PROCESS_START
            )
            actionsList.add(ftpssAction)
        } else if (i == howManySteps && useSounds) {
            val ftpseAction = ProcessSoundAction(
                process.name,
                SoundIDs.SOUND_ID_PROCESS_END
            )
            actionsList.add(ftpseAction)
        } else if (isFullSecond(i)) {
            if ((i * STEP_LENGTH_IN_MILLISECONDS / 1000.0 % (process.intervalTime * 60) == 0.0) && useSounds) {
                val ftpsiAction = ProcessSoundAction(
                    process.name,
                    SoundIDs.SOUND_ID_INTERVAL
                )
                actionsList.add(ftpsiAction)
            }
        }
        // add the chain of actions to the overall list
        result.add(actionsList)
    }

    // does this process chain?
    if (process.hasAutoChain && process.gotoUuid != null && process.gotoUuid != "0") {
        val actionsList = mutableListOf<ProcessStepAction>()
        val ftpchainAction = ProcessGotoAction(
            process.name,
            process.gotoUuid
        )
        actionsList.add(ftpchainAction)
        result.add(actionsList)
    }

    return result
}

private fun isFullSecond(stepIndex: Int): Boolean {
    return (stepIndex * STEP_LENGTH_IN_MILLISECONDS) % 1000.0 == 0.0
}