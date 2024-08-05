package com.exner.tools.meditationtimer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.network.TimerEndpoint
import com.exner.tools.meditationtimer.network.TimerEndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Strategy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

enum class ProcessStateConstants {
    AWAITING_PERMISSIONS, // IDLE
    PERMISSIONS_GRANTED,
    PERMISSIONS_DENIED,
    STARTING_DISCOVERY,
    DISCOVERY_STARTED,
    PARTNER_CHOSEN,
    CONNECTING,
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

/****
 * Here's the flow:
 *
 * AWAITING PERMISSIONS ("IDLE")
 * - show brief explanation why permissions are needed, and a button "Get permissions"
 * - check whether permissions are all given.
 *   - If so -> PERMISSIONS_GRANTED
 *   - Otherwise -> PERMISSIONS_DENIED (same as AWAITING_PERMISSIONS?)
 *
 * PERMISSIONS_GRANTED
 * - show "Start discovery" button
 * - the button has two callbacks
 *   - success -> DISCOVERY_STARTED
 *   - fail -> ERROR
 * - the discovery method also has a callback
 *   - called when partner found -> PARTNER_FOUND
 *
 * PARTNER_FOUND
 * - authenticate
 *   - If OK -> AUTHENTICATION_OK
 *   - else -> AUTHENTICATION_DENIED (back to DISCOVERY_STARTED)
 *
 * AUTHENTICATION_OK
 * - connect (requestConnection) gets passed a callback, and has a failure callback, too
 *   - callback -> CONNECTION_ESTABLISHED
 *   - fail -> CONNECTION_DENIED
 *
 */

const val endpointId: String = "com.exner.tools.activitytimerfortv"
const val userName: String = "Anonymous"
const val checkInterval: Long = 500 // this should be milliseconds

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

    private lateinit var endpointDiscoveryCallback: TimerEndpointDiscoveryCallback
    private lateinit var connectionsClient: ConnectionsClient

    fun provideDiscoveryCallback(endpointDiscoveryCallback: TimerEndpointDiscoveryCallback) {
        this.endpointDiscoveryCallback = endpointDiscoveryCallback
    }

    fun provideConnectionsClient(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
    }

    val discoveredEndpoints: MutableMap<String, TimerEndpoint> = mutableMapOf()
    val pendingConnections: MutableMap<String, TimerEndpoint> = mutableMapOf()
    val establishedConnections: MutableMap<String, TimerEndpoint> = mutableMapOf()

    var endpointsFound: Flow<List<TimerEndpoint>> = flow {
        while (processStateFlow.value.currentState == ProcessStateConstants.AWAITING_PERMISSIONS || processStateFlow.value.currentState == ProcessStateConstants.PERMISSIONS_GRANTED || processStateFlow.value.currentState == ProcessStateConstants.STARTING_DISCOVERY || processStateFlow.value.currentState == ProcessStateConstants.DISCOVERY_STARTED || processStateFlow.value.currentState == ProcessStateConstants.PERMISSIONS_DENIED || processStateFlow.value.currentState == ProcessStateConstants.PARTNER_CHOSEN) {
            emit(discoveredEndpoints.values.toList());
            delay(checkInterval)
        }
    }

