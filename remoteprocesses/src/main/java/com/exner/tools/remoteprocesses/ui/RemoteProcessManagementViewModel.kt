package com.exner.tools.remoteprocesses.ui

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@HiltViewModel
class RemoteProcessManagementViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository,
) : ViewModel() {

    private val _remoteProcessesRaw = MutableStateFlow(emptyList<com.exner.tools.remoteprocesses.network.GenericProcess>())
    val remoteProcessesRaw: StateFlow<List<com.exner.tools.remoteprocesses.network.GenericProcess>>
        get() = _remoteProcessesRaw

    // val observeRemoteProcesses: Flow<List<MeditationTimerProcess>>
    private val baseURL = "https://www.jan-exner.de/orm/ft/"

    fun loadRemoteProcesses() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: com.exner.tools.remoteprocesses.network.RemoteProcessesService =
            retrofit.create(com.exner.tools.remoteprocesses.network.RemoteProcessesService::class.java)
        val call: Call<com.exner.tools.remoteprocesses.network.RemoteProcessData?>? = service.getProcessData()

        call?.enqueue(object : Callback<com.exner.tools.remoteprocesses.network.RemoteProcessData?> {
            override fun onResponse(
                call: Call<com.exner.tools.remoteprocesses.network.RemoteProcessData?>,
                response: Response<com.exner.tools.remoteprocesses.network.RemoteProcessData?>
            ) {
                if (response.code() == 200) {
                    val processesResponse = response.body()!!
                    val newList = mutableListOf<com.exner.tools.remoteprocesses.network.GenericProcess>()
                    for (genericProcess in processesResponse.processes) {
                        Log.v("PROCESSES", genericProcess.name)
                        newList.add(genericProcess)
                    }
                    _remoteProcessesRaw.value = newList
                }
            }

            override fun onFailure(
                call: Call<com.exner.tools.remoteprocesses.network.RemoteProcessData?>,
                t: Throwable
            ) {
                Log.i("PROCESSES", "Failed! $t")
            }
        })
    }

    fun importProcessesFromRemote(
        listOfProcessUuidsToImport: SnapshotStateList<String>,
        importAndUploadRestOfChainAutomatically: Boolean
    ) {
        val processUuidsThatHaveBeenImportedSoFar = mutableListOf<String>()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: com.exner.tools.remoteprocesses.network.RemoteProcessesService =
            retrofit.create(com.exner.tools.remoteprocesses.network.RemoteProcessesService::class.java)

        if (listOfProcessUuidsToImport.isNotEmpty()) {
            listOfProcessUuidsToImport.forEach { uuid ->
                importProcessFromRemote(
                    service,
                    uuid,
                    importAndUploadRestOfChainAutomatically
                )
                processUuidsThatHaveBeenImportedSoFar.add(uuid)
            }
        }
    }

    private fun importProcessFromRemote(
        service: com.exner.tools.remoteprocesses.network.RemoteProcessesService,
        uuid: String,
        importAndUploadRestOfChainAutomatically: Boolean,
    ) {
        // check - does it actually have to be loaded?
        runBlocking {
            if (!repository.doesProcessWithUuidExist(uuid)) {
                // OK - load it!
                val call: Call<com.exner.tools.remoteprocesses.network.GenericProcess?>? = service.getProcess(uuid = uuid)

                call?.enqueue(object : Callback<com.exner.tools.remoteprocesses.network.GenericProcess?> {
                    override fun onResponse(
                        call: Call<com.exner.tools.remoteprocesses.network.GenericProcess?>,
                        response: Response<com.exner.tools.remoteprocesses.network.GenericProcess?>
                    ) {
                        if (response.code() == 200) {
                            val genericProcess = response.body()!!
                            val meditationTimerProcess =
                                com.exner.tools.remoteprocesses.network.createMeditationTimerProcessFrom(
                                    genericProcess
                                )
                            runBlocking {
                                repository.insert(meditationTimerProcess)
                                if (importAndUploadRestOfChainAutomatically && null != meditationTimerProcess.gotoUuid) {
                                    importProcessFromRemote(
                                        service,
                                        meditationTimerProcess.gotoUuid!!,
                                        true
                                    )
                                }
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<com.exner.tools.remoteprocesses.network.GenericProcess?>,
                        t: Throwable
                    ) {
                        // Nothing to do
                    }
                })
            }
        }
    }
}