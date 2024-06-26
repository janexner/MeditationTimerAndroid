package com.exner.tools.meditationtimer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.exner.tools.meditationtimer.audio.SoundPoolHolder
import com.exner.tools.meditationtimer.audio.VibratorHolder
import com.exner.tools.meditationtimer.data.preferences.MeditationTimerUserPreferencesManager
import com.exner.tools.meditationtimer.ui.MainViewModel
import com.exner.tools.meditationtimer.ui.destinations.MeditationTimerGlobalScaffold
import com.exner.tools.meditationtimer.ui.theme.MeditationTimerTheme
import com.exner.tools.meditationtimer.ui.theme.Theme
import com.google.android.material.elevation.SurfaceColors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesManager: MeditationTimerUserPreferencesManager

    private val viewModel: MainViewModel by viewModels()

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (userTheme.value == Theme.Dark || (userTheme.value == Theme.Auto && isSystemInDarkTheme())) {
                    window.navigationBarColor = Color(0xFF000000).toArgb()
                    window.insetsController?.setSystemBarsAppearance(
                        0,
                        APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                } else {
                    window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
                    window.insetsController?.setSystemBarsAppearance(
                        APPEARANCE_LIGHT_NAVIGATION_BARS,
                        APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                }
            }

            MeditationTimerTheme(
                darkTheme = userTheme.value == Theme.Dark || (userTheme.value == Theme.Auto && isSystemInDarkTheme())
            ) {
                MeditationTimerGlobalScaffold()
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
