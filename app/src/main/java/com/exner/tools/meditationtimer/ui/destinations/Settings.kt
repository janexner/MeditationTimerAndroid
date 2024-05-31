package com.exner.tools.meditationtimer.ui.destinations

import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    val nightMode by settingsViewModel.nightMode.collectAsStateWithLifecycle()
    val beforeCountingWait by settingsViewModel.beforeCountingWait.collectAsStateWithLifecycle()
    val howLongToWaitBeforeCounting by settingsViewModel.howLongToWaitBeforeCounting.collectAsStateWithLifecycle()
    val countBackwards by settingsViewModel.countBackwards.collectAsStateWithLifecycle()
    val chainToSameCategoryOnly by settingsViewModel.chainToSameCategoryOnly.collectAsStateWithLifecycle()
    val noSounds by settingsViewModel.noSounds.collectAsStateWithLifecycle()
    val vibrateEnabled by settingsViewModel.vibrateEnabled.collectAsStateWithLifecycle()
    val onlyShowFirstInChain by settingsViewModel.onlyShowFirstInChain.collectAsStateWithLifecycle()

    // unlock screen rotation
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR)

    // show vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TextAndSwitch(text = "Force night mode", checked = nightMode) {
            settingsViewModel.updateNightMode(it)
        }
        TextAndSwitch(text = "Before counting, wait", checked = beforeCountingWait) {
            settingsViewModel.updateBeforeCountingWait(it)
        }
        AnimatedVisibility(visible = beforeCountingWait) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp, 0.dp)
            ) {
                TextFieldForTimes(
                    value = howLongToWaitBeforeCounting,
                    label = { Text(text = "How long to wait before counting (seconds)") },
                    onValueChange = {
                        settingsViewModel.updateHowLongToWaitBeforeCounting(it)
                    }
                ) { Text(text = "5") }
            }
        }
        TextAndSwitch(
            text = "Count backwards (down to 0)",
            checked = countBackwards
        ) {
            settingsViewModel.updateCountBackwards(it)
        }
        TextAndSwitch(
            text = "No Sound (count silently)",
            checked = noSounds
        ) {
            settingsViewModel.updateNoSounds(it)
        }
        TextAndSwitch(
            text = "Vibrate",
            checked = vibrateEnabled
        ) {
            settingsViewModel.updateVibrateEnabled(it)
        }
        TextAndSwitch(
            text = "Chain to same category only",
            checked = chainToSameCategoryOnly
        ) {
            settingsViewModel.updateChainToSameCategoryOnly(it)
        }
        TextAndSwitch(
            text = "Only show first process of any chain (hide others)",
            checked = onlyShowFirstInChain
        ) {
            settingsViewModel.updateOnlyShowFirstInChain(it)
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
