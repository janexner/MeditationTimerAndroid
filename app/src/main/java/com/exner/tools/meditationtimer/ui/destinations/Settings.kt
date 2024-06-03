package com.exner.tools.meditationtimer.ui.destinations

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.meditationtimer.ui.LockScreenOrientation
import com.exner.tools.meditationtimer.ui.SettingsViewModel
import com.exner.tools.meditationtimer.ui.TextAndSwitch
import com.exner.tools.meditationtimer.ui.TextAndTriStateToggle
import com.exner.tools.meditationtimer.ui.theme.MeditationTimerTheme
import com.exner.tools.meditationtimer.ui.theme.Theme
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun Settings(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val userSelectedTheme by settingsViewModel.userSelectedTheme.collectAsStateWithLifecycle()
    val countBackwards by settingsViewModel.countBackwards.collectAsStateWithLifecycle()
    val chainToSameCategoryOnly by settingsViewModel.chainToSameCategoryOnly.collectAsStateWithLifecycle()
    val noSounds by settingsViewModel.noSounds.collectAsStateWithLifecycle()
    val vibrateEnabled by settingsViewModel.vibrateEnabled.collectAsStateWithLifecycle()
    val showSimpleDisplay by settingsViewModel.showSimpleDisplay.collectAsStateWithLifecycle()

    // unlock screen rotation
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR)

    // show vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TextAndTriStateToggle(
            text = "Theme",
            currentTheme = userSelectedTheme,
            updateTheme = { it: Theme ->
                settingsViewModel.updateUserSelectedTheme(
                    it
                )
            }
        )
        TextAndSwitch(text = "Simplify Display", checked = showSimpleDisplay) {
            settingsViewModel.updateShowSimpleDisplay(it)
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
