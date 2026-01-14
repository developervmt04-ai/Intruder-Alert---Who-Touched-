package com.example.thirdeye.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import com.example.thirdeye.R
import com.example.thirdeye.R.layout.intruder_widget
import com.example.thirdeye.ui.splash.SplashActivity

import java.io.File


class IntruderWidget : AppWidgetProvider() {

    companion object {
        fun updateWidgetDirect(
            context: Context,
            state: String,
            dataTime: String,
            isLocked: Boolean = false,
            bitmap: android.graphics.Bitmap? = null
        ) {
            val awm = AppWidgetManager.getInstance(context)
            val ids = awm.getAppWidgetIds(ComponentName(context, IntruderWidget::class.java))

            val views = RemoteViews(context.packageName, intruder_widget)
            views.setTextViewText(R.id.tvIntrusionState, state)
            views.setTextViewText(R.id.tvLastIntrusionTime, dataTime)

            val finalBitmap = bitmap ?: BitmapFactory.decodeResource(context.resources, R.drawable.blurbg)
            views.setImageViewBitmap(R.id.ivIntruder, finalBitmap)


            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                if (!isLocked) putExtra("navigateTo", "intruder")
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.widget , pendingIntent)

            for (id in ids) {
                awm.updateAppWidget(id, views)
            }
        }

    }
}

