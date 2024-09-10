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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.meditationtimer.R
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import com.exner.tools.meditationtimer.ui.BodyText
import com.exner.tools.meditationtimer.ui.HeaderText
import com.exner.tools.meditationtimer.ui.ProcessDetailsViewModel
import com.exner.tools.meditationtimer.ui.SmallBodyText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ProcessDeleteDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessEditDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessRunDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun ProcessDetails(
    processUuid: String,
    processDetailsViewModel: ProcessDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by processDetailsViewModel.name.observeAsState()
    val info by processDetailsViewModel.info.observeAsState()
    val processTime by processDetailsViewModel.processTime.observeAsState()
    val intervalTime by processDetailsViewModel.intervalTime.observeAsState()
    val hasAutoChain by processDetailsViewModel.hasAutoChain.observeAsState()
    val gotoId by processDetailsViewModel.gotoUuid.observeAsState()
    val gotoName by processDetailsViewModel.gotoName.observeAsState()
    val currentCategory: MeditationTimerProcessCategory by processDetailsViewModel.currentCategory.collectAsStateWithLifecycle(
        initialValue = MeditationTimerProcessCategory("None", -1L)
    )

    processDetailsViewModel.getProcess(processUuid = processUuid)

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // top - process information
                val modifier1 = Modifier.padding(8.dp)
                HeaderText(
                    text = name ?: "Name",
                    modifier = modifier1
                )
                Text(
                    text = info ?: "",
                    modifier = modifier1.padding(8.dp, 0.dp)
                )
                Text(
                    text = "Category: " + currentCategory.name,
                    modifier = modifier1.padding(8.dp, 0.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(8.dp))
                ProcessTimerData(
                    processTime,
                    intervalTime,
                )
                if (hasAutoChain == true && (null != gotoId) && ("" != gotoId!!)) {
                    if (null != gotoName) {
                        ListItem(
                            headlineContent = { SmallBodyText(text = "After") },
                            supportingContent = { BodyText(text = "Afterwards, '$gotoName' will be started.") },
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
            MeditationTimerDetailsBottomBar(processUuid = processUuid, navigator = navigator)
        }
    )
}

@Composable
fun MeditationTimerDetailsBottomBar(
    processUuid: String,
    navigator: DestinationsNavigator
) {
    BottomAppBar(
        actions = {

            IconButton(onClick = {
                navigator.navigate(
                    ProcessEditDestination(processUuid = processUuid)
                )
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit"
                )
            }

            IconButton(onClick = {
                navigator.navigate(
                    ProcessDeleteDestination(processUuid = processUuid)
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
                        ProcessRunDestination(processUuid = processUuid)
                    )
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            )
        }
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

