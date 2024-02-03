package com.exner.tools.meditationtimer.network

import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess

data class GenericProcess (
    var name: String,
    var info: String,

    var categoryId: Long?,

    var processTime: Int,
    var intervalTime: Int,

    var hasAutoChain: Boolean,
    var gotoUuid: String?,
    var gotoName: String?,

    var uuid: String
)

fun createMeditationTimerProcessFrom(genericProcess: GenericProcess): MeditationTimerProcess {
    val result = MeditationTimerProcess(
        name = genericProcess.name,
        info = genericProcess.info,
        uuid = genericProcess.uuid,
        processTime = genericProcess.processTime / 60, // minutes!
        intervalTime = genericProcess.intervalTime / 60,
        hasAutoChain = genericProcess.hasAutoChain,
        gotoUuid = genericProcess.gotoUuid,
        gotoName = genericProcess.gotoName,
        categoryId = genericProcess.categoryId,
        uid = 0
    )
    return result
}