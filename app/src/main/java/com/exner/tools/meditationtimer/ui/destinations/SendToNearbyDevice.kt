package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.meditationtimer.ui.ProcessStateConstants
import com.exner.tools.meditationtimer.ui.SendToNearbyDeviceViewModel
import com.exner.tools.meditationtimer.ui.tools.Permissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

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
        rememberMultiplePermissionsState(permissions = permissions.getAllNecessaryPermissionsAsListOfStrings())

    val processState by sendToNearbyDeviceViewModel.processStateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // some sanity checking for state
        if (processState.currentState == ProcessStateConstants.AWAITING_PERMISSIONS && permissionsNeeded.allPermissionsGranted) {
            sendToNearbyDeviceViewModel.setCurrentState(ProcessStateConstants.AWAITING_DISCOVERY)
        }

        // UI, depending on state
        when (processState.currentState) {
            ProcessStateConstants.AWAITING_PERMISSIONS -> {
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

            ProcessStateConstants.PERMISSIONS_DENIED -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = "Without the necessary permissions, importing from nearby devices is not possible.")
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

            ProcessStateConstants.AWAITING_DISCOVERY -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = "All permissions OK, looking for devices...")
                }
            }

            ProcessStateConstants.DISCOVERED -> TODO()
            ProcessStateConstants.CONNECTED -> TODO()
            ProcessStateConstants.RECEIVING -> TODO()
            ProcessStateConstants.DISCONNECTED -> TODO()
            ProcessStateConstants.ERROR -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = "Some error occurred. It may help to move away from this screen and try it all again.")
                }
            }
        }
    }
}
