package com.malabarmatrix.slithersync.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissions {
    const val REQ_ACTIVITY_RECOGNITION = 2001

    fun hasActivityRecognition(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestActivityRecognition(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
            REQ_ACTIVITY_RECOGNITION
        )
    }
}
