package com.exner.tools.meditationtimer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import com.exner.tools.meditationtimer.audio.SoundPoolHolder
import com.exner.tools.meditationtimer.audio.VibratorHolder
import com.exner.tools.meditationtimer.data.preferences.MeditationTimerUserPreferencesManager
import com.exner.tools.meditationtimer.ui.MainViewModel
import com.exner.tools.meditationtimer.ui.destinations.MeditationTimerGlobalScaffold
import com.exner.tools.meditationtimer.ui.theme.MeditationTimerTheme
import com.exner.tools.meditationtimer.ui.theme.Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesManager: MeditationTimerUserPreferencesManager

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        enableEdgeToEdge()

        setContent {
            // night mode has two possible triggers:
            // - device may be in night mode
            // - force night mode setting may be on
            val userTheme = viewModel.userSelectedTheme.collectAsState()

            // window size class
            val windowSizeClass = calculateWindowSizeClass(this)

            MeditationTimerTheme(
                darkTheme = userTheme.value == Theme.Dark || (userTheme.value == Theme.Auto && isSystemInDarkTheme())
            ) {
                MeditationTimerGlobalScaffold(windowSizeClass)
            }
        }

        // experiment: vibrate
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        VibratorHolder.initialise(vibrator)
    }

    override fun onResume() {
        super.onResume()

        // load all sounds
        SoundPoolHolder.loadSounds(this)
    }

    override fun onPause() {
        super.onPause()

        // release the kraken
        SoundPoolHolder.release()
    }
}
