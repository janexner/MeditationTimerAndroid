package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.meditationtimer.ui.HeaderText
import com.exner.tools.meditationtimer.ui.ProcessEditViewModel
import com.exner.tools.meditationtimer.ui.TextAndSwitch
import com.exner.tools.meditationtimer.ui.TextFieldForTimes
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun ProcessEdit(
    processId: Long,
    processEditViewModel: ProcessEditViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by processEditViewModel.name.observeAsState()
    val processTime by processEditViewModel.processTime.observeAsState()
    val intervalTime by processEditViewModel.intervalTime.observeAsState()
    val hasAutoChain by processEditViewModel.hasAutoChain.observeAsState()
    // some odd ones out
    val nextProcessesName by processEditViewModel.nextProcessesName.observeAsState()
    val processIdsAndNames by processEditViewModel.processIdsAndNames.observeAsState()
    val categoryName by processEditViewModel.categoryName.observeAsState()
    val categoryIdsAndNames by processEditViewModel.categoryIdsAndNames.observeAsState()

    processEditViewModel.getProcess(processId)
    processEditViewModel.getProcessIdsAndNames()
    processEditViewModel.getCategoryIdsAndNames()

    var modified by remember { mutableStateOf(false) }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .wrapContentHeight()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // top - fields
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = name ?: "Name",
                        onValueChange = {
                            processEditViewModel.updateName(it)
                            modified = true
                        },
                        label = { Text(text = "Process name") },
                        singleLine = true,
                        modifier = Modifier.weight(0.75f)
                    )
                }
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
                        value = categoryName ?: "None",
                        placeholder = { Text("Select a Category") },
                        onValueChange = {},
                        label = { Text("Process category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "None") },
                            onClick = {
                                processEditViewModel.updateCategoryId(-1L)
                                processEditViewModel.updateCategoryName("None")
                                modified = true
                                categoryExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        categoryIdsAndNames?.forEach { idAndName ->
                            DropdownMenuItem(
                                text = { Text(text = idAndName.name) },
                                onClick = {
                                    processEditViewModel.updateCategoryId(idAndName.uid)
                                    processEditViewModel.updateCategoryName(idAndName.name)
                                    modified = true
                                    categoryExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
                HeaderText(text = "Times")
                TextFieldForTimes(
                    value = processTime ?: 30,
                    label = { Text(text = "Process time (total) in minutes") },
                    onValueChange = {
                        processEditViewModel.updateProcessTime(it)
                        modified = true
                    },
                )
                TextFieldForTimes(
                    value = intervalTime ?: 5,
                    label = { Text(text = "Interval time in minutes") },
                    onValueChange = {
                        processEditViewModel.updateIntervalTime(it)
                        modified = true
                    },
                )
                HeaderText(text = "After the process")
                TextAndSwitch(
                    text = "Automatically start another process",
                    checked = hasAutoChain == true,
                ) {
                    processEditViewModel.updateHasAutoChain(it)
                    modified = true
                }
                AnimatedVisibility(visible = hasAutoChain == true) {
                    var expanded by remember { mutableStateOf(false) }
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                // The `menuAnchor` modifier must be passed to the text field for correctness.
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                value = nextProcessesName ?: "",
                                placeholder = { Text("Select next Process") },
                                onValueChange = {},
                                label = { Text("Next Process") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                processIdsAndNames?.forEach { idAndName ->
                                    DropdownMenuItem(
                                        text = { Text(text = idAndName.name) },
                                        onClick = {
                                            processEditViewModel.updateGotoId(idAndName.uid)
                                            processEditViewModel.updateNextProcessesName(idAndName.name)
                                            modified = true
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            MeditationTimerEditBottomBar(navigator = navigator, commitProcess = {
                processEditViewModel.commitProcess()
            })
        }
    )
}

@Composable
fun MeditationTimerEditBottomBar(navigator: DestinationsNavigator, commitProcess: () -> Unit) {
    BottomAppBar(
        actions = {},
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Save") },
                icon = {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = "Save the process")
                },
                onClick = {
                    commitProcess()
                    navigator.navigateUp()
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            )
        }
    )
}
