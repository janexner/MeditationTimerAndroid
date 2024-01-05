package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.meditationtimer.ui.CategoryListViewModel
import com.exner.tools.meditationtimer.ui.destinations.destinations.ProcessEditDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CategoryList(
    categoryListViewModel: CategoryListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier.fillMaxWidth().padding(innerPadding)
            ) {

            }
        },
        bottomBar = {
            CategoryListBottomBar(navigator)
        }
    )
}

@Composable
private fun CategoryListBottomBar(
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
