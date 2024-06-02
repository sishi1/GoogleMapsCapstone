package com.example.googlemapscapstone.utils

import android.app.AlertDialog
import android.content.Context

fun showAlertDialog(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String = "OK",
    positiveButtonAction: (() -> Unit)? = null,
    negativeButtonText: String = "Cancel",
    negativeButtonAction: (() -> Unit)? = null
) {
    AlertDialog.Builder(context).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButtonText) { dialog, _ ->
            dialog.dismiss()
            positiveButtonAction?.invoke()
        }
        setNegativeButton(negativeButtonText) { dialog, _ ->
            dialog.dismiss()
            negativeButtonAction?.invoke()
        }
        create()
        show()
    }
}