package com.exner.tools.meditationtimer.ui.destinations

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.meditationtimer.steps.ProcessDisplayStepAction
import com.exner.tools.meditationtimer.steps.ProcessLeadInDisplayStepAction
import com.exner.tools.meditationtimer.ui.BigTimerText
import com.exner.tools.meditationtimer.ui.KeepScreenOn
import com.exner.tools.meditationtimer.ui.MediumTimerAndIntervalText
import com.exner.tools.meditationtimer.ui.NotesText
import com.exner.tools.meditationtimer.ui.ProcessRunViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.time.Duration.Companion.seconds

@Destination<RootGraph>
@Composable
fun ProcessRun(
    processUuid: String,
    navigator: DestinationsNavigator
) {
    val processRunViewModel = hiltViewModel<ProcessRunViewModel, ProcessRunViewModel.ProcessRunViewModelFactory> { factory ->
        factory.create(processUuid) { navigator.navigateUp() }
    }

    val displayAction by processRunViewModel.displayAction.observeAsState()
    val numberOfSteps by processRunViewModel.numberOfSteps.observeAsState()
    val currentStepNumber by processRunViewModel.currentStepNumber.observeAsState()
    val hasLoop by processRunViewModel.hasLoop.observeAsState()
    val hasHours by processRunViewModel.hasHours.observeAsState()
    val showStages by processRunViewModel.showStages.observeAsState()

    // Always!
    KeepScreenOn()

    Scaffold(
        content = { innerPadding ->
            val configuration = LocalConfiguration.current
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                // first, a nice process indicator (if possible)
                if (hasLoop != true) {
                    val currentProgress =
                        if (numberOfSteps != null && numberOfSteps != 0) currentStepNumber!!.toFloat() / numberOfSteps!! else 0.0f
                    LinearProgressIndicator(
                        progress = { currentProgress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // show the display, depending on where we are right now
                when (displayAction) {
                    is ProcessLeadInDisplayStepAction -> {
                        // TODO
                        val plAction = (displayAction as ProcessLeadInDisplayStepAction)
                        BigTimerText(
                            duration = plAction.currentLeadInTime.seconds,
                            hasHours == true
                        )
                    }

                    is ProcessDisplayStepAction -> {
                        // TODO
                        val pdAction = (displayAction as ProcessDisplayStepAction)
                        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                BigTimerText(
                                    duration = pdAction.currentIntervalTime.seconds,
                                    withHours = hasHours == true,
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .alignByBaseline()
                                )
                                if (showStages == true) {
                                    MediumTimerAndIntervalText(
                                        duration = pdAction.currentProcessTime.seconds,
                                        withHours = hasHours == true,
                                        intervalText = "${pdAction.currentRound} of ${pdAction.totalRounds}",
                                        modifier = Modifier.alignByBaseline()
                                    )
                                }
                            }
                        } else {
                            BigTimerText(
                                duration = pdAction.currentIntervalTime.seconds,
                                withHours = hasHours == true
                            )
                            if (showStages == true) {
                                MediumTimerAndIntervalText(
                                    duration = pdAction.currentProcessTime.seconds,
                                    withHours = hasHours == true,
                                    intervalText = "${pdAction.currentRound} of ${pdAction.totalRounds}"
                                )
                            }
                            // show notes
                            Spacer(Modifier.fillMaxHeight(0.3f))
                            NotesText(notesText = pdAction.currentNotes)
                        }
                    }

                    else -> {
                        // nothing to do for us
                    }
                }
            }
        },
        bottomBar = {
            FotoTimerRunBottomBar(navigator = navigator) { processRunViewModel.cancel() }
        }
    )
}

@Composable
fun FotoTimerRunBottomBar(navigator: DestinationsNavigator, cancelAction: () -> Unit) {
    BottomAppBar(
        actions = {},
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Stop") },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Stop"
                    )
                },
                onClick = {
                    cancelAction()
                    navigator.navigateUp()
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            )
        }
    )
}
