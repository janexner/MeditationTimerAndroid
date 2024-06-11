package com.exner.tools.meditationtimer.network

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes



class TimerConnectionLifecycleCallback(
    val context: Context
) : ConnectionLifecycleCallback() {
    override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
        // Automatically accept the connection on both sides.
        val payloadCallback = TimerPayloadCallback()
        Nearby.getConnectionsClient(context).acceptConnection("com.exner.tools.activitytimerfortv", payloadCallback)
    }

    override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
        when (result.getStatus().getStatusCode()) {
            ConnectionsStatusCodes.STATUS_OK -> {}
            ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {}
            ConnectionsStatusCodes.STATUS_ERROR -> {}
            else -> {}
        }

    }

    override fun onDisconnected(p0: String) {
        TODO("Not yet implemented")
    }
}