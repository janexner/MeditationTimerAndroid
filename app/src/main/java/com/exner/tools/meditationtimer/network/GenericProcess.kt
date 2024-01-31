package com.exner.tools.meditationtimer.network

import com.google.gson.annotations.SerializedName

data class GenericProcess (
    var name: String,

    var processTime: Int,
    var intervalTime: Int,

    var hasAutoChain: Boolean,
    var gotoId: String,

    var uuid: String
)