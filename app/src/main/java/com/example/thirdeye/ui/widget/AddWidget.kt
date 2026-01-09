package com.example.thirdeye.ui.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.widget.Toast

object AddWidget {

    fun addWidget(context: Context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Toast.makeText(context, "Please add widget manually", Toast.LENGTH_SHORT).show()
            return
        }

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val provider = ComponentName(context, IntruderWidget::class.java)

        if (!appWidgetManager.isRequestPinAppWidgetSupported) {
            Toast.makeText(context, "Launcher does not support widgets", Toast.LENGTH_SHORT).show()
            return
        }


        appWidgetManager.requestPinAppWidget(
            provider,
            null,
            null
        )
    }
}
