package com.exner.tools.meditationtimer.network

class TimerEndpoint(
    val endpointId: String,
    val userName: String
) {
    override fun equals(other: Any?): Boolean {
        if (other is TimerEndpoint) {
            return endpointId == other.endpointId && userName == other.userName
        }
        return false
    }
}