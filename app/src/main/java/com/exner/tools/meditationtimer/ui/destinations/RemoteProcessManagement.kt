package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.meditationtimer.ui.BodyText
import com.exner.tools.meditationtimer.ui.HeaderText
import com.exner.tools.meditationtimer.ui.ProcessListViewModel
import com.exner.tools.meditationtimer.ui.RemoteProcessManagementViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

sealed class ProcessListTabs(val name: String) {
    data object LocalOnlyTab : ProcessListTabs("Local")
    data object RemoteOnlyTab : ProcessListTabs("Remote")
}

@Destination
@Composable
fun RemoteProcessManagement(
    processListViewModel: ProcessListViewModel = hiltViewModel(),
    remoteProcessManagementViewModel: RemoteProcessManagementViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val listStateLocal = rememberLazyListState()
    val localProcesses by processListViewModel.observeProcessesForCurrentCategory.collectAsStateWithLifecycle()

    val listOfProcessIdsToUpload = remember {
        mutableStateListOf<String>()
    }

    val loadingRemote = remember { mutableStateOf(false) }
    val listStateRemote = rememberLazyListState()
    val remoteProcesses by remoteProcessManagementViewModel.remoteProcessesRaw.collectAsStateWithLifecycle()

    val listOfProcessUuidsToImport = remember {
        mutableStateListOf<String>()
    }

    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabItems = listOf(ProcessListTabs.RemoteOnlyTab)

    processListViewModel.updateCategoryId(-2L)
    remoteProcessManagementViewModel.loadRemoteProcesses()

    Scaffold(
        content = { innerPadding ->
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = tabIndex,
                ) {
                    tabItems.forEachIndexed { index, settingsTabs ->
                        Tab(
                            selected = index == tabIndex,
                            onClick = { tabIndex = index },
                            text = { BodyText(text = settingsTabs.name) }
                        )
                    }
                }
                when (tabIndex) {
                    -1 -> Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Tick processes you want to upload, then use the 'Upload Processes' button to upload them.")
                        Spacer(modifier = Modifier.size(8.dp))
                        LazyColumn(
                            state = listStateLocal,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(localProcesses.size) { index ->
                                val genericProcess = localProcesses[index]
                                Surface {
                                    ListItem(
                                        leadingContent = {
                                            Checkbox(
                                                checked = listOfProcessIdsToUpload.contains(
                                                    genericProcess.uuid
                                                ),
                                                onCheckedChange = { checked ->
                                                    if (checked) {
                                                        listOfProcessIdsToUpload.add(
                                                            genericProcess.uuid
                                                        )
                                                    } else {
                                                        listOfProcessIdsToUpload.remove(
                                                            genericProcess.uuid
                                                        )
                                                    }
                                                })
                                        },
                                        headlineContent = {
                                            HeaderText(text = genericProcess.name)
                                        },
                                        supportingContent = {
                                            BodyText(text = "${genericProcess.processTime} / ${genericProcess.intervalTime} > ${genericProcess.gotoUuid}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                    0 -> Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Tick processes you want to import, then use the 'Import Processes' button to import them.")
                        Spacer(modifier = Modifier.size(8.dp))
                        LazyColumn(
                            state = listStateRemote,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(remoteProcesses.size) { index ->
                                val genericProcess = remoteProcesses[index]
                                Surface {
                                    ListItem(
                                        leadingContent = {
                                            Checkbox(
                                                checked = listOfProcessUuidsToImport.contains(
                                                    genericProcess.uuid
                                                ),
                                                onCheckedChange = { checked ->
                                                    if (checked) {
                                                        listOfProcessUuidsToImport.add(
                                                            genericProcess.uuid
                                                        )
                                                    } else {
                                                        listOfProcessUuidsToImport.remove(
                                                            genericProcess.uuid
                                                        )
                                                    }
                                                })
                                        },
                                        headlineContent = {
                                            HeaderText(text = genericProcess.name)
                                        },
                                        supportingContent = {
                                            val nextOrNotText = if (null != genericProcess.gotoName) " > ${genericProcess.gotoName}" else ""
                                            BodyText(text = "${genericProcess.processTime} / ${genericProcess.intervalTime}$nextOrNotText")
                                        }
                                    )
                                }
                            }

                            // load indicator
                            item {
                                if (loadingRemote.value) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(50.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                actions = {},
                floatingActionButton = {
                    if (tabIndex == -1 && listOfProcessIdsToUpload.size > 0) {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Upload Processes") },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Upload"
                                )
                            },
                            onClick = {
                                // TODO sync them to server!
                                navigator.navigateUp()
                            },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        )
                    } else if (tabIndex == 0 && listOfProcessUuidsToImport.size > 0) {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Import Processes") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.AddCircle,
                                    contentDescription = "Import"
                                )
                            },
                            onClick = {
                                remoteProcessManagementViewModel.importProcessesFromRemote(listOfProcessUuidsToImport)
                                navigator.navigateUp()
                            },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        )
                    }
                }
            )
        }
    )
}

