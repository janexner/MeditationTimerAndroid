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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.meditationtimer.ui.BodyText
import com.exner.tools.meditationtimer.ui.HeaderText
import com.exner.tools.meditationtimer.ui.RemoteProcessManagementViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun RemoteProcessManagement(
    remoteProcessManagementViewModel: RemoteProcessManagementViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val loading = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val remoteProcesses by remoteProcessManagementViewModel.remoteProcessesRaw.collectAsStateWithLifecycle()

    val listOfProcessUuidsToImport = remember {
        mutableStateListOf<String>()
    }
    
    remoteProcessManagementViewModel.loadRemoteProcesses()

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Tick processes you want to import, then use the 'Import processes' button to import them.")
                Spacer(modifier = Modifier.size(8.dp))
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(remoteProcesses.size) { index ->
                        val genericProcess = remoteProcesses[index]
                        Surface {
                            ListItem(
                                leadingContent = {
                                    Checkbox(
                                        checked = listOfProcessUuidsToImport.contains(genericProcess.uuid),
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                listOfProcessUuidsToImport.add(genericProcess.uuid)
                                            } else {
                                                listOfProcessUuidsToImport.remove(genericProcess.uuid)
                                            }
                                        })
                                },
                                headlineContent = {
                                    HeaderText(text = genericProcess.name)
                                },
                                supportingContent = {
                                    BodyText(text = "${genericProcess.processTime} / ${genericProcess.intervalTime} > ${genericProcess.gotoId}")
                                }
                            )
                        }
                    }

                    // load indicator
                    item {
                        if (loading.value) {
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
        },
        bottomBar = {
            FotoTimerRemoteProcessManagementBottomBar(navigator = navigator)
        }
    )
}

@Composable
fun FotoTimerRemoteProcessManagementBottomBar(navigator: DestinationsNavigator) {
    BottomAppBar(
        actions = {},
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Import Processes") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Import"
                    )
                },
                onClick = {
                    navigator.navigateUp()
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            )
        }
    )
}