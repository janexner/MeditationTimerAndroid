package com.exner.tools.meditationtimer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.exner.tools.meditationtimer.network.GenericProcess
import com.exner.tools.meditationtimer.network.RemoteProcessData
import com.exner.tools.meditationtimer.network.RemoteProcessesService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@HiltViewModel
class RemoteProcessManagementViewModel @Inject constructor() : ViewModel() {

    private val _remoteProcessesRaw = MutableStateFlow(emptyList<GenericProcess>())
    val remoteProcessesRaw: StateFlow<List<GenericProcess>>
        get() = _remoteProcessesRaw

    // val observeRemoteProcesses: Flow<List<MeditationTimerProcess>>
    private val baseURL = "http://www.jan-exner.de/orm/ft/"

    fun loadRemoteProcesses() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: RemoteProcessesService =
            retrofit.create(RemoteProcessesService::class.java)
        val call: Call<RemoteProcessData?>? = service.getProcessData()

        call?.enqueue(object : Callback<RemoteProcessData?> {
            override fun onResponse(
                call: Call<RemoteProcessData?>,
                response: Response<RemoteProcessData?>
            ) {
                if (response.code() == 200) {
                    val movieResponse = response.body()!!
                    val newList = mutableListOf<GenericProcess>()
                    for (genericProcess in movieResponse.processes) {
                        Log.v("PROCESSES", genericProcess.name)
                        newList.add(genericProcess)
                    }
                    _remoteProcessesRaw.value = newList
                }
            }

            override fun onFailure(
                call: Call<RemoteProcessData?>,
                t: Throwable
            ) {
                Log.i("PROCESSES", "Failed! $t")
            }
        })
    }
}