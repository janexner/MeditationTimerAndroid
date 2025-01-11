package com.exner.tools.meditationtimer.ui.destinations.wrappers

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.exner.tools.meditationtimer.network.Permissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.wrapper.DestinationWrapper

object AskForPermissionsWrapper : DestinationWrapper {

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun <T> DestinationScope<T>.Wrap(screenContent: @Composable () -> Unit) {

        val context = LocalContext.current
        val permissions = Permissions(context = context)
        val permissionsNeeded =
            rememberMultiplePermissionsState(
                permissions = permissions.getAllNecessaryPermissionsAsListOfStrings(),
                onPermissionsResult = { results ->
                    results.forEach { result ->
                        Log.d("PGW PERMISSIONS", "${result.key} : ${result.value}")
                    }
                }
            )

        Scaffold(
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(8.dp)
                        .fillMaxSize()
                ) {
                    Text(text = "If you would like to send processes to your TV running Activity Timer, this app needs permission for Bluetooth, WiFi, and the discovery of nearby devices, which may also need location permissions.")
                }
            },
            bottomBar = {
                PermissionsGrantedWrapperBottomBar(
                    requestPermissionsAction = permissionsNeeded::launchMultiplePermissionRequest
                )
            }
        )

        if (permissionsNeeded.allPermissionsGranted) {
            screenContent()
        }
    }
}

@Composable
fun PermissionsGrantedWrapperBottomBar(
    requestPermissionsAction: () -> Unit
) {
    BottomAppBar(
        actions = {
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Get permissions") },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                        contentDescription = "Get permissions"
                    )
                },
                onClick = {
                    requestPermissionsAction()
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            )
        }
    )
}