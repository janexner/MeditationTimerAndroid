package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import com.exner.tools.meditationtimer.ui.BodyText
import com.exner.tools.meditationtimer.ui.HeaderText
import com.exner.tools.meditationtimer.ui.ProcessListViewModel
import com.exner.tools.meditationtimer.ui.SettingsViewModel
import com.exner.tools.meditationtimer.ui.destinations.destinations.ProcessDetailsDestination
import com.exner.tools.meditationtimer.ui.destinations.destinations.ProcessEditDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun ProcessList(
    processListViewModel: ProcessListViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val processes: List<MeditationTimerProcess> by processListViewModel.observeProcessesForCurrentCategory.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val currentCategory: MeditationTimerProcessCategory by processListViewModel.currentCategory.collectAsStateWithLifecycle(
        initialValue = MeditationTimerProcessCategory("All", -1L)
    )
    val categories: List<MeditationTimerProcessCategory> by processListViewModel.observeCategoriesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val onlyShowFirstInChain = settingsViewModel.onlyShowFirstInChain.collectAsStateWithLifecycle()

    var modified by remember { mutableStateOf(false) }

    processListViewModel.updateOnlyFirstState(onlyShowFirstInChain.value)

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                var categoryExpanded by remember {
                    mutableStateOf(false)
                }
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        // The `menuAnchor` modifier must be passed to the text field for correctness.
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = currentCategory.name,
                        onValueChange = {},
                        label = { Text("Process category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "All") },
                            onClick = {
                                processListViewModel.updateCategoryId(-2L)
                                modified = true
                                categoryExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(text = category.name) },
                                onClick = {
                                    processListViewModel.updateCategoryId(category.uid)
                                    modified = true
                                    categoryExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(text = "None") },
                            onClick = {
                                processListViewModel.updateCategoryId(-1L)
                                modified = true
                                categoryExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(innerPadding)
                ) {
                    items(count = processes.size) { meditationTimerProcess ->
                        val mtProcess = processes[meditationTimerProcess]
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    navigator.navigate(
                                        ProcessDetailsDestination(
                                            processId = mtProcess.uid,
                                        )
                                    )
                                },
                        ) {
                            var supText = "${mtProcess.processTime}/${mtProcess.intervalTime}"
                            if (mtProcess.hasAutoChain && null != mtProcess.gotoUuid && mtProcess.gotoUuid != "") {
                                supText += ". Next: '${mtProcess.gotoName}'"
                            }
                            ListItem(
                                headlineContent = { HeaderText(text = mtProcess.name) },
                                supportingContent = { BodyText(text = supText) }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            MeditationTimerListBottomBar(navigator)
        }
    )
}

@Composable
private fun MeditationTimerListBottomBar(
    navigator: DestinationsNavigator
) {
    BottomAppBar(
        actions = {
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Add") },
                icon = {
                    Icon(Icons.Filled.Add, "Add a process")
                },
                onClick = {
                    navigator.navigate(
                        ProcessEditDestination(-1)
                    )
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            )
        }
    )
}
