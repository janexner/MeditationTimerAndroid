package com.exner.tools.meditationtimer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.network.TimerEndpoint
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ProcessStateConstants {
    AWAITING_PERMISSIONS, // IDLE
    PERMISSIONS_GRANTED,
    PERMISSIONS_DENIED,
    STARTING_DISCOVERY,
    DISCOVERY_STARTED,
    PARTNER_CHOSEN,
    CONNECTING,
    AUTHENTICATION_REQUESTED,
    AUTHENTICATION_OK,
    AUTHENTICATION_DENIED,
    CONNECTION_ESTABLISHED,
    CONNECTION_DENIED,
    SENDING,
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

const val endpointId: String = "com.exner.tools.timers"
const val userName: String = "Timer"
const val checkInterval: Long = 500 // this should be milliseconds

data class ProcessState(
    val currentState: ProcessStateConstants = ProcessStateConstants.AWAITING_PERMISSIONS,
    val message: String = ""
)

data class EndpointConnectionInformation(
    val endpointId: String = "",
    val connectionInfo: ConnectionInfo = ConnectionInfo("", "", false)
)

@HiltViewModel
class SendToNearbyDeviceViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository,
) : ViewModel() {

    private val _processStateFlow = MutableStateFlow(ProcessState())
    val processStateFlow: StateFlow<ProcessState> = _processStateFlow.asStateFlow()

    val processList = repository.observeProcesses

    private val _connectionInfo = MutableStateFlow(EndpointConnectionInformation())
    val connectionInfo: StateFlow<EndpointConnectionInformation> = _connectionInfo

    private lateinit var endpointDiscoveryCallback: EndpointDiscoveryCallback
    private lateinit var connectionsClient: ConnectionsClient

    // TODO create object for auth that holds endpointId and connectionInfo

    val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.d("SNDVMPTU", "Payload received ${payload.id}")
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            Log.d("SNDVMPTU", "Payload Transfer Update: ${update.status}")
        }
    }

    val timerLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(
            endpointId: String,
            connectionInfo: ConnectionInfo
        ) {
            Log.d(
                "SNDVMCLC",
                "onConnectionInitiated ${connectionInfo.endpointName} / ${connectionInfo.authenticationDigits}"
            )
            // authenticate
            _connectionInfo.value = EndpointConnectionInformation(
                endpointId = endpointId,
                connectionInfo = connectionInfo
            )
            // now move to auth requested
            _processStateFlow.value = ProcessState(ProcessStateConstants.AUTHENTICATION_REQUESTED,
                connectionInfo.endpointName
            )
        }

        override fun onConnectionResult(
            endpointId: String,
            connectionResolution: ConnectionResolution
        ) {
            Log.d(
                "SNDVMCLC",
                "onConnectionResult $endpointId: $connectionResolution"
            )
            if (!connectionResolution.status.isSuccess) {
                // failed
                pendingConnections.remove(endpointId)
                var statusMessage = connectionResolution.status.statusMessage
                if (null == statusMessage) {
                    statusMessage = "Unknown issue"
                }
                _processStateFlow.value = ProcessState(
                    ProcessStateConstants.CONNECTION_DENIED,
                    message = statusMessage
                )
            } else {
                // this worked!
                val newEndpoint = pendingConnections.remove(endpointId)
                establishedConnections[endpointId] = newEndpoint!!
                _processStateFlow.value = ProcessState(
                    ProcessStateConstants.CONNECTION_ESTABLISHED,
                    "Connection established: ${newEndpoint.userName}"
                )
            }
        }

        override fun onDisconnected(endpointId: String) {
            establishedConnections.remove(endpointId)
            _processStateFlow.value = ProcessState(ProcessStateConstants.DONE, "Disconnected")
        }


    }

    fun provideConnectionsClient(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
    }
    fun provideEndpointDiscoveryCallback(endpointDiscoveryCallback: EndpointDiscoveryCallback) {
        this.endpointDiscoveryCallback = endpointDiscoveryCallback
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

    override fun onCleared() {
        connectionsClient.stopDiscovery()
        connectionsClient.stopAllEndpoints()
        super.onCleared()
    }

    fun transitionToNewState(
        newState: ProcessStateConstants,
        message: String = "OK"
    ) {
        // all the logic should be here
        // DO NOT CALL RECURSIVELY!
        when (newState) {
            ProcessStateConstants.AUTHENTICATION_REQUESTED -> {
                _processStateFlow.value = ProcessState(newState, "Auth requested")
                Log.d("SNDVM", "Authentication requested...")
            }

            ProcessStateConstants.PERMISSIONS_GRANTED -> {
                _processStateFlow.value = ProcessState(newState, "OK")
                Log.d("SNDVM", "Permissions OK, automatically starting discovery...")
                _processStateFlow.value = ProcessState(
                    ProcessStateConstants.STARTING_DISCOVERY,
                    message = "Automatically moving to discovery..."
                )
                startDiscovery()
            }

            ProcessStateConstants.PERMISSIONS_DENIED -> {
                _processStateFlow.value = ProcessState(newState, "Denied: $message")
            }

            ProcessStateConstants.CANCELLED -> {
                Log.d("SNDVM", "Cancelled. Disconnecting everything...")
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopDiscovery()
                _processStateFlow.value = ProcessState(newState, "Cancelled")
            }

            // not sure I need this anymore
            ProcessStateConstants.STARTING_DISCOVERY -> {
                _processStateFlow.value = ProcessState(newState, "OK")
                // trigger the actual discovery
                startDiscovery()
            }

            ProcessStateConstants.DISCOVERY_STARTED -> {
                Log.d("SNDVM", "Discovery started...")
                _processStateFlow.value = ProcessState(newState, "OK")
            }

            ProcessStateConstants.PARTNER_CHOSEN -> {
                Log.d("SNDVM", "Chose partner: $message.")
                // stop discovery, bcs
                connectionsClient.stopDiscovery()
                // this is where we build an endpoint and initiate connection
                val endpointId = message // probably should check that!
                // find endpoint in discoveredEndpoints
                val endpoint = discoveredEndpoints[endpointId]
                _processStateFlow.value = ProcessState(ProcessStateConstants.PARTNER_CHOSEN, "Partner chosen: $message...")
                if (endpoint != null) {
                    // initiate connection
                    connectionsClient.requestConnection(
                        userName,
                        endpoint.endpointId,
                        timerLifecycleCallback
                    )
                }
            }

            ProcessStateConstants.AWAITING_PERMISSIONS -> TODO()

            ProcessStateConstants.CONNECTING -> {
                Log.d("SNDVM", "Accepting connection... $message")
                connectionsClient.acceptConnection(message, payloadCallback)
            }

            ProcessStateConstants.AUTHENTICATION_OK -> {
                val newEndpoint = discoveredEndpoints.remove(connectionInfo.value.endpointId)
                pendingConnections[connectionInfo.value.endpointId] = newEndpoint!! // TODO
                _processStateFlow.value = ProcessState(ProcessStateConstants.CONNECTING, connectionInfo.value.endpointId)
                Log.d("SNDVM", "Now accepting the connection...")
                connectionsClient.acceptConnection(connectionInfo.value.endpointId, payloadCallback)
            }
            ProcessStateConstants.AUTHENTICATION_DENIED -> {
                Log.d("SNDVM", "Connection denied!")
                _processStateFlow.value = ProcessState(ProcessStateConstants.DISCOVERY_STARTED, "Connection denied")
            }

            ProcessStateConstants.CONNECTION_ESTABLISHED -> {
                // Nothing to do
            }

            ProcessStateConstants.CONNECTION_DENIED -> TODO()

            ProcessStateConstants.SENDING -> {
                val uuid = message
                Log.d("SNDVM", "Will try and send $uuid...")
                viewModelScope.launch {
                    val process = repository.loadProcessByUuid(uuid)
                    if (process != null) {
                        establishedConnections.forEach{ connection ->
                            Log.d("SNDVM", "Sending payload to endpoint ${connection.value.endpointId}...")
                            connectionsClient.sendPayload(
                                connection.value.endpointId,
                                process.toPayload()
                            )
                            Log.d("SNDVM", "Payload presumably sent.")
                        }
                        _processStateFlow.value = ProcessState(ProcessStateConstants.CONNECTION_ESTABLISHED, "OK")
                    }
                }
            }

            ProcessStateConstants.DONE -> {
                Log.d("SNDVM", "Done. Disconnecting everything...")
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopDiscovery()
                _processStateFlow.value = ProcessState(newState, "Done")
            }

            ProcessStateConstants.ERROR -> {
                Log.d("SNDVM", "Error!")
            }
        }
    }

    private fun startDiscovery() {
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

}