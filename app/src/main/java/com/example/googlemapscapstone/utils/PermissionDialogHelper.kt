package com.example.googlemapscapstone.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

private const val PERMISSION_MESSAGE = "Location permission is required to use this feature."

fun showRationaleDialog(activity: Activity, onProceed: () -> Unit) {
    showAlertDialog(
        context = activity,
        title = "Permission Needed",
        message = "Location permission is required to use this feature. Please allow location permission.",
        positiveButtonText = "Allow",
        positiveButtonAction = onProceed,
        negativeButtonText = "Cancel",
        negativeButtonAction = {
            showToast(activity, PERMISSION_MESSAGE)
        }
    )
}

fun showSettingsAlertDialog(activity: Activity) {
    showAlertDialog(
        context = activity,
        title = "Permission Denied",
        message = "Location permission is required to use this feature. Please allow location permission in App Settings.",
        positiveButtonText = "Settings",
        positiveButtonAction = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivity(intent)
        },
        negativeButtonText = "Cancel",
        negativeButtonAction = {
            showToast(activity, PERMISSION_MESSAGE)
        }
    )
}