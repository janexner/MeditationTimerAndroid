package com.exner.tools.meditationtimer.network

import android.content.Context
import android.util.Log
import com.exner.tools.meditationtimer.ui.endpointId
import com.exner.tools.meditationtimer.ui.userName
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback


class TimerEndpointDiscoveryCallback(
    val context: Context
) : EndpointDiscoveryCallback() {
    override fun onEndpointFound(p0: String, endpointInfo: DiscoveredEndpointInfo) {
        Log.d("TEDC", "On Endpoint Found... $p0 / ${endpointInfo.endpointName}")

        // An endpoint was found. We request a connection to it.
        val endpoint = TimerEndpoint(p0, endpointInfo.endpointName)
        val connectionsClient = Nearby.getConnectionsClient(context)
        val connectionLifecycleCallback = TimerConnectionLifecycleCallback(context = context)

        connectionsClient.requestConnection(
                endpoint.userName,
                endpoint.endpointId,
                connectionLifecycleCallback
            )
            .addOnSuccessListener { _: Void? ->
                Log.d("TEDC", "Connection request succeeded!")
            }
            .addOnFailureListener { e: Exception? ->
                if (e != null) {
                    Log.d("TEDC", "Connection failed: ${e.message}")
                }
            }

    }

    override fun onEndpointLost(p0: String) {
        Log.d("TEDC", "On Endpoint Lost... $p0")
    }
}