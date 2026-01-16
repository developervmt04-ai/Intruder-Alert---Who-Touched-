package com.example.thirdeye.utils

import android.app.AlertDialog
import android.content.Context
import com.example.thirdeye.R

object showStopServiceDialog {
    fun showDialog(
        context: Context,

        onYesClick: ()-> Unit,



    ) {

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.biometric_unavailable_title)).setMessage(
                "Intruder monitoring is currently active.\n\n" +
                        "To enable biometric lock, please stop monitoring first."
            ).setPositiveButton("Stop monitoring") { _, _ ->
                onYesClick.invoke()

            }
            .setNegativeButton("Cancel",null).show()


    }
}