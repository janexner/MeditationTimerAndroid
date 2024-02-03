package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.meditationtimer.ui.HeaderText
import com.exner.tools.meditationtimer.ui.ProcessDeleteViewModel
import com.exner.tools.meditationtimer.ui.destinations.destinations.ProcessListDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun ProcessDelete(
    processUuid: String,
    processDeleteViewModel: ProcessDeleteViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val processName by processDeleteViewModel.processName.observeAsState()
    val processIsTarget by processDeleteViewModel.processIsTarget.observeAsState()
    val dependantProcesses by processDeleteViewModel.processChainingDependencies.observeAsState()

    processDeleteViewModel.checkProcess(processUuid)

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderText(text = "Delete Process")
                HorizontalDivider(modifier = Modifier.padding(8.dp))
                Text(
                    text = "You are about to delete a process,"
                )
                Text(text = "'$processName'.")
                // double-check in case there are chains that contain this
                if (processIsTarget == true) {
                    if (dependantProcesses != null && dependantProcesses!!.dependentProcessIdsAndNames.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Other processes link to this one!")
                        Spacer(modifier = Modifier.height(8.dp))
                        if (dependantProcesses!!.dependentProcessIdsAndNames.isNotEmpty()) {
                            dependantProcesses!!.dependentProcessIdsAndNames.forEach {
                                Text(text = it.name)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "If you delete this process, those others will no longer be able to link to it, meaning they will stop when they try to.")
                    }
                }
            }
        },
        bottomBar = {
            FotoTimerDeleteBottomBar(navigator = navigator) {
                processDeleteViewModel.deleteProcess(processUuid)
            }
        }
    )
}

@Composable
fun FotoTimerDeleteBottomBar(
    navigator: DestinationsNavigator,
    deleteAction: () -> Unit,
) {
    BottomAppBar(
        actions = {},
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Delete") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete the process"
                    )
                },
                onClick = {
                    deleteAction()
                    navigator.navigate(ProcessListDestination)
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            )
        }
    )
}
