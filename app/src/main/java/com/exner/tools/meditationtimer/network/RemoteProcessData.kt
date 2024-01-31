package com.exner.tools.meditationtimer.network

data class RemoteProcessData (
    var version: Int,
    var date: Long,
    var namespace: String,
    var processes: Array<GenericProcess>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RemoteProcessData

        if (version != other.version) return false
        if (date != other.date) return false
        if (namespace != other.namespace) return false
        if (!processes.contentEquals(other.processes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = version
        result = 31 * result + date.hashCode()
        result = 31 * result + namespace.hashCode()
        result = 31 * result + processes.contentHashCode()
        return result
    }
}