    private val connectionLifecycleCallback = object: ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.d("SNDVMCLC", "onConnectionInitiated $endpointId: $connectionInfo")
            val endpoint = discoveredEndpoints.remove(endpointId)
            pendingConnections[endpointId] = endpoint!! // TODO
            transitionToNewState(ProcessStateConstants.CONNECTING, endpointId)
        }

        override fun onConnectionResult(endpointId: String, connectionResolution: ConnectionResolution) {
            Log.d("SNDVMCLC", "onConnectionResult $endpointId: $connectionResolution")
            if (!connectionResolution.status.isSuccess) {
                // failed
                pendingConnections.remove(endpointId)
                var message = connectionResolution.status.statusMessage
                if (null == message) {
                    message = "Unknown issue"
                }
                transitionToNewState(ProcessStateConstants.CONNECTION_DENIED, message = message)
            } else {
                // this worked!
                val endpoint = pendingConnections.remove(endpointId)
                establishedConnections[endpointId] = endpoint!!
                transitionToNewState(ProcessStateConstants.CONNECTION_ESTABLISHED, "OK")
            }
        }

        override fun onDisconnected(endpointId: String) {
            TODO("Not yet implemented")
        }

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
                        transitionToNewState(
                            newState = ProcessStateConstants.STARTING_DISCOVERY,
                            message = "Automatically move to discovery..."
                        )
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
                    ProcessStateConstants.STARTING_DISCOVERY -> {
                        _processStateFlow.value = ProcessState(newState, "OK")
                        // trigger the actual discovery
                        val discoveryOptions =
                            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT)
                                .build()
                        connectionsClient.startDiscovery(
                            endpointId,
                            object : EndpointDiscoveryCallback() {
                                override fun onEndpointFound(
                                    endpointId: String,
                                    endpointInfo: DiscoveredEndpointInfo
                                ) {
                                    Log.d(
                                        "SNDVM/TEDC",
                                        "OnEndpointFound... $endpointId / ${endpointInfo.endpointName}"
                                    )
                                    val discoveredEndpoint =
                                        TimerEndpoint(endpointId, endpointInfo.endpointName)
                                    discoveredEndpoints[endpointId] = discoveredEndpoint
                                }

                                override fun onEndpointLost(endpointId: String) {
                                    Log.d("SNDVM/TEDC", "On Endpoint Lost... $endpointId")
                                    discoveredEndpoints.remove(endpointId)
                                }
                            },
                            discoveryOptions
                        )
                            .addOnSuccessListener { _ ->
                                Log.d("SNDVM", "Success! Discovery started")
                                transitionToNewState(ProcessStateConstants.DISCOVERY_STARTED)
                            }
                            .addOnFailureListener { e: Exception? ->
                                val errorMessage = "Error discovering" + if (e != null) {
                                    ": ${e.message}"
                                } else {
                                    ""
                                }
                                Log.d("SNDVM", errorMessage)
                                transitionToNewState(
                                    ProcessStateConstants.ERROR,
                                    message = errorMessage
                                )
                            }
                    }

                    ProcessStateConstants.DISCOVERY_STARTED -> {
                        _processStateFlow.value = ProcessState(newState, "Discovering...")
                    }

                    ProcessStateConstants.CANCELLED -> {
                        connectionsClient.stopDiscovery()
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

            ProcessStateConstants.STARTING_DISCOVERY -> {
                // handled in the UI
                when (newState) {
                    ProcessStateConstants.DISCOVERY_STARTED -> {
                        Log.d("SNDVM", "Discovery started...")
                        _processStateFlow.value = ProcessState(newState, "OK")
                    }

                    ProcessStateConstants.CANCELLED -> {
                        connectionsClient.stopDiscovery()
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
                when (newState) {
                    ProcessStateConstants.PARTNER_CHOSEN -> {
                        Log.d("SNDVM", "Chose partner: $message.")
                        // stop discovery, bcs
                        connectionsClient.stopDiscovery()
                        // this is where we build an endpoint and initiate connection
                        val endpointId = message // probably should check that!
                        // find endpoint in discoveredEndpoints
                        val endpoint = discoveredEndpoints.get(endpointId)
                        if (endpoint != null) {
                            // initiate connection
                            connectionsClient.requestConnection("Phone", endpoint.endpointId, connectionLifecycleCallback)
                            // if that doesn't work, we go back to discovery
                            // TODO
                            _processStateFlow.value = ProcessState(newState, "OK")
                        }
                    }

                    ProcessStateConstants.CANCELLED -> {
                        Log.d("SNDVM", "Cancelling discovery...")
                        connectionsClient.stopDiscovery()
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

            ProcessStateConstants.PARTNER_CHOSEN -> {
                Log.d("SNDVM", "Found device...")
                // trigger authentication on both devices
                // TODO
            }

            ProcessStateConstants.CONNECTING -> {
                Log.d("SNDVM", "Connecting to device")
                // TODO
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

    fun cancel() {
        transitionToNewState(ProcessStateConstants.CANCELLED)
    }

}