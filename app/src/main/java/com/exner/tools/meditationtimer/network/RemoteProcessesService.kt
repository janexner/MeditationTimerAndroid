package com.exner.tools.meditationtimer.network

import retrofit2.Call
import retrofit2.http.GET

interface RemoteProcessesService {
    @GET("testJSON.php")
    fun getProcessData() : Call<RemoteProcessData?>?
}
