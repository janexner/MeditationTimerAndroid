package com.exner.tools.meditationtimer.ui.destinations

import android.content.res.Configuration
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.exner.tools.meditationtimer.BuildConfig
import com.exner.tools.meditationtimer.ui.HeaderText
import com.exner.tools.meditationtimer.ui.theme.MeditationTimerTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun About() {

    // what's the orientation, right now?
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            // show horizontally
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) { AboutScreenElements() }
        }

        else -> {
            // show
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) { AboutScreenElements() }
        }
    }
}

@Composable
fun AboutScreenElements() {
    Column {
        HeaderText(
            text = "About Meditation Timer",
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Meditation Timer ${BuildConfig.VERSION_NAME}",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    val payload = "buildConfigAID=${BuildConfig.APPLICATION_ID}"
    val url = "https://www.jan-exner.de/software/android/meditationtimer/about/?$payload"
    val uriHandler = LocalUriHandler.current
    AndroidView(factory = { context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (request != null) {
                        return if (request.url.toString() == url) { // load this, and only this, URL in webView
                            false
                        } else { // load anything else in the Android default browser
                            uriHandler.openUri(request.url.toString())
                            true
                        }
                    }
                    return true
                }
            }
            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })
}

@Preview(
    showSystemUi = true,
    device = Devices.PHONE
)
@Preview(
    showSystemUi = true,
    device = Devices.NEXUS_5
)
@Preview(
    showSystemUi = true,
    device = Devices.TABLET
)
@Composable
fun FTAPreview() {
    MeditationTimerTheme {
        About()
    }
}