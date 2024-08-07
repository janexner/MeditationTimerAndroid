package com.exner.tools.meditationtimer.data.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.nearby.connection.Payload
import kotlin.text.Charsets.UTF_8

@Entity
data class MeditationTimerProcess (
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "info") val info : String,
    @ColumnInfo(name = "uuid") val uuid: String,

    @ColumnInfo(name = "process_time") val processTime : Int = 30,
    @ColumnInfo(name = "interval_time") val intervalTime: Int = 5,

    @ColumnInfo(name = "has_auto_chain") val hasAutoChain: Boolean = false,
    @ColumnInfo(name = "goto_uuid") val gotoUuid: String?,
    @ColumnInfo(name = "goto_name") val gotoName: String?,

    @ColumnInfo(name = "category_id") val categoryId: Long?,

    @PrimaryKey(autoGenerate = true) val uid: Long = 0
) {
    fun toPayload() = Payload.fromBytes(
        "$name|$info|$uuid|$processTime|$intervalTime|$hasAutoChain|$gotoUuid|$gotoName|$categoryId|$uid".toByteArray(UTF_8)
    )
}