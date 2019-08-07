package com.example.android.architecture.blueprints.todoapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import timber.log.Timber

class PermissionChecker(private val context: Context) {

    private val isLocationPermissionGranted: Boolean
        get() = context.let {
            ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

    private val isReadContactPermissionGranted: Boolean
        get() = context.let {
            ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CONTACTS
            )
        } == PackageManager.PERMISSION_GRANTED

    fun locationPermissionCheck(): Boolean {
        if (!isLocationPermissionGranted) {
            Timber.w("Location Permission not granted.")
            return true
        }
        return false
    }

    fun readContactPermissionCheck(): Boolean {
        if (!isReadContactPermissionGranted) {
            Timber.w("Location Permission not granted.")
            return true
        }
        return false
    }

    companion object {
        val REQUEST_CONTACTS_CODE = 8436
    }
}