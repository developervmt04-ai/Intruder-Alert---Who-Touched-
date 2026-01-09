package com.example.thirdeye.utils

import android.app.AlertDialog
import android.content.Context

object showStopServiceDialog {
    fun showDialog(
        context: Context,

        onYesClick: ()-> Unit,



    ) {

        AlertDialog.Builder(context)
            .setTitle("Biometric unavailable").setMessage(
                "Intruder monitoring is currently active.\n\n" +
                        "To enable biometric lock, please stop monitoring first."
            ).setPositiveButton("Stop monitoring") { _, _ ->
                onYesClick.invoke()

            }
            .setNegativeButton("Cancel",null).show()


    }
}