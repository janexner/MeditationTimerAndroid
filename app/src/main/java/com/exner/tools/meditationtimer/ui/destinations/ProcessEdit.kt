package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import com.exner.tools.meditationtimer.ui.HeaderText
import com.exner.tools.meditationtimer.ui.ProcessEditViewModel
import com.exner.tools.meditationtimer.ui.SettingsViewModel
import com.exner.tools.meditationtimer.ui.TextAndSwitch
import com.exner.tools.meditationtimer.ui.TextFieldForTimes
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun ProcessEdit(
    processUuid: String?,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val chainToSameCategoryOnly by settingsViewModel.chainToSameCategoryOnly.collectAsStateWithLifecycle()

    val processEditViewModel = hiltViewModel<ProcessEditViewModel, ProcessEditViewModel.ProcessEditViewModelFactory> { factory ->
        factory.create(processUuid, chainToSameCategoryOnly)
    }

    val name by processEditViewModel.name.observeAsState()
    val info by processEditViewModel.info.observeAsState()
    val processTime by processEditViewModel.processTime.observeAsState()
    val intervalTime by processEditViewModel.intervalTime.observeAsState()
    val hasAutoChain by processEditViewModel.hasAutoChain.observeAsState()
    val gotoName by processEditViewModel.gotoName.observeAsState()
    val backgroundUri by processEditViewModel.backgroundUri.observeAsState()
    // some odd ones out
    val processes: List<MeditationTimerProcess> by processEditViewModel.observeProcessesForCurrentCategory.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val currentCategory: MeditationTimerProcessCategory by processEditViewModel.currentCategory.collectAsStateWithLifecycle(
        initialValue = MeditationTimerProcessCategory("All", -1L)
    )
    val categories: List<MeditationTimerProcessCategory> by processEditViewModel.observeCategoriesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val enableExportToActivityTimer by settingsViewModel.enableExportToActivityTimer.collectAsStateWithLifecycle()

    var modified by remember { mutableStateOf(false) }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .consumeWindowInsets(innerPadding)
                    .padding(8.dp)
                    .wrapContentHeight()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                // top - fields
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    TextField(
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    TextField(
                        value = info ?: "Details",
                        onValueChange = {
                            processEditViewModel.updateInfo(it)
                            modified = true
                        },
                        label = { Text(text = "Details / Information") },
                        singleLine = false,
                        modifier = Modifier.weight(0.75f)
                    )
                }
                var categoryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextField(
                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                            modifier = Modifier
                                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                                .fillMaxWidth()
                                .padding(8.dp),
                            readOnly = true,
                            value = if (currentCategory.uid == -1L) "None" else currentCategory.name,
                            onValueChange = {},
                            label = { Text("Process category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        )
                    }
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "None") },
                            onClick = {
                                processEditViewModel.updateCategoryId(
                                    -1L,
                                    chainToSameCategoryOnly
                                )
                                modified = true
                                categoryExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(text = category.name) },
                                onClick = {
                                    processEditViewModel.updateCategoryId(
                                        category.uid,
                                        chainToSameCategoryOnly
                                    )
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
                            TextField(
                                // The `menuAnchor` modifier must be passed to the text field for correctness.
                                modifier = Modifier
                                    .menuAnchor(
                                        type = MenuAnchorType.PrimaryEditable,
                                        enabled = true
                                    )
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                readOnly = true,
                                value = gotoName ?: "",
                                placeholder = { Text("Select next Process") },
                                onValueChange = {},
                                label = { Text("Next Process") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                processes.forEach { process ->
                                    if (!chainToSameCategoryOnly || process.categoryId == -1L || process.categoryId == currentCategory.uid) {
                                        DropdownMenuItem(
                                            text = { Text(text = process.name) },
                                            onClick = {
                                                processEditViewModel.updateGotoUuidAndName(
                                                    process.uuid,
                                                    process.name
                                                )
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
                AnimatedVisibility(visible = enableExportToActivityTimer) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HeaderText("Background Image URL")
                        Text(text = "Will only be used by ActivityTimer for TV.")
                        Text(text = "Setting it here makes sense if you then export the process to ActivityTimer, because it is much easier to type, or to copy and paste a URL on your phone.")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            TextField(
                                value = backgroundUri ?: "",
                                onValueChange = {
                                    processEditViewModel.updateBackgroundUri(it)
                                    modified = true
                                },
                                label = { Text(text = "URL") },
                                placeholder = { Text(text = "URL of an image") },
                                singleLine = false,
                                modifier = Modifier.weight(0.75f)
                            )
                        }
                        if (null != backgroundUri && "" != backgroundUri) {
                            AsyncImage(
                                model = backgroundUri,
                                contentDescription = "Background image",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.imePadding(),
                actions = {},
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Save") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Save the process"
                            )
                        },
                        onClick = {
                            processEditViewModel.commitProcess()
                            navigator.navigateUp()
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    )
                }
            )
        }
    )
}
