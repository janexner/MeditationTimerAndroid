package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AboutDestination
import com.ramcosta.composedestinations.generated.destinations.CategoryListDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessListDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessRunDestination
import com.ramcosta.composedestinations.generated.destinations.SendToNearbyDeviceDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator

@Composable
fun MeditationTimerGlobalScaffold() {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    val destinationsNavigator = navController.rememberDestinationsNavigator()
    val destination = navController.currentDestinationAsState().value

    Scaffold(
        topBar = {
            MeditationTimerTopBar(destination, destinationsNavigator)
        },
        content = { innerPadding ->
            val newPadding = PaddingValues.Absolute(
                innerPadding.calculateLeftPadding(LayoutDirection.Ltr),
                innerPadding.calculateTopPadding(),
                innerPadding.calculateRightPadding(LayoutDirection.Ltr),
                0.dp
            )
            DestinationsNavHost(
                navController = navController,
                navGraph = NavGraphs.root,
                modifier = Modifier.padding(newPadding)
            ) {
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeditationTimerTopBar(
    destination: DestinationSpec?,
    destinationsNavigator: DestinationsNavigator,
) {
    var displayMainMenu by remember { mutableStateOf(false) }
    
    TopAppBar(
        title = { Text(text = "Meditation Timer") },
        navigationIcon = {
            when (destination) {
                ProcessListDestination -> {
                    // no back button here
                }

                ProcessRunDestination -> {
                    // none here, either
                }

                else -> {
                    IconButton(onClick = { destinationsNavigator.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(
                onClick = {
                    displayMainMenu = !displayMainMenu
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
            DropdownMenu(
                expanded = displayMainMenu,
                onDismissRequest = { displayMainMenu = false }
            ) {
                DropdownMenuItem(
                    enabled = destination != CategoryListDestination,
                    text = { Text(text = "Manage categories") },
                    onClick = {
                        displayMainMenu = false
                        destinationsNavigator.navigate(CategoryListDestination())
                    }
                )
                DropdownMenuItem(
                    enabled = destination != SettingsDestination,
                    text = { Text(text = "Settings") },
                    onClick = {
                        displayMainMenu = false
                        destinationsNavigator.navigate(SettingsDestination())
                    }
                )
                DropdownMenuItem(
                    enabled = destination != SendToNearbyDeviceDestination,
                    text = { Text(text = "Share to nearby device") },
                    onClick = {
                        displayMainMenu = false
                        destinationsNavigator.navigate(SendToNearbyDeviceDestination)
                    }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    enabled = destination != AboutDestination,
                    text = { Text(text = "About Meditation Timer") },
                    onClick = {
                        displayMainMenu = false
                        destinationsNavigator.navigate(AboutDestination())
                    }
                )
            }
        }
    )
}
