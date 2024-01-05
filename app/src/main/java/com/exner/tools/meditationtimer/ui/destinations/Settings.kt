package com.exner.tools.meditationtimer.ui.destinations

import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.meditationtimer.ui.LockScreenOrientation
import com.exner.tools.meditationtimer.ui.SettingsViewModel
import com.exner.tools.meditationtimer.ui.TextAndSwitch
import com.exner.tools.meditationtimer.ui.TextFieldForTimes
import com.exner.tools.meditationtimer.ui.theme.MeditationTimerTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun Settings(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val beforeCountingWait by settingsViewModel.beforeCountingWait.observeAsState()
    val howLongToWaitBeforeCounting by settingsViewModel.howLongToWaitBeforeCounting.observeAsState()
    val countBackwards by settingsViewModel.countBackwards.observeAsState()
    val chainToSameCategoryOnly by settingsViewModel.chainToSameCategoryOnly.observeAsState()
    val noSounds by settingsViewModel.noSounds.observeAsState()
    val vibrateEnabled by settingsViewModel.vibrateEnabled.observeAsState()
    val sortProcessesAlphabetically by settingsViewModel.sortProcessesAlphabetically.observeAsState()

    // unlock screen rotation
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR)

    // show vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TextAndSwitch(text = "Before counting, wait", checked = beforeCountingWait == true) {
            settingsViewModel.updateBeforeCountingWait(it)
        }
        AnimatedVisibility(visible = beforeCountingWait == true) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextFieldForTimes(
                    value = howLongToWaitBeforeCounting ?: 5,
                    label = { Text(text = "How long to wait before counting (seconds)") },
                    onValueChange = {
                        settingsViewModel.updateHowLongToWaitBeforeCounting(it)
                    }
                ) { Text(text = "5") }
            }
        }
        TextAndSwitch(
            text = "Count backwards (down to 0)",
            checked = countBackwards == true
        ) {
            settingsViewModel.updateCountBackwards(it)
        }
        TextAndSwitch(
            text = "No Sound (count silently)",
            checked = noSounds == true
        ) {
            settingsViewModel.updateNoSounds(it)
        }
        TextAndSwitch(
            text = "Vibrate",
            checked = vibrateEnabled == true
        ) {
            settingsViewModel.updateVibrateEnabled(it)
        }
        TextAndSwitch(
            text = "Sort processes alphabetically",
            checked = sortProcessesAlphabetically == true
        ) {
            settingsViewModel.updateSortProcessesAlphabetically(it)
        }
        TextAndSwitch(
            text = "Chain to same category only",
            checked = chainToSameCategoryOnly == true
        ) {
            settingsViewModel.updateChainToSameCategoryOnly(it)
        }
    }

}

@Preview(name = "Nexus 5", group = "Medium", showSystemUi = true, device = Devices.NEXUS_5)
@Preview(name = "Pixel 4 XL", group = "Medium", showSystemUi = true, device = Devices.PIXEL_4_XL)
@Preview(name = "Big", group = "Medium", showSystemUi = true, fontScale = 1.3f)
@Preview(
    name = "Phone normal Font",
    group = "Medium Landscape",
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Preview(
    name = "Phone big Font",
    group = "Medium Landscape",
    showSystemUi = true,
    device = "spec:width=288dp,height=608dp,dpi=560,isRound=false,chinSize=0dp,orientation=landscape",
    fontScale = 1.3f
)
@Composable
fun SettingsPreview() {
    MeditationTimerTheme {
        Settings()
    }
}
