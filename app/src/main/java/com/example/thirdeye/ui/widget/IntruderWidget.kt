package com.example.thirdeye.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RemoteViews
import com.example.thirdeye.R
import com.example.thirdeye.ui.splash.SplashActivity

class IntruderWidget : AppWidgetProvider() {

    companion object {

        fun updateWidgetDirect(
            context: Context,
            state: String,
            dataTime: String,
            isLocked: Boolean = false,
            bitmap: Bitmap? = null
        ) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, IntruderWidget::class.java)
            )

            if (appWidgetIds.isEmpty()) return

            val views = RemoteViews(context.packageName, R.layout.intruder_widget)

            if (bitmap != null) {
                views.setViewVisibility(R.id.emptyIcon, View.GONE)
                views.setViewVisibility(R.id.intruderWidgetDetails, View.VISIBLE)

                val finalBitmap = scaleToWidgetSize(bitmap)
                views.setImageViewBitmap(R.id.ivIntruder, finalBitmap)

                views.setTextViewText(R.id.widget_time, dataTime)

            } else {
                views.setViewVisibility(R.id.emptyIcon, View.VISIBLE)
                views.setViewVisibility(R.id.intruderWidgetDetails, View.GONE)

                views.setImageViewResource(R.id.ivIntruder, R.drawable.blurbg)

                views.setTextViewText(R.id.tvIntrusionState, state)
            }

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
            views.setOnClickPendingIntent(R.id.widget, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetIds, views)
        }
        private fun scaleToWidgetSize(bitmap: Bitmap): Bitmap {

            val targetWidth = 125
            val targetHeight = 125

            return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        }



    }

}