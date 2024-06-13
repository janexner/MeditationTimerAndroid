package com.exner.tools.meditationtimer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.network.TimerEndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


enum class ProcessStateConstants {
    AWAITING_PERMISSIONS, // IDLE
    PERMISSIONS_GRANTED,
    PERMISSIONS_DENIED,
    DISCOVERY_STARTED,
    FOUND_PARTNER,
    AUTHENTICATION_OK,
    AUTHENTICATION_DENIED,
    CONNECTION_ESTABLISHED,
    CONNECTION_DENIED,
    SENDING,
    DISCONNECTED,
    DONE,
    CANCELLED,
    ERROR
}

const val endpointId = "com.exner.tools.activitytimerfortv"
const val userName = "Anonymous"

data class ProcessState(
    val currentState: ProcessStateConstants = ProcessStateConstants.AWAITING_PERMISSIONS,
    val message: String = ""
)

// convenience function for all those invalid transitions
fun invalidTransitionProcessState(
    currentState: ProcessStateConstants,
    newState: ProcessStateConstants
): ProcessState {
    return ProcessState(
        ProcessStateConstants.ERROR,
        "Invalid transition: ${currentState.name} > ${newState.name}"
    )
}

@HiltViewModel
class SendToNearbyDeviceViewModel @Inject constructor(
    repository: MeditationTimerDataRepository,
) : ViewModel() {

    private val _processStateFlow = MutableStateFlow(ProcessState())
    val processStateFlow: StateFlow<ProcessState> = _processStateFlow.asStateFlow()

    lateinit var endpointDiscoveryCallback: TimerEndpointDiscoveryCallback
    lateinit var connectionsClient: ConnectionsClient

    fun provideDiscoveryCallback(endpointDiscoveryCallback: TimerEndpointDiscoveryCallback) {
        this.endpointDiscoveryCallback = endpointDiscoveryCallback
    }

    fun provideConnectionsClient(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
    }

    fun transitionToNewState(
        newState: ProcessStateConstants,
        message: String = "OK"
    ) {
        // all the logic should be here
        when (processStateFlow.value.currentState) {
            ProcessStateConstants.AWAITING_PERMISSIONS -> {
                // handled in the UI
                when (newState) {
                    ProcessStateConstants.PERMISSIONS_GRANTED -> {
                        _processStateFlow.value = ProcessState(newState, "OK")
                    }

                    ProcessStateConstants.PERMISSIONS_DENIED -> {
                        _processStateFlow.value = ProcessState(newState, "Denied: $message")
                    }

                    ProcessStateConstants.CANCELLED -> {
                        _processStateFlow.value = ProcessState(newState, "Cancelled")
                    }

                    else -> {
                        _processStateFlow.value = invalidTransitionProcessState(
                            currentState = processStateFlow.value.currentState,
                            newState = newState
                        )
                    }
                }
            }

            ProcessStateConstants.PERMISSIONS_GRANTED -> {
                // handled in the UI
                when (newState) {
                    ProcessStateConstants.DISCOVERY_STARTED -> {
                        _processStateFlow.value = ProcessState(newState, "OK")
                        // trigger the actual discovery
                        val discoveryOptions =
                            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT)
                                .build()
                        connectionsClient.startDiscovery(
                            endpointId,
                            endpointDiscoveryCallback,
                            discoveryOptions
                        )
                            .addOnSuccessListener { _: Void? ->
                                Log.d("SNDVM", "Success! Discovered a nearby device!")
                                transitionToNewState(ProcessStateConstants.FOUND_PARTNER)
                            }
                            .addOnFailureListener { e: Exception? ->
                                val errorMessage = "Error discovering" + if (e != null) {
                                    ": ${e.message}"
                                } else {
                                    ""
                                }
                                transitionToNewState(ProcessStateConstants.ERROR, message = errorMessage)
                            }
                    }

                    ProcessStateConstants.CANCELLED -> {
                        _processStateFlow.value = ProcessState(newState, "Cancelled")
                    }

                    else -> {
                        _processStateFlow.value = invalidTransitionProcessState(
                            currentState = processStateFlow.value.currentState,
                            newState = newState
                        )
                    }
                }
            }

            ProcessStateConstants.PERMISSIONS_DENIED -> TODO()
            ProcessStateConstants.DISCOVERY_STARTED -> {
                // not sure what to do here...
                Log.d("SNDVM", "Now in discovery...")
            }
            ProcessStateConstants.FOUND_PARTNER -> {
                // trigger authentication on both devices
            }

            ProcessStateConstants.AUTHENTICATION_OK -> TODO()
            ProcessStateConstants.AUTHENTICATION_DENIED -> TODO()
            ProcessStateConstants.CONNECTION_ESTABLISHED -> TODO()
            ProcessStateConstants.CONNECTION_DENIED -> TODO()
            ProcessStateConstants.SENDING -> TODO()
            ProcessStateConstants.DISCONNECTED -> TODO()
            ProcessStateConstants.DONE, ProcessStateConstants.CANCELLED, ProcessStateConstants.ERROR -> {
                // don't think there is anything to do here
            }
        }
    }

}