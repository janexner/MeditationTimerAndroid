package com.exner.tools.meditationtimer.steps

open class ProcessStepAction(
    val processName: String
)

class ProcessStartAction(
    processName: String,
    val processUuiD: String
): ProcessStepAction(processName)

class ProcessDisplayStepAction(
    processName: String,
    val processParameters: String,
    val currentRound: Int,
    val totalRounds: Int,
    val currentProcessTime: Int,
    val currentIntervalTime: Int,
    val currentNotes: String
) : ProcessStepAction(processName)

class ProcessLeadInDisplayStepAction(
    processName: String,
    val processParameters: String,
    val currentLeadInTime: Int
) : ProcessStepAction(processName)

class ProcessSoundAction(
    processName: String,
    val soundId: Long
) : ProcessStepAction(processName)

class ProcessGotoAction(
    processName: String,
    val gotoUuid: String
): ProcessStepAction(processName)

class ProcessJumpbackAction(
    processName: String,
    val stepNumber: Int
): ProcessStepAction(processName)