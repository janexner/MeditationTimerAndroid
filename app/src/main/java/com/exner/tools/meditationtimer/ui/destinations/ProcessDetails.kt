package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.meditationtimer.R
import com.exner.tools.meditationtimer.ui.BodyText
import com.exner.tools.meditationtimer.ui.HeaderText
import com.exner.tools.meditationtimer.ui.ProcessDetailsViewModel
import com.exner.tools.meditationtimer.ui.SmallBodyText
import com.exner.tools.meditationtimer.ui.destinations.destinations.ProcessDeleteDestination
import com.exner.tools.meditationtimer.ui.destinations.destinations.ProcessEditDestination
import com.exner.tools.meditationtimer.ui.destinations.destinations.ProcessRunDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun ProcessDetails(
    processId: Long,
    processDetailsViewModel: ProcessDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by processDetailsViewModel.name.observeAsState()
    val processTime by processDetailsViewModel.processTime.observeAsState()
    val intervalTime by processDetailsViewModel.intervalTime.observeAsState()
    val hasAutoChain by processDetailsViewModel.hasAutoChain.observeAsState()
    val gotoId by processDetailsViewModel.gotoId.observeAsState()
    // this one is the odd one out
    val nextProcessesName by processDetailsViewModel.nextProcessesName.observeAsState()

    processDetailsViewModel.getProcess(processId)

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // top - process information
                ProcessName(name, modifier = Modifier.padding(8.dp))
                Divider(modifier = Modifier.padding(8.dp))
                ProcessTimerData(
                    processTime,
                    intervalTime,
                )
                if (hasAutoChain == true && (null != gotoId) && (-1L < gotoId!!)) {
                    if (null != nextProcessesName) {
                        ListItem(
                            headlineContent = { SmallBodyText(text = "After") },
                            supportingContent = { BodyText(text = "Afterwards, '$nextProcessesName' will be started.") },
                            leadingContent = {
                                Icon(
                                    painterResource(id = R.drawable.ic_baseline_navigate_next_24),
                                    contentDescription = "Process End",
                                )
                            }
                        )
                    } else {
                        ListItem(
                            headlineContent = { SmallBodyText(text = "After") },
                            supportingContent = { BodyText(text = "This process chains into a process that does not exist!") },
                            leadingContent = {
                                Icon(
                                    painterResource(id = R.drawable.baseline_error_24),
                                    contentDescription = "Problem"
                                )
                            }
                        )
                    }
                }
                // middle - spacer
                Spacer(modifier = Modifier)
            }
        },
        bottomBar = {
            MeditationTimerDetailsBottomBar(processId = processId, navigator = navigator)
        }
    )
}

@Composable
fun MeditationTimerDetailsBottomBar(
    processId: Long,
    navigator: DestinationsNavigator
) {
    BottomAppBar(
        actions = {

            IconButton(onClick = {
                navigator.navigate(
                    ProcessEditDestination(processId = processId)
                )
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit"
                )
            }

            IconButton(onClick = {
                navigator.navigate(
                    ProcessDeleteDestination(processId = processId)
                )
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete"
                )
            }

        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Start") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Start"
                    )
                },
                onClick = {
                    navigator.navigate(
                        ProcessRunDestination(processId = processId)
                    )
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            )
        }
    )
}

@Composable
fun ProcessName(name: String?, modifier: Modifier) {
    HeaderText(
        text = name ?: "Name",
        modifier = modifier
    )
}

@Composable
fun ProcessTimerData(
    processTime: String?,
    intervalTime: String?
) {
    ListItem(
        headlineContent = { SmallBodyText(text = "Times") },
        supportingContent = { BodyText(text = "The process runs for $processTime minutes, with an interval every $intervalTime minutes") },
        leadingContent = {
            Icon(
                painterResource(id = R.drawable.ic_baseline_timer_24),
                contentDescription = "Process Times",
            )
        }
    )
}
