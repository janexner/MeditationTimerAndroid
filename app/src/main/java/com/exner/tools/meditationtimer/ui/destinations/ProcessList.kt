package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import com.exner.tools.meditationtimer.ui.BodyText
import com.exner.tools.meditationtimer.ui.CategoryListDefinitions
import com.exner.tools.meditationtimer.ui.HeaderText
import com.exner.tools.meditationtimer.ui.ProcessListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ProcessDetailsDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessEditDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(start = true)
@Composable
fun ProcessList(
    processListViewModel: ProcessListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val processes: List<MeditationTimerProcess> by processListViewModel.observeProcessesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val currentCategory: MeditationTimerProcessCategory by processListViewModel.currentCategory.collectAsStateWithLifecycle(
        initialValue = MeditationTimerProcessCategory("All", CategoryListDefinitions.CATEGORY_UID_ALL)
    )
    val categories: List<MeditationTimerProcessCategory> by processListViewModel.observeCategoriesRaw.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    var modified by remember { mutableStateOf(false) }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                var categoryExpanded by remember {
                    mutableStateOf(false)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 0.dp)
                        .wrapContentSize(Alignment.TopEnd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Category: ")
                        Button(
                            onClick = { categoryExpanded = true }
                        ) {
                            Text(text = currentCategory.name)
                        }
                    }
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "All") },
                            onClick = {
                                processListViewModel.updateCategoryId(CategoryListDefinitions.CATEGORY_UID_ALL)
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
                                processListViewModel.updateCategoryId(CategoryListDefinitions.CATEGORY_UID_NONE)
                                modified = true
                                categoryExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }

                val filteredProcesses = if (currentCategory.uid == CategoryListDefinitions.CATEGORY_UID_ALL) {
                    processes
                } else if (currentCategory.uid == CategoryListDefinitions.CATEGORY_UID_NONE) {
                    processes.filter { process ->
                        CategoryListDefinitions.CATEGORY_UID_NONE == process.categoryId || 0L == process.categoryId || null == process.categoryId
                    }
                } else {
                    processes.filter { process ->
                        currentCategory.uid == process.categoryId
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(innerPadding)
                ) {
                    items(items = filteredProcesses, key = {it.uuid}) { mtProcess ->
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    navigator.navigate(
                                        ProcessDetailsDestination(
                                            processUuid = mtProcess.uuid,
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
            BottomAppBar(
                actions = {
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Add") },
                        icon = {
                            Icon(Icons.Default.Add, "Add a process")
                        },
                        onClick = {
                            navigator.navigate(
                                ProcessEditDestination(null)
                            )
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    )
                }
            )
        }
    )
}

