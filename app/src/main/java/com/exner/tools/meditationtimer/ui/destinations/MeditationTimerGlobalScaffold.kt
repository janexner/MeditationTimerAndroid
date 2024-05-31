package com.exner.tools.meditationtimer.ui.destinations

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavHostController
import com.exner.tools.meditationtimer.ui.destinations.destinations.AboutDestination
import com.exner.tools.meditationtimer.ui.destinations.destinations.CategoryListDestination
import com.exner.tools.meditationtimer.ui.destinations.destinations.Destination
import com.exner.tools.meditationtimer.ui.destinations.destinations.ProcessListDestination
import com.exner.tools.meditationtimer.ui.destinations.destinations.ProcessRunDestination
import com.exner.tools.meditationtimer.ui.destinations.destinations.SettingsDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine

@Composable
fun MeditationTimerGlobalScaffold() {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    val destination = navController.appCurrentDestinationAsState().value

    Scaffold(
        topBar = {
            MeditationTimerTopBar(destination, navController)
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
    destination: Destination?,
    navController: NavHostController,
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
                    IconButton(onClick = { navController.navigateUp() }) {
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
                        navController.navigate(CategoryListDestination())
                    }
                )
                DropdownMenuItem(
                    enabled = destination != SettingsDestination,
                    text = { Text(text = "Settings") },
                    onClick = {
                        displayMainMenu = false
                        navController.navigate(SettingsDestination())
                    }
                )
                DropdownMenuItem(
                    enabled = destination != AboutDestination,
                    text = { Text(text = "About Meditation Timer") },
                    onClick = {
                        displayMainMenu = false
                        navController.navigate(AboutDestination())
                    }
                )
            }
        }
    )
}
