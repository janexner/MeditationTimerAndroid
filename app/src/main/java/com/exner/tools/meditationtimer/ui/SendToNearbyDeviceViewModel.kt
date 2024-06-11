package com.exner.tools.meditationtimer.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.network.TimerEndpointDiscoveryCallback
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


enum class ProcessStateConstants {
    AWAITING_PERMISSIONS,
    PERMISSIONS_DENIED,
    AWAITING_DISCOVERY,
    DISCOVERED,
    CONNECTED,
    RECEIVING,
    DISCONNECTED,
    ERROR
}

const val endpointId = "com.exner.tools.activitytimerfortv"
const val userName = "Anonymous"

data class ProcessState(
    val currentState: ProcessStateConstants = ProcessStateConstants.AWAITING_PERMISSIONS
)

@HiltViewModel
class SendToNearbyDeviceViewModel @Inject constructor(
    repository: MeditationTimerDataRepository
) : ViewModel() {

    private val _processStateFlow = MutableStateFlow(ProcessState())
    val processStateFlow: StateFlow<ProcessState> = _processStateFlow.asStateFlow()

    fun setCurrentState(newState: ProcessStateConstants) {
        _processStateFlow.value = ProcessState(newState)
    }

    fun startDiscovery(context: Context) {
        val discoveryOptions =
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        val endpointDiscoveryCallback = TimerEndpointDiscoveryCallback(context = context)
        Nearby.getConnectionsClient(context)
            .startDiscovery(
                endpointId,
                endpointDiscoveryCallback,
                discoveryOptions
            )
            .addOnSuccessListener { unused: Void? ->
                Log.d("SNDVM", "Success! Discovered a nearby device!")
            }
            .addOnFailureListener { e: Exception? ->
                if (e != null) {
                    Log.d("SNDVM", "Error discovering: ${e.message}")
                }
            }
    }

}