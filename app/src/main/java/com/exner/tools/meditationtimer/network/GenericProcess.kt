package com.exner.tools.meditationtimer.network

import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess

data class GenericProcess (
    var name: String,
    var info: String,

    var categoryId: Long?,

    var processTime: Int,
    var intervalTime: Int,

    var hasAutoChain: Boolean,
    var gotoUUid: String?,
    var gotoName: String?,

    var uuid: String
)

fun createGenericProcessFrom(meditationTimerProcess: MeditationTimerProcess): GenericProcess {
    val result = GenericProcess(
        name = meditationTimerProcess.name,
        info = "",
        categoryId = meditationTimerProcess.categoryId,
        processTime = meditationTimerProcess.processTime * 60,
        intervalTime = meditationTimerProcess.intervalTime * 60,
        hasAutoChain = meditationTimerProcess.hasAutoChain,
        gotoUUid = meditationTimerProcess.gotoUuid,
        gotoName = meditationTimerProcess.gotoName,
        uuid = meditationTimerProcess.uuid
    )

    return result
}