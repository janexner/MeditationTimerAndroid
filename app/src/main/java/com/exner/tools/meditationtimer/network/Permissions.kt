package com.exner.tools.meditationtimer.network

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class Permissions(
    val context: Context
) {
    data class IndividualPermission(
        val name: String,
        val minSdkVersion: Int,
        val maxSdkVersion: Int? = null
    )

    val allNecessaryPermissions = listOf(
        IndividualPermission(
            name = "android.permission.ACCESS_WIFI_STATE",
            minSdkVersion = 28,
            maxSdkVersion = 31
        ),
        IndividualPermission(
            name = "android.permission.CHANGE_WIFI_STATE",
            minSdkVersion = 28,
            maxSdkVersion = 31
        ),
        IndividualPermission(
            name = "android.permission.BLUETOOTH",
            minSdkVersion = 28,
            maxSdkVersion = 30
        ),
        IndividualPermission(
            name = "android.permission.BLUETOOTH_ADMIN",
            minSdkVersion = 28,
            maxSdkVersion = 30
        ),
        IndividualPermission(
            name = "android.permission.ACCESS_COARSE_LOCATION",
            minSdkVersion = 28,
            maxSdkVersion = 28
        ),
        IndividualPermission(
            name = "android.permission.ACCESS_FINE_LOCATION",
            minSdkVersion = 29,
            maxSdkVersion = 31
        ),
        IndividualPermission(
            name = "android.permission.BLUETOOTH_ADVERTISE",
            minSdkVersion = 31,
        ),
        IndividualPermission(
            name = "android.permission.BLUETOOTH_CONNECT",
            minSdkVersion = 31,
        ),
        IndividualPermission(
            name = "android.permission.BLUETOOTH_SCAN",
            minSdkVersion = 31,
        ),
        IndividualPermission(
            name = "android.permission.NEARBY_WIFI_DEVICES",
            minSdkVersion = 32,
        ),
    )

    private val currentReleaseLevel: Int = Build.VERSION.SDK_INT

    private val missingPermissions: MutableList<IndividualPermission> = mutableListOf()

    private val allNecessaryPermissionsAsListOfStrings: MutableList<String> = mutableListOf()

    // check all the permissions we need
    init {
        allNecessaryPermissions.forEach { individualPermission ->
            if (individualPermission.minSdkVersion <= currentReleaseLevel && (individualPermission.maxSdkVersion == null || individualPermission.maxSdkVersion >= currentReleaseLevel)) {
                allNecessaryPermissionsAsListOfStrings.add(individualPermission.name)
                val checkResult =
                    ContextCompat.checkSelfPermission(context, individualPermission.name)
                if (checkResult == PackageManager.PERMISSION_DENIED) {
                    missingPermissions.add(individualPermission)
                } else if (checkResult == PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.remove(individualPermission)
                }
            }
        }
    }

    fun getAllNecessaryPermissionsAsListOfStrings(): List<String> {
        return allNecessaryPermissionsAsListOfStrings
    }

    fun doWeHaveAllPermissions(): Boolean {
        return (0 == missingPermissions.size)
    }

    fun getMissingPermissions(): List<IndividualPermission> {
        return missingPermissions
    }
}