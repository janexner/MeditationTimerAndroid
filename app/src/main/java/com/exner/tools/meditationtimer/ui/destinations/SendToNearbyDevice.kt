package com.exner.tools.meditationtimer.ui.destinations

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.meditationtimer.network.Permissions
import com.exner.tools.meditationtimer.network.TimerEndpoint
import com.exner.tools.meditationtimer.ui.ProcessState
import com.exner.tools.meditationtimer.ui.ProcessStateConstants
import com.exner.tools.meditationtimer.ui.SendToNearbyDeviceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.nearby.Nearby
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.meditationtimer.ui.BodyText


@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun SendToNearbyDevice(
    sendToNearbyDeviceViewModel: SendToNearbyDeviceViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val permissions = Permissions(context = context)

    val permissionsNeeded =
        rememberMultiplePermissionsState(
            permissions = permissions.getAllNecessaryPermissionsAsListOfStrings()
        )

    val processState by sendToNearbyDeviceViewModel.processStateFlow.collectAsState()

    val connectionsClient = Nearby.getConnectionsClient(context)
    sendToNearbyDeviceViewModel.provideConnectionsClient(connectionsClient = connectionsClient)

    val discoveredEndpoints: List<TimerEndpoint> by sendToNearbyDeviceViewModel.endpointsFound.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    // some sanity checking for state
    Log.d("STND", "All permissions granted: ${permissionsNeeded.allPermissionsGranted}")
    if (processState.currentState == ProcessStateConstants.AWAITING_PERMISSIONS && permissionsNeeded.allPermissionsGranted) {
        sendToNearbyDeviceViewModel.transitionToNewState(ProcessStateConstants.PERMISSIONS_GRANTED)
    } else if (processState.currentState == ProcessStateConstants.AWAITING_PERMISSIONS) {
        Log.d("STND", "Needed permissions: ${permissions.getAllNecessaryPermissionsAsListOfStrings()}")
    }

    Scaffold(
        content = { innerPadding ->
            val configuration = LocalConfiguration.current
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "Now at ${processState.currentState.name}")
                Spacer(modifier = Modifier.size(16.dp))

                // UI, depending on state
                when (processState.currentState) {
                    ProcessStateConstants.AWAITING_PERMISSIONS -> {
                        ProcessStateAwaitingPermissionsScreen(permissionsNeeded)
                    }

                    ProcessStateConstants.PERMISSIONS_GRANTED -> {
                        ProcessStatePermissionsGrantedScreen()
                    }

                    ProcessStateConstants.PERMISSIONS_DENIED -> {
                        ProcessStateAwaitingPermissionsScreen(permissionsNeeded)
                    }

                    ProcessStateConstants.STARTING_DISCOVERY -> {
                        ProcessStateStartingDiscovery()
                    }

                    ProcessStateConstants.DISCOVERY_STARTED -> {
                        ProcessStateDiscoveryStartedScreen(discoveredEndpoints) { endpointId ->
                            sendToNearbyDeviceViewModel.transitionToNewState(ProcessStateConstants.PARTNER_CHOSEN, endpointId)
                        }
                    }

                    ProcessStateConstants.PARTNER_CHOSEN -> {
                        ProcessStatePartnerFoundScreen()
                    }

                    ProcessStateConstants.CONNECTING -> {
                        ProcessStateConnectingScreen()
                    }

                    ProcessStateConstants.AUTHENTICATION_OK -> {}
                    ProcessStateConstants.AUTHENTICATION_DENIED -> {}
                    ProcessStateConstants.CONNECTION_ESTABLISHED -> {}
                    ProcessStateConstants.CONNECTION_DENIED -> {}
                    ProcessStateConstants.SENDING -> {}
                    ProcessStateConstants.DISCONNECTED -> {}
                    ProcessStateConstants.DONE -> {
                        ProcessStateDoneScreen()
                    }

                    ProcessStateConstants.CANCELLED -> {
                        ProcessStateCancelledScreen()
                    }

                    ProcessStateConstants.ERROR -> {
                        ProcessStateErrorScreen()
                    }

                }
            }
        },
        bottomBar = {
            SendToNearbyBottomBar(
                processState = processState,
                transition = sendToNearbyDeviceViewModel::transitionToNewState
            )
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ProcessStateAwaitingPermissionsScreen(permissionsNeeded: MultiplePermissionsState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "If you would like to send processes to your TV, this app needs permission for Bluetooth, WiFi, and the discovery of nearby devices, which may also need location permissions.")
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = {
                permissionsNeeded.launchMultiplePermissionRequest()
            }
        ) {
            Text(text = "Request permissions")
        }
    }
}

@Composable
private fun ProcessStatePermissionsGrantedScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "All permissions OK.")
    }
}

@Composable
private fun ProcessStateStartingDiscovery() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Starting discovery...")
    }
}

@Composable
private fun ProcessStateDiscoveryStartedScreen(discoveredEndpoints: List<TimerEndpoint>, onItemClick : (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .padding(PaddingValues(8.dp))
            .fillMaxSize()
    ) {
        item {
            Text(text = "Looking for partners... once found, tap to connect.")
        }
        items(discoveredEndpoints) { endpoint ->
            Box(modifier = Modifier.padding(PaddingValues(8.dp)).clickable {
                onItemClick(endpoint.endpointId)
            }) {
                BodyText(text = endpoint.endpointId)
            }
        }
    }
}

@Composable
private fun ProcessStatePartnerFoundScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Connecting to partner...")
    }
}

@Composable
private fun ProcessStateConnectingScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Connecting to partner...")
    }
}

@Composable
private fun ProcessStateDoneScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "All done.")
    }
}

@Composable
private fun ProcessStateCancelledScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Cancelled.")
    }
}

@Composable
private fun ProcessStateErrorScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Some error occurred. It may help to move away from this screen and try it all again.")
    }
}

@Composable
fun SendToNearbyBottomBar(
    processState: ProcessState,
    transition: (ProcessStateConstants, String) -> Unit
) {
    BottomAppBar(
        actions = {
            IconButton(onClick = {
                transition(ProcessStateConstants.CANCELLED, "Cancelled")
            }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Cancel"
                )
            }
        },
        floatingActionButton = {
            when (processState.currentState) {
                ProcessStateConstants.AWAITING_PERMISSIONS -> {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Request Permissions") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Request permissions"
                            )
                        },
                        onClick = {
                            // how do we do this?
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    )
                }
                ProcessStateConstants.PERMISSIONS_GRANTED -> {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Discover Devices") },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Discover Devices"
                            )
                        },
                        onClick = {
                            transition(ProcessStateConstants.DISCOVERY_STARTED, "Discovering")
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    )
                }
                ProcessStateConstants.AUTHENTICATION_OK -> {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Send") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Send Processes"
                            )
                        },
                        onClick = {
                            transition(ProcessStateConstants.SENDING, "Sending")
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    )
                }
                else -> {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Cancel") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Cancel"
                            )
                        },
                        onClick = {
                            transition(ProcessStateConstants.CANCELLED, "Cancel")
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    )
                }
            }
        }
    )
}
