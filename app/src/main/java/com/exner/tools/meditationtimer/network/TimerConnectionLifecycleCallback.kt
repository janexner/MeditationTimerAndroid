package com.exner.tools.meditationtimer.network

import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution

class TimerConnectionLifecycleCallback: ConnectionLifecycleCallback() {
    override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
        TODO("Not yet implemented")
    }

    override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
        TODO("Not yet implemented")
    }

    override fun onDisconnected(p0: String) {
        TODO("Not yet implemented")
    }
}