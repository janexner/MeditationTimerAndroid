package com.exner.tools.meditationtimer.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteProcessesService {
    @GET("allProcessesJSON.php")
    fun getProcessData() : Call<RemoteProcessData?>?

    @GET("processJSON.php?")
    fun getProcess(@Query("uuid") uuid : String) : Call<GenericProcess?>?
}
