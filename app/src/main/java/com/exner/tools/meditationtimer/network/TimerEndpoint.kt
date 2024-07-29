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

    override fun hashCode(): Int {
        var result = endpointId.hashCode()
        result = 31 * result + userName.hashCode()
        return result
    }
}