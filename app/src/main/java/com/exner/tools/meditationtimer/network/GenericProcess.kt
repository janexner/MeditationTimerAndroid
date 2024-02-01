package com.exner.tools.meditationtimer.network

import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess

data class GenericProcess (
    var name: String,

    var processTime: Int,
    var intervalTime: Int,

    var hasAutoChain: Boolean,
    var gotoUUid: String?,
    var gotoName: String?,

    var uuid: String
)

fun createGenericProcessFrom(meditationTimerProcess: MeditationTimerProcess): GenericProcess {
    val result = GenericProcess(
        meditationTimerProcess.name,
        meditationTimerProcess.processTime * 60,
        meditationTimerProcess.intervalTime * 60,
        meditationTimerProcess.hasAutoChain,
        meditationTimerProcess.gotoUuid,
        meditationTimerProcess.gotoName,
        meditationTimerProcess.uuid
    )

    return result
}